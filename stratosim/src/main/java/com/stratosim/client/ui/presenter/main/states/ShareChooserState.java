package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.EmailAddress;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.FileVisibility;

public class ShareChooserState extends AbstractState {
  private final State previous;
  private final boolean previousIsSimulate;

  private final FileVisibility fileVisibility;

  ShareChooserState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      State previous, FileVisibility fileVisibility, boolean previousIsSimulate) {
    super(display, asyncManager);
    this.previous = checkNotNull(previous);
    this.previousIsSimulate = previousIsSimulate;
    this.fileVisibility = checkNotNull(fileVisibility);
  }

  @Override
  public boolean isShareChooserVisible() {
    return true;
  }

  @Override
  public FileVisibility getShareChooserFileVisibility() {
    return fileVisibility;
  }
  
  @Override
  public boolean getShareChooserIsSimulate() {
    return previousIsSimulate;
  }

  private boolean isLosingOwnership(FileVisibility newFileVisibility) {
    boolean isLosingOwnership =
        newFileVisibility.getPermissions().get(StratoSimStatic.getDirectData().getUserEmail()) != FileRole.OWNER;

    return isLosingOwnership;
  }

  private boolean hasNoOwner(FileVisibility newFileVisibility) {
    boolean hasNoOwner = true;
    for (Map.Entry<EmailAddress, FileRole> entry : newFileVisibility.getPermissions().entrySet()) {
      if (entry.getValue() == FileRole.OWNER) {
        hasNoOwner = false;
        break;
      }
    }

    return hasNoOwner;
  }

  @Override
  public State goShareChooserClose(FileVisibility newFileVisibility) {
    if (!newFileVisibility.equals(fileVisibility)) {
      return new ConfirmationState(display, asyncManager, "Unsaved Sharing Settings",
          "Your have unsaved changes to sharing settings.  "
              + "To save your changes you must click \"Done\" on the sharing settings dialog.  "
              + "Are you sure you want to cancel without saving?", previous, new ShareChooserState(
              display, asyncManager, previous, newFileVisibility, previousIsSimulate));
    } else {
      return previous;
    }
  }

  @Override
  public State goShareChooserChange(FileVisibility newFileVisibility) {
    checkNotNull(newFileVisibility);

    if (hasNoOwner(newFileVisibility)) {
      return new AlertState(display, asyncManager, "Owner Required",
          "Files require one or more owners to manage sharing settings.  "
              + "Please set yourself as owner or transfer ownership to another person "
              + "who can manage this.", new ShareChooserState(display, asyncManager, previous,
              newFileVisibility, previousIsSimulate));

    } else if (isLosingOwnership(newFileVisibility)) {
      return new ConfirmationState(display, asyncManager, "Losing Ownership",
          "You are removing yourself as an owner of this file.  "
              + "Once you lose ownership, you will no longer be able to change sharing settings.  "
              + "Are you sure you want to transfer ownership of this file?",
          new WaitForSetFileVisibilityState(display, asyncManager, previous, newFileVisibility),
          new ShareChooserState(display, asyncManager, previous, newFileVisibility,
              previousIsSimulate));
    } else {
      return new WaitForSetFileVisibilityState(display, asyncManager, previous, newFileVisibility);
    }
  }
}
