#!/usr/bin/python

# Expand Inkscape Macros into Pure Postscript.

import os
import re
import sys

file_name = sys.argv[1]

input_file = open(file_name)
contents = input_file.read()
input_file.close()

contents_lines = contents.split("\n")
contents_lines = contents_lines[71:-5]
contents = " ".join(contents_lines)

# Replace Inkscape macros with their standard postscript expansions.
contents = re.sub("S", "STROKE", contents)
contents = re.sub("Q", "RESTORE", contents)
contents = re.sub("cm", "6 ARRAY ASTORE CONCAT", contents)
contents = re.sub("c", "CURVETO", contents)
contents = re.sub("l", "LINETO", contents)
contents = re.sub("m", "MOVETO", contents)
contents = re.sub("f", "FILL", contents)
contents = re.sub("h", "CLOSEPATH", contents)
contents = re.sub("q", "SAVE", contents)
contents = re.sub("rg", "SETRGBCOLOR", contents)

contents = re.sub("CLOSEPATH", "closepath", contents)
contents = re.sub("CURVETO", "curveto", contents)
contents = re.sub("LINETO", "lineto", contents)
contents = re.sub("MOVETO", "moveto", contents)
contents = re.sub("FILL", "fill", contents)
contents = re.sub("SETRGBCOLOR", "setrgbcolor", contents)
contents = re.sub("ARRAY", "array", contents)
contents = re.sub("ASTORE", "astore", contents)
contents = re.sub("CONCAT", "concat", contents)
contents = re.sub("STROKE", "stroke", contents)
contents = re.sub("SAVE", "save", contents)
contents = re.sub("RESTORE", "restore", contents)

contents = re.sub("[0-9]+\.?[0-9]* w", "", contents)
contents = re.sub("[0-9]+\.?[0-9]* j", "", contents)
contents = re.sub("[0-9]+\.?[0-9]* M", "", contents)
contents = re.sub("[0-9]+\.?[0-9]* J", "", contents)
contents = re.sub("[0-9]+\.?[0-9]* d", "", contents)
contents = re.sub("\[\]", "", contents)
contents = re.sub("\s+", " ", contents)

temp_name = file_name + '~~~'
output = open(temp_name,'w')

output.write(contents)
output.close()
os.rename(temp_name, file_name)
