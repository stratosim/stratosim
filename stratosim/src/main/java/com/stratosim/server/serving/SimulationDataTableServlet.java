package com.stratosim.server.serving;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPrivateForever;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPublicForever;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.stratosim.server.UsersHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.UserPersistenceLayer;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class SimulationDataTableServlet extends AbstractStratoSimServlet {
  private static final long serialVersionUID = -4281869250662630791L;

  private static final Logger logger = Logger.getLogger(SimulationDataTableServlet.class
      .getCanonicalName());

  @Override
  public void doHandledGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, DataSourceException, AccessException, PersistenceException {

    DataSourceRequest dSRequest = new DataSourceRequest(request);

    try {
      String versionKeyString = checkNotNull(request.getParameter("key"));
      final DownloadFormat format = DownloadFormat.CSV;

      VersionMetadataKey versionMetadataKey = VersionMetadataKey.parseFrom(versionKeyString);

      Blob blob = userLayer(request).getCircuitDataFromVersion(versionMetadataKey, format);

      // TODO(tpondich): Do this on the simulation server and pass csv transparently here.
      // But be careful of csv encoding! We can only do a hacky parse because the libs suck.
      // We can't use the Csv Stuff provided with visualization datasource because it doesn't
      // support
      // scientific notation. A bug has been filed.
      String csv = new String(blob.getBytes());
      String headerRow = csv.substring(0, csv.indexOf("\n"));
      csv = csv.substring(csv.indexOf("\n") + 1);
      String dimensionsRow = csv.substring(0, csv.indexOf("\n"));
      csv = csv.substring(csv.indexOf("\n") + 1);
      String colorsRow = csv.substring(0, csv.indexOf("\n"));
      csv = csv.substring(csv.indexOf("\n") + 1);
      String groupsRow = csv.substring(0, csv.indexOf("\n"));
      csv = csv.substring(csv.indexOf("\n") + 1);
      String dataRows = csv;

      String[] columnNames = headerRow.split(",");
      String[] dimensions = dimensionsRow.split(",");
      String[] colors = colorsRow.split(",");
      String[] groups = groupsRow.split(",");

      DataTable data = new DataTable();

      for (int i = 0; i < columnNames.length; i++) {
        ColumnDescription columnDescription =
            new ColumnDescription("" + i, ValueType.NUMBER, columnNames[i]);
        columnDescription.setCustomProperty("dimension", decodeVariableDimension(dimensions[i]));
        columnDescription.setCustomProperty("color", colors[i]);
        columnDescription.setCustomProperty("group", groups[i]);

        data.addColumn(columnDescription);
      }

      String[] rows = dataRows.split("\n");
      boolean firstRow = true;
      for (String row : rows) {
        TableRow tableRow = new TableRow();
        String[] values = row.split(",");

        for (int i = 0; i < data.getNumberOfColumns(); i++) {
          tableRow.addCell(Double.parseDouble(values[i]));
        }

        if (firstRow) {
          for (int i = 0; i < columnNames.length; i++) {
            // Because the GWT Wrapper for the Visualization Library doesn't support properties on
            // columns, but does on cells, embed the column properties with the cells in the first
            // row.

            for (Map.Entry<String, String> propertyEntry : data.getColumnDescription(i)
                .getCustomProperties().entrySet()) {
              tableRow.getCell(i).setCustomProperty(propertyEntry.getKey(),
                  propertyEntry.getValue());
            }
          }
          firstRow = false;
        }

        data.addRow(tableRow);
      }

      // Cache the version specific response forever since it can't change.
      if (userLayer(request).isPublic(versionMetadataKey.getFileKey())) {
        makeCachedPublicForever(response);
      } else {
        makeCachedPrivateForever(response);
      }

      DataSourceHelper.setServletResponse(data, dSRequest, response);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Parsing Error", e);
      DataSourceHelper.setServletErrorResponse(new DataSourceException(ReasonType.INTERNAL_ERROR,
          ""), dSRequest, response);
    }
  }

  // This format is used in the simulation server
  // (simulate.py). This format is also in CircuitToSpice as well as the spice templates.
  // Do not change without updating them.

  // TODO(tpondich): Clean up dimensions and encapsulate.
  private static String decodeVariableDimension(String dimension) {

    if (dimension.equals("time")) {
      return "Time (s)";
    } else if (dimension.equals("frequency")) {
      return "Frequency (Hz)";
    } else if (dimension.equals("v")) {
      return "Voltage (V)";
    } else if (dimension.equals("i")) {
      return "Current (A)";
    } else if (dimension.equals("mv")) {
      return "Gain (Vout/Vin)";
    } else if (dimension.equals("pv")) {
      return "Phase (Vout/Vin)";
    } else if (dimension.equals("mi")) {
      return "Gain (Iout/Iin)";
    } else if (dimension.equals("pi")) {
      return "Phase (Iout/Iin)";
    } else {
      throw new IllegalArgumentException("Invalid dimension: " + dimension);
    }
  }

  private static UserPersistenceLayer userLayer(HttpServletRequest request) {
    return PersistenceLayerFactory.createUserLayer(UsersHelper.getCurrentUser(request));
  }
}
