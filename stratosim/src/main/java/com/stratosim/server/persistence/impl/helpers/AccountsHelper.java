package com.stratosim.server.persistence.impl.helpers;

import com.stratosim.server.HashUtils;
import com.stratosim.server.persistence.kinds.AccountKind;
import com.stratosim.server.persistence.schema.CustomKeyFactory;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class AccountsHelper {
  
  private final DatastoreHelper datastoreHelper;
  
  public AccountsHelper(DatastoreHelper helper) {
    this.datastoreHelper = helper;
  }

  // ---------------------------------------------------------------------------------------------
  // StratoSim Accounts
  // ---------------------------------------------------------------------------------------------

  public boolean isValidAccount(LowercaseEmailAddress user, String usernamePasswordHash) throws PersistenceException, IllegalArgumentException {
    PKey<AccountKind> key = CustomKeyFactory.accountKey(user);
    
    AccountKind account = datastoreHelper.uncachedGet(key, new AccountKind());

    String datastoreSalt = account.salt.get();
    String datastoreSaltHash = account.hash.get();
    String clientSaltHash = HashUtils.sha256String(datastoreSalt + usernamePasswordHash);
    if (datastoreSaltHash.equals(clientSaltHash)) {
      return true;
    }

    return false;
  }
  
  public void putAccount(LowercaseEmailAddress user, String usernamePasswordHash) throws PersistenceException, IllegalArgumentException {
    PKey<AccountKind> key = CustomKeyFactory.accountKey(user);

    String salt = "" + HashUtils.secureRandom().nextLong();
    String hash = HashUtils.sha256String(salt + usernamePasswordHash);
    
    AccountKind accountKind = new AccountKind();
    accountKind.user.set(user);
    accountKind.salt.set(salt);
    accountKind.hash.set(hash);

    // This will also reset a password for an existing account.
    datastoreHelper.uncachedOverwriteOrPut(key, accountKind);
  }
  
}
