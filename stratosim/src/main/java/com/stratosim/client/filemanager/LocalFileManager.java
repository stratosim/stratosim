package com.stratosim.client.filemanager;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public enum LocalFileManager {

  INSTANCE;

  private boolean openRequestInProgress = false;
  private AsyncCallback<Circuit> openRequestCallback;

  private boolean deleteRequestInProgress = false;
  private AsyncCallback<Void> deleteRequestCallback;

  private boolean saveRequestInProgress = false;
  private AsyncCallback<VersionMetadata> saveRequestCallback;
  private Circuit saveCircuit;

  private boolean copyRequestInProgress = false;
  private AsyncCallback<VersionMetadata> copyRequestCallback;
  private Circuit copyCircuit;

  private boolean listLatestRequestInProgress = false;
  private AsyncCallback<ImmutableList<VersionMetadata>> listLatestRequestCallback;
  
  private boolean listVersionsRequestInProgress = false;
  private AsyncCallback<ImmutableList<VersionMetadata>> listVersionsRequestCallback;

  private Map<VersionMetadataKey, Circuit> files = Maps.newHashMap();

  public void listLatest(AsyncCallback<ImmutableList<VersionMetadata>> callback) {
    if (listLatestRequestInProgress == false) {
      listLatestRequestCallback = callback;
      StratoSimStatic.getFileService().listLatest(listLatestCallback);
    } else {
      throw new IllegalStateException();
    }
  }
  
  public void listVersions(FileKey fileKey, AsyncCallback<ImmutableList<VersionMetadata>> callback) {
    if (listVersionsRequestInProgress == false) {
      listVersionsRequestCallback = callback;
      StratoSimStatic.getFileService().listVersions(fileKey, listVersionsCallback);
    } else {
      throw new IllegalStateException();
    }
  }

  public Circuit create(String name) {
    Circuit circuit = new Circuit();
    circuit.setName(name);
    circuit.setFileRole(FileRole.OWNER);

    return circuit;
  }

  public void deleteFile(FileKey fileKey, AsyncCallback<Void> callback) {
    checkNotNull(fileKey);

    if (deleteRequestInProgress == false) {
      deleteRequestCallback = callback;
      StratoSimStatic.getFileService().deleteFile(fileKey, deleteCallback);
    } else {
      throw new IllegalStateException();
    }
  }

  public void save(Circuit circuit, AsyncCallback<VersionMetadata> callback) {
    checkNotNull(circuit);
    checkNotNull(callback);
    checkArgument(!isSaved(circuit));
    checkArgument(circuit.getFileRole() == FileRole.OWNER
        || circuit.getFileRole() == FileRole.WRITER);

    if (saveRequestInProgress == false) {
      saveCircuit = circuit.snapshot();
      saveRequestCallback = callback;
      StratoSimStatic.getFileService().save(saveCircuit, saveCallback);
    } else {
      throw new IllegalStateException();
    }
  }

  public void copy(Circuit circuit, AsyncCallback<VersionMetadata> callback) {
    checkNotNull(circuit);
    checkNotNull(callback);

    if (copyRequestInProgress == false) {
      copyCircuit = circuit.snapshot();
      copyRequestCallback = callback;
      StratoSimStatic.getFileService().copy(copyCircuit, copyCallback);
    } else {
      throw new IllegalStateException();
    }
  }

  public boolean isSaved(Circuit circuit) {
    return circuit.equals(files.get(circuit.getVersionKey()));
  }

  public void open(final VersionMetadataKey versionKey, AsyncCallback<Circuit> callback) {
    executeOpen(new Runnable() {
      @Override
      public void run() {
        StratoSimStatic.getFileService().open(versionKey, openCallback);
      }
    }, callback);
  }

  public void open(final FileKey fileKey, AsyncCallback<Circuit> callback) {
    executeOpen(new Runnable() {
      @Override
      public void run() {
        StratoSimStatic.getFileService().openLatest(fileKey, openCallback);
      }
    }, callback);
  }

  private void executeOpen(Runnable opener, AsyncCallback<Circuit> callback) {
    if (openRequestInProgress == false) {
      openRequestInProgress = true;
      openRequestCallback = callback;
      opener.run();
    } else {
      throw new IllegalStateException();
    }
  }

  private AsyncCallback<ImmutableList<VersionMetadata>> listLatestCallback =
      new AsyncCallback<ImmutableList<VersionMetadata>>() {
        public void onFailure(Throwable caught) {
          listLatestRequestCallback.onFailure(caught);
        }

        public void onSuccess(ImmutableList<VersionMetadata> list) {
          // TODO(tpondich): Update latest cache.

          listLatestRequestCallback.onSuccess(list);
        }
      };
      
      private AsyncCallback<ImmutableList<VersionMetadata>> listVersionsCallback =
          new AsyncCallback<ImmutableList<VersionMetadata>>() {
            public void onFailure(Throwable caught) {
              listVersionsRequestCallback.onFailure(caught);
            }

            public void onSuccess(ImmutableList<VersionMetadata> list) {
              // TODO(tpondich): Update latest cache.

              listVersionsRequestCallback.onSuccess(list);
            }
          };


  private AsyncCallback<Void> deleteCallback = new AsyncCallback<Void>() {
    public void onFailure(Throwable caught) {
      deleteRequestInProgress = false;
      deleteRequestCallback.onFailure(caught);
    }

    public void onSuccess(Void v) {
      deleteRequestInProgress = false;

      deleteRequestCallback.onSuccess(v);
      deleteRequestCallback = null;
    }
  };

  private AsyncCallback<Circuit> openCallback = new AsyncCallback<Circuit>() {
    public void onFailure(Throwable caught) {
      openRequestInProgress = false;
      openRequestCallback.onFailure(caught);
    }

    public void onSuccess(Circuit circuit) {
      files.put(circuit.getVersionKey(), circuit);

      openRequestInProgress = false;

      openRequestCallback.onSuccess(circuit.snapshot());
      openRequestCallback = null;
    }
  };

  private AsyncCallback<VersionMetadata> saveCallback = new AsyncCallback<VersionMetadata>() {
    public void onFailure(Throwable caught) {
      saveCircuit = null;
      saveRequestInProgress = false;
      saveRequestCallback.onFailure(caught);
    }

    public void onSuccess(VersionMetadata metadata) {
      saveCircuit.setFileKey(metadata.getFileKey());
      saveCircuit.setVersionKey(metadata.getVersionKey());

      // The circuit was already snapshotted.
      files.put(metadata.getVersionKey(), saveCircuit);

      saveCircuit = null;

      saveRequestInProgress = false;

      saveRequestCallback.onSuccess(metadata);
      saveRequestCallback = null;
    }
  };

  private AsyncCallback<VersionMetadata> copyCallback = new AsyncCallback<VersionMetadata>() {
    public void onFailure(Throwable caught) {
      copyCircuit = null;
      copyRequestInProgress = false;
      copyRequestCallback.onFailure(caught);
    }

    public void onSuccess(VersionMetadata metadata) {
      copyCircuit.setFileKey(metadata.getFileKey());
      copyCircuit.setVersionKey(metadata.getVersionKey());

      // The circuit was already snapshotted.
      files.put(metadata.getVersionKey(), copyCircuit);

      copyCircuit = null;

      copyRequestInProgress = false;

      copyRequestCallback.onSuccess(metadata);
      copyRequestCallback = null;
    }
  };

}
