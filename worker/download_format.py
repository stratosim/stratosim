from ghostscript import run_ghostscript
from pstoedit import run_pstoedit
from ngspice import run_ngspice
from gnuplot import run_gnuplot

class DownloadFormat:
    def __init__(self, name, parent, conversion):
        self.name = name
        self.parent = parent
        self.conversion = conversion
        
    def __str__(self):
        return self.name
        
    def get_name(self):
        return self.name
    
    def get_parent(self):
        return self.parent
    
    def get_conversion(self):
        return self.conversion

# Define the conversion pathways for each format. All formats must eventually
# derive from CIRCUIT_POSTSCRIPT or CIRCUIT_SPICE root.  The formats should
# also match the ones defined in Java.

__format = []

__CIRCUIT_POSTSCRIPT = DownloadFormat("ps", None, None)
__format.append(__CIRCUIT_POSTSCRIPT)
__CIRCUIT_PDF = DownloadFormat("pdf", __CIRCUIT_POSTSCRIPT, run_ghostscript)
__format.append(__CIRCUIT_PDF)
__CIRCUIT_PNG = DownloadFormat("png", __CIRCUIT_POSTSCRIPT, run_ghostscript)
__format.append(__CIRCUIT_PNG)
__CIRCUIT_SVG = DownloadFormat("svg", __CIRCUIT_POSTSCRIPT, run_pstoedit)
__format.append(__CIRCUIT_SVG)
__CIRCUIT_THUMBNAIL = DownloadFormat("thumbnail", __CIRCUIT_POSTSCRIPT, run_ghostscript)
__format.append(__CIRCUIT_THUMBNAIL)

__CIRCUIT_SPICE = DownloadFormat("spice", None, None)
__format.append(__CIRCUIT_SPICE)
__SIMULATION_CSV = DownloadFormat("csv", __CIRCUIT_SPICE, run_ngspice)
__format.append(__SIMULATION_CSV)
__SIMULATION_POSTSCRIPT = DownloadFormat("simulationps", __SIMULATION_CSV, run_gnuplot)
__format.append(__SIMULATION_POSTSCRIPT)
__SIMULATION_PDF = DownloadFormat("simulationpdf", __SIMULATION_CSV, run_gnuplot)
__format.append(__SIMULATION_PDF)
__SIMULATION_PNG = DownloadFormat("simulationpng", __SIMULATION_CSV, run_gnuplot)
__format.append(__SIMULATION_PNG)

def parse_download_format(str):
    for format in __format:
        if format.get_name() == str:
            return format
        
    return None