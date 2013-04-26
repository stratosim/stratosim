package com.stratosim.client.history;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class TokenParser {

  private static final char SEPARATOR = '/';  // regex special chars should be escaped
  private static final Joiner JOINER = Joiner.on(SEPARATOR);
  private static final Splitter SPLITTER = Splitter.on(SEPARATOR)
      .trimResults().omitEmptyStrings();
  
  private final String name;
  private final ImmutableList<String> args;

  private TokenParser(String name, ImmutableList<String> args) {
    checkNotNull(name);
    checkNotNull(args);
    checkArgument(name.length() > 0);
    this.name = name;
    this.args = args;
  }

  public static TokenParser parseToken(String token) {
    checkNotNull(token);

    ImmutableList<String> parts = ImmutableList.copyOf(SPLITTER.split(token));
    String tokenName = parts.get(0);
    ImmutableList<String> args = parts.subList(1, parts.size());
    
    return new TokenParser(tokenName, args);
  }

  public String getName() {
    return name;
  }

  public ImmutableList<String> getArgs() {
    return args;
  }

  public static String buildToken(String name, ImmutableList<String> args) {
    checkNotNull(name);
    checkNotNull(args);
    checkForSeparator(ImmutableList.of(name));
    checkForSeparator(args);

    if (args.size() == 0) {
      return name;
    }
    
    return JOINER.join(Iterables.concat(ImmutableList.of(name), args));
  }

  private static void checkForSeparator(ImmutableCollection<String> strs) {
    for (String s : strs) {
      if (s.contains(String.valueOf(SEPARATOR))) {
        throw new IllegalArgumentException("illegal character in: " + s);
      }
    }
  }
}
