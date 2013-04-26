package com.stratosim.client.ui.presenter.main.states;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;

public class DeleteConfirmationState extends ConfirmationState {

  DeleteConfirmationState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      State onNoOrFail) {
    super(display, asyncManager, "Confirm Delete",
        "Once a file is deleted, the file and all it's versions will be permanently inaccessible.  "
            + "All public links and embeddings to any version will no longer work. "
            + "Are you sure you want to delete this file?", new WaitForDeleteState(display,
            asyncManager, onNoOrFail), onNoOrFail);
  }
}
