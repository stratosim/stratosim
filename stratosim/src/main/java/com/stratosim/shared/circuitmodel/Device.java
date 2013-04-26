package com.stratosim.shared.circuitmodel;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.drawing.CircuitObjectDrawer;
import com.stratosim.shared.drawing.ColorMapper;
import com.stratosim.shared.drawing.DrawableContext;

public class Device implements DrawableObject, AbsolutePositionedObject, Serializable {
  private static final long serialVersionUID = 8364841978196614602L;

  private DeviceType deviceType;

  private Point location;
  private int rotation;
  private boolean mirrored;

  private Label label;

  // The ports themselves are not Immutable.
  private ImmutableMap<String, Port> portMap;
  // The parameters are also not Immutable.
  private ImmutableMap<String, Parameter> parameterMap;

  @SuppressWarnings("unused")
  // for GWT RPC
  private Device() {}

  public Device(DeviceType deviceType, Point location, int rotation, boolean mirrored,
      ImmutableCollection<Parameter> parameters) {
    this.deviceType = deviceType;

    this.location = location;
    this.rotation = rotation;
    this.mirrored = mirrored;

    ImmutableMap.Builder<String, Parameter> builder = ImmutableMap.builder();
    for (Parameter parameter : parameters) {
      builder.put(parameter.getType().getName(), parameter);
    }
    parameterMap = builder.build();

    portMap = null;

    updateLabel();
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public ImmutableList<Port> getPorts() {
    return ImmutableList.copyOf(portMap.values());
  }

  public void setPorts(ImmutableList<Port> ports) {
    ImmutableMap.Builder<String, Port> builder = ImmutableMap.builder();
    for (Port port : ports) {
      builder.put(port.getName(), port);
    }
    portMap = builder.build();
  }

  private void updateLabel() {
    StringBuffer lineBuffer = new StringBuffer();
    String[] tokens = getType().getLabelTemplate().split("(\\{|\\})");
    for (String token : tokens) {
      if (token.length() == 0) {
        continue;
      }
      char leadingChar = token.charAt(0);
      if (leadingChar == '%') {
        String var = token.substring(1);
        if (var.equals("MODEL")) {
          if (getType().getModel() != null) {
            lineBuffer.append(getType().getModel());
          }
        } else {
          Parameter parameter = getParameter(var);
          if (parameter != null) {
            lineBuffer.append(parameter.getValue());
          } else {
            throw new IllegalStateException("Invalid in Label: " + var);
          }
        }
      } else {
        lineBuffer.append(token);
      }
    }

    String labelString = lineBuffer.toString();
    if (labelString.startsWith("\n")) {
      labelString = labelString.substring(1);
    }

    label = new Label(labelString, new Point(0, -getType().getH() / 2 - TEXT_MARGIN), this);
  }

  public Collection<Parameter> getParameters() {
    Collection<Parameter> parametersCopy = Lists.newArrayList();
    for (Parameter parameter : parameterMap.values()) {
      Parameter parameterCopy = parameter.getType().create();
      parameterCopy.setValue(parameter.getValue());
      parametersCopy.add(parameterCopy);
    }
    return parametersCopy;
  }

  public Label getLabel() {
    return label;
  }

  public void setParameters(Collection<Parameter> newParameters) {
    for (Parameter newParameter : newParameters) {
      Parameter oldParameter = parameterMap.get(newParameter.getType().getName());
      if (oldParameter == null) {
        throw new IllegalArgumentException("Parameter '" + newParameter.getType().getName()
            + "' not found");
      }
      oldParameter.setValue(newParameter.getValue());
    }

    updateLabel();
  }

  public Port getPort(String portName) {
    return portMap.get(portName);
  }

  public void setParameter(String parameterName, String value) {
    parameterMap.get(parameterName).setValue(value);
    updateLabel();
  }

  public @Nullable
  Parameter getParameter(String parameterName) {
    return parameterMap.get(parameterName);
  }

  public int getRotation() {
    return rotation;
  }

  public boolean getMirrored() {
    return mirrored;
  }

  // TODO(tpondich): The problem here is obvious.
  public void rotateRight() {
    if (mirrored) {
      rotation = (rotation + 3) % 4;
    } else {
      rotation = (rotation + 1) % 4;
    }
  }

  public void rotateLeft() {
    if (mirrored) {
      rotation = (rotation + 1) % 4;
    } else {
      rotation = (rotation + 3) % 4;
    }
  }

  public void flipHorizontal() {
    mirrored = !mirrored;
  }

  public void flipVertical() {
    mirrored = !mirrored;

    if (rotation == 1) {
      rotation = 3;
    } else if (rotation == 3) {
      rotation = 1;
    } else if (rotation == 0) {
      rotation = 2;
    } else if (rotation == 2) {
      rotation = 0;
    }
  }

  public boolean isMirrored() {
    return mirrored;
  }

  public void setMirrored(boolean mirrored) {
    this.mirrored = mirrored;
  }

  public int getWidth() {
    int rotation = getRotation();
    if (rotation == 0 || rotation == 2) {
      return getType().getW();
    } else {
      return getType().getH();
    }
  }

  public int getHeight() {
    int rotation = getRotation();
    if (rotation == 0 || rotation == 2) {
      return getType().getH();
    } else {
      return getType().getW();
    }
  }

  @Override
  public boolean contains(Point p, int margin) {
    if (getLocation() == null || p == null) {
      return false;
    }

    if (Math.abs(p.getX() - getLocation().getX()) < (getWidth() / 2 + margin)
        && Math.abs(p.getY() - getLocation().getY()) < (getHeight() / 2 + margin)) {
      return true;
    }

    if (label.contains(p, margin)) {
      return true;
    }

    return false;
  }

  @Override
  public void draw(DrawableContext context, boolean selected, boolean hovered) {

    if (getLocation() == null) {
      return;
    }

    context.save();

    // Draw bounding box for debugging.
    // This is shared code, no way to detect hosted mode.
    /*
     * context.save(); context.translate(getLocation().getX(), getLocation().getY());
     * context.setColor("#ccffcc"); context.beginPath(); context.fillRect(-getWidth() / 2,
     * -getHeight() / 2, getWidth(), getHeight()); context.stroke(); context.restore();
     */

    context.translate(getLocation().getX(), getLocation().getY());

    if (selected) {
      context
          .setColor(SELECTED_COLOR.getRed(), SELECTED_COLOR.getGreen(), SELECTED_COLOR.getBlue());
      context.fillRect(-getWidth() / 2, -getHeight() / 2, getWidth(), getHeight());

    } else if (hovered) {
      context.setColor(HOVERED_COLOR.getRed(), HOVERED_COLOR.getGreen(), HOVERED_COLOR.getBlue());
      context.fillRect(-getWidth() / 2, -getHeight() / 2, getWidth(), getHeight());

    }
    
    // TODO(tpondich): Permanent solution for devices being drawn based on parameters.
    Parameter colorParameter = getParameter("Color");
    if (colorParameter != null && colorParameter.getType().getValidator().isValid(colorParameter)) {
      Color color = ColorMapper.get(colorParameter.getValue());
      context.setColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    context.scale(isMirrored() ? -1 : 1, 1);
    context.rotate(Math.PI / 2 * getRotation());

    // The PostScript is generated by Inkscape. It needs to be flipped and translated to be centered
    // at the origin.
    context.scale(1, -1);
    context.translate(-getType().getW() / 2, -getType().getH() / 2);

    CircuitObjectDrawer.render(context, getType().getPostScriptImage());

    context.restore();
  }

  public DeviceType getType() {
    return deviceType;
  }

  public void setType(DeviceType deviceType) {
    this.deviceType = deviceType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + (mirrored ? 1231 : 1237);
    result = prime * result + ((parameterMap == null) ? 0 : parameterMap.hashCode());
    result = prime * result + rotation;
    return result;
  }

  // Equals only compares location, not wire connections.
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Device other = (Device) obj;
    if (deviceType == null) {
      if (other.deviceType != null) return false;
    } else if (!deviceType.equals(other.deviceType)) return false;
    if (location == null) {
      if (other.location != null) return false;
    } else if (!location.equals(other.location)) return false;
    if (mirrored != other.mirrored) return false;
    if (parameterMap == null) {
      if (other.parameterMap != null) return false;
    } else if (!parameterMap.equals(other.parameterMap)) return false;
    if (rotation != other.rotation) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Device [deviceType=" + deviceType + ", location=" + location + ", rotation=" + rotation
        + ", mirrored=" + mirrored + ", portMap=" + portMap + ", parameterMap=" + parameterMap
        + "]";
  }
}
