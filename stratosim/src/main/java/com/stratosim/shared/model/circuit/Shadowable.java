package com.stratosim.shared.model.circuit;

/**
 * A shadow behaves like its device (all the normal operations work). The client may optionally call
 * {@code #commit()} at any time to update the original device with the shadow's state.
 */
public interface Shadowable<ShadowType> {

  /**
   * Create a new shadow.
   */
  ShadowType newShadow();

}
