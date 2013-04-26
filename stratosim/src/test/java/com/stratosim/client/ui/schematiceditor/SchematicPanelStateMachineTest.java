package com.stratosim.client.ui.schematiceditor;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stratosim.client.ui.schematiceditor.DeviceEditor;
import com.stratosim.client.ui.schematiceditor.Editor;
import com.stratosim.client.ui.schematiceditor.IdleState;
import com.stratosim.client.ui.schematiceditor.SchematicPanelLink;
import com.stratosim.client.ui.schematiceditor.SchematicPanelLinkFactory;
import com.stratosim.client.ui.schematiceditor.State;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.circuitmodel.WireJunction;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.devicemodel.DeviceTypeBuilder;
import com.stratosim.shared.drawing.PostScriptContext;
import com.stratosim.shared.validator.ParameterValueValidator;
import com.stratosim.shared.validator.RegExValidator;

/**
 * Test the SchematicPanel testState Machine.
 * 
 * Untested Assumptions (guaranteed by SchematicPanel): MouseMove always occurs before MouseDown.
 * MouseUp always occurs at the same location as MouseDown unless a MouseMove occurs.
 */
public class SchematicPanelStateMachineTest extends TestCase {
  boolean writePostScript = false;

  private String testName;

  private Circuit referenceCircuit;

  private State testState;
  private Circuit testCircuit;

  private static final int CLICK_MARGIN = 10;

  private static DeviceType stubTwoPortDeviceTypeCustom(String type, int x1, int y1, int x2,
      int y2, int w, int h) {
    assert (x1 % CLICK_MARGIN == 0);
    assert (y1 % CLICK_MARGIN == 0);
    assert (x2 % CLICK_MARGIN == 0);
    assert (y2 % CLICK_MARGIN == 0);

    DeviceTypeBuilder deviceBuilder =
        new DeviceTypeBuilder().type(type).prefix("DEVICE").port("0", x1, y1).port("1", x2, y2)
            .parameter("PARAMETER", new ParameterValueValidator(new RegExValidator(""))).spiceTemplate("").labelTemplate("")
            .draw("").width(w).height(h);
    return deviceBuilder.build();
  }

  @Before
  public void setUp() {
    SchematicPanelLinkFactory.set(new SchematicPanelLink() {
      @Override
      public void showEditorCenter(Editor editor, Point position) {}

      @Override
      public void showEditorBottomRight(Editor editor, Point position) {}
    });

    testCircuit = new Circuit();
    testState = new IdleState(testCircuit);
    referenceCircuit = new Circuit();

    testName = "Need to assign variable testName!";
  }

  @After
  public void tearDown() {
    if (writePostScript) {
      writePs();
    }
  }

  private void writePs() {
    StringBuffer ps = new StringBuffer();
    PostScriptContext psContext =
        new PostScriptContext(ps, testCircuit.getTopLeft(), testCircuit.getBottomRight());

    // TODO(tpondich): Flagging repeated code.
    for (Device device : testCircuit.getDevices()) {
      device.draw(psContext, false, false);
      device.getLabel().draw(psContext, false, false);
    }

    for (Wire wire : testCircuit.getWires()) {
      wire.draw(psContext, false, false);
    }

    for (WireJunction junction : testCircuit.getWireJunctions()) {
      junction.draw(psContext, false, false);
    }

    // can't combine this loop with previous device one because
    // ports should be drawn on top of devices and wires
    for (Device device : testCircuit.getDevices()) {
      for (Port port : device.getPorts()) {
        port.draw(psContext, false, false);
      }
    }

    // TODO(tpondich): Out of place here.
    ps.append("showpage\n");

    System.out.println("FINAL POSTSCRIPT FOR: " + testName);
    System.out.println("***********************************");
    System.out.println(ps.toString());
    System.out.println("***********************************");
  }

  /**
   * Syntactic simplicity for instantiating points.
   * 
   * @param x
   * @param y
   * @return
   */
  private static Point P(int x, int y) {
    assert (x % CLICK_MARGIN == 0);
    assert (y % CLICK_MARGIN == 0);

    return new Point(x, y);
  }

  private void placeSingleDevice(DeviceType deviceType, Point location) {
    placeSingleDevice(deviceType, location, 0);
  }

  private void placeSingleDevice(DeviceType deviceType, Point location, int rotation) {
    testState = testState.goSelectDeviceType(deviceType);
    assertEquals("PlacingDeviceState", testState.toString());
    testState = testState.goMouseMove(location);
    assertEquals("PlacingDeviceState", testState.toString());
    for (int i = 0; i < rotation; i++) {
      testState = testState.goPressRotate();
      assertEquals("PlacingDeviceState", testState.toString());
    }
    testState = testState.goMouseDown(location);
    assertEquals("PanningState", testState.toString());
    testState = testState.goMouseUp(location);
    assertEquals("PlacingDeviceState", testState.toString());
    testState = testState.goPressEscape();
    assertEquals("IdleState", testState.toString());
  }

  private void placeDirectWire(Point location1, Point location2) {
    testState = testState.goMouseMove(location1);
    assertEquals("IdleState", testState.toString());
    testState = testState.goMouseDown(location1);
    assertEquals("MousingCircuitObjectState", testState.toString());
    testState = testState.goMouseUp(location1);
    assertEquals("PlacingWireFromPortState", testState.toString());
    testState = testState.goMouseMove(location2);
    assertEquals("PlacingWireFromPortState", testState.toString());
    testState = testState.goMouseDown(location2);
    assertEquals("PanningState", testState.toString());
    testState = testState.goMouseUp(location2);
    assertEquals("SelectedWireState", testState.toString());
    testState = testState.goPressEscape();
    assertEquals("IdleState", testState.toString());
  }

  private void placeWire(Point... locations) {
    assert (locations.length > 2);

    testState = testState.goMouseMove(locations[0]);
    assertEquals("IdleState", testState.toString());
    testState = testState.goMouseDown(locations[0]);
    assertEquals("MousingCircuitObjectState", testState.toString());
    testState = testState.goMouseUp(locations[0]);
    assertEquals("PlacingWireFromPortState", testState.toString());

    for (int i = 1; i < locations.length - 1; i++) {
      testState = testState.goMouseMove(locations[i]);
      assertEquals("PlacingWireFromPortState", testState.toString());
      testState = testState.goMouseDown(locations[i]);
      assertEquals("PanningState", testState.toString());
      testState = testState.goMouseUp(locations[i]);
      assertEquals("PlacingWireFromPortState", testState.toString());
    }

    testState = testState.goMouseMove(locations[locations.length - 1]);
    assertEquals("PlacingWireFromPortState", testState.toString());
    testState = testState.goMouseDown(locations[locations.length - 1]);
    assertEquals("PanningState", testState.toString());
    testState = testState.goMouseUp(locations[locations.length - 1]);
    assertEquals("SelectedWireState", testState.toString());

    testState = testState.goPressEscape();
    assertEquals("IdleState", testState.toString());
  }

  private void drag(String expectedtestStateOnUp, String expectedtestStateOnMove,
      Point... locations) {
    assert (locations.length > 2);

    testState = testState.goMouseMove(locations[0]);
    assertEquals("IdleState", testState.toString());
    testState = testState.goMouseDown(locations[0]);
    assertEquals("MousingCircuitObjectState", testState.toString());

    for (int i = 1; i < locations.length; i++) {
      testState = testState.goMouseMove(locations[i]);
      assertEquals(expectedtestStateOnMove, testState.toString());
    }

    testState = testState.goMouseUp(locations[locations.length - 1]);
    assertEquals(expectedtestStateOnUp, testState.toString());
    testState = testState.goPressEscape();
    assertEquals("IdleState", testState.toString());
  }

  private void dragDevice(Point... locations) {
    drag("SelectedDeviceState", "DraggingDeviceState", locations);
  }

  private void dragWire(Point... locations) {
    drag("SelectedWireState", "DraggingWireState", locations);
  }

  private void delete(String expectedtestStateOnUp, Point location) {
    testState = testState.goMouseMove(location);
    assertEquals("IdleState", testState.toString());
    testState = testState.goMouseDown(location);
    assertEquals("MousingCircuitObjectState", testState.toString());
    testState = testState.goMouseUp(location);
    assertEquals(expectedtestStateOnUp, testState.toString());
    testState = testState.goPressDelete();
    assertEquals("IdleState", testState.toString());
  }

  private void deleteDevice(Point location) {
    delete("SelectedDeviceState", location);
  }

  private void deleteWire(Point location) {
    delete("SelectedWireState", location);
  }

  @Test
  public void testPlaceSingleDevice() {
    testName = "testPlaceSingleDevice";

    // Test SetUp
    // (Empty)

    // Test Execution
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    placeSingleDevice(deviceType1, P(0, 0));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDontDeleteSingleDevice() {
    testName = "testDontDeleteSingleDevice";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Test Execution
    testState = testState.goPressDelete();

    // Reference SetUp
    Device newReferenceDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    referenceCircuit.addDevice(newReferenceDevice);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDeleteSingleDevice() {
    testName = "testDeleteSingleDevice";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Test Execution
    deleteDevice(P(0, 20));

    // Reference SetUp
    // (Empty)

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDragSingleIsolatedDevice() {
    testName = "testDragSingleIsolatedDevice";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Test Execution
    dragDevice(P(0, 20), P(100, 100), P(200, 200));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(200, 200), 0, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testPlaceSingleDirectWire() {
    testName = "testPlaceSingleDirectWire";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Test Execution
    placeDirectWire(P(0, -60), P(0, 60));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    PortOwnerId newDeviceId = referenceCircuit.addDevice(newDevice);
    Wire newWire =
        new Wire(Arrays.asList(P(0, -60), P(0, 60)), referenceCircuit.getPortIdOfDevicePort(
            newDeviceId, "1"), referenceCircuit.getPortIdOfDevicePort(newDeviceId, "0"));
    referenceCircuit.addWire(newWire);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testPlaceSingleWire() {
    testName = "testPlaceSingleWire";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Test Execution
    placeWire(P(0, -60), P(10, -60), P(10, 60), P(0, 60));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    PortOwnerId newDeviceId = referenceCircuit.addDevice(newDevice);
    Wire newWire =
        new Wire(Arrays.asList(P(0, -60), P(0, -60), P(10, -60), P(10, 60), P(0, 60)),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId, "1"),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId, "0"));
    referenceCircuit.addWire(newWire);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDeleteSingleWire() {
    testName = "testDeleteSingleWire";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    PortOwnerId newTestDeviceId = testCircuit.addDevice(newTestDevice);
    Wire newTestWire =
        new Wire(Arrays.asList(P(0, -60), P(0, -60), P(50, -60), P(50, 60), P(0, 60)),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "1"),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "0"));
    testCircuit.addWire(newTestWire);

    // Test Execution
    deleteWire(P(50, 0));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDeleteDeviceWithWire() {
    testName = "testDeleteDeviceWithWire";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    PortOwnerId newTestDeviceId = testCircuit.addDevice(newTestDevice);
    Wire newTestWire =
        new Wire(Arrays.asList(P(0, -60), P(0, -60), P(50, -60), P(50, 60), P(0, 60)),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "1"),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "0"));
    testCircuit.addWire(newTestWire);

    // Test Execution
    deleteDevice(P(10, 0));

    // Reference SetUp
    // (Empty)

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDragWire() {
    testName = "testDragWire";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    PortOwnerId newTestDeviceId = testCircuit.addDevice(newTestDevice);
    Wire newTestWire =
        new Wire(Arrays.asList(P(0, -60), P(0, -60), P(50, -60), P(50, 60), P(0, 60)),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "1"),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "0"));
    testCircuit.addWire(newTestWire);

    // Test Execution
    dragWire(P(60, 0), P(80, 20), P(100, 200), P(80, -400));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    PortOwnerId newDeviceId = referenceCircuit.addDevice(newDevice);
    Wire newWire =
        new Wire(Arrays.asList(P(0, -60), P(0, -60), P(80, -60), P(80, 60), P(0, 60)),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId, "1"),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId, "0"));
    referenceCircuit.addWire(newWire);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDragDeviceWithWire() {
    testName = "testDragDeviceWithWire";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    PortOwnerId newTestDeviceId = testCircuit.addDevice(newTestDevice);
    Wire newTestWire =
        new Wire(Arrays.asList(P(0, -60), P(0, -60), P(50, -60), P(50, 60), P(0, 60)),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "1"),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId, "0"));
    testCircuit.addWire(newTestWire);

    // Test Execution
    dragDevice(P(0, 0), P(-40, 0), P(-80, 0), P(-60, 0));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(-60, 0), 0, false, "DEVICE1");
    PortOwnerId newDeviceId = referenceCircuit.addDevice(newDevice);
    Wire newWire =
        new Wire(Arrays.asList(P(-60, -60), P(-60, -60), P(50, -60), P(50, 60), P(-60, 60)),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId, "1"),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId, "0"));
    referenceCircuit.addWire(newWire);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testPlaceTwoDeviceCircuit() {
    testName = "testPlaceTwoDeviceCircuit";

    // Test SetUp
    // (Empty)

    // Test Execution
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    placeSingleDevice(deviceType1, P(100, 100));
    placeSingleDevice(deviceType1, P(200, 100));
    placeWire(P(100, 40), P(100, 0), P(200, 0), P(200, 40));
    placeWire(P(100, 160), P(100, 200), P(200, 200), P(200, 160));

    // Reference SetUp
    Device newDevice1 = deviceType1.create(P(100, 100), 0, false, "DEVICE1");
    PortOwnerId newDeviceId1 = referenceCircuit.addDevice(newDevice1);
    Device newDevice2 = deviceType1.create(P(200, 100), 0, false, "DEVICE2");
    PortOwnerId newDeviceId2 = referenceCircuit.addDevice(newDevice2);
    Wire newWire1 =
        new Wire(Arrays.asList(P(100, 40), P(100, 0), P(200, 0), P(200, 40)),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId1, "1"),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId2, "1"));
    referenceCircuit.addWire(newWire1);
    Wire newWire2 =
        new Wire(Arrays.asList(P(100, 160), P(100, 200), P(200, 200), P(200, 160)),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId1, "0"),
            referenceCircuit.getPortIdOfDevicePort(newDeviceId2, "0"));
    referenceCircuit.addWire(newWire2);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDragDeviceInTwoDeviceCircuit() {
    testName = "testDragDeviceInTwoDeviceCircuit";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice1 = deviceType1.create(P(100, 100), 0, false, "DEVICE1");
    PortOwnerId newTestDeviceId1 = testCircuit.addDevice(newTestDevice1);
    Device newTestDevice2 = deviceType1.create(P(200, 100), 0, false, "DEVICE2");
    PortOwnerId newTestDeviceId2 = testCircuit.addDevice(newTestDevice2);
    Wire newTestWire1 =
        new Wire(Arrays.asList(P(100, 40), P(100, 0), P(200, 0), P(200, 40)),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId1, "1"),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId2, "1"));
    WireId newTestWireId1 = testCircuit.addWire(newTestWire1);
    Wire newTestWire2 =
        new Wire(Arrays.asList(P(100, 160), P(100, 200), P(200, 200), P(200, 160)),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId1, "0"),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId2, "0"));
    WireId newTestWireId2 = testCircuit.addWire(newTestWire2);

    // Reference SetUp
    referenceCircuit = testCircuit.snapshot();
    referenceCircuit.replaceDevice(newTestDeviceId1,
        deviceType1.create(P(0, 120), 0, false, "DEVICE1"));
    referenceCircuit.replaceWire(
        newTestWireId1,
        new Wire(Arrays.asList(P(0, 60), P(0, 0), P(200, 0), P(200, 40)), referenceCircuit
            .getPortIdOfDevicePort(newTestDeviceId1, "1"), referenceCircuit.getPortIdOfDevicePort(
            newTestDeviceId2, "1")));
    referenceCircuit.replaceWire(
        newTestWireId2,
        new Wire(Arrays.asList(P(0, 180), P(100, 200), P(200, 200), P(200, 160)), referenceCircuit
            .getPortIdOfDevicePort(newTestDeviceId1, "0"), referenceCircuit.getPortIdOfDevicePort(
            newTestDeviceId2, "0")));

    // Test Execution
    dragDevice(P(100, 120), P(100, 100), P(200, 200), P(0, 120)); // Move
    // Device1

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDragDirectWireInTwoDeviceCircuit() {
    testName = "testDragDirectWireInTwoDeviceCircuit";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice1 = deviceType1.create(P(-100, 0), 0, false, "DEVICE1");
    PortOwnerId newTestDeviceId1 = testCircuit.addDevice(newTestDevice1);
    Device newTestDevice2 = deviceType1.create(P(100, 0), 0, false, "DEVICE2");
    PortOwnerId newTestDeviceId2 = testCircuit.addDevice(newTestDevice2);
    Wire newTestWire1 =
        new Wire(Arrays.asList(P(-100, -60), P(100, -60)), testCircuit.getPortIdOfDevicePort(
            newTestDeviceId1, "1"), testCircuit.getPortIdOfDevicePort(newTestDeviceId2, "1"));
    WireId newTestWireId1 = testCircuit.addWire(newTestWire1);
    Wire newTestWire2 =
        new Wire(Arrays.asList(P(-100, 60), P(100, 60)), testCircuit.getPortIdOfDevicePort(
            newTestDeviceId1, "0"), testCircuit.getPortIdOfDevicePort(newTestDeviceId2, "0"));
    testCircuit.addWire(newTestWire2);

    // Reference SetUp
    referenceCircuit = testCircuit.snapshot();
    Wire newWire1 =
        new Wire(Arrays.asList(P(-100, -60), P(-100, -100), P(100, -100), P(100, -60)),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId1, "1"),
            testCircuit.getPortIdOfDevicePort(newTestDeviceId2, "1"));
    referenceCircuit.replaceWire(newTestWireId1, newWire1);

    // Test Execution
    dragWire(P(0, -60), P(-1000, -40), P(1000, 50), P(-1000, -100));

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testDragWireInTwoDeviceCircuit() {

  }

  @Test
  public void testCreateWireJunction() {

  }

  @Test
  public void testDeleteWireConnectedToJunctionStart() {

  }

  @Test
  public void testDeleteWireConnectedToJunctionEnd() {

  }

  @Test
  public void testMergeWiresOnDelete() {

  }

  @Test
  public void testSelectAndDeselect() {

  }

  // KNOWN FAILURE: MISSING KEY BINDING, BUT UNSURE ON HOW TO BIND WELL
  // SINCE WE SHOULDN'T USE CONTROL KEYS
  @Test
  public void testRotateAndPlace() {
    testName = "testRotateAndPlace";

    // Test SetUp
    // (Empty)

    // Test Execution
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    placeSingleDevice(deviceType1, P(0, 0), 1);

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 1, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testPlaceAndRotateRightOnce() {
    testName = "testPlaceAndRotateRightOnce";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 1, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Test Execution
    testState = testState.goMouseMove(P(10, 10));
    assertEquals("IdleState", testState.toString());
    testState = testState.goMouseDown(P(10, 10));
    assertEquals("MousingCircuitObjectState", testState.toString());
    testState = testState.goMouseUp(P(10, 10));
    assertEquals("SelectedDeviceState", testState.toString());
    testState = testState.goEditorAction(DeviceEditor.DeviceEditorAction.ROTATE_RIGHT);
    assertEquals("SelectedDeviceState", testState.toString());

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testPlaceAndRotateLeftOnce() {
    testName = "testPlaceAndRotateLeftOnce";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 3, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Test Execution
    testState = testState.goMouseMove(P(10, 10));
    assertEquals("IdleState", testState.toString());
    testState = testState.goMouseDown(P(10, 10));
    assertEquals("MousingCircuitObjectState", testState.toString());
    testState = testState.goMouseUp(P(10, 10));
    assertEquals("SelectedDeviceState", testState.toString());

    testState = testState.goEditorAction(DeviceEditor.DeviceEditorAction.ROTATE_LEFT);
    assertEquals("SelectedDeviceState", testState.toString());

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testPlaceAndRotateRightTwice() {
    testName = "testPlaceAndRotateRightTwice";

    // Test SetUp
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    Device newTestDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    testCircuit.addDevice(newTestDevice);

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 2, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Test Execution
    testState = testState.goMouseMove(P(10, 10));
    assertEquals("IdleState", testState.toString());
    testState = testState.goMouseDown(P(10, 10));
    assertEquals("MousingCircuitObjectState", testState.toString());
    testState = testState.goMouseUp(P(10, 10));
    assertEquals("SelectedDeviceState", testState.toString());

    testState = testState.goEditorAction(DeviceEditor.DeviceEditorAction.ROTATE_RIGHT);
    assertEquals("SelectedDeviceState", testState.toString());
    testState = testState.goEditorAction(DeviceEditor.DeviceEditorAction.ROTATE_RIGHT);
    assertEquals("SelectedDeviceState", testState.toString());

    // Verification
    assertEquals(referenceCircuit, testCircuit);
  }

  @Test
  public void testSingleDeviceRotateWithWire() {

  }

  @Test
  public void testDontAutoConnectIfPortNonEmpty() {

  }

  @Test
  public void testAutoConnectOnPlace() {

  }

  @Test
  public void testAutoConnectOnDrag() {

  }
}
