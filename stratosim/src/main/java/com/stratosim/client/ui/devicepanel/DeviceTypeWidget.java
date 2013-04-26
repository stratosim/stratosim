package com.stratosim.client.ui.devicepanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Image;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.drawing.CanvasContext;
import com.stratosim.shared.drawing.DrawableContext;

public class DeviceTypeWidget {
  private Canvas canvas;

  private final static int TEXT_SIZE = 10;
  private final static int TEXT_MARGIN = 10;
  private final static int MARGIN = 0;

  public DeviceTypeWidget(DeviceType deviceType, int width, int height) {
    canvas = Canvas.createIfSupported();
    canvas.setWidth(width + "px");
    canvas.setHeight(height + "px");

    DrawableContext context = new CanvasContext(canvas.getContext2d());

    Device obj = deviceType.create(new Point(0, 0), 2, true, "");
    canvas.setCoordinateSpaceWidth(width);
    canvas.setCoordinateSpaceHeight(height);
    double scaleX = (double) (width - MARGIN * 2) / (obj.getWidth());
    double scaleY = (double) (height - TEXT_SIZE - TEXT_MARGIN - MARGIN * 2) / (obj.getHeight());
    double scale = Math.min(scaleX, scaleY);
    context.translate(width / 2, (height - TEXT_SIZE) / 2);
    context.scale(scale, scale);
    canvas.getContext2d().setLineWidth(2.0 / scale);
    obj.draw(context, false, false);
    obj.getLabel().draw(context, false, false, TEXT_SIZE / scale);
  }

  public Image getImage() {
    return new Image(canvas.toDataUrl("image/png"));
  }
}
