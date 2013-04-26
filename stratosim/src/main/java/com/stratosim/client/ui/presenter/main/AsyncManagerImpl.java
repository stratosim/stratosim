package com.stratosim.client.ui.presenter.main;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.QueryResponse;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.presenter.main.MainPresenter.AsyncManager;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.DeletingWithCollaboratorsException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.ShareURLHelper;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class AsyncManagerImpl implements AsyncManager {
  private MainPresenter presenter;

  private class SaveCallback implements AsyncCallback<VersionMetadata> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goSaveCallbackFailure();
    }

    @Override
    public void onSuccess(VersionMetadata result) {
      presenter.goSaveCallbackSuccess(result);
    }
  }

  private class CopyCallback implements AsyncCallback<VersionMetadata> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goCopyCallbackFailure();
    }

    @Override
    public void onSuccess(VersionMetadata result) {
      presenter.goCopyCallbackSuccess(result);
    }
  }

  private class OpenCallback implements AsyncCallback<Circuit> {
    @Override
    public void onFailure(Throwable caught) {
      if (caught instanceof AccessException) {
        presenter.goOpenCallbackAccessException();
      } else if (caught instanceof PersistenceException) {
        presenter.goOpenCallbackPersistenceException();
      } else {
        presenter.goOpenCallbackFailure();
      }
    }

    @Override
    public void onSuccess(Circuit result) {
      presenter.goOpenCallbackSuccess(result);
    }
  }

  private class DeleteCallback implements AsyncCallback<Void> {
    @Override
    public void onFailure(Throwable caught) {
      if (caught instanceof DeletingWithCollaboratorsException) {
        presenter.goDeleteCallbackDeletingWithCollaboratorsException();
      } else {
        presenter.goDeleteCallbackFailure();
      }
    }

    @Override
    public void onSuccess(Void v) {
      presenter.goDeleteCallbackSuccess();
    }
  }

  private class SimulateCallback implements Callback {
    public void onResponse(QueryResponse response) {
      if (response.isError()) {
        presenter.goSimulationDatatableCallbackFailure();
      } else {
        DataTable data = response.getDataTable();
        presenter.goSimulationDatatableCallbackSuccess(data);
      }
    }
  };

  public Callback getSimulationDatatableCallback() {
    return new SimulateCallback();
  }

  private class CircuitDownloadAvailableCallback implements AsyncCallback<Boolean> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goCircuitDownloadAvailableCallbackFailure();
    }

    @Override
    public void onSuccess(Boolean result) {
      presenter.goCircuitDownloadAvailableCallbackSuccess(result);
    }
  }

  private class GetPermissionsCallback implements AsyncCallback<FileVisibility> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goGetPermissionsCallbackFailure();
    }

    @Override
    public void onSuccess(FileVisibility result) {
      presenter.goGetPermissionsCallbackSuccess(result);
    }
  }

  private class SetPermissionsCallback implements AsyncCallback<Void> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goSetPermissionsCallbackFailure();
    }

    @Override
    public void onSuccess(Void v) {
      presenter.goSetPermissionsCallbackSuccess();
    }
  }

  private class TriggerRenderCallback implements AsyncCallback<Boolean> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goTriggerCallbackFailure();
    }

    @Override
    public void onSuccess(Boolean isAvailable) {
      presenter.goTriggerCallbackSuccess(isAvailable);
    }
  }

  private class ListLatestCallback implements AsyncCallback<ImmutableList<VersionMetadata>> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goListLatestCallbackFailure();
    }

    @Override
    public void onSuccess(ImmutableList<VersionMetadata> result) {
      presenter.goListLatestCallbackSuccess(result);
    }
  }
  
  private class ListVersionsCallback implements AsyncCallback<ImmutableList<VersionMetadata>> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goListVersionsCallbackFailure();
    }

    @Override
    public void onSuccess(ImmutableList<VersionMetadata> result) {
      presenter.goListVersionsCallbackSuccess(result);
    }
  }
  
  private class GetLatestThumbnailsCallback implements AsyncCallback<ImmutableMap<FileKey, String>> {
    @Override
    public void onFailure(Throwable caught) {
      presenter.goGetLatestThumbnailsCallbackFailure();
    }

    @Override
    public void onSuccess(ImmutableMap<FileKey, String> result) {
      presenter.goGetLatestThumbnailsCallbackSuccess(result);
    }
  }

  @Override
  public void fireSaveAsync(Circuit circuit) {
    StratoSimStatic.getLocalFileManager().save(circuit, new SaveCallback());
  }

  @Override
  public void fireCopyAsync(Circuit circuit) {
    StratoSimStatic.getLocalFileManager().copy(circuit, new CopyCallback());
  }

  @Override
  public void fireOpenAsync(FileKey key) {
    StratoSimStatic.getLocalFileManager().open(key, new OpenCallback());
  }

  @Override
  public void fireOpenAsync(VersionMetadataKey key) {
    StratoSimStatic.getLocalFileManager().open(key, new OpenCallback());
  }

  @Override
  public void fireDeleteAsync(FileKey key) {
    StratoSimStatic.getLocalFileManager().deleteFile(key, new DeleteCallback());
  }

  @Override
  public void fireListLatestAsync() {
    StratoSimStatic.getLocalFileManager().listLatest(new ListLatestCallback());
  }
  
  @Override
  public void fireListVersionsAsync(FileKey fileKey) {
    StratoSimStatic.getLocalFileManager().listVersions(fileKey, new ListVersionsCallback());
  }

  @Override
  public void fireSetFileVisibilityAsync(FileKey fileKey, FileVisibility fileVisibility) {
    StratoSimStatic.getFileService().setFileVisibility(fileKey, fileVisibility,
        new SetPermissionsCallback());
  }

  @Override
  public void fireGetFileVisibilityAsync(FileKey fileKey) {
    StratoSimStatic.getFileService().getFileVisibility(fileKey, new GetPermissionsCallback());
  }

  @Override
  public void fireDownloadAvailableAsync(VersionMetadataKey versionKey, DownloadFormat format) {
    StratoSimStatic.getCircuitDownloadAvailableService().isAvailable(versionKey, format,
        new CircuitDownloadAvailableCallback());
  }

  @Override
  public void fireTriggerRenderAsync(VersionMetadataKey versionKey, DownloadFormat format) {
    StratoSimStatic.getTriggerDownloadService().trigger(versionKey, format, new TriggerRenderCallback());
  }
  
  @Override
  public void fireGetLatestThumbnailsAsync() {
    StratoSimStatic.getFileService().getLatestThumbnails(new GetLatestThumbnailsCallback());
  }

  @Override
  public void fireSimulationDatatableAsync(VersionMetadataKey versionKey) {
    Query query = Query.create(ShareURLHelper.getSimulationDataTableServiceUrl(versionKey));
    query.send(getSimulationDatatableCallback());
  }
  
  @Override
  public void fireTimerAsync(int timeout) {
    Timer timer = new Timer() {
      public void run() {
        presenter.goTimerCallback();
      }
    };
    timer.schedule(timeout);
  }


  @Override
  public void setPresenter(MainPresenter presenter) {
    this.presenter = presenter;
  }

}
