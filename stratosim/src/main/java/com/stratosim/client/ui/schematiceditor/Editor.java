package com.stratosim.client.ui.schematiceditor;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

abstract class Editor extends Composite implements HasValueChangeHandlers<EditorAction> {
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorAction> handler) {
    return super.addHandler(handler, ValueChangeEvent.getType());
  }

  protected void fireValueChangeEvent(EditorAction value) {
    ValueChangeEvent.fire(this, value);
  }

  // This hack lets buttons not be focusable, so that they don't steal keyboard
  // focus from the schematic panel.
  protected void rejectFocus() {
    ((SchematicPanel) this.getParent().getParent()).setFocus(true);
  }
}


interface EditorAction {

}
