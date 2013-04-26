package com.stratosim.server.persistence.kinds;

import static com.stratosim.server.persistence.schema.Property.newProperty;

import org.joda.time.Duration;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;

public class CircuitDataKind extends ExplicitPropertiesKind<CircuitDataKind>
    implements MemcacheCacheable {

  public final Property<Blob> data = newProperty("data", Type.blob());

  public CircuitDataKind() {
    super("circuitData");
  }

  @Override
  public Duration getExpirationDelta() {
    return Duration.standardDays(365);
  }

}
