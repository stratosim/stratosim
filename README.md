stratosim
=========

Settings
--------

Create a settings file modeled after settings.yaml.sample.
Then run configure.py to generate required files.

$ python configure.py /path/to/settings.py

Preparing the environment
-------------------------

To set up your environment, first make sure you have maven 3
(run 'mvn -version' to check) and jdk 6 installed.

$ mvn -version
Apache Maven 3.0.4
Maven home: /usr/share/maven
Java version: 1.7.0_09, vendor: Oracle Corporation
Java home: /usr/lib/jvm/java-7-openjdk-amd64/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "3.5.0-17-generic", arch: "amd64", family: "unix"

See local-repository-jars/README for a note about setting up maven. You'll
probably need to run 'local-repository-jars/install-jars'.

$ local-repository-jars/install-jars

Building and running locally with maven
---------------------------------------

Switch to the maven project directory and verify everything's ok.

$ cd stratosim
stratosim$ mvn verify

This might take a while and download a bunch of stuff. Finally, you should
be ready to run the local appengine server. Add the skipTests=true option
if the tests aren't passing right now. (Then fix the tests!)

stratosim$ mvn appengine:devserver -DskipTests=true

This command will start the server and wait. The server log will print out
in this terminal. Switch to your browser and visit http://localhost:8080.

I've had issues with http://localhost:8080/app, I think because of an XSRF
issue that prevents the GWT app from loading. The other stuff (/tutorial,
/a/appstats, etc.) should be fine though.

Deploying with maven
--------------------

The Google maven plugin has a goal for deployment. This will update the
appengine project version specified in:
  stratosim/src/main/webapp/WEB-INF/appengine-web.xml
skipTests=true might be necessary again.

stratosim$ mvn appengine:update -DskipTests=true

Setting up Eclipse
------------------

I was able to create an Eclipse project with the maven goal.

stratosim$ mvn eclipse:eclipse

This set up a project with classpath pointed to the appropriate JARs in
maven's local repository, and even configured the appengine path. I didn't
try deploying using the Google plugin, though.

You could also try directly using m2e (Eclipse maven integration).

