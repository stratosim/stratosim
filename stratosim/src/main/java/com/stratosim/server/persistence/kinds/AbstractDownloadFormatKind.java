package com.stratosim.server.persistence.kinds;

import static com.google.common.base.Preconditions.checkArgument;
import static com.stratosim.server.persistence.schema.Property.newProperty;

import org.joda.time.Duration;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.filemodel.DownloadFormat;

public abstract class AbstractDownloadFormatKind<K extends AbstractDownloadFormatKind<K>>
    extends ExplicitPropertiesKind<K> implements MemcacheCacheable {

  public final Property<Blob> data = newProperty("data", Type.blob());
  
  protected AbstractDownloadFormatKind(DownloadFormat downloadFormat) {
    super(getKindName(downloadFormat));
  }

  @Override
  public Duration getExpirationDelta() {
    return Duration.standardDays(365);
  }

  /**
   * Creates a backwards-compatible (with the datastore before this class was written) kind name.
   * @param downloadFormat The format.
   * @return Kind name.
   */
  private static String getKindName(DownloadFormat downloadFormat) {
    String lowercaseName = downloadFormat.getFormat().toLowerCase();
    checkArgument(lowercaseName.length() > 1);
    
    String first = lowercaseName.substring(0, 1).toUpperCase();
    String second = lowercaseName.substring(1, lowercaseName.length());
    
    return "circuit" + first + second;
  }

}
