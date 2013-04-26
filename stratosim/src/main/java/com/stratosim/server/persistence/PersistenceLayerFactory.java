package com.stratosim.server.persistence;

import static com.google.common.base.Preconditions.checkState;

import com.google.appengine.api.users.UserServiceFactory;
import com.stratosim.server.persistence.impl.AccessCheckerPersistenceLayerImpl;
import com.stratosim.server.persistence.impl.AccountsPersistenceLayerImpl;
import com.stratosim.server.persistence.impl.AdminPersistenceLayerImpl;
import com.stratosim.server.persistence.impl.GoogleAPIsPersistenceLayerImpl;
import com.stratosim.server.persistence.impl.UserPersistenceLayerImpl;
import com.stratosim.server.persistence.impl.WhitelistPersistenceLayerImpl;
import com.stratosim.server.persistence.impl.WorkerPersistenceLayerImpl;
import com.stratosim.server.persistence.objectify.impl.ObjectifyTestPersistenceLayerImpl;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class PersistenceLayerFactory {

  private PersistenceLayerFactory() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  public static UserPersistenceLayer createUserLayer(LowercaseEmailAddress user) {
    return new UserPersistenceLayerImpl(user);
  }

  public static AccountsPersistenceLayer createAccountsLayer(LowercaseEmailAddress user) {
    return new AccountsPersistenceLayerImpl(user);
  }

  public static GoogleAPIsPersistenceLayer createGoogleAPIsPersistenceLayer(
      LowercaseEmailAddress user) {
    return new GoogleAPIsPersistenceLayerImpl(user);
  }

  public static WorkerPersistenceLayer createInternalLayer() {
    // web.xml restrict access. The user here is null.
    return new WorkerPersistenceLayerImpl();
  }

  public static AccessCheckPersistenceLayer createAccessCheckLayer() {
    return new AccessCheckerPersistenceLayerImpl();
  }

  public static WhitelistPersistenceLayer createWhitelistLayer() {
    // web.xml restrict access. The user here is null.
    return new WhitelistPersistenceLayerImpl();
  }

  public static AdminPersistenceLayer createAdminLayer() {
    checkAdmin();
    // For tighter admin security, break encapsulation and grab the user's email 
    // directly.
    return new AdminPersistenceLayerImpl(new LowercaseEmailAddress((UserServiceFactory
        .getUserService().getCurrentUser().getEmail())));
  }

  private static void checkAdmin() {
    // For tighter admin security, break encapsulation and verify admin by calling 
    // AppEngine Users API directly.
    checkState(UserServiceFactory.getUserService().isUserAdmin());
  }
  
  // ----------------------------------------------
  
  public static ObjectifyTestPersistenceLayer getObjectifyTestLayer() {
    return new ObjectifyTestPersistenceLayerImpl();
  }

}
