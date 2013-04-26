import cherrypy
from server import HealthCheck, Worker

cherrypy.tree.mount(HealthCheck(), '/')
cherrypy.tree.mount(Worker(), '/worker', config='/home/ec2-user/worker/app.prod.config')
