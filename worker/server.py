# Cherrypy worker server to handle requests from AppEngine. Should be started
# using cherryd.

import base64
import cherrypy
import random
import socket
import sys
import time
import traceback

from datetime import datetime

from download_format import *

class Worker:
    @cherrypy.expose
    def index(self, input_format=None, output_format=None, circuit_hash=None, data=None):
        cfg = cherrypy.request.app.config['stratosim']
        
        request_id = str(random.random())[2:]
        start_time = datetime.now()
        
        if cfg['use_client_key']:
            received_key = cherrypy.request.headers['Client-Key']
            if cfg['client_key'] != received_key:
                cherrypy.log(request_id + ' INVALID CLIENT KEY') 
                raise cherrypy.HTTPError(403)
        
        if not input_format or not output_format or not circuit_hash or not data:
            cherrypy.log(request_id + ' INVALID REQUEST: ' +
                         str({
                              'input_format': input_format,
                              'output_format': output_format,
                              'circuit_hash': circuit_hash,
                              'data': data}) + ", " + str(cherrypy.request.method)) 
            raise cherrypy.HTTPError(400)
        
        cherrypy.log(request_id + ' ' + input_format + ' -> ' + output_format)

        input_download_format = parse_download_format(input_format)
        if input_download_format is None:
            cherrypy.log(request_id + ' INVALID FORMAT: ' + input_format_string)
            raise cherrypy.HTTPError(400)

        output_download_format = parse_download_format(output_format)
        if output_download_format is None:
            cherrypy.log(request_id + ' INVALID FORMAT: ' + output_format_string)
            raise cherrypy.HTTPError(400)

        # Check if there is a way to get to the output_format from the input_format.
        pipeline_tasks = []
        pipeline_tasks.append(output_download_format)
        while (pipeline_tasks[-1] != input_download_format):
            parent = pipeline_tasks[-1].get_parent()
            if parent is None:
                cherrypy.log(request_id + ' NO PATH: ' + input_format + '->' + output_format)
                raise cherrypy.HTTPError(400)
            pipeline_tasks.append(parent)
        pipeline_tasks.reverse()
            
        # Apache's Java Encoder does not pad URL safe encoding, so pad it here.
        data = str(data)
        padding_len = len(data) % 4
        data = data + ('=' * padding_len)

        data = str(base64.urlsafe_b64decode(data))

        #try:
        for pipeline_task in pipeline_tasks[1:]:
            tmp_file = circuit_hash + '_' + request_id + '_' + pipeline_task.get_name()
            cherrypy.log(request_id + '   CREATING ' + pipeline_task.get_name())
            data = pipeline_task.get_conversion()(circuit_hash, pipeline_task.get_name(), data, cfg['tmp_dir'], tmp_file, cfg['timeout_s'])

        end_time = datetime.now()
        
        processing_time = end_time - start_time

        cherrypy.log(request_id + ' SUCCESSFULLY SERVED in ' + str(processing_time))
        return '{hostname : "%s", request_id : "%s", time : "%s", data : "%s"}' %\
            (socket.gethostname(), request_id, str(processing_time), base64.urlsafe_b64encode(data))

        #except:
        #    cherrypy.log(request_id + ' ERROR RUNNING BINARY')
        #    raise cherrypy.HTTPError(500)

class HealthCheck:
    @cherrypy.expose
    def index(self):
        return 'ok'
