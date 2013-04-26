package com.stratosim.shared.drawing;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireJunction;

public class CircuitObjectDrawer {

  private CircuitObjectDrawer() {
    throw new UnsupportedOperationException();
  }

  public static void render(DrawableContext context, Circuit circuit) {
    for (Device device : circuit.getDevices()) {
      device.draw(context, false, false);
      device.getLabel().draw(context, false, false);
    }

    for (Wire wire : circuit.getWires()) {
      wire.draw(context, false, false);
    }

    for (WireJunction junction : circuit.getWireJunctions()) {
      junction.draw(context, false, false);
    }

    // can't combine this loop with previous device one because
    // ports should be drawn on top of devices and wires
    for (Device device : circuit.getDevices()) {
      for (Port port : device.getPorts()) {
        port.draw(context, false, false);
      }
    }
  }

  public static void render(DrawableContext context, String postscript) {
    
    context.save();

    Iterable<String> stack = Splitter.on(CharMatcher.WHITESPACE).split(postscript);

    // In the PostScript abstraction, these are the same stack. If the valueStack
    // contains a null value, we treat that element as an array and look on the
    // array stack.
    Stack<Double> valueStack = new Stack<Double>();
    Stack<List<Double>> arrayStack = new Stack<List<Double>>();

    for (String value : stack) {
      double doubleValue;

      try {
        doubleValue = Double.parseDouble(value);
        valueStack.push(doubleValue);

      } catch (NumberFormatException ex) {
        if (value.equals("moveto")) {
          double y = valueStack.pop();
          double x = valueStack.pop();
          context.moveTo(x, y);

        } else if (value.equals("lineto")) {
          double y = valueStack.pop();
          double x = valueStack.pop();
          context.lineTo(x, y);

        } else if (value.equals("rlineto")) {
          double y = valueStack.pop();
          double x = valueStack.pop();
          context.relativeLineTo(x, y);

        } else if (value.equals("curveto")) {
          double d = valueStack.pop();
          double c = valueStack.pop();
          double b = valueStack.pop();
          double a = valueStack.pop();
          double y = valueStack.pop();
          double x = valueStack.pop();
          context.curveTo(x, y, a, b, c, d);

        } else if (value.equals("newpath")) {
          context.newPath();

        } else if (value.equals("closepath")) {
          context.closePath();

        } else if (value.equals("fill")) {
          context.fill();

        } else if (value.equals("stroke")) {
          context.stroke();

        } else if (value.equals("rectfill")) {
          double h = valueStack.pop();
          double w = valueStack.pop();
          double y = valueStack.pop();
          double x = valueStack.pop();
          context.fillRect(x, y, w, h);

        } else if (value.equals("setrgbcolor")) {
          double b = valueStack.pop();
          double g = valueStack.pop();
          double r = valueStack.pop();
          context.setColor(r, g, b);

        } else if (value.equals("exch")) {
          double v1 = valueStack.pop();
          double v2 = valueStack.pop();
          valueStack.push(v1);
          valueStack.push(v2);

        } else if (value.equals("dup")) {
          double v = valueStack.pop();
          valueStack.push(v);
          valueStack.push(v);

        } else if (value.equals("neg")) {
          double v = valueStack.pop();
          valueStack.push(-v);

        } else if (value.equals("roll")) {
          int j = valueStack.pop().intValue();
          int n = valueStack.pop().intValue();

          double[] vals = new double[n];
          for (int i = 0; i < n; i++) {
            vals[i] = valueStack.pop();
          }

          for (int i = j - 1; i >= 0; i--) {
            valueStack.push(vals[i]);
          }
          for (int i = n - 1; i > j - 1; i--) {
            valueStack.push(vals[i]);
          }

        } else if (value.equals("array")) {
          int n = valueStack.pop().intValue();

          valueStack.push(null);

          List<Double> array = Lists.newArrayList();
          for (int i = 0; i < n; i++) {
            array.add(null);
          }

          arrayStack.push(array);

        } else if (value.equals("astore")) {
          Double shouldBeNull = valueStack.pop();
          checkState(shouldBeNull == null);

          List<Double> oldArray = arrayStack.pop();
          List<Double> array = Lists.newArrayList();
          for (int i = 0; i < oldArray.size(); i++) {
            array.add(valueStack.pop());
          }
          Collections.reverse(array);

          valueStack.push(null);
          arrayStack.push(array);

        } else if (value.equals("concat")) {
          Double shouldBeNull = valueStack.pop();
          checkState(shouldBeNull == null);

          List<Double> matrix = arrayStack.pop();

          double[] primitive = new double[matrix.size()];
          for (int i = 0; i < matrix.size(); i++) {
            primitive[i] = matrix.get(i);
          }
          context.concat(primitive);

        } else if (value.equals("save")) {
          context.save();

        } else if (value.equals("restore")) {
          context.restore();

        } else {
          System.err.println("******** " + value);
        }
      }
    }

    context.restore();
    
  }
}
