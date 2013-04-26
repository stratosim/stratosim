package com.stratosim.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.ScatterChart;
import com.stratosim.client.devicemanager.LocalDeviceManager;
import com.stratosim.client.history.HistoryState;
import com.stratosim.client.history.HistoryStateFactory;
import com.stratosim.client.history.NewFile;
import com.stratosim.client.history.OpenFile;
import com.stratosim.client.history.OpenVersion;
import com.stratosim.client.history.SimulateFile;
import com.stratosim.client.ui.presenter.Presenter;
import com.stratosim.client.ui.presenter.error.ErrorPresenterImpl;
import com.stratosim.client.ui.presenter.loading.LoadingPresenterImpl;
import com.stratosim.client.ui.presenter.main.AsyncManagerImpl;
import com.stratosim.client.ui.presenter.main.NewMainPresenterImpl;
import com.stratosim.client.ui.presenter.main.OpenFileMainPresenterImpl;
import com.stratosim.client.ui.presenter.main.OpenVersionMainPresenterImpl;
import com.stratosim.client.ui.presenter.main.SimulateFileMainPresenterImpl;
import com.stratosim.client.ui.view.error.ErrorView;
import com.stratosim.client.ui.view.loading.LoadingView;
import com.stratosim.client.ui.view.main.MainView;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class AppController implements ValueChangeHandler<String>, Presenter {

  private static Logger logger = Logger.getLogger(AppController.class.getName());

  private static final int POLL_DELAY = 200;

  private MainView mainView;
  private AsyncManagerImpl asyncImpl;

  private ErrorView errorView;
  private LoadingView loadingView;

  private Presenter presenter;
  private HasWidgets container;

  public AppController() {
    String[] visualizationPackages = {Table.PACKAGE, ScatterChart.PACKAGE};
    VisualizationUtils.loadVisualizationApi(new Runnable() {
      @Override
      public void run() {}
    }, visualizationPackages);

    final LocalDeviceManager deviceManager = StratoSimStatic.getLocalDeviceManager();
    deviceManager.initialize();

    loadingPresenter();

    // Make sure all needed resources fetched before rendering the UI.
    // TODO(tpondich): Timeout
    Timer deferUI = new Timer() {
      @Override
      public void run() {
        if (deviceManager.isReady()) {
          this.cancel();
          bind();
          History.fireCurrentHistoryState();
        }
      }
    };

    if (!Canvas.isSupported()) {
      errorPresenter("Your web browser must support HTML5 canvas.");
      return;
    }

    deferUI.scheduleRepeating(POLL_DELAY);
  }

  @Override
  public void go(HasWidgets container) {
    this.container = container;
    goCurrentPresenter();
  }

  @Override
  public void onValueChange(ValueChangeEvent<String> event) {

    String token = event.getValue();
    if (token.isEmpty()) {
      newPresenter();

    } else {
      try {
        HistoryState historyState = HistoryStateFactory.parseToken(token);

        if (historyState instanceof OpenFile) {
          FileKey fileKey = ((OpenFile) historyState).getFileKey();
          openFilePresenter(fileKey);

        } else if (historyState instanceof SimulateFile) {
          FileKey fileKey = ((SimulateFile) historyState).getFileKey();
          simulateFilePresenter(fileKey);

        } else if (historyState instanceof OpenVersion) {
          VersionMetadataKey versionKey = ((OpenVersion) historyState).getVersionKey();
          openVersionPresenter(versionKey);

        } else if (historyState instanceof NewFile) {
          newPresenter();

        } else {
          throw new IllegalStateException("bad token class: " + historyState.getClass());
        }

      } catch (Exception e) {
        logger.log(Level.SEVERE, "Caught exception while parsing history state.", e);
        errorPresenter("Error parsing URL. Please check your browser's address bar!");
      }
    }

    goCurrentPresenter();
  }

  private void initViewAndAsyncIfNeeded() {
    if (mainView == null) {
      mainView = new MainView();
    }
    if (asyncImpl == null) {
      asyncImpl = new AsyncManagerImpl();
    }
  }

  private void errorPresenter(String text) {
    if (errorView == null) {
      errorView = new ErrorView();
    }
    presenter = new ErrorPresenterImpl(errorView, text);
  }

  private void loadingPresenter() {
    if (loadingView == null) {
      loadingView = new LoadingView();
    }
    presenter = new LoadingPresenterImpl(loadingView);
  }

  private void openVersionPresenter(VersionMetadataKey key) {
    initViewAndAsyncIfNeeded();
    presenter = new OpenVersionMainPresenterImpl(mainView, asyncImpl, key);
  }

  private void openFilePresenter(FileKey key) {
    initViewAndAsyncIfNeeded();
    presenter = new OpenFileMainPresenterImpl(mainView, asyncImpl, key);
  }

  private void simulateFilePresenter(FileKey key) {
    initViewAndAsyncIfNeeded();
    presenter = new SimulateFileMainPresenterImpl(mainView, asyncImpl, key);
  }

  private void newPresenter() {
    initViewAndAsyncIfNeeded();
    presenter = new NewMainPresenterImpl(mainView, asyncImpl);
  }

  private void bind() {
    History.addValueChangeHandler(this);
  }

  private void goCurrentPresenter() {
    GWT.runAsync(new RunAsyncCallback() {
      @Override
      public void onSuccess() {
        if (container != null) {
          presenter.go(container);
        }
      }

      @Override
      public void onFailure(Throwable reason) {
        logger.log(Level.SEVERE, "AppController async callback failed", reason);
      }
    });
  }

  @Override
  public void unregisterAll() {
    // ignore
  }

}
