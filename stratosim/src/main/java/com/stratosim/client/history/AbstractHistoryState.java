package com.stratosim.client.history;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.History;

abstract class AbstractHistoryState implements HistoryState {

  private final String name;
  private final ImmutableList<String> args;

  protected AbstractHistoryState(String name, ImmutableList<String> args) {
    this.name = checkNotNull(name);
    this.args = checkNotNull(args);
  }

  @Override
  public void fire() {
    History.newItem(TokenParser.buildToken(name, args), false);
  }

  @Override
  public void fireAndHandle() {
    History.newItem(TokenParser.buildToken(name, args), true);
  }

}
