package com.stratosim.server.persistence.kinds;

import static com.stratosim.server.persistence.schema.Property.newProperty;

import org.joda.time.Duration;

import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class PublicFileKind extends ExplicitPropertiesKind<PublicFileKind>
    implements MemcacheCacheable {

  public final Property<LowercaseEmailAddress> addedBy = newProperty("addedBy", Type.lowercaseEmail());

  public PublicFileKind() {
    super("publicFile");
  }

  @Override
  public Duration getExpirationDelta() {
    return Duration.standardMinutes(5);
  }

}
