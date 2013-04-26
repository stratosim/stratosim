package com.stratosim.server.persistence.impl.helpers;

import static com.stratosim.server.persistence.schema.Property.newProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.stratosim.server.persistence.impl.helpers.DatastoreHelper;
import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.PersistenceException;

public class DatastoreHelperTest {
  
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  private DatastoreHelper datastoreHelper;
  
  private DatastoreService datastore;

  @Before
  public void setUp() throws Exception {
    helper.setUp();
    
    datastoreHelper = new DatastoreHelper();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @After
  public void tearDown() throws Exception {
    helper.tearDown();
  }

  @Test
  public void testConstruct() {
  }

  @Test
  public void testOverwriteOrPut() throws EntityNotFoundException {
    Entity oldEntity = new Entity("testKind");
    oldEntity.setUnindexedProperty("testString", "old value");
    oldEntity.setUnindexedProperty("testString2", "other old value");
    Key key = datastore.put(oldEntity);
    PKey<TestKind> pkey = new PKey<TestKind>(KeyFactory.keyToString(key));
    
    TestKind testKind = new TestKind();
    testKind.testString.set("my value");
    testKind.testString2.set("other value");
    
    PKey<TestKind> newPkey = datastoreHelper.overwriteOrPut(pkey, testKind);
    assertEquals(pkey, newPkey);
    
    Entity entity = datastore.get(key);
    assertEquals("testKind", entity.getKind());
    assertEquals("my value", entity.getProperty("testString"));
    assertEquals("other value", entity.getProperty("testString2"));
    assertEquals(2, entity.getProperties().size());
  }

  @Test
  public void testGet() throws PersistenceException {
    Entity oldEntity = new Entity("testKind");
    oldEntity.setUnindexedProperty("testString", "old value");
    oldEntity.setUnindexedProperty("testString2", "other old value");
    Key key = datastore.put(oldEntity);
    PKey<TestKind> pkey = new PKey<TestKind>(KeyFactory.keyToString(key));
    
    TestKind oldTestKind = new TestKind();
    TestKind testKind = datastoreHelper.get(pkey, oldTestKind);
    assertSame(oldTestKind, testKind);
    
    assertEquals("old value", testKind.testString.get());
    assertEquals("other old value", testKind.testString2.get());
    assertEquals(0, testKind.other.size());
  }
  
  @Test(expected = PersistenceException.class)
  public void testGet_missing() throws PersistenceException {
    Entity oldEntity = new Entity("testKind");
    Key key = datastore.put(oldEntity);
    PKey<TestKind> pkey = new PKey<TestKind>(KeyFactory.keyToString(key));
    datastore.delete(key);
    
    datastoreHelper.get(pkey, new TestKind());
  }

  @Test(expected = EntityNotFoundException.class)
  public void testDelete() throws EntityNotFoundException {
    Entity oldEntity = new Entity("testKind");
    Key key = datastore.put(oldEntity);
    PKey<TestKind> pkey = new PKey<TestKind>(KeyFactory.keyToString(key));
    
    datastoreHelper.delete(pkey);
    
    datastore.get(key);
  }

  @Test
  public void testHasValue() {
    Entity oldEntity = new Entity("testKind");
    Key key = datastore.put(oldEntity);
    PKey<TestKind> pkey = new PKey<TestKind>(KeyFactory.keyToString(key));
    
    assertTrue(datastoreHelper.hasValue(pkey, new TestKind()));
    
    datastore.delete(key);
    
    assertFalse(datastoreHelper.hasValue(pkey, new TestKind()));
  }

  @Test
  public void testUpdatePermissions() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetPermissions() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetAllFileRoles() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetAllUserRoles() {
    fail("Not yet implemented");
  }

  @Test
  public void testIsWhitelistedUser() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetWhitelistedUser() {
    fail("Not yet implemented");
  }

  @Test
  public void testPutWhitelistedUser() {
    fail("Not yet implemented");
  }

  @Test
  public void testAddVersion() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetVersions() {
    fail("Not yet implemented");
  }

  @Test
  public void testNewQuery() {
    fail("Not yet implemented");
  }

  @Test
  public void testPrepareQuery() {
    fail("Not yet implemented");
  }

  @Test
  public void testAddTypedFilter() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testGetOrNew() {
    fail("Not yet implemented");
  }

  public static class TestKind extends ExplicitPropertiesKind<TestKind> implements MemcacheCacheable {

    public final Property<String> testString = newProperty("testString", Type.string());
    
    public final Property<String> testString2 = newProperty("testString2", Type.string());
    
    public TestKind() {
      super("testKind");
    }

    @Override
    public Duration getExpirationDelta() {
      return Duration.standardMinutes(1);
    }
    
  }
  
}
