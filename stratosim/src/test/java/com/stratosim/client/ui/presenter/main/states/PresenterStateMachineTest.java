package com.stratosim.client.ui.presenter.main.states;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;
import com.stratosim.client.ui.presenter.main.AbstractMainPresenter;
import com.stratosim.client.ui.presenter.main.MainPresenter;

/**
 * Test the Presenter State Machine.
 * 
 */
public class PresenterStateMachineTest extends GWTTestCase {
  private TestingPresenter testingPresenter;
  private MainPresenter.Display mockDisplay;
  private MainPresenter.AsyncManager mockAsyncManager;

  class TestingPresenter extends AbstractMainPresenter implements MainPresenter {

    public TestingPresenter(Display display, AsyncManager asyncManager) {
      super(display, asyncManager);
    }

  }
  
  @Test
  public void testInitialState() {    
    mockDisplay = EasyMock.createMock(MainPresenter.Display.class);
    mockAsyncManager = EasyMock.createMock(MainPresenter.AsyncManager.class);
    
    testingPresenter = new TestingPresenter(mockDisplay, mockAsyncManager);

    
    EasyMock.replay(mockDisplay);
    EasyMock.replay(mockAsyncManager);
    
    EasyMock.verify(mockDisplay);
    EasyMock.verify(mockAsyncManager);
    
    /*
    // Test SetUp
    State state = new SchematicNewState();

    // Test Execution
    DeviceType deviceType1 = stubTwoPortDeviceTypeCustom("TestDevice", 0, 60, 0, -60, 32, 120);
    placeSingleDevice(deviceType1, P(0, 0));

    // Reference SetUp
    Device newDevice = deviceType1.create(P(0, 0), 0, false, "DEVICE1");
    referenceCircuit.addDevice(newDevice);

    // Verification
    */
  }

  @Override
  public String getModuleName() {
    return "com.stratosim.StratoSim";
  }
}
