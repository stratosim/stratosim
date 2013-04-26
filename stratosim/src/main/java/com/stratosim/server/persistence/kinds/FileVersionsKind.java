package com.stratosim.server.persistence.kinds;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
import com.google.appengine.api.datastore.Blob;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stratosim.server.algorithm.InvertibleFunction;
import com.stratosim.server.persistence.schema.MapPropertiesKind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.Type;

public class FileVersionsKind
    extends MapPropertiesKind<FileVersionsKind, Instant, VersionMetadataPb>
    implements MemcacheCacheable {

  public FileVersionsKind() {
    super("fileVersions", new InstantStringFunction(), new VersionMetadataPbStringFunction());
  }
  
  private static class InstantStringFunction implements InvertibleFunction<Instant, String> {

    @Override
    public String forward(Instant time) {
      checkNotNull(time);
      return Long.toString(time.getMillis());
    }

    @Override
    public Instant reverse(String timeString) {
      checkNotNull(timeString);
      return new Instant(Long.parseLong(timeString));
    }
  }
  
  private static class VersionMetadataPbStringFunction
      implements InvertibleFunction<VersionMetadataPb, Object> {

    @Override
    public Object forward(VersionMetadataPb metadata) {
      checkNotNull(metadata);
      return new Blob(metadata.toByteArray());
    }

    @Override
    public VersionMetadataPb reverse(Object obj) {
      checkNotNull(obj);
      Blob blob = Type.tryCast(obj);
      try {
        return VersionMetadataPb.parseFrom(blob.getBytes());
      } catch (InvalidProtocolBufferException ex) {
        throw new IllegalArgumentException("error parsing VersionMetadata proto", ex);
      }
    }
  }

  @Override
  public Duration getExpirationDelta() {
    return Duration.standardMinutes(5);
  }

}
