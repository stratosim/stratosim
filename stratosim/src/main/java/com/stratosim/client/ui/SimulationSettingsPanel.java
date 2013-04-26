package com.stratosim.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.stratosim.client.ui.widget.BetterTextBox;
import com.stratosim.shared.circuitmodel.SimulationSettings;
import com.stratosim.shared.validator.RegExValidator;
import com.stratosim.shared.validator.StringValidator;

public class SimulationSettingsPanel extends Composite implements HasChangeHandlers {
  @UiField
  BetterTextBox transientDuration;
  @UiField
  BetterTextBox startFrequency;
  @UiField
  BetterTextBox stopFrequency;
  @UiField
  TabLayoutPanel simulationType;

  // TODO(tpondich): Duplicated in validator, partially in device manager instance.
  private static String DECIMAL = "[0-9]+(?:\\.[0-9]+)?";
  private static String NUMERIC_VALUE_BASE = DECIMAL + "(?:e-?" + DECIMAL + ")?(?:m|u|n|p|f|k|meg|g|t)?";

  private static StringValidator frequencyValidator = new RegExValidator("^" + NUMERIC_VALUE_BASE + "Hz" + "$");
  private static StringValidator timeValidator = new RegExValidator("^" + NUMERIC_VALUE_BASE + "s" + "$");

  private static SimulationSettingsPanelUiBinder uiBinder = GWT
      .create(SimulationSettingsPanelUiBinder.class);

  interface SimulationSettingsPanelUiBinder extends UiBinder<Widget, SimulationSettingsPanel> {}

  public SimulationSettingsPanel() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  // TODO(tpondich): These styles piggy back off of the parameter styles in MainView.
  //                 Should be localized.
  private void setStyleOfValue(BetterTextBox value, StringValidator validator) {
    if (validator.isValid(value.getValue())) {
      value.setStyleDependentName("invalid", false);
      value.setStyleDependentName("valid", true);
    } else {
      value.setStyleDependentName("valid", false);
      value.setStyleDependentName("invalid", true);
    }
  }
  
  @UiHandler("transientDuration")
  public void onTrasientDurationKeyUp(KeyUpEvent event) {
    setStyleOfValue(transientDuration, timeValidator);
    fireChangeEvent();
  }
  
  @UiHandler("transientDuration")
  public void onTrasientDurationChange(ChangeEvent event) {
    // Can't set style because the order of events is blur, change and
    // then it won't ever defocus.
    fireChangeEvent();
  }
  
  @UiHandler("transientDuration")
  public void onTrasientDurationFocus(FocusEvent event) {
    setStyleOfValue(transientDuration, timeValidator);
  }
  
  @UiHandler("transientDuration")
  public void onTrasientDurationBlur(BlurEvent event) {
    transientDuration.setStyleDependentName("valid", false);
    transientDuration.setStyleDependentName("invalid", false);
  }

  @UiHandler("startFrequency")
  public void onStartFrequencyKeyUp(KeyUpEvent event) {
    setStyleOfValue(startFrequency, frequencyValidator);
    fireChangeEvent();
  }
  
  @UiHandler("startFrequency")
  public void onStartFrequencyChange(ChangeEvent event) {
    // Can't set style because the order of events is blur, change and
    // then it won't ever defocus.
    fireChangeEvent();
  }
  
  @UiHandler("startFrequency")
  public void onStartFrequencyFocus(FocusEvent event) {
    setStyleOfValue(startFrequency, frequencyValidator);
  }
  
  @UiHandler("startFrequency")
  public void onStartFrequencyBlur(BlurEvent event) {
    startFrequency.setStyleDependentName("valid", false);
    startFrequency.setStyleDependentName("invalid", false);
  }
  
  @UiHandler("stopFrequency")
  public void onStopFrequencyKeyUp(KeyUpEvent event) {
    setStyleOfValue(stopFrequency, frequencyValidator);
    fireChangeEvent();
  }
  
  @UiHandler("stopFrequency")
  public void onStopFrequencyChange(ChangeEvent event) {
    // Can't set style because the order of events is blur, change and
    // then it won't ever defocus.
    fireChangeEvent();
  }
  
  @UiHandler("stopFrequency")
  public void onStopFrequencyFocus(FocusEvent event) {
    setStyleOfValue(stopFrequency, frequencyValidator);
  }
  
  @UiHandler("stopFrequency")
  public void onStopFrequencyBlur(BlurEvent event) {
    stopFrequency.setStyleDependentName("valid", false);
    stopFrequency.setStyleDependentName("invalid", false);
  }
  
  @UiHandler("simulationType")
  public void onSimulationTypeChange(SelectionEvent<Integer> event) {
    fireChangeEvent();
  }

  public void setValues(SimulationSettings settings) {
    transientDuration.setValue(settings.getTransientDuration());
    startFrequency.setValue(settings.getStartFrequency());
    stopFrequency.setValue(settings.getStopFrequency());
    // TODO(tpondich): There has to be a better way to do this.
    simulationType.selectTab(settings.isTransient() ? 0 : 1);
  }

  public SimulationSettings getValues() {
    return new SimulationSettings(simulationType.getSelectedIndex() == 0 ? true : false,
        transientDuration.getValue(), startFrequency.getValue(), stopFrequency.getValue());
  }

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return super.addHandler(handler, ChangeEvent.getType());
  }

  private void fireChangeEvent() {
    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
  }
}
