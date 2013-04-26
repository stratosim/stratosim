package com.stratosim.client.ui.devicepanel;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class DevicePanelButton extends ToggleButton {

	private boolean isDown = false;
	
	public DevicePanelButton(String text) {
		super(text);
		init();
	}
	
	public DevicePanelButton(Image img) {
		super(img, img);
		init();
	}
	
	private void init() {
		super.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				superSetDown(true);
			}
		});
		
		super.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				// superSetDown(false);
				isDown = !isDown;
				setStyle();
			}
		});
		
		setStylePrimaryName("stratosim-DeviceList-Button");
		setStyle();
	}
	
	private void superSetDown(boolean down) {
		super.setDown(down);
	}
	
	@Override
	public boolean isDown() {
		return isDown;
	}

	@Override
	public void setDown(boolean down) {
		isDown = down;
		setStyle();
	}
	
	private void setStyle() {
		this.setStyleName("stratosim-DeviceList-Button-selected", isDown);
		this.setStyleName("stratosim-DeviceList-Button-unselected", !isDown);
	}
}
