DeviceManagementPage = ClassUtils.defineClass(AbstractDataPage, function DeviceManagementPage() {
  AbstractDataPage.call(this, DeviceManagementPage.name);
  
  this._deviceId;
  
  this._programList;
  this._programItems;
  this._removeSelectedButton;
  
  this._cacheChangeListener = function(event) {
    if (event.type == Backend.CacheChangeEvent.TYPE_DEVICE_PROGRAMS && this._deviceId == event.objectId) {
      this._refreshProgramList();
    }
  }.bind(this);
});

DeviceManagementPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var toolbarPanel = UIUtils.appendBlock(contentPanel, "ToolbarPanel");
  var manualModeButton = UIUtils.appendButton(toolbarPanel, "ManualModeButton", this.getLocale().ManualModeButton);
  manualModeButton.setClickListener(function() {
    Application.showPage(ManualModePage.name, {deviceId: this._deviceId});
  }.bind(this));

  var deviceSettingsButton = UIUtils.appendButton(toolbarPanel, "DeviceSettingsButton", this.getLocale().DeviceSettingsButton);
  deviceSettingsButton.setClickListener(function() {
    Application.showPage(DeviceSettingsPage.name, {deviceId: this._deviceId});
  }.bind(this));
  
  var programSelectionPanel = UIUtils.appendBlock(contentPanel, "ProgramSelectionPanel");
  UIUtils.appendLabel(programSelectionPanel, "ProgramSelectionLabel", this.getLocale().ProgramSelectionLabel);
  
  this._programList = UIUtils.appendBlock(programSelectionPanel, "ProgramSelectionList");

  var buttonsPanel = UIUtils.appendBlock(programSelectionPanel, "ProgramButtonsPanel");
  this._removeSelectedButton = UIUtils.appendButton(buttonsPanel, "RemoveSelectedButton", this.getLocale().RemoveSelectedButton);
  this._removeSelectedButton.setClickListener(function() {
  }.bind(this));

  var addProgramButton = UIUtils.appendButton(buttonsPanel, "AddProgramButton", this.getLocale().AddProgramButton);
  this._removeSelectedButton.setClickListener(function() {
  }.bind(this));
  
}

DeviceManagementPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  
  var programs = Backend.getPrograms(this._deviceId);
  if (programs == null) {
    this._programList.innerHTML = this.getLocale().UpdatingListOfProgramsLabel;
  } else if (programs.length == 0) {
    this._programList.innerHTML = this.getLocale().NoProgramsAvailableLabel;
  } else {
    this._refreshProgramList(programs);
  }
  
  UIUtils.setEnabled(this._removeSelectedButton, false);
  Backend.addCacheChangeListener(this._cacheChangeListener);
}

DeviceManagementPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  Backend.removeCacheChangeListener(this._cacheChangeListener);
}

DeviceManagementPage.prototype._refreshProgramList = function() {
  UIUtils.emptyContainer(this._programList);
  UIUtils.setEnabled(this._removeSelectedButton, false);
  
  var programs = Backend.getPrograms(this._deviceId);
  if (programs == null) {
    return;
  }
  
  this._programItems = [];
  for (var i = 0; i < programs.length; i++) {
    this._addProgramToList(programs[i]);
  }
}

DeviceManagementPage.prototype._addProgramToList = function(program) {
  var programItem = UIUtils.appendBlock(this._programList, program.id);
  programItem._program = program;
  
  this._programItems.push(programItem);
  
  UIUtils.addClass(programItem, "program-item notselectable");

  var selectionBox = UIUtils.appendCheckbox(programItem, "Selection");
  UIUtils.addClass(selectionBox, "program-selection");
  programItem._selectionBox = selectionBox;
  selectionBox.setChangeListener(function() {
    UIUtils.setEnabled(this._removeSelectedButton, this._getSelectedPrograms().length > 0);
  }.bind(this));
                       
  var itemTitle = UIUtils.appendLabel(programItem, "Title", program.title);
  UIUtils.addClass(itemTitle, "program-title");
}

DeviceManagementPage.prototype._getSelectedPrograms = function() {
  var selectedPrograms = [];

  for (var i = 0; i < this._programItems.length; i++) {
    if (this._programItems[i]._selectionBox.isChecked()) {
      selectedPrograms.push(this._programItems[i]._program);
    }
  }
  
  return selectedPrograms;
}
