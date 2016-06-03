CreateProgramPage = ClassUtils.defineClass(AbstractDataPage, function CreateProgramPage() {
  AbstractDataPage.call(this, CreateProgramPage.name);
  
  this._deviceId;
  
  this._commandList;
  this._removeCommandButton;
  this._saveButton;
  this._programPlayButton;
  this._addCommandButton;
});

CreateProgramPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var amimationPanel = UIUtils.appendBlock(contentPanel, "AnimationPanel");
  
  var programPanel = UIUtils.appendBlock(contentPanel, "ProgramPanel");
  var headerProgramPanel = UIUtils.appendBlock(contentPanel, "ProgramHeaderPanel");
  UIUtils.appendLabel(headerProgramPanel, "ProgramLabel", this.getLocale().ProgramLabel);
  var programNameInput = UIUtils.appendTextInput(headerProgramPanel, "ProgramNameInput");
  var programPlaybackPanel = UIUtils.appendBlock(headerProgramPanel, "ProgramPlaybackPanel");
  this._programPlayButton = UIUtils.appendButton(headerProgramPanel, "PlayButton", this.getLocale().PlayButton);
  
  this._commandList = UIUtils.appendList(programPanel, "ProgramCommandList");

  var footerProgramPanel = UIUtils.appendBlock(contentPanel, "ProgramFooterPanel");
  this._removeCommandButton = UIUtils.appendButton(footerProgramPanel, "RemoveCommandButton", this.getLocale().RemoveCommandButton);
  this._addCommandButton = UIUtils.appendExpandableButton(footerProgramPanel, "AddCommandButton", this.getLocale().AddCommandButton);
  
  this._commandList.setSelectionListener(function(selectedItem) {
    descriptionText.innerHTML = selectedItem != null ? selectedItem.element._program.description : "";
  });
  this._commandList.setOrderListener(function(items) {
  });
  
  
  UIUtils.appendLabel(contentPanel, "DescriptionLabel", this.getLocale().DescriptionLabel);
  var descriptionInput = UIUtils.appendTextInput(contentPanel, "DescriptionInput");
  
  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().literals.CancelOperationButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._saveButton = UIUtils.appendButton(buttonsPanel, "SaveButton", this.getLocale().SaveButton);
  this._saveButton.setClickListener(function() {
    
  });
}

CreateProgramPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  
  var clickListener = function(command) {
    console.debug("Command: " + command);
  }
  
  var commands = Backend.getSupportedCommands(Backend.getDeviceInfo(this._deviceId));
  var actions = [];
  for (var i in commands) {
    var command = commands[i];
    
    var action = {display: command.display, clickListener: clickListener.bind(this, command.data)};
    actions.push(action);
  }
  this._addCommandButton.setExpandableActions(actions);
  
  this._commandList.clear();
  
  UIUtils.setEnabled(this._removeCommandButton, false);
  UIUtils.setEnabled(this._saveButton, false);
  UIUtils.setEnabled(this._programPlayButton, false);
}

CreateProgramPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
}


CreateProgramPage.prototype._addCommandToList = function(program) {
  var programItem = document.createElement("div");
  this._programList.addItem({element: programItem});
  
  programItem._program = program;
  program._item = programItem;
  
  UIUtils.addClass(programItem, "program-item notselectable");

  var selectionBox = UIUtils.appendCheckbox(programItem, "Selection");
  UIUtils.addClass(selectionBox, "program-selection");
  programItem._selectionBox = selectionBox;
  selectionBox.setChangeListener(function() {
    var selectionLength = this._getSelectedPrograms().length;
    
    UIUtils.setEnabled(this._removeSelectedButton, selectionLength > 0);
    UIUtils.setEnabled(this._loadSelectedButton, selectionLength > 0);
    UIUtils.setEnabled(this._uploadSelectedButton, selectionLength == 1);
  }.bind(this));
  
  var itemTitle = UIUtils.appendLabel(programItem, "Title", program.title);
  UIUtils.addClass(itemTitle, "program-title");
}

CreateProgramPage.prototype._getSelectedCommand = function() {
  var selectedPrograms = [];

  var programItems = this._programList.getItems();
  for (var i = 0; i < programItems.length; i++) {
    if (programItems[i].element._selectionBox.isChecked()) {
      selectedPrograms.push(programItems[i].element._program);
    }
  }
  
  return selectedPrograms;
}
