package com.stratosim.client.ui.schematiceditor;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.stratosim.client.ui.widget.BetterTextBox;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Parameter;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.WireId;

class ParameterEditor extends Editor {
  private final Grid grid;
  // TODO(tpondich): Should operate on parameters not on the shadow.
  private Device shadow;

  class ParameterEditorCloseAction implements EditorAction {
    ParameterEditorCloseAction() {
    }
  }
  
  class ParameterEditorAction implements EditorAction {
    private String parameterName;
    private String newValue;

    ParameterEditorAction(String parameterName, String newValue) {
      this.parameterName = parameterName;
      this.newValue = newValue;
    }

    public String getParameterName() {
      return parameterName;
    }

    public String getValue() {
      return newValue;
    }
  }

  public ParameterEditor(Device selected, boolean isEditable) {
    // TODO(tpondich): Yea... seriously we need a clonery thing.
    shadow =
        selected
            .getType()
            .getCustom()
            .create(selected.getLocation(), selected.getRotation(), selected.isMirrored(),
                selected.getParameter("Name").getValue());
    shadow.setParameters(selected.getParameters());
    for (Port port : selected.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        shadow.getPort(port.getName()).addWire(wireId);
      }
    }

    grid = new Grid(shadow.getParameters().size(), 2);
    int row = 0;
    for (Parameter parameter : shadow.getParameters()) {
      Label name = new Label(parameter.getType().getName() + ":");
      BetterTextBox value = new BetterTextBox();
      value.addFocusHandler(valueFocusHandler);
      value.addBlurHandler(valueBlurHandler);
      value.addKeyUpHandler(valueKeyUpHandler);
      value.setText(parameter.getValue());
      value.setTitle(parameter.getType().getName());
      value.setEnabled(isEditable);
      grid.setWidget(row, 0, name);
      grid.setWidget(row, 1, value);
      row++;
    }
    initWidget(grid);

    setStyleName("stratosim-Editor");
  }

  private void setStyleOfValue(BetterTextBox value) {
    Parameter parameter = shadow.getParameter(value.getTitle());
    // TODO(tpondich): Might as well just have a Parameter::validate
    // method now
    // that invokes the validator on itself.
    if (parameter.getType().getValidator().isValid(parameter)) {
      value.setStyleDependentName("invalid", false);
      value.setStyleDependentName("valid", true);
    } else {
      value.setStyleDependentName("valid", false);
      value.setStyleDependentName("invalid", true);
    }
  }

  private FocusHandler valueFocusHandler = new FocusHandler() {
    @Override
    public void onFocus(FocusEvent event) {
      BetterTextBox value = (BetterTextBox) event.getSource();
      setStyleOfValue(value);
    }
  };

  private KeyUpHandler valueKeyUpHandler = new KeyUpHandler() {
    @Override
    public void onKeyUp(KeyUpEvent event) {
      if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        fireValueChangeEvent(new ParameterEditorCloseAction());
        return;
      }
      BetterTextBox value = (BetterTextBox) event.getSource();
      Parameter parameter = shadow.getParameter(value.getTitle());
      parameter.setValue(value.getValue());
      shadow.setParameter(parameter.getType().getName(), parameter.getValue());
      setStyleOfValue(value);

      fireValueChangeEvent(new ParameterEditorAction(value.getTitle(), shadow.getParameter(
          value.getTitle()).getValue()));
    }
  };

  private BlurHandler valueBlurHandler = new BlurHandler() {
    @Override
    public void onBlur(BlurEvent event) {
      BetterTextBox value = (BetterTextBox) event.getSource();
      value.setStyleDependentName("valid", false);
      value.setStyleDependentName("invalid", false);
    }
  };
}
