package com.stratosim.shared.drawing;

/**
 * A PostScript compatible (theory not practice) API for drawing commands.
 * 
 * @author tarun
 *
 */
public interface DrawableContext {
  
  void save();
  void restore();

  void translate(double x, double y);
  void rotate(double angle);
  void scale(double x, double y);
  void concat(double[] transformMatrix);

  void setColor(double r, double g, double b);

  void newPath();
  void closePath();

  void moveTo(double x, double y);
  void lineTo(double x, double y);
  void relativeLineTo(double x, double y);
  void curveTo(double x, double y, double a, double b, double c, double d);
  // void relativeCurveTo(double x, double y, double a, double b, double c, double d);
  void arc(double x, double y, double r, double a1, double a2);

  void stroke();
  void fill();
  
  void fillRect(double x, double y, double w, double h);
  
  void setLineWidth(double lineWidth);
  double getLineWidth();
  
  double getTextWidth(String text);
  void setTextHeight(double height);

  double getTextHeight();

  void fillTextLeft(String text, double x, double y);
  void fillTextRight(String text, double x, double y);
  void fillTextCentered(String text, double x, double y);
  void setMonospaceFont();
  void setSansSerifFont();
}
