package com.stratosim.shared.model.circuit.impl.data;

import com.stratosim.shared.model.util.CircuitCoord;

public final class ImmutableDeviceData {
  
  private final CircuitCoord location;
  private final int rotation;
  private final boolean mirrored;

  public CircuitCoord getLocation() {
    return location;
  }
  
  public int getRotation() {
    return rotation;
  }
  
  public boolean isMirrored() {
    return mirrored;
  }
  
  public interface Builder {
    Builder setLocation(CircuitCoord cc);
    Builder setRotation(int rotation);
    Builder toggleMirrored();
    
    ImmutableDeviceData build();
  }
  
  /**
   * @return An empty builder. All parameters must be set before calling {@link Builder#build()}.
   */
  public static Builder newBuilder() {
    return new BuilderImpl();
  }
  
  /**
   * @return A builder initialized with the current object's values. The caller may set
   * any of the parameters (or none) before calling {@link Builder#build()}.
   */
  public Builder newCopyBuilder() {
    return new BuilderImpl(this);
  }
  
  // ---------------------------------------------------------------------------------------------
  // Implementation
  // ---------------------------------------------------------------------------------------------

  private ImmutableDeviceData(CircuitCoord location, int rotation, boolean mirrored) {
    this.location = location;
    this.rotation = rotation;
    this.mirrored = mirrored;
  }
  
  private static class BuilderImpl implements Builder {
    private CircuitCoord location = null;
    private Integer rotation = null;
    private Boolean mirrored = null;
    
    public BuilderImpl() {}
    
    public BuilderImpl(ImmutableDeviceData data) {
      this.location = data.location;
      this.rotation = data.rotation;
      this.mirrored = data.mirrored;
    }
    
    @Override
    public Builder setLocation(CircuitCoord cc) {
      this.location = cc;
      return this;
    }

    @Override
    public Builder setRotation(int rotation) {
      this.rotation = rotation;
      return this;
    }

    @Override
    public Builder toggleMirrored() {
      this.mirrored = !this.mirrored;
      return this;
    }

    @Override
    public ImmutableDeviceData build() {
      return new ImmutableDeviceData(location, rotation, mirrored);
    }
  }
}
