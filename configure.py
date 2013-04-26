import jinja2
import sys
import yaml

FILENAMES = [
  'stratosim/pom.xml',
  'stratosim/src/main/java/com/stratosim/client/ui/view/loading/LoadingView.ui.xml',
  'stratosim/src/main/java/com/stratosim/server/AppInfo.java',
  'stratosim/src/main/webapp/robots.txt',
  'stratosim/src/main/webapp/WEB-INF/appengine-web.xml',
  'stratosim/src/main/webapp/WEB-INF/templates/NewCollaboratorEmail.html',
  'stratosim/src/main/webapp/WEB-INF/templates/include/Head.ftl',
  'worker/global.prod.config',
  'worker/app.prod.config',
]

def main():
  if len(sys.argv) < 2:
    print 'Must specify YAML file with settings.'
    sys.exit(1)
  
  SETTINGS_YAML = sys.argv[1]
  with open(SETTINGS_YAML) as yaml_file:
    settings = yaml.load(yaml_file.read())
  
  for filename in FILENAMES:
    with open('%s.template' % filename) as template_file:
      template = jinja2.Template(template_file.read())
      with open(filename, 'w') as rendered_file:
        rendered_file.write(template.render(settings))

if __name__ == '__main__':
  main()

