package com.stratosim.client.ui.widget;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

// TODO(tpondich): Use value change handlers

public class GridList<T> extends ResizeComposite implements HasChangeHandlers, HasBlurHandlers {
  private static GridListUiBinder uiBinder = GWT.create(GridListUiBinder.class);

  interface GridListUiBinder extends UiBinder<Widget, GridList<?>> {}

  @UiField
  ScrollPanel scroll;
  @UiField
  FocusPanel focus;
  @UiField
  Grid grid;

  private List<ToggleButton> buttons;
  private List<Label> labels;
  private List<T> values;

  public GridList() {
    create(1);
  }

  public GridList(int columns) {
    create(columns);
  }

  public void create(int columns) {
    buttons = Lists.newArrayList();
    values = Lists.newArrayList();
    labels = Lists.newArrayList();

    initWidget(uiBinder.createAndBindUi(this));

    setStylePrimaryName("stratosim-GridList");

    // Any additional initialization based on styleName.
    setColumns(columns);
  }

  @UiHandler("focus")
  void onBlur(BlurEvent event) {
    fireBlurEvent();
  }

  public void setColumns(int columns) {
    // TODO(tpondich): Test that this will actually work after initialization.
    grid.removeStyleName("stratosim-GridList-" + grid.getColumnCount());
    
    grid.clear();
    grid.resize(1, columns);

    grid.addStyleName("stratosim-GridList-" + columns);
    
    for (int i = 0; i < values.size(); i++) {
      replace(buttons.get(i), labels.get(i), values.get(i), i);
    }
  }

  public void setStylePrimaryName(String styleName) {
    String oldStyleName = getStylePrimaryName();
    super.setStylePrimaryName(styleName);

    grid.removeStyleName(oldStyleName + "-InnerPanel");
    grid.addStyleName(getStylePrimaryName() + "-InnerPanel");

    for (Widget button : buttons) {
      button.removeStyleName(oldStyleName + "-Button");
      button.addStyleName(getStylePrimaryName() + "-Button");
    }

    for (Widget label : labels) {
      label.removeStyleName(oldStyleName + "-Caption");
      label.addStyleName(getStylePrimaryName() + "-Caption");
    }
  }

  public void setFocus(boolean isFocused) {
    focus.setFocus(isFocused);
  }

  private ToggleButton createImageToggleButton(Image up, Image down, String title) {
    ToggleButton button = new ToggleButton(up, down);
    button.setTitle(title);
    button.addStyleName(getStylePrimaryName() + "-Button");
    return button;
  }

  public void add(Image up, Image down, String caption, String title, T value) {
    ToggleButton button = createImageToggleButton(up, down, title);
    add(button, caption, value);
  }

  public void add(Image up, Image down, String title, T value) {
    ToggleButton button = createImageToggleButton(up, down, title);
    add(button, null, value);
  }

  public void replace(Image up, Image down, String caption, String title, T value, int index) {
    ToggleButton button = createImageToggleButton(up, down, title);
    button.setDown(buttons.get(index).isDown());
    Label label = new Label(caption);
    label.addStyleName(getStylePrimaryName() + "-Caption");
    replace(button, label, value, index);
  }

  public void replace(Image up, Image down, String title, T value, int index) {
    ToggleButton button = createImageToggleButton(up, down, title);
    button.setDown(buttons.get(index).isDown());
    replace(button, null, value, index);
  }

  public void add(String string, T value) {
    ToggleButton button = new ToggleButton(string);
    button.addStyleName(getStylePrimaryName() + "-Button");
    add(button, null, value);
  }
  
  public void add(String string, String caption, T value) {
    ToggleButton button = new ToggleButton(string);
    button.addStyleName(getStylePrimaryName() + "-Button");
    add(button, caption, value);
  }

  private void add(ToggleButton button, String caption, T value) {
    Label label = new Label(caption);
    label.addStyleName(getStylePrimaryName() + "-Caption");
    replace(button, label, value, buttons.size());
  }

  private void replace(ToggleButton button, Label label, T value, int index) {
    // TODO(tpondich): Check bounds on index
    // Make sure this doesn't allocate too often...

    button.addValueChangeHandler(deselectSiblings);
    int requiredRows = index / grid.getColumnCount() * 2 + 2;
    if (grid.getRowCount() < requiredRows) {
      grid.resizeRows(requiredRows);
    }
    grid.setWidget(index / grid.getColumnCount() * 2, index % grid.getColumnCount(), button);

    if (label != null) {
      grid.setWidget(index / grid.getColumnCount() * 2 + 1, index % grid.getColumnCount(), label);
    }

    if (index >= buttons.size()) {
      if (index == 0) {
        button.setDown(true);
      }
      buttons.add(button);
      labels.add(label);
      values.add(value);
    } else {
      buttons.set(index, button);
      labels.set(index, label);
      values.set(index, value);
    }
  }

  private void setDownButton(ToggleButton source, boolean fireEvents) {
    HasWidgets parent = (HasWidgets) source.getParent();
    for (Widget child : parent) {
      if (child instanceof ToggleButton && child != source) {
        ((ToggleButton) child).setDown(false);
      }
    }
    source.setDown(true);
    
    if (fireEvents) {
      fireChangeEvent();
    }
  }
  
  private ValueChangeHandler<Boolean> deselectSiblings = new ValueChangeHandler<Boolean>() {
    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
      ToggleButton source = (ToggleButton) event.getSource();
      setDownButton(source, true);
    }
  };

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return super.addHandler(handler, ChangeEvent.getType());
  }

  private void fireChangeEvent() {
    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
  }

  @Override
  public HandlerRegistration addBlurHandler(BlurHandler handler) {
    return super.addHandler(handler, BlurEvent.getType());
  }

  private void fireBlurEvent() {
    DomEvent.fireNativeEvent(Document.get().createBlurEvent(), this);
  }

  public String getSelectedText() {
    return buttons.get(getSelectedIndex()).getText();
  }

  public T getSelectedItem() {
    return values.get(getSelectedIndex());
  }


  public void setSelected(T item) {
    setSelected(item, false);
  }
  
  public void setSelected(T item, boolean fireEvents) {
    setSelectedIndex(values.indexOf(item), fireEvents);
  }

  public int getSelectedIndex() {
    int i = 0;
    for (ToggleButton button : buttons) {
      if (button.isDown()) {
        return i;
      }
      i++;
    }

    return -1;
  }
  
  public void setSelectedIndex(int i) {
    setSelectedIndex(i, false);
  }

  public void setSelectedIndex(int i, boolean fireEvents) {
    Preconditions.checkArgument(i >= 0, "Index out of range: " + i);
    Preconditions.checkArgument(i < buttons.size(), "Index out of range: " + i);
    
    boolean doFire = fireEvents && i != getSelectedIndex();
    setDownButton(buttons.get(i), doFire);
  }

  public int getRowCount() {
    return grid.getRowCount();
  }

  public int getColumnCount() {
    return grid.getColumnCount();
  }

  public void clear() {
    int columns = grid.getColumnCount();
    
    grid.clear();
    buttons.clear();
    labels.clear();
    values.clear();

    setColumns(columns);
  }

  public int getItemCount() {
    return values.size();
  }
}
