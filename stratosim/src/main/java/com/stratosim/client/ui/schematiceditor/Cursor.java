package com.stratosim.client.ui.schematiceditor;

enum Cursor {
  // The toString of these fields must correspond to css cursors.
  
  ARROW {
    public String toString() {
      return "default";
    }
  },
  POINTER {
    public String toString() {
      return "pointer";
    }
  },
  CROSSHAIR {
    public String toString() {
      return "crosshair";
    }
  },
  MOVE {
    public String toString() {
      return "move";
    }
  },
}
