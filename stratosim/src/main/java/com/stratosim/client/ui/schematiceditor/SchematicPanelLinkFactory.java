package com.stratosim.client.ui.schematiceditor;

class SchematicPanelLinkFactory {

  private static SchematicPanelLink link = null;

  private SchematicPanelLinkFactory() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  public static void set(SchematicPanelLink newLink) {
    link = newLink;
  }

  public static SchematicPanelLink create() {
    return link;
  }

}
