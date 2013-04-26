package com.stratosim.server.persistence.kinds;

import static com.stratosim.server.persistence.schema.Property.newProperty;

import org.joda.time.Duration;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;

public class CircuitThumbnailKind extends ExplicitPropertiesKind<CircuitThumbnailKind>
    implements MemcacheCacheable {

  public final Property<Blob> data = newProperty("data", Type.blob());

  public CircuitThumbnailKind() {
    super("circuitThumbnail");
  }

  @Override
  public Duration getExpirationDelta() {
    return Duration.standardDays(365);
  }

}
