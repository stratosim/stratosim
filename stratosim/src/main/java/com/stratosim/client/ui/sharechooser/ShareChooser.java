package com.stratosim.client.ui.sharechooser;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.stratosim.client.BaseURLHelper.getServingBase;
import static com.stratosim.shared.ShareURLHelper.getCircuitDownloadServiceUrl;
import static com.stratosim.shared.ShareURLHelper.getCircuitLinkUrl;
import static com.stratosim.shared.ShareURLHelper.getEmbedServiceUrl;
import static com.stratosim.shared.ShareURLHelper.getSimulateLinkUrl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.shared.filemodel.StratoSimKey;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.EmailAddress;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class ShareChooser extends ResizeComposite implements HasValueChangeHandlers<FileVisibility> {
  @UiField
  Grid grid;

  @UiField
  TextBox addEmail;
  @UiField
  PushButton addButton;

  @UiField
  PushButton doneButton;

  @UiField
  GridList<Boolean> publicOptions;

  // TODO(tpondich): Push images into UIBinder.
  @UiField
  ImageResource ownButtonImage;
  @UiField
  ImageResource editButtonImage;
  @UiField
  ImageResource viewButtonImage;
  @UiField
  ImageResource removeButtonImage;

  // TODO(tpondich): Push images into UIBinder by improving
  // the constructor for GridList
  @UiField
  ImageResource linkButtonImage;
  @UiField
  ImageResource htmlButtonImage;
  @UiField
  ImageResource pdfButtonImage;
  @UiField
  ImageResource pngButtonImage;
  @UiField
  ImageResource svgButtonImage;
  @UiField
  ImageResource epsButtonImage;

  @UiField
  ImageResource publicButtonImage;
  @UiField
  ImageResource privateButtonImage;

  @UiField
  ImageResource fileButtonImage;
  @UiField
  ImageResource versionButtonImage;

  @UiField
  GridList<String> circuitEmbedTypes;
  @UiField
  GridList<String> simulateEmbedTypes;
  @UiField
  GridList<String> embedTargets;

  @UiField
  TextBox embedCode;

  @UiField
  Label warningLabel;

  private List<EmailAddress> collaborators;
  private List<GridList<FileRole>> permissions;

  // For creating links.
  private FileKey fileKey;
  private VersionMetadataKey versionKey;
  private boolean isSimulate;

  private static ShareChooserPanelUiBinder uiBinder = GWT.create(ShareChooserPanelUiBinder.class);

  interface ShareChooserPanelUiBinder extends UiBinder<Widget, ShareChooser> {}

  public ShareChooser() {
    initWidget(uiBinder.createAndBindUi(this));

    // TODO(tpondich): Make own widgets for this.
    addEmail.getElement().setAttribute("placeholder", "Enter new collaborator's email address...");

    // TODO(tpondich): Push this into UIBinder.
    publicOptions.add(new Image(privateButtonImage), new Image(privateButtonImage), "Private",
        "Private: Only collaborators can view", false);
    publicOptions.add(new Image(publicButtonImage), new Image(publicButtonImage), "Public",
        "Public: Anyone with link can view", true);

    circuitEmbedTypes.add(new Image(linkButtonImage), new Image(linkButtonImage),
        "Link: URL to directly access this circuit", "link");
    circuitEmbedTypes.add(new Image(htmlButtonImage), new Image(htmlButtonImage),
        "Embed: Code to embed StratoSim widget", "embed");

    circuitEmbedTypes.add(new Image(pdfButtonImage), new Image(pdfButtonImage),
        "PDF: Link to PDF of schematic", DownloadFormat.PDF.getFormat());
    circuitEmbedTypes.add(new Image(pngButtonImage), new Image(pngButtonImage),
        "PNG: Link to PNG of schematic", DownloadFormat.PNG.getFormat());
    circuitEmbedTypes.add(new Image(svgButtonImage), new Image(pngButtonImage),
        "SVG: Link to SVG of schematic", DownloadFormat.SVG.getFormat());
    circuitEmbedTypes.add(new Image(epsButtonImage), new Image(epsButtonImage),
        "EPS: Link to EPS of schematic", DownloadFormat.PS.getFormat());

    simulateEmbedTypes.add(new Image(linkButtonImage), new Image(linkButtonImage),
        "Link: URL to directly access this simulation", "simulatelink");
    simulateEmbedTypes.add(new Image(htmlButtonImage), new Image(htmlButtonImage),
        "Embed: Code to embed StratoSim widget", "simulateembed");

    simulateEmbedTypes.add(new Image(pdfButtonImage), new Image(pdfButtonImage),
        "PDF: Link to PDF of simulation", DownloadFormat.SIMULATIONPDF.getFormat());
    simulateEmbedTypes.add(new Image(pngButtonImage), new Image(pngButtonImage),
        "PNG: Link to PNG of simulation", DownloadFormat.SIMULATIONPNG.getFormat());
    // simulateEmbedTypes.add(new Image(svgButtonImage), new Image(pngButtonImage),
    // "SVG: Link to SVG of simulation", DownloadFormat.SIMULATIONSVG.getFormat());
    simulateEmbedTypes.add(new Image(epsButtonImage), new Image(epsButtonImage),
        "EPS: Link to EPS of simulation", DownloadFormat.SIMULATIONPS.getFormat());

    embedTargets.add(new Image(fileButtonImage), new Image(fileButtonImage),
        "File: Always link to latest version of file", "file");
    embedTargets.add(new Image(versionButtonImage), new Image(versionButtonImage),
        "Version: Link to this specific version of file", "version");

    collaborators = Lists.newArrayList();
    permissions = Lists.newArrayList();
  }

  @UiHandler("doneButton")
  public void onDoneButtonClick(ClickEvent event) {
    fireValueChangeEvent(getFileVisibility());
  }

  @UiHandler("addEmail")
  public void onChange(ChangeEvent event) {
    setWarningsBasedOnUI();
  }

  @UiHandler("addEmail")
  public void onKeyUp(KeyUpEvent event) {
    setWarningsBasedOnUI();
    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      if (addButton.isEnabled()) {
        maybeAddNew();
      }
    }
  }

  @UiHandler("addButton")
  public void onAddButtonClick(ClickEvent event) {
    maybeAddNew();
  }

  private void maybeAddNew() {
    if (!EmailAddress.isValid(addEmail.getText())) {
      return;
    }
    EmailAddress emailAddress = new EmailAddress(addEmail.getText());
    if (collaborators.contains(emailAddress)) {
      return;
    }

    addEmail.setText("");

    addItem(emailAddress, FileRole.READER);
    setWarningsBasedOnUI();

    addEmail.setFocus(true);
    addButton.setEnabled(false);
  }

  private void setWarningsBasedOnUI() {
    // Add button enabled? Is the email valid?
    boolean alreadyExistingCollaboratorWarning = false;
    boolean collaboratorTextInvalidWarning = false;
    addButton.setEnabled(false);
    if (EmailAddress.isValid(addEmail.getText())) {
      if (!collaborators.contains(new EmailAddress(addEmail.getText()))) {
        addButton.setEnabled(true);
      } else {
        alreadyExistingCollaboratorWarning = true;
      }
    } else if (!addEmail.getText().equals("")) {
      collaboratorTextInvalidWarning = true;
    }

    boolean collaboratorTextNotBlankWarning = !addEmail.getText().equals("");

    boolean doneEnabled = true;

    // TODO(tpondich): No strings in code!
    if (alreadyExistingCollaboratorWarning) {
      warningLabel.setText("The new collaborator already has permissions for this file.");
      doneEnabled = false;
    } else if (collaboratorTextInvalidWarning) {
      warningLabel
          .setText("The new collaborator's email is invalid. Please correct it and add the collaborator or clear the email before clicking Done.");
      doneEnabled = false;
    } else if (collaboratorTextNotBlankWarning) {
      warningLabel
          .setText("The new collaborator hasn't been added. Please add the collaborator or clear the email before clicking Done.");
      doneEnabled = false;
    } else {
      warningLabel.setText("");
    }

    doneButton.setEnabled(doneEnabled);
  }

  private void setLinkBasedOnUI() {
    // Don't show the sharing links if the circuit is private.
    if (publicOptions.getSelectedItem()) {
      circuitEmbedTypes.setVisible(!isSimulate);
      simulateEmbedTypes.setVisible(isSimulate);
    } else {
      circuitEmbedTypes.setVisible(false);
      simulateEmbedTypes.setVisible(false);
      circuitEmbedTypes.setSelected("link", false);
      simulateEmbedTypes.setSelected("simulatelink", false);
    }

    // TODO(tpondich): Ideally it's own widget.
    boolean isLink = !isSimulate && circuitEmbedTypes.getSelectedItem().equals("link");
    boolean isSimulateLink =
        isSimulate && simulateEmbedTypes.getSelectedItem().equals("simulatelink");
    boolean isEmbed = !isSimulate && circuitEmbedTypes.getSelectedItem().equals("embed");
    boolean isSimulateEmbed =
        isSimulate && circuitEmbedTypes.getSelectedItem().equals("simulateembed");

    StratoSimKey key = embedTargets.getSelectedItem().equals("file") ? fileKey : versionKey;

    String code = null;

    if (isLink) {
      code = getServingBase() + getCircuitLinkUrl(key);
    } else if (isSimulateLink) {
      code = getServingBase() + getSimulateLinkUrl(key);
    } else if (isEmbed) {
      String embedURL = getServingBase() + getEmbedServiceUrl(key) + "?circuitHeight=400";
      code = "<iframe width=\"100%\" height=\"440px\" src=\"" + embedURL + "\"></iframe>";
    } else if (isSimulateEmbed) {
      String embedURL = getServingBase() + getEmbedServiceUrl(key) + "?circuitHeight=400";
      code = "<iframe width=\"100%\" height=\"440px\" src=\"" + embedURL + "\"></iframe>";
    } else {
      String format =
          isSimulate ? simulateEmbedTypes.getSelectedItem() : circuitEmbedTypes.getSelectedItem();

      code = getServingBase() + getCircuitDownloadServiceUrl(key, DownloadFormat.from(format));
    }

    embedCode.setText(code);
  }

  @UiHandler("circuitEmbedTypes")
  void onCircuitEmbedTypesChange(ChangeEvent event) {
    setLinkBasedOnUI();
  }

  @UiHandler("simulateEmbedTypes")
  void onSimulateEmbedTypesChange(ChangeEvent event) {
    setLinkBasedOnUI();
  }

  @UiHandler("embedTargets")
  void onEmbedTargetChange(ChangeEvent event) {
    setLinkBasedOnUI();
  }

  @UiHandler("publicOptions")
  void onPublicOptionsChange(ChangeEvent event) {
    setLinkBasedOnUI();
  }

  @UiHandler("embedCode")
  void onEmbedCodeClick(ClickEvent event) {
    embedCode.selectAll();
  }

  private ChangeHandler permissionsChangeHandler = new ChangeHandler() {
    @Override
    public void onChange(ChangeEvent event) {
      setWarningsBasedOnUI();
    }
  };

  // TODO(tpondich): Ideally it's own widget.
  private GridList<FileRole> createPermissionsGridList() {
    GridList<FileRole> permissionsGridList = new GridList<FileRole>(4);
    permissionsGridList.add(new Image(ownButtonImage), new Image(ownButtonImage),
        "Own: Collaborator can edit sharing permissions and delete", FileRole.OWNER);
    permissionsGridList.add(new Image(editButtonImage), new Image(editButtonImage),
        "Edit: Collaborator can edit the file", FileRole.WRITER);
    permissionsGridList.add(new Image(viewButtonImage), new Image(viewButtonImage),
        "View: Collaborator can view the file", FileRole.READER);
    permissionsGridList.add(new Image(removeButtonImage), new Image(removeButtonImage),
        "Remove: Remove all permissions for this collaborator", FileRole.NONE);
    permissionsGridList.setStylePrimaryName("stratosim-ShareChooser-OptionList");
    permissionsGridList.addChangeHandler(permissionsChangeHandler);
    return permissionsGridList;
  }

  private void addItem(EmailAddress email, FileRole role) {
    TextBox label = new TextBox();
    label.setValue(email.getEmail());
    label.setReadOnly(true);
    label.addStyleName("stratosim-ShareChooser-TextBox");
    label.addStyleName("stratosim-ShareChooser-TextBox-readonly");
    collaborators.add(email);

    GridList<FileRole> permissionsList = createPermissionsGridList();
    permissions.add(permissionsList);
    permissionsList.setSelected(role);

    grid.resize(grid.getRowCount() + 1, 2);
    // Move the add new text box down.
    grid.setWidget(grid.getRowCount() - 1, 0, grid.getWidget(grid.getRowCount() - 2, 0));
    grid.setWidget(grid.getRowCount() - 1, 1, grid.getWidget(grid.getRowCount() - 2, 1));

    // Add the new entry.
    grid.setWidget(grid.getRowCount() - 2, 0, label);
    grid.setWidget(grid.getRowCount() - 2, 1, permissionsList);
  }

  public void setFileVisiblity(FileVisibility fileVisibility, boolean isSimulate, FileKey fileKey,
      VersionMetadataKey versionKey) {
    clear();

    this.fileKey = checkNotNull(fileKey);
    this.versionKey = checkNotNull(versionKey);

    this.isSimulate = isSimulate;

    for (Map.Entry<EmailAddress, FileRole> entry : fileVisibility.getPermissions().entrySet()) {
      addItem(entry.getKey(), entry.getValue());
    }

    publicOptions.setSelected(fileVisibility.isPublic());

    setLinkBasedOnUI();
  }

  public FileVisibility getFileVisibility() {
    ImmutableMap.Builder<EmailAddress, FileRole> permissionsMap = ImmutableMap.builder();
    for (int i = 0; i < collaborators.size(); i++) {
      permissionsMap.put(collaborators.get(i), permissions.get(i).getSelectedItem());
    }

    boolean isPublic = publicOptions.getSelectedItem();

    return new FileVisibility(permissionsMap.build(), isPublic);
  }

  public void clear() {
    // Maintain the textbox and headings while clearing.
    grid.setWidget(1, 0, grid.getWidget(grid.getRowCount() - 1, 0));
    grid.setWidget(1, 1, grid.getWidget(grid.getRowCount() - 1, 1));
    grid.resize(2, 2);

    collaborators.clear();
    permissions.clear();

    addEmail.setText("");

    embedCode.setText("");

    warningLabel.setText("");

    fileKey = null;
    versionKey = null;
  }

  private void fireValueChangeEvent(FileVisibility value) {
    ValueChangeEvent.fire(this, value);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FileVisibility> handler) {
    return super.addHandler(handler, ValueChangeEvent.getType());
  }
}
