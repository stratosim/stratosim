import base64
import os
import sys
import subprocess
import signal
import threading

# TODO(tpondich): This is not well encapsulated.
logfile = None
proc1 = None

def timeout_handler():
    global logfile
    global proc1

    logfile.write("************************ TIMEOUT\n")
    if proc1:
        logfile.write("************************ KILLING GNUPLOT\n")
        proc1.terminate()
    logfile.close()
    
def __decode_variable_dimension(dimension):
    if dimension == "v":
        return 'Voltage (V)'
    elif dimension == "i":
        return 'Current (A)'
    elif dimension == "mv":
       return 'Gain (Vout/Vin)'
    elif dimension == "pv":
        return 'Phase (Vout/Vin)'
    elif dimension == "mi":
        return 'Gain (Iout/Iin)'
    elif dimension == "pi":
        return 'Phase (Iout/Iin)'
    else:
        raise NameError('Invalid dimension: ' + dimension)

def run_gnuplot(key, format, data, tmpdir, tmpfile, timeout):
    t = threading.Timer(timeout, timeout_handler)
    t.start()

    logfile = open(tmpdir + tmpfile + '.log', 'w')
    
    # PARSE THE INFORMATION OUT OF THE DATA
    names = data[:data.find("\n")].split(',')
    data = data[data.find("\n") + 1:]
    dimensions = data[:data.find("\n")].split(',')
    data = data[data.find("\n") + 1:]
    colors = data[:data.find("\n")].split(',')
    data = data[data.find("\n") + 1:]
    groups = data[:data.find("\n")].split(',')
    data = data[data.find("\n") + 1:]
    
    # DETERMINE CHARTS
    chart_types = dict()
    chart_types_ordered = []
    for i in range(1, len(names)):
        key = groups[i] + "|" + dimensions[i]
        if not key in chart_types.keys():
          chart_types_ordered.append(key)
          chart_types[key] = []
        chart_types[key].append(i);
    
    # SET BASIC LAYOUT ATTRIBUTES
    if format == "simulationpdf":
        GRAPHS_PER_PAGE = 3
        MARGIN = 1
        PAGE_WIDTH = 8
        PAGE_HEIGHT = 11
        LINE_WIDTH = 5
    elif format == "simulationpng":
        GRAPHS_PER_PAGE = len(chart_types_ordered)
        MARGIN = 0
        PAGE_WIDTH = 800
        PAGE_HEIGHT = (300 + MARGIN) * GRAPHS_PER_PAGE + MARGIN
        LINE_WIDTH = 2

    X_SCALE = 1.0 / PAGE_WIDTH
    Y_SCALE = 1.0 / PAGE_HEIGHT

    MARGIN_X = X_SCALE * MARGIN
    MARGIN_Y = Y_SCALE * MARGIN

    GRAPH_HEIGHT = (1.0 - (GRAPHS_PER_PAGE + 1) * MARGIN_Y) / GRAPHS_PER_PAGE
    GRAPH_WIDTH = (1.0 - 2 * MARGIN_X)
    
    gnuplotin = ""
    
    # SET TERMINAL
    if (format == "simulationps"):
        gnuplotin = gnuplotin + "set terminal postscript enhanced"
    elif (format == "simulationpdf"):
        gnuplotin = gnuplotin + "set terminal pdfcairo size " + str(PAGE_WIDTH) + "," + str(PAGE_HEIGHT) + " enhanced"
    elif (format == "simulationpng"):
        gnuplotin = gnuplotin + "set terminal pngcairo size " + str(PAGE_WIDTH) + "," + str(PAGE_HEIGHT) + " enhanced"
    else:
        raise NameError('Invalid Format: ' + format)
    
    # STYLE TO MATCH GOOGLE VIZ AS MUCH AS POSSIBLE
    gnuplotin = gnuplotin + """
      set datafile separator ","
      set style line 81 lt 1
      set style line 81 lt rgb "#808080"
      set grid back linestyle 81
      set border 3
      set xtics nomirror linestyle 81
      set ytics nomirror linestyle 81
      set key outside
      set grid
    """

    if dimensions[0] == 'time':
        gnuplotin = gnuplotin + "set xlabel 'Time (s)'\n"
    elif dimensions[0] == 'frequency':
        gnuplotin = gnuplotin + "set xlabel 'Frequency (Hz)'\n" 
    else:
        raise NameError('Invalid Scale: ' + dimensions[0])

    # SET CHART SIZES
    if format == "simulationps":
        gnuplotin = gnuplotin + "set size " + str(1.0) + ", " + str(1.0 / GRAPHS_PER_PAGE) + "\n"
    elif format == "simulationpdf" or format == "simulationpng":
        gnuplotin = gnuplotin + "set size " + str(GRAPH_WIDTH) + ", " + str(GRAPH_HEIGHT) + "\n"

    # PNG IS ALL ON ONE PAGE
    if format == "simulationpng":
        gnuplotin = gnuplotin + "set multiplot\n"

    i = 0
    for ytitle in chart_types_ordered:
        page_offset = i % GRAPHS_PER_PAGE

        # PDFS HAVE PAGE BREAKS EVERY 3 PLOTS
        if page_offset == 0 and format == "simulationpdf":
            gnuplotin = gnuplotin + """
               set nomultiplot
               set multiplot
            """

        # MULTIPLOT IS ON FOR PDFS AND PNGS SO SET POSITION
        if format == "simulationpdf" or format == "simulationpng":
            gnuplotin = gnuplotin + "set origin " + str(MARGIN_X) + ", " + str(1.0 - MARGIN_Y * (page_offset + 1) - GRAPH_HEIGHT * (page_offset + 1)) + "\n"

        gnuplotin = gnuplotin + "set ylabel '" + __decode_variable_dimension(ytitle[ytitle.find('|') + 1:]) + "' \n"

        # PLOT DATA
        plot = "plot "
        for column in chart_types[ytitle]:
            plot = plot + "'-' using 1:" + str(column + 1) + " with lines title '" + names[column] + "' lc rgb '" + colors[column] + "' lt 1 lw " + str(LINE_WIDTH) + ", "
        plot = plot[:-2]
        gnuplotin = gnuplotin + plot + "\n"

        for column in chart_types[ytitle]:
            gnuplotin = gnuplotin + data + "\n" + "e" + "\n"

        i = i + 1

    additional_args = ['/usr/local/bin/gnuplot']

    logfile.write("************************ INPUT CSV\n")
    logfile.write(gnuplotin)
    logfile.flush()
    logfile.write("************************ RUNNING GNUPLOT\n")
    proc1 = subprocess.Popen(additional_args,
                           stdin=subprocess.PIPE,
                           stdout=subprocess.PIPE,
                           stderr=subprocess.PIPE)

    (gnuplotout, gnuploterr) = proc1.communicate(gnuplotin)

    logfile.write("\n************************ GNUPLOT STDERR\n")
    if gnuploterr:
        logfile.write(gnuploterr)
        logfile.flush()

    logfile.write("\n************************ DONE\n")
    logfile.close()
    
    t.cancel()
    
    return gnuplotout
