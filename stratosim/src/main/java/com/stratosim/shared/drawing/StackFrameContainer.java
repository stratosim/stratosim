package com.stratosim.shared.drawing;

class StackFrameContainer {
  private final double textHeight;
  private final String font;

  private final double currentPointX;
  private final double currentPointY;
  
  StackFrameContainer(double textHeight, String font, double currentPointX, double currentPointY) {
    this.textHeight = textHeight;
    this.font = font;
    this.currentPointX = currentPointX;
    this.currentPointY = currentPointY;
  }

  double getTextHeight() {
    return textHeight;
  }

  String getFont() {
    return font;
  }

  double getCurrentPointX() {
    return currentPointX;
  }

  double getCurrentPointY() {
    return currentPointY;
  }
  
}