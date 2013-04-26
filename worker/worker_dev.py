import cherrypy
from server import HealthCheck, Worker

cherrypy.tree.mount(HealthCheck(), '/')
cherrypy.tree.mount(Worker(), '/worker', config='app.dev.config')
