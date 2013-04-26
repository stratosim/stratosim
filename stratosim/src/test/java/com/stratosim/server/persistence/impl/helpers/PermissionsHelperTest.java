package com.stratosim.server.persistence.impl.helpers;

import static com.stratosim.shared.filemodel.FileRole.NONE;
import static com.stratosim.shared.filemodel.FileRole.OWNER;
import static com.stratosim.shared.filemodel.FileRole.READER;
import static com.stratosim.shared.filemodel.FileRole.WRITER;
import static org.easymock.EasyMock.eq;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.stratosim.server.persistence.impl.helpers.DatastoreHelper;
import com.stratosim.server.persistence.impl.helpers.PermissionsHelper;
import com.stratosim.server.persistence.kinds.FileRoleKind;
import com.stratosim.server.persistence.kinds.UserFileKind;
import com.stratosim.server.persistence.schema.CustomKeyFactory;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.RemovingLastOwnerException;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class PermissionsHelperTest {
  
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  private static final LowercaseEmailAddress u1 = new LowercaseEmailAddress("u1@example.com");
  private static final LowercaseEmailAddress u2 = new LowercaseEmailAddress("u2@example.com");
  private static final LowercaseEmailAddress u3 = new LowercaseEmailAddress("u3@example.com");

  private static final FileKey f1 = new FileKey("f1");
  private static final FileKey f2 = new FileKey("f2");
  
  private IMocksControl control;
  private DatastoreHelper datastoreHelper;
  private PermissionsHelper permissionsHelper;
  
  @Before
  public void setUp() {
    helper.setUp();
    
    control = EasyMock.createControl();
    datastoreHelper = control.createMock(DatastoreHelper.class);
    permissionsHelper = new PermissionsHelper(datastoreHelper);
  }
  
  @After
  public void tearDown() throws Exception {
    helper.tearDown();
  }

  @Test
  public void testCheckAtLeastOneOwner_pass() throws RemovingLastOwnerException {
    FileKey fileKey = new FileKey("my key");
    ImmutableList<FileRole> roles = ImmutableList.of(OWNER, READER, OWNER, WRITER, READER);
    
    PermissionsHelper.checkAtLeastOneOwner(fileKey, roles);
  }

  @Test(expected = RemovingLastOwnerException.class)
  public void testCheckAtLeastOneOwner_throw() throws RemovingLastOwnerException {
    FileKey fileKey = new FileKey("my key");
    ImmutableList<FileRole> roles = ImmutableList.of(READER, WRITER, READER);
    
    PermissionsHelper.checkAtLeastOneOwner(fileKey, roles);
  }
  
  @Test
  public void testCheckNoNone_pass() {
    ImmutableList<FileRole> roles = ImmutableList.of(OWNER, READER, OWNER, WRITER, READER);
    
    PermissionsHelper.checkNoNone(roles);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCheckNoNone_throw() {
    ImmutableList<FileRole> roles = ImmutableList.of(OWNER, READER, NONE, WRITER, READER);
    
    PermissionsHelper.checkNoNone(roles);
  }

  @Test
  public void testApplyPermissionDiff() {
    Map<LowercaseEmailAddress, FileRole> fileRole = Maps.newHashMap();
    Map<FileKey, FileRole> userFile = Maps.newHashMap();

    Map<LowercaseEmailAddress, FileRole> expectedFileRole = Maps.newHashMap();
    Map<FileKey, FileRole> expectedUserFile = Maps.newHashMap();
    
    // add user for f1
    PermissionsHelper.applyPermissionDiff(fileRole, userFile, u1, f1, OWNER);
    
    expectedFileRole.put(u1, OWNER);
    expectedUserFile.put(f1, OWNER);
    assertEquals(expectedFileRole, fileRole);
    assertEquals(expectedUserFile, userFile);
    
    // add user for f2
    PermissionsHelper.applyPermissionDiff(fileRole, userFile, u2, f2, READER);
    
    expectedFileRole.put(u2, READER);
    expectedUserFile.put(f2, READER);
    assertEquals(expectedFileRole, fileRole);
    assertEquals(expectedUserFile, userFile);
    
    // add another user for f1
    PermissionsHelper.applyPermissionDiff(fileRole, userFile, u3, f1, WRITER);
    
    expectedFileRole.put(u3, WRITER);
    expectedUserFile.put(f1, WRITER);
    assertEquals(expectedFileRole, fileRole);
    assertEquals(expectedUserFile, userFile);
    
    // remove user for f1
    PermissionsHelper.applyPermissionDiff(fileRole, userFile, u1, f1, NONE);
    
    expectedFileRole.remove(u1);
    expectedUserFile.remove(f1);
    assertEquals(expectedFileRole, fileRole);
    assertEquals(expectedUserFile, userFile);

    // repeat same remove; should be no change
    PermissionsHelper.applyPermissionDiff(fileRole, userFile, u1, f1, NONE);
    
    assertEquals(expectedFileRole, fileRole);
    assertEquals(expectedUserFile, userFile);
  }
  
  @Test
  public void testComputePermissionsDiff_emptyStart() {
    testComputePermissionsDiff_template(ImmutableMap.<LowercaseEmailAddress, FileRole>of(),
      ImmutableMap.of(u1, OWNER, u2, READER, u3, WRITER),
      ImmutableMap.of(u1, OWNER, u2, READER, u3, WRITER));
  }
  
  @Test
  public void testComputePermissionsDiff_emptyEnd() {
    testComputePermissionsDiff_template(ImmutableMap.of(u1, OWNER, u2, READER, u3, WRITER),
      ImmutableMap.<LowercaseEmailAddress, FileRole>of(), ImmutableMap.of(u1, NONE, u2, NONE, u3, NONE));
  }

  @Test
  public void testComputePermissionsDiff_diff() {
    testComputePermissionsDiff_template(ImmutableMap.of(u1, OWNER, u2, READER, u3, WRITER),
      ImmutableMap.of(u1, OWNER, u2, WRITER), ImmutableMap.of(u2, WRITER, u3, NONE));
  }

  private static void testComputePermissionsDiff_template(ImmutableMap<LowercaseEmailAddress, FileRole> src,
      ImmutableMap<LowercaseEmailAddress, FileRole> dst, ImmutableMap<LowercaseEmailAddress, FileRole> expectedDiff) {
    ImmutableMap<LowercaseEmailAddress, FileRole> diff = PermissionsHelper.computePermissionsDiff(src, dst);
    assertEquals(expectedDiff, diff);
  }
  
  @Test
  public void testGetAllFileRoles() throws PersistenceException {
    PKey<FileRoleKind> fileRoleKey = CustomKeyFactory.fileRoleKey(f1); 
    ImmutableMap<LowercaseEmailAddress, FileRole> map = ImmutableMap.of(u1, OWNER, u2, READER); 
    FileRoleKind fileRoleKind = new FileRoleKind();
    fileRoleKind.properties.putAll(map);
    
    EasyMock.expect(datastoreHelper.uncachedGet(eq(fileRoleKey),
        EasyMock.<FileRoleKind>anyObject())).andReturn(fileRoleKind);
    
    control.replay();
    
    assertEquals(map, permissionsHelper.getAllFileRoles(f1));
  }

  @Test
  public void testGetAllUserRoles() throws PersistenceException {
    PKey<UserFileKind> userFileKey = CustomKeyFactory.userFileKey(u1); 
    ImmutableMap<FileKey, FileRole> map = ImmutableMap.of(f1, OWNER, f2, READER); 
    UserFileKind userFileKind = new UserFileKind();
    userFileKind.properties.putAll(map);
    
    EasyMock.expect(datastoreHelper.uncachedGet(eq(userFileKey),
        EasyMock.<UserFileKind>anyObject())).andReturn(userFileKind);
    
    control.replay();
    
    assertEquals(map, permissionsHelper.getAllUserRoles(u1));
  }

}
