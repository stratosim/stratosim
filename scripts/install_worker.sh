#!/bin/bash

# This script will install all the necessary tools on a bare Amazon Linux AMI instance.
# It will not perform any version specific configuration. This script does not
# need to actually be run. It is primarily for documentation purposes. To create
# an image for a new version, clone the base worker image, copy the worker and set
# it up in init.d.

# Install pip
curl https://raw.github.com/pypa/pip/master/contrib/get-pip.py | sudo python
# Install cherrypy
sudo pip install cherrypy

# Install numpy
sudo yum -y install gcc python-devel
sudo pip install numpy

# Install ngspice
sudo yum -y install make bison flex
mkdir downloads && cd downloads
wget http://downloads.sourceforge.net/project/ngspice/ng-spice-rework/24/ngspice-24.tar.gz
mkdir ~/bin
cd ~/bin
tar xf ~/downloads/ngspice-24.tar.gz
cd ngspice-24
mkdir release
cd release
../configure --enable-cider --enable-xspice
make 
sudo make install

# Install gnuplot
sudo yum -y install cairo-devel pango-devel
cd ~/downloads
wget http://sourceforge.net/projects/gnuplot/files/gnuplot/4.6.0/gnuplot-4.6.0.tar.gz/download
cd ~/bin
tar xf ~/downloads/gnuplot-4.6.0.tar.gz
cd gnuplot-4.6.0/
./configure
make
sudo make install

# Install ghostscript
sudo yum -y install ghostscript

# Install pstoedit
sudo yum -y install pstoedit

# Clean up
cd ~
rm -rf downloads
rm -rf bin
rm -f configure_worker.sh
