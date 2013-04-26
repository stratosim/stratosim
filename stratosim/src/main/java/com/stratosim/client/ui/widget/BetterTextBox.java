package com.stratosim.client.ui.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

// Should this just extend TextBox? I wasn't sure of the side effects
// of all the TextBox functions so I thought this is safer, but the boiler
// plate is growing rapidly. Also we may want to extend ValueBoxBase.

// WARNING: This class is susceptible to cross browser issues and is used
// everywhere.  Be very careful when modifying and avoid as much modification
// as possible.

/**
 * This class is for disallowing Unicode.
 * 
 * @author tarun
 * 
 */
// TODO(tpondich): Need a validating TextBox.
public class BetterTextBox extends Composite
    implements
      HasChangeHandlers,
      HasKeyUpHandlers,
      HasKeyDownHandlers,
      HasFocusHandlers,
      HasBlurHandlers,
      HasText,
      HasValue<String> {
  private static BetterTextBoxUiBinder uiBinder = GWT.create(BetterTextBoxUiBinder.class);

  @UiField
  TextBox textBox;
  
  private boolean doSelectAllOnClick = true;

  // We may want to extend this later to avoid other forms of input.
  // However, it's better UI to let the user enter it and provide feedback
  // on validity so they don't think it's broken. We will enforce printable
  // ASCII because Unicode breaks rendering of PS, and that will look bad.

  interface BetterTextBoxUiBinder extends UiBinder<Widget, BetterTextBox> {}

  public BetterTextBox() {
    initWidget(uiBinder.createAndBindUi(this));

    setStylePrimaryName("stratosim-BetterTextBox");
  }

  // TODO(tarun): I would much prefer a library to handle this.
  private static boolean isPrintable(char ch) {
    return ch >= 32 && ch < 127;
  }

  private static String validString(String in) {
    if (in == null) {
      return null;
    }

    String out = "";
    for (int i = 0; i < in.length(); i++) {
      if (isPrintable(in.charAt(i))) {
        out += in.charAt(i);
      }
    }
    return out;
  }

  private void scanTextBox() {
    String in = textBox.getText();
    if (in == null) {
      return;
    }

    int position = textBox.getCursorPos();
    String valid = validString(in);

    if (!in.equals(valid)) {
      textBox.setText(valid);
      if (position < valid.length()) textBox.setCursorPos(position);
    }
  }

  @UiHandler("textBox")
  void onChange(ChangeEvent event) {
    scanTextBox();
    fireChangeEvent();
  }
  
  @UiHandler("textBox")
  void onKeyUp(KeyUpEvent event) {
    scanTextBox();
    fireKeyUpEvent(event.isControlKeyDown(), event.isAltKeyDown(), event.isShiftKeyDown(),
        event.isMetaKeyDown(), event.getNativeKeyCode());
  }

  @UiHandler("textBox")
  void onBlur(BlurEvent event) {
    doSelectAllOnClick = true;

    fireBlurEvent();
  }

  @UiHandler("textBox")
  void onKeyDown(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      fireChangeEvent();
      textBox.setFocus(false);
    }
    fireKeyDownEvent(event.isControlKeyDown(), event.isAltKeyDown(), event.isShiftKeyDown(),
        event.isMetaKeyDown(), event.getNativeKeyCode());
  }

  @UiHandler("textBox")
  void onFocus(FocusEvent event) {
    fireFocusEvent();
  }
  
  @UiHandler("textBox")
  void onClick(ClickEvent event) {
    if (doSelectAllOnClick && textBox.getSelectionLength() == 0) {
      textBox.selectAll();
      doSelectAllOnClick = false;
    }
  }

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return super.addHandler(handler, ChangeEvent.getType());
  }

  private void fireChangeEvent() {
    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
    ValueChangeEvent.fire(this, textBox.getValue());
  }

  private void fireKeyUpEvent(boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey,
      int keyCode) {
    DomEvent.fireNativeEvent(
        Document.get().createKeyUpEvent(ctrlKey, altKey, shiftKey, metaKey, keyCode), this);
  }

  private void fireKeyDownEvent(boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey,
      int keyCode) {
    DomEvent.fireNativeEvent(
        Document.get().createKeyDownEvent(ctrlKey, altKey, shiftKey, metaKey, keyCode), this);
  }

  private void fireBlurEvent() {
    DomEvent.fireNativeEvent(Document.get().createBlurEvent(), this);
  }

  private void fireFocusEvent() {
    DomEvent.fireNativeEvent(Document.get().createFocusEvent(), this);
  }

  @Override
  public void setText(String text) {
    textBox.setText(text);
  }

  @Override
  public String getText() {
    return textBox.getText();
  }

  public void setEnabled(boolean enabled) {
    textBox.setEnabled(enabled);
  }

  public void setTitle(String title) {
    textBox.setTitle(title);
  }
  
  public void selectAll() {
    textBox.selectAll();
  }
  
  public void setSelectionRange(int pos, int length) {
    textBox.setSelectionRange(pos, length);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
    return super.addHandler(handler, ValueChangeEvent.getType());
  }

  @Override
  public String getValue() {
    return textBox.getValue();
  }

  @Override
  public void setValue(String value) {
    String validString = validString(value);
    if (!validString.equals(textBox.getValue())) {
      textBox.setValue(validString);
    }
  }

  @Override
  public void setValue(String value, boolean fireEvents) {
    String validString = validString(value);
    textBox.setValue(validString);
    if (fireEvents && !validString.equals(textBox.getValue())) {
      fireChangeEvent();
    }
  }

  @Override
  public HandlerRegistration addBlurHandler(BlurHandler handler) {
    return super.addHandler(handler, BlurEvent.getType());
  }

  @Override
  public HandlerRegistration addFocusHandler(FocusHandler handler) {
    return super.addHandler(handler, FocusEvent.getType());
  }

  @Override
  public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
    return super.addHandler(handler, KeyDownEvent.getType());
  }

  @Override
  public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
    return super.addHandler(handler, KeyUpEvent.getType());
  }
}
