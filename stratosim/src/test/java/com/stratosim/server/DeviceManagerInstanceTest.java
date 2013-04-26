package com.stratosim.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.stratosim.server.DeviceManagerInstance;

public class DeviceManagerInstanceTest {

  @Test
  public void testSerialization() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(out);
    
    os.writeObject(DeviceManagerInstance.INSTANCE);
    
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    ObjectInputStream is = new ObjectInputStream(in);
    
    Object obj = is.readObject();
    Assert.assertSame(DeviceManagerInstance.INSTANCE, obj);
  }

}
