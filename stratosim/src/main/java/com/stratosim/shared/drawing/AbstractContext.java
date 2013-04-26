package com.stratosim.shared.drawing;

import java.util.Stack;

public abstract class AbstractContext implements DrawableContext {

  private double textHeight = 20;
  private String font = "Courier";
  private double currentPointX;
  private double currentPointY;

  private Stack<StackFrameContainer> stack = new Stack<StackFrameContainer>();

  @Override
  public final void save() {
    stack.push(new StackFrameContainer(textHeight, font, currentPointX, currentPointY));

    saveImpl();
  }

  protected abstract void saveImpl();

  @Override
  public final void restore() {
    StackFrameContainer container = stack.pop();
    
    textHeight = container.getTextHeight();
    font = container.getFont();
    currentPointX = container.getCurrentPointX();
    currentPointY = container.getCurrentPointY();

    restoreImpl();
  }

  protected abstract void restoreImpl();

  @Override
  public final void translate(double x, double y) {
    translateImpl(x, y);
  }

  protected abstract void translateImpl(double x, double y);

  @Override
  public final void rotate(double angle) {
    rotateImpl(angle);
  }

  protected abstract void rotateImpl(double angle);

  @Override
  public final void scale(double x, double y) {
    scaleImpl(x, y);
  }

  protected abstract void scaleImpl(double x, double y);
  
  @Override
  public final void concat(double[] matrix) {
    concatImpl(matrix);
  }

  protected abstract void concatImpl(double[] matrix);

  @Override
  public final void setColor(double r, double g, double b) {
    setColorImpl(r, g, b);
  }

  protected abstract void setColorImpl(double r, double g, double b);

  @Override
  public final void fillTextLeft(String text, double x, double y) {
    fillTextLeftImpl(text, x, y);
  }

  protected abstract void fillTextLeftImpl(String text, double x, double y);

  @Override
  public final void fillTextRight(String text, double x, double y) {
    fillTextRightImpl(text, x, y);
  }

  protected abstract void fillTextRightImpl(String text, double x, double y);

  @Override
  public final void fillTextCentered(String text, double x, double y) {
    fillTextCenteredImpl(text, x, y);
  }

  protected abstract void fillTextCenteredImpl(String text, double x, double y);

  protected abstract void setFontImpl(String font, double textHeight);

  @Override
  public final void setMonospaceFont() {
    this.font = "Courier";
    setFontImpl(font, textHeight);
  }

  @Override
  public final void setSansSerifFont() {
    this.font = "Helvetica";
    setFontImpl(font, textHeight);
  }

  @Override
  public final double getTextWidth(String text) {
    return getTextWidthImpl(text);
  }

  protected abstract double getTextWidthImpl(String text);

  @Override
  public final void setTextHeight(double textHeight) {
    this.textHeight = textHeight;
    setFontImpl(font, textHeight);
  }

  @Override
  public final double getTextHeight() {
    return this.textHeight;
  }

  @Override
  public final void fillRect(double x, double y, double w, double h) {
    fillRectImpl(x, y, w, h);
  }

  protected abstract void fillRectImpl(double x, double y, double w, double h);

  @Override
  public final void newPath() {
    beginPathImpl();
  }

  protected abstract void beginPathImpl();

  @Override
  public final void closePath() {
    closePathImpl();
  }

  protected abstract void closePathImpl();

  @Override
  public final void moveTo(double x, double y) {
    currentPointX = x;
    currentPointY = y;

    moveToImpl(x, y);
  }

  protected abstract void moveToImpl(double x, double y);

  @Override
  public final void lineTo(double x, double y) {
    currentPointX = x;
    currentPointY = y;
    
    lineToImpl(x, y);
  }

  protected abstract void lineToImpl(double x, double y);

  @Override
  public final void relativeLineTo(double x, double y) {
    lineToImpl(currentPointX + x, currentPointY + y);

    currentPointX += x;
    currentPointY += y;
  }

  @Override
  public final void curveTo(double x, double y, double a, double b, double c, double d) {
    curveToImpl(x, y, a, b, c, d);
  }

  protected abstract void curveToImpl(double x, double y, double a, double b, double c, double d);

  @Override
  public final void arc(double x, double y, double r, double a1, double a2) {
    arcImpl(x, y, r, a1, a2);
  }

  protected abstract void arcImpl(double x, double y, double r, double a1, double a2);

  @Override
  public final void stroke() {
    strokeImpl();
  }

  protected abstract void strokeImpl();

  @Override
  public final void fill() {
    fillImpl();
  }

  protected abstract void fillImpl();

  @Override
  public final void setLineWidth(double lineWidth) {
    setLineWidthImpl(lineWidth);
  }

  protected abstract void setLineWidthImpl(double lineWidth);

  @Override
  public final double getLineWidth() {
    return getLineWidthImpl();
  }

  protected abstract double getLineWidthImpl();

}
