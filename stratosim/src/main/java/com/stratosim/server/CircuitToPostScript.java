package com.stratosim.server;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.drawing.CircuitObjectDrawer;
import com.stratosim.shared.drawing.PostScriptContext;

public class CircuitToPostScript {

  private CircuitToPostScript() {
    throw new UnsupportedOperationException("Not instantiable");
  }

  public static String convert(Circuit circuit) {
    StringBuffer ps = new StringBuffer();
    PostScriptContext psContext =
        new PostScriptContext(ps, circuit.getTopLeft(), circuit.getBottomRight());
    CircuitObjectDrawer.render(psContext, circuit);
    
    // TODO(tpondich): Why didn't we put this in ps context?
    ps.append("showpage\n");

    return ps.toString();
  }

}
