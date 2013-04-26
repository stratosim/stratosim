package com.stratosim.server.persistence.kinds;

import static com.stratosim.server.persistence.schema.Property.newProperty;

import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class AccountKind extends ExplicitPropertiesKind<AccountKind> {

  public final Property<LowercaseEmailAddress> user = newProperty("user", Type.lowercaseEmail());
  
  public final Property<String> salt = newProperty("salt", Type.string());
  
  public final Property<String> hash = newProperty("hash", Type.string());
  
  public AccountKind() {
    super("account");
  }

}
