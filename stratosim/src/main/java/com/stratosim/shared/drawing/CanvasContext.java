package com.stratosim.shared.drawing;

import com.google.gwt.canvas.dom.client.Context2d;

public class CanvasContext extends AbstractContext {
  private Context2d context;

  public CanvasContext(Context2d context) {
    this.context = context;
  }

  @Override
  public void saveImpl() {
    context.save();
  }

  @Override
  public void restoreImpl() {
    context.restore();
  }

  @Override
  public void translateImpl(double x, double y) {
    context.translate(x, y);
  }

  @Override
  public void rotateImpl(double angle) {
    context.rotate(angle);
  }

  @Override
  public void scaleImpl(double x, double y) {
    context.scale(x, y);
  }
  
  @Override
  public void concatImpl(double[] matrix) {
    context.transform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
  }

  @Override
  protected void setFontImpl(String font, double fontsize) {
    context.setFont(fontsize + "px " + font);
  }

  @Override
  public void setColorImpl(double r, double g, double b) {
    String html5Color = getHtml5Color(r, g, b);
    
    context.setStrokeStyle(html5Color);
    context.setFillStyle(html5Color);
  }
  
  private static String getHtml5Color(double r, double g, double b) {
    return "#" + toTwoDigitHex(r * 255) + toTwoDigitHex(g * 255) + toTwoDigitHex(b * 255);
  }
  
  private static String toTwoDigitHex(double i) {
    String s = Integer.toHexString((int)i);
    if (s.length() == 1) {
      s = "0" + s;
    }
    
    return s;
  }

  /**
   * The point given is used as the bottom left of the text.
   */
  @Override
  public void fillTextLeftImpl(String text, double x, double y) {
    context.fillText(text, x, y);
  }
  
  /**
   * The point given is used as the bottom right of the text.
   */
  @Override
  public void fillTextRightImpl(String text, double x, double y) {
    context.fillText(text, x - context.measureText(text).getWidth(), y);
  }

  @Override
  public void fillTextCenteredImpl(String text, double x, double y) {
    context.fillText(text, x - context.measureText(text).getWidth() / 2, y);
  }

  @Override
  public void fillRectImpl(double x, double y, double w, double h) {
    context.fillRect(x, y, w, h);
  }

  @Override
  public void beginPathImpl() {
    context.beginPath();
  }

  @Override
  public void closePathImpl() {
    context.closePath();
  }

  @Override
  public void moveToImpl(double x, double y) {
    context.moveTo(x, y);
  }

  @Override
  public void lineToImpl(double x, double y) {
    context.lineTo(x, y);
  }

  @Override
  public void curveToImpl(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
    context.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
  }

  @Override
  public void arcImpl(double x, double y, double radius, double startAngle, double endAngle) {
    context.arc(x, y, radius, startAngle, endAngle);
  }

  @Override
  public void strokeImpl() {
    context.stroke();
    context.beginPath();
  }

  @Override
  public void fillImpl() {
    context.fill();
    context.beginPath();
  }

  @Override
  public double getTextWidthImpl(String text) {
    return context.measureText(text).getWidth();
  }
  
  @Override
  public void setLineWidthImpl(double lineWidth) {
    context.setLineWidth(lineWidth);
  }
  
  @Override
  public double getLineWidthImpl() {
    return context.getLineWidth();
  }
}
