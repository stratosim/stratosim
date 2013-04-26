package com.stratosim.server.persistence.kinds;

import static com.stratosim.server.persistence.schema.Property.newProperty;

import org.joda.time.Duration;

import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class GoogleAPIsKind extends ExplicitPropertiesKind<GoogleAPIsKind> implements MemcacheCacheable {

  public final Property<LowercaseEmailAddress> user = newProperty("user", Type.lowercaseEmail());
  
  public final Property<String> refreshToken = newProperty("refreshToken", Type.string());
  
  public final Property<String> accessToken = newProperty("accessToken", Type.string());

  
  public GoogleAPIsKind() {
    super("googleAPIs");
  }

  @Override
  public Duration getExpirationDelta() {
    return Duration.standardDays(365);
  }

}
