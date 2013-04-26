import csv
import os
import sys
import subprocess
import signal
import threading

import StringIO

import numpy
import spice_read

# TODO(tpondich): This is not well encapsulated.
logfile = None
proc1 = None

def timeout_handler():
    global logfile
    global proc1
    
    logfile.write("************************ TIMEOUT\n")
    if proc1:
        logfile.write("************************ KILLING NGSPICE\n")
        proc1.terminate()
    logfile.close()

def __read_spiceplot(plot):
    scale = plot.get_scalevector()
    zdata = plot.get_datavectors()
    
    # TODO(tpondich):
    # Hack to sort the encoded_names of probes
    data_map = dict()
    for d in zdata:
        name = str(d.name)

        if name.startswith("v(n") or name.startswith("i(vn"):
            # Convert spice encoding to our encoding for current.
            if name.startswith("i(vn"):
                name = name.replace("i(vn", "i(n", 1)

            sortable_name = name[:name.find('(')] + (name[name.find('name_') + 5:name.find('|color_')]).decode("hex")
            data_map[sortable_name] = d
        
    data = []
    for key in sorted(data_map.iterkeys()):
        d = data_map[key]
        data.append(d)
    # End hack

    csv_rows = []

    encoded_names = []
    for column in data:
        encoded_name = str(column.name)
        # Convert spice encoding to our encoding for current.
        if encoded_name.startswith("i(vn"):
            encoded_name = encoded_name.replace("i(vn", "i(n", 1)

        # Split complex numbers into magnitude and phase.
        if (numpy.iscomplexobj(d.get_data()[0])):
            encoded_names.append("m" + encoded_name)
            encoded_names.append("p" + encoded_name)
        else:
            encoded_names.append(encoded_name)

    names = []
    dimensions = []
    colors = []
    groups = []
    
    names.append(str(scale.name))
    dimensions.append(str(scale.name))
    colors.append('')
    groups.append('')
    
    for encoded_name in encoded_names:
        dimension = encoded_name[0:encoded_name.find("(")]
        dimensions.append(dimension)

        encoded_info = encoded_name[encoded_name.find("(") + 1:-1]
        encoded_parts = encoded_info.split("|")
        for encoded_part in encoded_parts:
            key_value = encoded_part.split("_")
            if len(key_value) != 2:
                continue
            if key_value[0] == 'name':
                names.append(key_value[1].decode("hex"))
            elif key_value[0] == 'color':
                colors.append(key_value[1].decode("hex"))
            elif key_value[0] == 'group':
                groups.append(key_value[1].decode("hex"))
            else:
                raise NameError(key_value)

    csv_rows.append(names)
    csv_rows.append(dimensions)
    csv_rows.append(colors)
    csv_rows.append(groups)

    for i in range(0, len(scale.get_data())):
        row = []
        row.append(float(scale.get_data()[i]))
        
        for d in data:
            if d.name.startswith("v(n") or d.name.startswith("i(vn"):
                if (d.get_data()[i].imag == 0):
                    row.append(float(d.get_data()[i]))
                else:
                    row.append(float(numpy.abs(d.get_data()[i])))
                    row.append(float(numpy.angle(d.get_data()[i])))

        csv_rows.append(row)
        
    return csv_rows

def __parse_file(infile):
    plots = spice_read.spice_read(infile).get_plots()
    return __read_spiceplot(plots[0])

def __get_csv(infile):
    csv_file = StringIO.StringIO()
    writer = csv.writer(csv_file, lineterminator='\n')
    
    rows = __parse_file(infile)
    writer.writerows(rows)

    return csv_file.getvalue()

def run_ngspice(key, format, data, tmpdir, tmpfile, timeout):
    t = threading.Timer(timeout, timeout_handler)
    t.start()

    logfile = open(tmpdir + tmpfile + '.log', 'w')
    
    logfile.write("************************ INPUT SPICE\n")
    logfile.write(data)
    logfile.flush()
    logfile.write("************************ RUNNING NGSPICE\n")
    proc1 = subprocess.Popen(['/usr/local/bin/ngspice', '-b', '-r', tmpdir + tmpfile + '.raw'],
                            stdin=subprocess.PIPE,
                            stdout=subprocess.PIPE,
                            stderr=subprocess.PIPE)
    
    (ngspiceout, ngspiceerr) = proc1.communicate(data)
    logfile.write("\n************************ NGSPICE STDOUT\n")
    if ngspiceout:
        logfile.write(ngspiceout)
        logfile.flush()
    logfile.write("\n************************ NGSPICE STDERR\n")
    if ngspiceerr:
        logfile.write(ngspiceerr)
        logfile.flush()

    csv = __get_csv(tmpdir + tmpfile + '.raw')
    
    logfile.write("\n************************ DONE SERVING\n")
    
    # Keep the files for debugging.
    #os.delete(TMP_DIR + tmpfile + '.raw')
    
    logfile.close()
    
    t.cancel()

    return csv
