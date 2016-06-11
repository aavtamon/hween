DeviceManagementPage = ClassUtils.defineClass(AbstractDataPage, function DeviceManagementPage() {
  AbstractDataPage.call(this, DeviceManagementPage.name);
  
  this._deviceId;
  this._deviceType;
  
  this._programList;
  this._removeSelectedButton;
  this._triggerList;
  this._runButton;
  this._stopButton;
  
  this._cacheChangeListener = function(event) {
    if (this._deviceId != event.objectId) {
      return;
    }
    
    if (event.type == Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE) {
      this._refreshProgramList();
    } else if (event.type == Backend.CacheChangeEvent.TYPE_DEVICE_MODE) {
      this._updateModeButtons();
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
  var scheduleControlPanel = UIUtils.appendBlock(programSelectionPanel, "ScheduleControlPanel");
  UIUtils.appendLabel(scheduleControlPanel, "ProgramSelectionLabel", this.getLocale().ProgramSelectionLabel);
  this._triggerList = UIUtils.appendDropList(scheduleControlPanel, "ProgramSelectionLabel");
  
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

    var schedule = Backend.getDeviceSchedule(this._deviceId);
    schedule.programs = programs;
    Backend.setDeviceSchedule(this._deviceId, schedule);
  }.bind(this));
  

  var programButtonsPanel = UIUtils.appendBlock(programSelectionPanel, "ProgramButtonsPanel");
  this._removeSelectedButton = UIUtils.appendButton(programButtonsPanel, "RemoveSelectedButton", this.getLocale().RemoveSelectedButton);
  this._removeSelectedButton.setClickListener(function() {
    var items = this._programList.getItems();
    for (var i in items) {
      var item = items[i];
      if (item.element._selectionBox.isChecked()) {
        this._programList.removeItem(item);
        Backend.removeDevicePrograms(this._deviceId, item._program);
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
  
  this._runButton = UIUtils.appendButton(buttonsPanel, "RunButton", this.getLocale().RunButton);
  this._runButton.setClickListener(function() {
    Backend.setDeviceMode(this._deviceId, Backend.DeviceMode.RUNNING_SCHEDULE);
    Controller.reportToServer(Backend.getDeviceInfo(this._deviceId));
  }.bind(this));
  this._stopButton = UIUtils.appendButton(buttonsPanel, "StopButton", this.getLocale().StopButton);
  this._stopButton.setClickListener(function() {
    Backend.setDeviceMode(this._deviceId, Backend.DeviceMode.IDLE);
    Controller.reportToServer(Backend.getDeviceInfo(this._deviceId));
  }.bind(this));
}

DeviceManagementPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  this._deviceType = Backend.getDeviceInfo(this._deviceId).type;
  
  var schedule = Backend.getDeviceSchedule(this._deviceId);
  if (schedule == null) {
    this._programList.innerHTML = this.getLocale().UpdatingListOfProgramsLabel;
  } else if (schedule.programs.length == 0) {
    this._programList.innerHTML = this.getLocale().NoProgramsAvailableLabel;
  } else {
    this._refreshProgramList(schedule.programs);
  }
  
  this._triggerList.setItems(Backend.getSupportedProgramTriggers(this._deviceType));
  
  UIUtils.setEnabled(this._removeSelectedButton, false);
  
  this._updateModeButtons();
  
  Backend.addCacheChangeListener(this._cacheChangeListener);
}

DeviceManagementPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  Backend.removeCacheChangeListener(this._cacheChangeListener);
}

DeviceManagementPage.prototype._refreshProgramList = function() {
  this._programList.clear();
  UIUtils.setEnabled(this._removeSelectedButton, false);

  var schedule = Backend.getDeviceSchedule(this._deviceId);
  if (schedule == null) {
    return;
  }

  for (var i = 0; i < schedule.programs.length; i++) {
    this._addProgramToList(schedule.programs[i]);
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
    Backend.updateDeviceProgram(this._deviceId, program);
  }.bind(this));
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


DeviceManagementPage.prototype._updateModeButtons = function() {
  var mode = Backend.getDeviceMode(this._deviceId);
  
  if (mode == Backend.DeviceMode.RUNNING_SCHEDULE) {
    UIUtils.setEnabled(this._runButton, false);
    UIUtils.setEnabled(this._stopButton, true);
  } else {
    UIUtils.setEnabled(this._runButton, true);
    UIUtils.setEnabled(this._stopButton, false);
  }
}

