package com.stratosim.server.persistence.kinds;

import static com.stratosim.server.persistence.schema.Property.newProperty;

import org.joda.time.Instant;

import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class WhitelistedUserKind extends ExplicitPropertiesKind<WhitelistedUserKind> {

  public final Property<LowercaseEmailAddress> user = newProperty("user", Type.lowercaseEmail());

  public final Property<Instant> accessStart = newProperty("accessStart", Type.instant());
  
  public WhitelistedUserKind() {
    super("whitelistedUser");
  }

}
