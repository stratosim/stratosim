package com.stratosim.client.history;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class HistoryStateFactory {

  private HistoryStateFactory() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  public static NewFile newFile() {
    return new NewFileImpl();
  }

  public static OpenFile openFile(FileKey fileKey) {
    return new OpenFileImpl(checkNotNull(fileKey));
  }
  
  public static SimulateFile simulateFile(FileKey fileKey) {
    return new SimulateFileImpl(checkNotNull(fileKey));
  }

  public static OpenVersion openVersion(VersionMetadataKey versionKey) {
    return new OpenVersionImpl(checkNotNull(versionKey));
  }
  
  public static SimulateVersion simulateVersion(VersionMetadataKey versionKey) {
    return new SimulateVersionImpl(checkNotNull(versionKey));
  }

  public static HistoryState parseToken(String token) {

    TokenParser parser = TokenParser.parseToken(token);

    if (parser.getName().equals(NewFileImpl.NAME)) {
      checkArgument(parser.getArgs().isEmpty());
      return new NewFileImpl();

    } else if (parser.getName().equals(OpenFileImpl.NAME)) {
      String fileKeyString = Iterables.getOnlyElement(parser.getArgs());
      return new OpenFileImpl(new FileKey(fileKeyString));

    } else if (parser.getName().equals(SimulateFileImpl.NAME)) {
      String fileKeyString = Iterables.getOnlyElement(parser.getArgs());
      return new SimulateFileImpl(new FileKey(fileKeyString));

    } else if (parser.getName().equals(OpenVersionImpl.NAME)) {
      checkArgument(parser.getArgs().size() == 2);
      String fileKeyString = parser.getArgs().get(0);
      String timeString = parser.getArgs().get(1);
      return new OpenVersionImpl(new VersionMetadataKey(
          new FileKey(fileKeyString), Long.parseLong(timeString)));

    } else if (parser.getName().equals(SimulateVersionImpl.NAME)) {
      checkArgument(parser.getArgs().size() == 2);
      String fileKeyString = parser.getArgs().get(0);
      String timeString = parser.getArgs().get(1);
      return new SimulateVersionImpl(new VersionMetadataKey(
          new FileKey(fileKeyString), Long.parseLong(timeString)));

    }

    throw new IllegalArgumentException("bad token name: " + parser.getName());
  }

  private static class NewFileImpl extends AbstractHistoryState implements NewFile {

    private static final String NAME = "new";

    public NewFileImpl() {
      super(NAME, ImmutableList.<String>of());
    }
  }

  private static class OpenFileImpl extends AbstractHistoryState implements OpenFile {

    private static final String NAME = "file";

    private final FileKey fileKey;

    public OpenFileImpl(FileKey fileKey) {
      super(NAME, ImmutableList.of(fileKey.get()));
      this.fileKey = fileKey;
    }

    @Override
    public FileKey getFileKey() {
      return fileKey;
    }
  }
  
  private static class SimulateFileImpl extends AbstractHistoryState implements SimulateFile {

    private static final String NAME = "simulate";

    private final FileKey fileKey;

    public SimulateFileImpl(FileKey fileKey) {
      super(NAME, ImmutableList.of(fileKey.get()));
      this.fileKey = fileKey;
    }

    @Override
    public FileKey getFileKey() {
      return fileKey;
    }
  }

  private static class OpenVersionImpl extends AbstractHistoryState implements OpenVersion {

    private static final String NAME = "version";

    private final VersionMetadataKey versionMetadataKey;

    public OpenVersionImpl(VersionMetadataKey versionMetadataKey) {
      super(NAME, ImmutableList.of(
          versionMetadataKey.getFileKey().get(),
          Long.toString(versionMetadataKey.getTimeMillis())));
      this.versionMetadataKey = versionMetadataKey;
    }

    @Override
    public VersionMetadataKey getVersionKey() {
      return versionMetadataKey;
    }
  }
  
  private static class SimulateVersionImpl extends AbstractHistoryState implements SimulateVersion {

    private static final String NAME = "simulateversion";

    private final VersionMetadataKey versionMetadataKey;

    public SimulateVersionImpl(VersionMetadataKey versionMetadataKey) {
      super(NAME, ImmutableList.of(
          versionMetadataKey.getFileKey().get(),
          Long.toString(versionMetadataKey.getTimeMillis())));
      this.versionMetadataKey = versionMetadataKey;
    }

    @Override
    public VersionMetadataKey getVersionKey() {
      return versionMetadataKey;
    }
  }
}
