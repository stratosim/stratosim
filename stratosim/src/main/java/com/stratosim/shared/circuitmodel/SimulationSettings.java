package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

// Immutable
public class SimulationSettings implements Serializable {
  private static final long serialVersionUID = -7598076074604641177L;
  
  private boolean isTransient;
  private String simulationLength;
  private String startFrequency;
  private String stopFrequency;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private SimulationSettings() {}

  public SimulationSettings(boolean isTransient, String simulationLength, String startFrequency,
      String stopFrequency) {
    this.isTransient = isTransient;
    this.simulationLength = simulationLength;
    this.startFrequency = startFrequency;
    this.stopFrequency = stopFrequency;
  }

  public boolean isTransient() {
    return isTransient;
  }

  public String getTransientDuration() {
    return simulationLength;
  }

  public String getStartFrequency() {
    return startFrequency;
  }

  public String getStopFrequency() {
    return stopFrequency;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isTransient ? 1231 : 1237);
    result = prime * result + ((simulationLength == null) ? 0 : simulationLength.hashCode());
    result = prime * result + ((startFrequency == null) ? 0 : startFrequency.hashCode());
    result = prime * result + ((stopFrequency == null) ? 0 : stopFrequency.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SimulationSettings other = (SimulationSettings) obj;
    if (isTransient != other.isTransient) return false;
    if (simulationLength == null) {
      if (other.simulationLength != null) return false;
    } else if (!simulationLength.equals(other.simulationLength)) return false;
    if (startFrequency == null) {
      if (other.startFrequency != null) return false;
    } else if (!startFrequency.equals(other.startFrequency)) return false;
    if (stopFrequency == null) {
      if (other.stopFrequency != null) return false;
    } else if (!stopFrequency.equals(other.stopFrequency)) return false;
    return true;
  }
}
