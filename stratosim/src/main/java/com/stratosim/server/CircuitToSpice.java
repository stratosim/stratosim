package com.stratosim.server;

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.Maps;
import com.stratosim.server.algorithm.UnionFind;
import com.stratosim.server.algorithm.WQUPCUnionFind;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Parameter;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireJunction;
import com.stratosim.shared.validator.CircuitValidator;
import com.stratosim.shared.validator.PreSimulateValidator;

public class CircuitToSpice {

  // TODO(tpondich): Define this somewhere else, analyze dynamically or something
  private final static int SIMULATION_STEPS = 250;

  private CircuitToSpice() {
    throw new UnsupportedOperationException("Not instantiable");
  }

  public static String encodeText(String s) {
    return new BigInteger(s.getBytes()).toString(16);
  }

  public static String decodeText(String s) {
    return new String(new BigInteger(s, 16).toByteArray());
  }

  // TODO(tpondich): This might as well be the validator
  // Validators should return values.
  // This will chop off units and create a clean spice number string.
  // This assumes a valid string. Ensure validation!
  public static String encodeNumeric(String s) {
    int i;
    for (i = 0; i < s.length(); i++) {
      if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '.' && s.charAt(i) != '-'
          && s.charAt(i) != 'e') {
        break;
      }
    }
    if (i < s.length()) {
      if (s.charAt(i) == 'm' || s.charAt(i) == 'u' || s.charAt(i) == 'n' || s.charAt(i) == 'p'
          || s.charAt(i) == 'f' || s.charAt(i) == 'k' || s.charAt(i) == 'g' || s.charAt(i) == 't') {
        i++;
      }
      // for meg
      if (i < s.length() && s.substring(i).startsWith("eg")) {
        i += 2;
      }
    }

    return s.substring(0, i);
  }

  // This will convert a valid spice number into a double.
  private static double parseNumeric(String numeric) {
    String plain = encodeNumeric(numeric);
    if (plain.endsWith("m")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e-3;
    } else if (plain.endsWith("u")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e-6;
    } else if (plain.endsWith("n")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e-9;
    } else if (plain.endsWith("p")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e-12;
    } else if (plain.endsWith("k")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e3;
    } else if (plain.endsWith("meg")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e6;
    } else if (plain.endsWith("g")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e9;
    } else if (plain.endsWith("t")) {
      return Float.parseFloat(plain.substring(0, plain.length() - 1)) * 1e12;
    }

    return Float.parseFloat(plain);
  }

  public static String convert(Circuit circuit) {
    CircuitValidator validator = new PreSimulateValidator();
    if (!validator.isValid(circuit)) {
      // The circuit is already validated on the client. If this is invalid now, someone
      // must have bypassed the client UI.
      if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
        return null;
      } else {
        throw new IllegalArgumentException("Circuit Invalid!\n" + validator.getMessage());
      }
    }

    // Maps each port to a unique integer.
    Map<Port, Integer> portMap = Maps.newHashMap();

    int portCount = 1; // 0 is used to tie together ground nodes.
    for (Device device : circuit.getDevices()) {
      for (Port port : device.getPorts()) {
        portMap.put(port, portCount);
        portCount++;
      }
    }
    for (WireJunction junction : circuit.getWireJunctions()) {
      portMap.put(junction.getOnlyPort(), portCount);
      portCount++;
    }

    UnionFind uf = new WQUPCUnionFind(portCount);
    for (Wire wire : circuit.getWires()) {
      uf.union(portMap.get(circuit.getPort(wire.getStartPortId())),
          portMap.get(circuit.getPort(wire.getEndPortId())));
    }

    for (Device d : circuit.getDevices()) {
      if (d.getType().getName().equals("Ground")) {
        Port ground = d.getPort("0");
        uf.union(0, portMap.get(ground));
      }
    }

    StringBuffer spiceBuffer = new StringBuffer();

    spiceBuffer.append("APPENGINERAND:" + Long.toHexString((new Random()).nextLong()) + "\n");

    int groundNode = uf.find(0);
    // Model Params --> MODEL_<N>
    Map<String, String> modelMap = Maps.newHashMap();
    int modelCount = 0;
    int componentCount = 0;
    for (Device d : circuit.getDevices()) {
      ++componentCount;
      String spiceTemplate = d.getType().getSpiceTemplate();
      String[] lines = spiceTemplate.split("\n");
      // Map %MODELNAME.<MODEL> to a MODEL<N> in modelMap
      Map<String, String> localModelMap = Maps.newHashMap();
      String parsedModelName = null;

      for (String line : lines) {
        StringBuffer lineBuffer = spiceBuffer;
        if (line.startsWith(".model")) {
          lineBuffer = new StringBuffer();
        }
        String[] tokens = line.split("(\\{|\\})");
        for (String token : tokens) {
          char leadingChar = token.charAt(0);
          if (leadingChar == '%' || leadingChar == '$') {
            String var = token.substring(1);
            if (var.startsWith("SPICENAME")) {
              lineBuffer.append(componentCount);
            } else if (var.startsWith("DEVICEID")) {
              lineBuffer.append(componentCount);
            } else if (var.startsWith("MODELNAME")) {
              String localModelName = var.substring(10);
              if (lineBuffer == spiceBuffer) {
                String globalModelName = localModelMap.get(localModelName);
                lineBuffer.append(globalModelName);
              } else {
                parsedModelName = localModelName;
              }
            } else {
              Port port = d.getPort(var);
              Parameter parameter = d.getParameter(var);
              if (port != null && parameter == null) {
                int spiceNumber = uf.find(portMap.get(port));
                if (spiceNumber == groundNode) {
                  lineBuffer.append(0);
                } else {
                  lineBuffer.append(spiceNumber);
                }
              } else if (parameter != null && port == null) {
                String sanitize = parameter.getValue();
                if (leadingChar == '$') {
                  sanitize = encodeText(sanitize);
                } else {
                  sanitize = encodeNumeric(sanitize);
                }
                // TODO(tpondich): Are there any non numeric inputs that would feed spice directly?
                lineBuffer.append(sanitize);
              } else {
                throw new IllegalStateException("Invalid spice template in "
                    + d.getType().getName() + " for " + token);
              }
            }
          } else {
            lineBuffer.append(token);
          }
        }
        if (lineBuffer != spiceBuffer) {
          // .model line
          String l = lineBuffer.toString().substring(7);
          String modelName = modelMap.get(l);
          if (modelName == null) {
            ++modelCount;
            modelName = "MODEL_" + modelCount;
            modelMap.put(l, modelName);
          }

          localModelMap.put(parsedModelName, modelName);
          parsedModelName = null;
        }
        spiceBuffer.append("\n");
      }
    }
    spiceBuffer.append("\n");

    // TODO(tpondich): Needs great improvement and also client side validation.
    double transientDuration = parseNumeric(circuit.getSimulationSettings().getTransientDuration());
    double startFrequency = parseNumeric(circuit.getSimulationSettings().getStartFrequency());
    double stopFrequency = parseNumeric(circuit.getSimulationSettings().getStopFrequency());

    if (circuit.getSimulationSettings().isTransient()) {
      spiceBuffer.append(".TRAN " + transientDuration / SIMULATION_STEPS + " " + transientDuration
          + " UIC\n");
    } else {
      spiceBuffer.append(".AC DEC "
          + (int) (SIMULATION_STEPS / (Math.log10(stopFrequency) - Math.log10(startFrequency)))
          + " " + startFrequency + " " + stopFrequency + "\n");
    }

    for (Map.Entry<String, String> entry : modelMap.entrySet()) {
      spiceBuffer.append(".model ");
      spiceBuffer.append(entry.getValue());
      spiceBuffer.append(" ");
      spiceBuffer.append(entry.getKey());
      spiceBuffer.append("\n");
    }

    spiceBuffer.append(".END\n");
    spiceBuffer.append("\n");

    return spiceBuffer.toString();
  }

}
