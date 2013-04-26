package com.stratosim.shared.model.drawing;

/**
 * Object that can be hidden.
 */
public interface Hidable extends Drawable {

  boolean isHidden();

  void hide();

  void unhide(); // not show() because this merely enables, but does not repaint

}
