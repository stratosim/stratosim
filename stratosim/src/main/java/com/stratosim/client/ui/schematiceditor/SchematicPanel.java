package com.stratosim.client.ui.schematiceditor;

import java.util.Stack;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireJunction;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.drawing.CanvasContext;
import com.stratosim.shared.drawing.DrawableContext;

public class SchematicPanel extends Composite implements RequiresResize {
  private AbsolutePanel innerContainer;

  private Circuit circuit;
  private Canvas canvas;
  private State currentState;

  private DrawableContext canvasContext;

  private Editor editor;

  // These defaults can be overridden in uiBinder
  private String crosshairColor = "rgba(0, 0, 0, 0.1)";
  private String gridColor = "#aaaaaa";

  // Current Mouse Position
  private Point mouseLocation = null;

  private Stack<Circuit> undoStack;
  private Stack<Circuit> redoStack;
  private Circuit previousSnapshot;

  public SchematicPanel() {
    canvas = Canvas.createIfSupported();
    canvas.setSize("100%", "100%");
    canvas.addMouseDownHandler(mouseDownHandler);
    canvas.addMouseUpHandler(mouseUpHandler);
    canvas.addMouseMoveHandler(mouseMoveHandler);
    canvas.addMouseOverHandler(mouseOverHandler);
    canvas.addMouseWheelHandler(mouseWheelHandler);
    canvas.addMouseOutHandler(mouseOutHandler);
    canvas.addKeyPressHandler(keyPressHandler);
    canvas.addKeyDownHandler(keyDownHandler);
    canvas.addBlurHandler(blurHandler);

    innerContainer = new AbsolutePanel();
    innerContainer.setSize("100%", "100%");
    innerContainer.add(canvas, 0, 0);

    initWidget(innerContainer);
    setStyleName("stratosim-SchematicPanel");

    canvasContext = new CanvasContext(canvas.getContext2d());

    undoStack = new Stack<Circuit>();
    redoStack = new Stack<Circuit>();

    SchematicPanelLinkFactory.set(new SchematicPanelLinkImpl());
    this.setCircuit(new Circuit());
  }

  public void setCrosshairColor(String cssColor) {
    crosshairColor = cssColor;
  }

  public void setGridColor(String cssColor) {
    gridColor = cssColor;
  }

  public Circuit getCircuit() {
    return circuit;
  }

  public void setCircuit(Circuit circuit) {
    undoStack.clear();
    redoStack.clear();

    setCircuitPreserveUndoHistory(circuit);
  }

  private void setCircuitPreserveUndoHistory(Circuit circuit) {
    if (editor != null) {
      innerContainer.remove(editor);
      editor = null;
    }
    this.circuit = circuit;
    this.previousSnapshot = circuit.snapshot();

    this.setCurrentState(new IdleState(circuit));

    paint();
  }

  /**
   * This must be called to maintain the undo/redo history when the wrapped circuit is modified.
   * Ideally only SchematicPanel should be modifying circuit. The name and the other stuff should
   * either be moved in to SchematicPanel or not be part of the undo state.
   */
  // TODO(tpondich): Fix this hack!
  public void onCircuitChange() {
    if (!circuit.equals(previousSnapshot)) {
      undoStack.push(previousSnapshot);
      previousSnapshot = circuit.snapshot();
      redoStack.clear();
    }

    paint();
  }

  private void setCurrentState(State newState) {
    if (currentState != newState) {
      if (editor != null) {
        editor.removeFromParent();
        editor = null;
      }
      currentState = newState;
      currentState.initEditor();
    }

    onCircuitChange();
  }

  private int snapToNearest(int v, int spacing) {
    int lower = v / spacing * spacing;
    int upper = lower + spacing;
    if (v - lower > spacing / 2) {
      return upper;
    } else {
      return lower;
    }
  }

  /**
   * Mouse location in circuit coordinates accounting for zoom, pan and snapping.
   */
  private Point getMouseLocation(MouseEvent<?> event) {
    double scale = circuit.getScale();
    Point pan = circuit.getPan();
    return new Point(snapToNearest(((int) (event.getX() / scale) - pan.getX()),
        SchematicPanelLink.CLICK_MARGIN * 2), snapToNearest(
        ((int) (event.getY() / scale) - pan.getY()), SchematicPanelLink.CLICK_MARGIN * 2));
  }

  private MouseDownHandler mouseDownHandler = new MouseDownHandler() {
    @Override
    public void onMouseDown(MouseDownEvent event) {
      mouseLocation = getMouseLocation(event);
      // Make sure the MouseMove was triggered.
      setCurrentState(currentState.goMouseMove(mouseLocation));
      setCurrentState(currentState.goMouseDown(mouseLocation));
    }
  };

  private MouseMoveHandler mouseMoveHandler = new MouseMoveHandler() {
    @Override
    public void onMouseMove(MouseMoveEvent event) {
      mouseLocation = getMouseLocation(event);
      setCurrentState(currentState.goMouseMove(mouseLocation));
      // Update location in new pan.
      mouseLocation = getMouseLocation(event);
    }
  };

  private MouseUpHandler mouseUpHandler = new MouseUpHandler() {
    @Override
    public void onMouseUp(MouseUpEvent event) {
      Point oldMouseLocation = mouseLocation;
      mouseLocation = getMouseLocation(event);
      if (!mouseLocation.equals(oldMouseLocation)) {
        // There was an unrecorded MouseMove, so we'll trigger it.
        setCurrentState(currentState.goMouseMove(mouseLocation));
      }
      mouseLocation = getMouseLocation(event);
      setCurrentState(currentState.goMouseUp(mouseLocation));
    }
  };

  private MouseOverHandler mouseOverHandler = new MouseOverHandler() {
    @Override
    public void onMouseOver(MouseOverEvent event) {
      mouseLocation = getMouseLocation(event);
      setCurrentState(currentState.goMouseMove(mouseLocation));
    }
  };

  private MouseOutHandler mouseOutHandler = new MouseOutHandler() {
    public void onMouseOut(MouseOutEvent event) {
      mouseLocation = null;
      setCurrentState(currentState.goMouseOut());
    }
  };

  private MouseWheelHandler mouseWheelHandler = new MouseWheelHandler() {
    @Override
    public void onMouseWheel(MouseWheelEvent event) {
      mouseLocation = getMouseLocation(event);
      setCurrentState(currentState.goMouseWheel(mouseLocation, event.isNorth()));
      // Update location in new scale.
      mouseLocation = getMouseLocation(event);
    }
  };

  private KeyPressHandler keyPressHandler = new KeyPressHandler() {
    @Override
    public void onKeyPress(KeyPressEvent event) {
      // TODO(tpondich): Will need to clean up and implement browser specific hacks for keys.
      // MainView should deal with ctrl key bindings.
      if (event.isControlKeyDown()) {
        switch (event.getCharCode()) {

          case 'z':
          case 26: /* chrome bug */
            undo();
            event.stopPropagation();
            event.preventDefault();
            break;

          case 'y':
          case 25: /* chrome bug */
            redo();
            event.stopPropagation();
            event.preventDefault();
            break;

          default:
            // Throw the event to MainView.
        }
      } else {
        switch (event.getCharCode()) {

          case 'r':
            setCurrentState(currentState.goPressRotate());
            event.stopPropagation();
            event.preventDefault();
            break;

          default:
            // Throw the event to MainView.
        }
      }
    }
  };

  private KeyDownHandler keyDownHandler = new KeyDownHandler() {
    @Override
    public void onKeyDown(KeyDownEvent event) {
      switch (event.getNativeKeyCode()) {

        case KeyCodes.KEY_ESCAPE:
          setCurrentState(currentState.goPressEscape());
          break;

        case KeyCodes.KEY_DELETE:
        case KeyCodes.KEY_BACKSPACE:
          setCurrentState(currentState.goPressDelete());
          break;
      }
    }
  };

  private BlurHandler blurHandler = new BlurHandler() {
    @Override
    public void onBlur(BlurEvent event) {
      setCurrentState(currentState.goBlur());
    }
  };

  private void paint() {
    // TODO(tpondich): Figure out how browsers buffer canvas
    // This might effectively repaint 3 times, which is bad.
    // We really only want to do this on resize, but I can't figure out
    // How to get the dimensions on init cleanly without passing them in.
    canvas.setCoordinateSpaceWidth(this.getOffsetWidth());
    canvas.setCoordinateSpaceHeight(this.getOffsetHeight());

    Context2d context = canvas.getContext2d();
    context.save();

    context.clearRect(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    double scale = circuit.getScale();
    Point pan = circuit.getPan();
    context.scale(scale, scale);
    context.translate(pan.getX(), pan.getY());

    context.setLineCap(LineCap.ROUND);

    context.setLineWidth(2.0 / scale);

    if (GWT.isProdMode()) {
      // The dots take way to long in hosted mode.
      CoordinateConvertHelper coordinateConvertHelper = new CoordinateConvertHelper(circuit);
      drawGrid(context, coordinateConvertHelper.pixelCoordToCircuitCoord(new Point(0, 0)),
          coordinateConvertHelper.pixelCoordToCircuitCoord(new Point(canvas.getOffsetWidth(),
              canvas.getOffsetHeight())));
    }

    for (Device c : getCircuit().getDevices()) {
      currentState.drawDrawableObject(canvasContext, c);
      currentState.drawDrawableObject(canvasContext, c.getLabel());
    }

    for (Wire w : getCircuit().getWires()) {
      currentState.drawDrawableObject(canvasContext, w);
    }

    for (WireJunction junction : getCircuit().getWireJunctions()) {
      currentState.drawDrawableObject(canvasContext, junction);
    }

    // Can't combine this loop with previous device one because
    // ports should be drawn on top of devices and wires.
    for (Device c : getCircuit().getDevices()) {
      for (Port p : c.getPorts()) {
        currentState.drawDrawableObject(canvasContext, p);
      }
    }

    currentState.drawShadows(canvasContext);

    context.restore();

    // TODO(josh): conditional so it doesn't draw on devices, etc.
    // TODO(tpondich): We should move this drawing into the state machine.
    if (!(currentState instanceof LockedState)) {
      drawCrosshair(mouseLocation, context);
    }

    DOM.setStyleAttribute(innerContainer.getElement(), "cursor", currentState.getCursor()
        .toString());

    if (!GWT.isProdMode()) {
      drawDebuggingInfo(context);
    }
  }

  private void drawDebuggingInfo(Context2d context) {
    String fileKey = "circuitnull";
    String versionKey = "circuitnull";
    String fileRole = "circuitnull";

    if (circuit != null) {
      fileKey = "null";
      versionKey = "null";
      fileRole = "null";

      if (circuit.getFileKey() != null) {
        fileKey = circuit.getFileKey().toString();
      }
      if (circuit.getVersionKey() != null) {
        versionKey = circuit.getVersionKey().toString();
      }
      if (circuit.getFileRole() != null) {
        fileRole = circuit.getFileRole().toString();
      }
    }

    context.fillText(fileKey, this.getOffsetWidth() - 300, this.getOffsetHeight() / 2);
    context.fillText(versionKey, this.getOffsetWidth() - 300, this.getOffsetHeight() / 2 + 20);
    context.fillText(fileRole, this.getOffsetWidth() - 300, this.getOffsetHeight() / 2 + 40);
  }

  private void drawCrosshair(Point mouseLocation, Context2d context) {
    if (mouseLocation == null) {
      return;
    }

    CoordinateConvertHelper coordinateConvertHelper = new CoordinateConvertHelper(circuit);

    int x = coordinateConvertHelper.circuitCoordToPixelCoord(mouseLocation).getX();
    int y = coordinateConvertHelper.circuitCoordToPixelCoord(mouseLocation).getY();

    context.save();
    context.setStrokeStyle(crosshairColor);
    context.beginPath();
    context.moveTo(0, y);
    context.lineTo(this.getOffsetWidth() * 2, y);
    context.stroke();
    context.beginPath();
    context.moveTo(x, 0);
    context.lineTo(x, this.getOffsetHeight() * 2);
    context.stroke();
    context.restore();
  }

  private void drawGrid(Context2d context, Point topLeft, Point bottomRight) {
    context.save();
    context.setFillStyle(gridColor);

    // TODO(tpondich): encapsulate in snapping functions.
    int step = SchematicPanelLink.CLICK_MARGIN * 4;
    int xmin = snapToNearest(topLeft.getX(), step) - step;
    int xmax = snapToNearest(bottomRight.getX(), step) + step;
    int ymin = snapToNearest(topLeft.getY(), step) - step;
    int ymax = snapToNearest(bottomRight.getY(), step) + step;

    for (int x = xmin; x < xmax; x += step) {
      for (int y = ymin; y < ymax; y += step) {
        context.beginPath();
        context.arc(x, y, context.getLineWidth() / 2, 0, Math.PI * 2);
        context.fill();
      }
    }
    context.restore();
  }

  private class SchematicPanelLinkImpl implements SchematicPanelLink {
    @Override
    public void showEditorCenter(Editor e, Point position) {
      assert (editor == null) : "Multiple Editors";
      editor = e;
      CoordinateConvertHelper coordinateConvertHelper = new CoordinateConvertHelper(circuit);
      Point pixel = coordinateConvertHelper.circuitCoordToPixelCoord(position);
      innerContainer.add(editor, pixel.getX(), pixel.getY());
      innerContainer.setWidgetPosition(editor,
          innerContainer.getWidgetLeft(editor) - editor.getOffsetWidth() / 2,
          innerContainer.getWidgetTop(editor) - editor.getOffsetHeight() / 2);
      editor.addValueChangeHandler(editorHandler);
    }

    @Override
    public void showEditorBottomRight(Editor e, Point position) {
      assert (editor == null) : "Multiple Editors";
      editor = e;
      CoordinateConvertHelper coordinateConvertHelper = new CoordinateConvertHelper(circuit);
      Point pixel = coordinateConvertHelper.circuitCoordToPixelCoord(position);
      innerContainer.add(editor, pixel.getX(), pixel.getY());
      editor.addValueChangeHandler(editorHandler);
    }
  }

  private ValueChangeHandler<EditorAction> editorHandler = new ValueChangeHandler<EditorAction>() {
    @Override
    public void onValueChange(ValueChangeEvent<EditorAction> event) {
      setCurrentState(currentState.goEditorAction((EditorAction) event.getValue()));
    }
  };

  @Override
  public void onResize() {
    paint();
  }

  public void setDeviceType(DeviceType deviceType) {
    setCurrentState(currentState.goSelectDeviceType(deviceType));
    canvas.setFocus(true);
  }

  public void setEditable(boolean editable) {
    setCurrentState(currentState.goSetEditable(editable));
  }

  public boolean hasUndoAvailable() {
    return !undoStack.isEmpty();
  }

  public boolean hasRedoAvailable() {
    return !redoStack.isEmpty();
  }

  public void undo() {
    if (!hasUndoAvailable()) {
      return;
    }

    // It is sufficient to snapshot when adding to the stack.
    // Here we do not create any new references to the circuit.
    redoStack.push(circuit);
    setCircuitPreserveUndoHistory(undoStack.pop());
  }

  public void redo() {
    if (!hasRedoAvailable()) {
      return;
    }

    // It is sufficient to snapshot when adding to the stack.
    // Here we do not create any new references to the circuit.
    undoStack.push(circuit);
    setCircuitPreserveUndoHistory(redoStack.pop());
  }

  public void zoomIn() {
    setCurrentState(currentState.goZoomIn(getOffsetWidth(), getOffsetHeight()));
  }

  public void zoomOut() {
    setCurrentState(currentState.goZoomOut(getOffsetWidth(), getOffsetHeight()));
  }

  public void zoomFit(int padLeft, int padRight, int padTop, int padBottom) {
    setCurrentState(currentState.goZoomFit(getOffsetWidth(), getOffsetHeight(), padLeft, padRight,
        padTop, padBottom));
  }

  public void setFocus(boolean focused) {
    canvas.setFocus(focused);
  }

}
