package com.stratosim.server.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.server.AppInfo;
import com.stratosim.server.external.ExternalWorkerHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.WorkerPersistenceLayer;
import com.stratosim.server.serving.AbstractStratoSimServlet;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;

public class WorkerCheckServlet extends AbstractStratoSimServlet {

  private static final long serialVersionUID = 8430762487174980208L;

  private final static int RETRY_DELAY = 2000;
  private final static int RETRIES = 15;

  private String TEST_SPICE = "" + "APPENGINERAND:8e7a4a1a3fd3e8e2\n"
      + "R1 2 name_50726f62652031|color_626c7565|group_766f6c7461676531|1 0\n" + "R2 2 0 10\n"
      + "V4 2 0 1\n" + ".TRAN 4.000000189989805E-6 0.0010000000474974513 UIC\n" + ".END";

  private String TEST_PS = "%!PS-Adobe-3.0 EPSF-3.0\n" + "%%BoundingBox: 0 0 197 160\n"
      + "<< /PageSize [197 160] >> setpagedevice\n" + "0.0 160.0 translate\n" + "1.0 -1.0 scale\n"
      + "-804.0 -220.0 translate\n" + "gsave\n" + "0.0 0.0 1.0 setrgbcolor\n"
      + "840.0 300.0 translate\n" + "1.0 1.0 scale\n" + "0.0 rotate\n" + "gsave\n" + "newpath\n"
      + "0.0 60.0 moveto\n" + "0.0 36.0 lineto\n" + "stroke\n" + "grestore\n" + "gsave\n"
      + "newpath\n" + "0.0 -60.0 moveto\n" + "0.0 -36.0 lineto\n" + "stroke\n" + "grestore\n"
      + "gsave\n" + "newpath\n" + "0.0 -36.0 moveto\n" + "16.0 -30.0 lineto\n"
      + "-16.0 -18.0 lineto\n" + "16.0 -6.0 lineto\n" + "-16.0 6.0 lineto\n" + "16.0 18.0 lineto\n"
      + "-16.0 30.0 lineto\n" + "0.0 36.0 lineto\n" + "stroke\n" + "grestore\n" + "grestore\n"
      + "gsave\n" + "/Courier findfont 20 scalefont setfont\n"
      + "/Helvetica findfont 20 scalefont setfont\n" + "0.0 0.0 1.0 setrgbcolor\n" + "gsave\n"
      + "861.0 300.0 translate\n" + "1 -1 scale\n" + "0 0 moveto\n" + "(R1) show\n" + "grestore\n"
      + "gsave\n" + "861.0 320.0 translate\n" + "1 -1 scale\n" + "0 0 moveto\n" + "(10ohms) show\n"
      + "grestore\n" + "grestore\n" + "gsave\n" + "1.0 0.4 0.0 setrgbcolor\n" + "newpath\n"
      + "840.0 360.0 5.0 0.0 360.0 arc\n" + "fill\n" + "grestore\n" + "gsave\n"
      + "1.0 0.4 0.0 setrgbcolor\n" + "newpath\n" + "840.0 240.0 5.0 0.0 360.0 arc\n" + "fill\n"
      + "grestore\n" + "showpage\n";

  @Override
  protected void doHandledGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String output = req.getParameter("outputFormat");

    if (output == null) {
      resp.setContentType("text/html");

      PrintWriter out = resp.getWriter();
      out.println("<html>");
      out.println("<head><title>Admin - Worker Check</title></head>");
      out.println("<body>");
      out.println("<p>");
      out.println("Backend Production Target: " + AppInfo.WORKER_PRODUCTION_URL);
      out.println("</p>");
      out.println("<table style=\"border-collapse:collapse\">");
      out.println(getRow(DownloadFormat.PDF, req.getRequestURI()));
      out.println(getRow(DownloadFormat.PNG, req.getRequestURI()));
      out.println(getRow(DownloadFormat.THUMBNAIL, req.getRequestURI()));
      out.println(getRow(DownloadFormat.SVG, req.getRequestURI()));
      out.println(getRow(DownloadFormat.CSV, req.getRequestURI()));
      out.println(getRow(DownloadFormat.SIMULATIONPDF, req.getRequestURI()));
      out.println(getRow(DownloadFormat.SIMULATIONPNG, req.getRequestURI()));
      // out.println(getRow(DownloadFormat.SIMULATIONPS, req.getRequestURI()));
      out.println("</table>");
      out.println("</body>");
      out.println("</html>");

    } else {
      resp.setContentType("text/html");

      DownloadFormat outputFormat = DownloadFormat.from(output);

      int latencyOrFail = testConversion(outputFormat);
      if (latencyOrFail == -1) {
        resp.getWriter().print("<span style=\"color:red\">FAIL</span>");
      } else {
        resp.getWriter().print(
            "<span style=\"color:green\">" + (latencyOrFail * RETRY_DELAY / 1000)
                + "s</span>");
      }
    }
  }

  private String getRow(DownloadFormat format, String srcBase) {
    String src = srcBase + "?outputFormat=" + format.getFormat();


    return "<tr><td style=\"border: 1px solid black;\">" + format.getFormat() + "</td>"
        + "<td style=\"border: 1px solid black;\">"
        + "<iframe style=\"height: 30px; border: none;\" src=\"" + src + "\" ></iframe>"
        + "</td></tr>";
  }

  private int testConversion(DownloadFormat outputFormat) {
    String input;
    DownloadFormat inputFormat;

    CircuitHash hash = new CircuitHash("test-" + Instant.now().getMillis());

    if (outputFormat.isRenderableFromPS()) {
      input = TEST_PS;
      inputFormat = DownloadFormat.PS;
    } else if (outputFormat.isRenderableFromSPICE()) {
      input = TEST_SPICE;
      inputFormat = DownloadFormat.SPICE;
    } else {
      throw new IllegalStateException();
    }

    ExternalWorkerHelper.getQueue()
        .add(
            ExternalWorkerHelper.createTask(outputFormat, inputFormat, hash,
                new Blob(input.getBytes())));

    for (int i = 0; i < RETRIES; i++) {
      if (silentWaitAndTest(hash, outputFormat)) {
        return i + 1;
      }
    }

    return -1;
  }

  private boolean silentWaitAndTest(CircuitHash hash, DownloadFormat format) {
    try {
      Thread.sleep(RETRY_DELAY);
    } catch (InterruptedException ex) {
      // No problem.
    }

    try {
      testExists(hash, format);
    } catch (PersistenceException e) {
      return false;
    }

    return true;
  }

  private void testExists(CircuitHash hash, DownloadFormat format) throws PersistenceException {
    WorkerPersistenceLayer workerLayer = PersistenceLayerFactory.createInternalLayer();
    workerLayer.getRenderedCircuit(hash, format);
  }
}
