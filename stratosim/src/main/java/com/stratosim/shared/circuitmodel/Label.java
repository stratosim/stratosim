package com.stratosim.shared.circuitmodel;

import java.io.Serializable;
import java.util.Arrays;

import com.stratosim.shared.drawing.DrawableContext;

// Immutable
public class Label extends AbstractRelativePositionedObject implements Serializable, DrawableObject {
  private static final long serialVersionUID = 8324204095955982037L;

  private String[] textLines;

  private transient int width = -1;
  private transient int height = -1;

  // For GWT Serialization.
  @SuppressWarnings("unused")
  private Label() {}

  public Label(String string, Point relLoc, AbsolutePositionedObject device) {
    super(device, relLoc);

    this.textLines = string.split("\n");
  }

  /**
   * Adjust the coordinates of the text based on the rotation. By accounting for the
   * size of the label itself.
   * 
   * @return
   */
  private Point getAdjustedLocation() {
    if (getOwner().getRotation() == 0) {
      return getLocation().translate(0, -height);
    } else if (getOwner().getRotation() == 1) {
      return getLocation().translate(0, -height / 2);
    } else if (getOwner().getRotation() == 2) {
      return getLocation().translate(0, 0);
    } else if (getOwner().getRotation() == 3) {
      return getLocation().translate(0, -height / 2);
    }

    assert (false) : "Invalid Rotation Value";
    return null;
  }

  private void computeCrudeSize() {
    width = 0;
    for (int i = 0; i < textLines.length; i++) {
      if (width < textLines[i].length() * TEXT_SIZE) {
        width = textLines[i].length() * TEXT_SIZE;
      }
    }
    height = textLines.length * TEXT_SIZE;
  }

  /**
   * Return width from last paint or a crude estimate.
   * 
   * @return
   */
  public int getWidth() {
    if (width == -1) {
      computeCrudeSize();
    }
    return width;
  }

  public int getHeight() {
    if (height == -1) {
      computeCrudeSize();
    }
    return height;
  }

  /**
   * The center of the label.
   * 
   * @return
   */
  public Point getCenterLocation() {
    if (getOwner().getRotation() == 0) {
      if (!getOwner().isMirrored()) {
        return getLocation().translate(0, -getHeight() / 2);
      } else {
        return getLocation().translate(0, -getHeight() / 2);
      }
    } else if (getOwner().getRotation() == 1) {
      if (!getOwner().isMirrored()) {
        return getLocation().translate(getWidth() / 2, 0);
      } else {
        return getLocation().translate(-getWidth() / 2, 0);
      }
    } else if (getOwner().getRotation() == 2) {
      if (!getOwner().isMirrored()) {
        return getLocation().translate(0, getHeight() / 2);
      } else {
        return getLocation().translate(0, getHeight() / 2);
      }
    } else if (getOwner().getRotation() == 3) {
      if (!getOwner().isMirrored()) {
        return getLocation().translate(-getWidth() / 2, 0);
      } else {
        return getLocation().translate(getWidth() / 2, 0);
      }
    }

    assert (false) : "Invalid Rotation Value";
    return null;
  }

  @Override
  public boolean contains(Point p, int margin) {
    if (p == null) {
      return false;
    }

    Point center = getCenterLocation();
    Point topLeft = center.translate(-getWidth() / 2, -getHeight() / 2);
    Point bottomRight = center.translate(getWidth() / 2, getHeight() / 2);

    if (p.getX() > topLeft.getX() && p.getX() < bottomRight.getX() && p.getY() > topLeft.getY()
        && p.getY() < bottomRight.getY()) {
      return true;
    } else {
      return false;
    }

  }

  private void computeBox(DrawableContext context) {
    height = (int) (context.getTextHeight() * textLines.length);
    width = 0;
    for (int i = 0; i < textLines.length; i++) {
      double lineWidth = context.getTextWidth(textLines[i]);
      if (width < lineWidth) {
        width = (int) lineWidth;
      }
    }
  }

  public void draw(DrawableContext context, boolean selected, boolean hovered) {
    draw(context, selected, hovered, TEXT_SIZE);
  }

  private void drawLabel(DrawableContext context) {
    Point loc = getAdjustedLocation();

    for (int i = 0; i < textLines.length; i++) {
      if (getOwner().getRotation() == 1 && getOwner().isMirrored() || getOwner().getRotation() == 3
          && !getOwner().isMirrored()) {
        context.fillTextRight(textLines[i], loc.getX(),
            context.getTextHeight() * (i + 1) + loc.getY());

      } else if (getOwner().getRotation() == 1 && !getOwner().isMirrored()
          || getOwner().getRotation() == 3 && getOwner().isMirrored()) {
        context.fillTextLeft(textLines[i], loc.getX(),
            context.getTextHeight() * (i + 1) + loc.getY());

      } else {
        context.fillTextCentered(textLines[i], loc.getX(),
            context.getTextHeight() * (i + 1) + loc.getY());

      }
    }
  }

  public void draw(DrawableContext context, boolean selected, boolean hovered, double textSize) {
    if (getLocation() == null) {
      return;
    }

    context.save();

    context.setTextHeight(textSize);
    context.setSansSerifFont();

    computeBox(context);

    if (hovered) {
      context.setColor(HOVERED_COLOR.getRed(), HOVERED_COLOR.getGreen(), HOVERED_COLOR.getBlue());

      context.save();
      context.translate(getCenterLocation().getX(), getCenterLocation().getY());
      context.fillRect(-getWidth() / 2, -getHeight() / 2, getWidth(), getHeight());
      context.restore();

    }

    context.setColor(DEFAULT_COLOR.getRed(), DEFAULT_COLOR.getGreen(), DEFAULT_COLOR.getBlue());
    drawLabel(context);

    context.restore();
  }

  @Override
  public String toString() {
    return "Label [textLines=" + Arrays.deepToString(textLines) + ", width=" + width + ", height="
        + height + "]";
  }


}
