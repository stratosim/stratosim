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
        logfile.write("************************ KILLING GHOSTSCRIPT\n")
        proc1.terminate()
    logfile.close()

def __fit(ps, page_width, page_height, margin):
    ps = str(ps)
    page_width = float(page_width)
    page_height = float(page_height)
    margin = float(margin)
    
    dims = ps[ps.find('[') + 1 : ps.find(']')].split(' ')
    width = int(dims[0])
    height = int(dims[1])
    
    scaleX = (page_width - margin * 2) / width
    scaleY = (page_height - margin * 2) / height
    scale = min(scaleX, scaleY)

    # TODO(tpondich): robustness needed!
    line1 = "<< /PageSize [" + str(page_width) + " " + str(page_height) + "] >> setpagedevice\n"
    line2 = str(margin) + " " + str(page_height - margin) + " translate\n"
    line3 = str(scale) + " -" + str(scale) + " scale\n"
    ps = ps[ps.find("scale\n") + 6 : ]
    ps = line1 + line2 + line3 + ps

    return ps

def run_ghostscript(key, format, data, tmpdir, tmpfile, timeout):
    t = threading.Timer(timeout, timeout_handler)
    t.start()

    logfile = open(tmpdir + tmpfile + '.log', 'w')

    PS_INCH = 72
    LETTER_WIDTH = 8.5 * PS_INCH
    LETTER_HEIGHT = 11 * PS_INCH
    LETTER_MARGIN = PS_INCH
    THUMBNAIL_WIDTH = 220
    THUMBNAIL_HEIGHT = 150
    THUMBNAIL_MARGIN = 10

    psin = ''
    additional_args = ['/usr/bin/gs', '-dQUIET', '-dNOPROMPT', '-dNOPAUSE', '-dPARANOIDSAFER', '-sOUTPUTFILE=%stdout']
    if format == 'thumbnail':
        additional_args.append('-sDEVICE=png256')
        additional_args.append('-dTextAlphaBits=4')
        additional_args.append('-dGraphicsAlphaBits=4')
        psin = __fit(data, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, THUMBNAIL_MARGIN)
    elif format == 'png':
        additional_args.append('-sDEVICE=png256')
        additional_args.append('-dTextAlphaBits=4')
        additional_args.append('-dGraphicsAlphaBits=4')
        psin = data
    elif format == 'pdf':
        additional_args.append('-sDEVICE=pdfwrite')
        psin = __fit(data, LETTER_WIDTH, LETTER_HEIGHT, LETTER_MARGIN)
    else:
        logfile.write('Invalid format: ' + format)
        logfile.close()
        return '' # TODO(tpondich): exception, catch return error to appengine.
    
    logfile.write("************************ INPUT PS\n")
    logfile.write(psin)
    logfile.flush()
    logfile.write("************************ RUNNING GHOSTSCRIPT\n")
    proc1 = subprocess.Popen(additional_args,
                           stdin=subprocess.PIPE,
                           stdout=subprocess.PIPE,
                           stderr=subprocess.PIPE)
    
    (gsout, gserr) = proc1.communicate(psin)
    
    logfile.write("\n************************ GHOSTSCRIPT STDERR\n")
    if gserr:
        logfile.write(gserr)
        logfile.flush()
    
    logfile.write("\n************************ DONE\n")
    logfile.close()
    
    t.cancel()
    
    return gsout
