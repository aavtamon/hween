DeviceManagementPage = ClassUtils.defineClass(AbstractDataPage, function DeviceManagementPage() {
  AbstractDataPage.call(this, DeviceManagementPage.name);
  
  this._deviceId;
  
  this._programList;
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
  
  this._programList = UIUtils.appendList(programSelectionPanel, "ProgramSelectionList", null, true);
  this._programList.setSelectionListener(function(selectedItem) {
    if (selectedItem != null) {
      selectedItem.element._selectionBox.setChecked(!selectedItem.element._selectionBox.isChecked());
    }
  });
  this._programList.setOrderListener(function(items) {
    var programs = [];
    var items = this._programList.getItems();
    for (var i in items) {
      programs.push(items[i].element._program);
    }

    Backend.setPrograms(this._deviceId, programs);
  }.bind(this));
  

  var programButtonsPanel = UIUtils.appendBlock(programSelectionPanel, "ProgramButtonsPanel");
  this._removeSelectedButton = UIUtils.appendButton(programButtonsPanel, "RemoveSelectedButton", this.getLocale().RemoveSelectedButton);
  this._removeSelectedButton.setClickListener(function() {
    var items = this._programList.getItems();
    for (var i in items) {
      var item = items[i];
      if (item.element._selectionBox.isChecked()) {
        this._programList.removeItem(item);
        Backend.removePrograms(this._deviceId, item._program);
      }
    }
  }.bind(this));

  var addProgramButton = UIUtils.appendExpandableButton(programButtonsPanel, "AddProgramButton", this.getLocale().AddProgramButton, [
    {display: this.getLocale().AddNewProgramButton, clickListener: function() {
      Application.showPage(CreateProgramPage.name, {deviceId: this._deviceId});
    }.bind(this)},
    {display: this.getLocale().AddLibraryProgramButton, clickListener: function() {
      Application.showPage(ManageLibraryProgramsPage.name, {deviceId: this._deviceId});
    }.bind(this)},
    {display: this.getLocale().AddStockProgramButton, clickListener: function() {
      Application.showPage(StockProgramsPage.name, {deviceId: this._deviceId});
    }.bind(this)}
  ]);
  
  var manageProgramsButton = UIUtils.appendButton(programButtonsPanel, "ManageProgramsButton", this.getLocale().ManageProgramsButton);
  manageProgramsButton.setClickListener(function() {
    Application.showPage(ManageLibraryProgramsPage.name, {deviceId: this._deviceId});
  }.bind(this));
  
  
  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", this.getLocale().BackButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  var sendToDeviceButton = UIUtils.appendButton(buttonsPanel, "SendToDeviceButton", this.getLocale().SendToDeviceButton);
  sendToDeviceButton.setClickListener(function() {
    Controller.reportToServer(Backend.getDeviceInfo(this._deviceId));
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
  this._programList.clear();
  UIUtils.setEnabled(this._removeSelectedButton, false);

  var programs = Backend.getPrograms(this._deviceId);
  if (programs == null) {
    return;
  }
  
  for (var i = 0; i < programs.length; i++) {
    this._addProgramToList(programs[i]);
  }
}

DeviceManagementPage.prototype._addProgramToList = function(program) {
  var programItem = document.createElement("div");
  this._programList.addItem({element: programItem});
  
  programItem._program = program;
  
  UIUtils.addClass(programItem, "program-item notselectable");

  var selectionBox = UIUtils.appendCheckbox(programItem, "Selection");
  UIUtils.addClass(selectionBox, "program-selection");
  programItem._selectionBox = selectionBox;
  selectionBox.setChangeListener(function() {
    UIUtils.setEnabled(this._removeSelectedButton, this._getSelectedPrograms().length > 0);
  }.bind(this));
  
  var itemTitle = UIUtils.appendLabel(programItem, "Title", program.title);
  UIUtils.addClass(itemTitle, "program-title");

  var freqChooser = UIUtils.appendDropList(programItem, "FrequencyChooser", Application.Configuration.PROGRAM_FREQUENCIES);
  freqChooser.selectData(program.frequency);
  UIUtils.addClass(freqChooser, "program-frequency");
  freqChooser.setChangeListener(function() {
    program.frequency = freqChooser.getValue();
    Backend.updateProgram(this._deviceId, program);
  });
}

DeviceManagementPage.prototype._getSelectedPrograms = function() {
  var selectedPrograms = [];

  var programItems = this._programList.getItems();
  for (var i = 0; i < programItems.length; i++) {
    if (programItems[i].element._selectionBox.isChecked()) {
      selectedPrograms.push(programItems[i].element._program);
    }
  }
  
  return selectedPrograms;
}

