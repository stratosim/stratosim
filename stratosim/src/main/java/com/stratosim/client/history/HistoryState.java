package com.stratosim.client.history;

public interface HistoryState {

  /**
   * Fire history event to change url, but don't invoke event handler.
   */
  void fire();

  /**
   * Fire history event to change url and invoke event handler.
   */
  void fireAndHandle();

}
