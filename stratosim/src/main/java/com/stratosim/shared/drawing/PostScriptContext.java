package com.stratosim.shared.drawing;

import java.util.Arrays;

import com.stratosim.shared.circuitmodel.Point;

public class PostScriptContext extends AbstractContext {

  private StringBuffer postScript;

  private static final int MARGIN = 20;

  public PostScriptContext(StringBuffer postScript, Point topLeft, Point bottomRight) {
    this.postScript = postScript;

    int x = topLeft.getX() - MARGIN;
    int y = topLeft.getY() - MARGIN;
    int width = bottomRight.getX() - topLeft.getX() + 2 * MARGIN;
    int height = bottomRight.getY() - topLeft.getY() + 2 * MARGIN;

    // This is the standard header for eps.
    postScript.append("%!PS-Adobe-3.0 EPSF-3.0\n");
    postScript.append("%%BoundingBox: 0 0 " + width + " " + height + "\n");

    // This is searched for and altered in post processing with pull queue workers.
    // Do not modify carelessly.
    postScript.append("<< /PageSize [" + width + " " + height + "] >> setpagedevice\n");
    translate(0, height);
    scale(1, -1);

    translate(-x, -y);

    // Draw bounding box for debugging.
    // This is shared code, no way to detect hosted mode.
    /*
     * setColor("#ffffaa"); fillRect(topLeft.getX(), topLeft.getY(), bottomRight.getX() -
     * topLeft.getX(), bottomRight.getY() - topLeft.getY());
     */
  }

  @Override
  public void saveImpl() {
    postScript.append("gsave\n");
  }

  @Override
  public void restoreImpl() {
    postScript.append("grestore\n");
  }

  @Override
  public void translateImpl(double x, double y) {
    postScript.append(x + " " + y + " translate\n");
  }

  @Override
  public void rotateImpl(double angle) {
    postScript.append(Math.toDegrees(angle) + " rotate\n");
  }

  @Override
  public void scaleImpl(double x, double y) {
    postScript.append(x + " " + y + " scale\n");
  }
  
  @Override
  public void concatImpl(double[] matrix) {
    postScript.append(Arrays.toString(matrix).replaceAll(",", "") + " concat\n");
  }

  @Override
  public void setColorImpl(double r, double g, double b) {
    postScript.append(r + " " + g + " " + b + " setrgbcolor\n");
  }

  private static String escape(String string) {
    // Replace \ with \\
    string = string.replaceAll("\\\\", "\\\\\\\\");
    // Replace newline with \n
    string = string.replaceAll("\n", "\\\\n");
    // Replace carriage return with \r
    string = string.replaceAll("\r", "\\\\r");
    // Replace tab with \t
    string = string.replaceAll("\t", "\\\\t");
    // Replace ( with \(
    string = string.replaceAll("\\(", "\\\\(");
    // Replace ) with \)
    string = string.replaceAll("\\)", "\\\\)");

    return string;
  }

  @Override
  public void fillTextLeftImpl(String text, double x, double y) {
    postScript.append("gsave\n");
    postScript.append(x + " " + y + " translate\n");
    postScript.append("1 -1 scale\n");
    postScript.append("0 0 moveto\n");
    postScript.append("(" + escape(text) + ")" + " show\n");
    postScript.append("grestore\n");
  }

  @Override
  public void fillTextRightImpl(String text, double x, double y) {
    postScript.append("gsave\n");
    postScript.append(x + " " + y + " translate\n");
    postScript.append("1 -1 scale\n");
    postScript.append("0 0 moveto\n");
    postScript.append("(" + escape(text) + ")\n");
    postScript.append("dup stringwidth pop -1 mul 0\n");
    postScript.append("moveto\n");
    postScript.append("show\n");
    postScript.append("grestore\n");
  }

  @Override
  // TODO(tpondich): Can this be a higher level function?
  public void fillTextCenteredImpl(String text, double x, double y) {
    postScript.append("gsave\n");
    postScript.append(x + " " + y + " translate\n");
    postScript.append("1 -1 scale\n");
    postScript.append("0 0 moveto\n");
    postScript.append("(" + escape(text) + ")\n");
    postScript.append("dup stringwidth pop 2 div -1 mul 0\n");
    postScript.append("moveto\n");
    postScript.append("show\n");
    postScript.append("grestore\n");
  }

  @Override
  // TODO(tpondich): This should be a higher level function (outside Context)
  public void fillRectImpl(double x, double y, double w, double h) {
    postScript.append("newpath\n");
    postScript.append(x + " " + y + " moveto\n");
    postScript.append(0 + " " + h + " rlineto\n");
    postScript.append(w + " " + 0 + " rlineto\n");
    postScript.append(0 + " " + -h + " rlineto\n");
    postScript.append(-w + " " + -0 + " rlineto\n");
    postScript.append("closepath\n");
    postScript.append("fill\n");
  }

  @Override
  public void beginPathImpl() {
    postScript.append("newpath\n");
  }

  @Override
  public void closePathImpl() {
    postScript.append("closepath\n");
  }

  @Override
  public void moveToImpl(double x, double y) {
    postScript.append(x + " " + y + " moveto\n");
  }

  @Override
  public void lineToImpl(double x, double y) {
    postScript.append(x + " " + y + " lineto\n");
  }

  @Override
  public void curveToImpl(double x, double y, double a, double b, double c, double d) {
    postScript.append(x + " " + y + " " + a + " " + b + " " + c + " " + d + " curveto\n");
  }

  @Override
  public void arcImpl(double x, double y, double r, double a1, double a2) {
    postScript.append(x + " " + y + " " + r + " " + Math.toDegrees(a1) + " " + Math.toDegrees(a2)
        + " arc\n");
  }

  @Override
  public void strokeImpl() {
    postScript.append("stroke\n");
  }

  @Override
  public void fillImpl() {
    postScript.append("fill\n");
  }

  @Override
  public double getTextWidthImpl(String text) {
    return text.length() * getTextHeight();
  }

  @Override
  protected void setFontImpl(String font, double textHeight) {
    postScript.append("/" + font + " findfont " + (int) textHeight + " scalefont setfont\n");
  }

  @Override
  public void setLineWidthImpl(double lineWidth) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public double getLineWidthImpl() {
    throw new UnsupportedOperationException("Not yet implemented.");
  }
}
