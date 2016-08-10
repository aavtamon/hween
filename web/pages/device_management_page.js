DeviceManagementPage = ClassUtils.defineClass(AbstractDataPage, function DeviceManagementPage() {
  AbstractDataPage.call(this, DeviceManagementPage.name);
  
  this._deviceId;
  this._deviceType;
  
  this._programList;
  this._removeSelectedButton;
  this._triggerList;
  this._runButton;
  this._stopButton;
  this._scheduleStatusPanel;
  this._manualModeButton;
  
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
  this._manualModeButton = UIUtils.appendButton(toolbarPanel, "ManualModeButton", this.getLocale().ManualModeButton);
  this._manualModeButton.setClickListener(function() {
    Application.showPage(ManualModePage.name, {deviceId: this._deviceId});
  }.bind(this));

  var deviceSettingsButton = UIUtils.appendButton(toolbarPanel, "DeviceSettingsButton", this.getLocale().DeviceSettingsButton);
  deviceSettingsButton.setClickListener(function() {
    Application.showPage(DeviceSettingsPage.name, {deviceId: this._deviceId});
  }.bind(this));
  
  var programSelectionPanel = UIUtils.appendBlock(contentPanel, "ProgramSelectionPanel");
  var scheduleControlPanel = UIUtils.appendBlock(programSelectionPanel, "ScheduleControlPanel");
  UIUtils.appendLabel(scheduleControlPanel, "ProgramSelectionLabel", this.getLocale().ProgramSelectionLabel);
  this._triggerList = UIUtils.appendDropList(scheduleControlPanel, "TriggersList");
  this._triggerList.setChangeListener(function() {
    var schedule = Backend.getDeviceSchedule(this._deviceId);
    schedule.trigger = this._triggerList.getValue();
    Backend.setDeviceSchedule(this._deviceId, schedule);
  }.bind(this));
  
  this._scheduleStatusPanel = UIUtils.appendBlock(scheduleControlPanel, "ScheduleStatusPanel");
  
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
    var programsToRemove  = [];
    for (var i in items) {
      var item = items[i];
      if (item.element._selectionBox.isChecked()) {
        this._programList.removeItem(item);
        programsToRemove.push(item.element._program);
      }
    }

    Backend.removeDevicePrograms(this._deviceId, programsToRemove);
  }.bind(this));

  var addProgramButton = UIUtils.appendExpandableButton(programButtonsPanel, "AddProgramButton", this.getLocale().AddProgramButton, [
    {display: this.getLocale().AddNewProgramButton, clickListener: function() {
      Application.showPage(CreateProgramPage.name, {deviceId: this._deviceId, addToDevice: true});
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
  
  var deviceInfo = Backend.getDeviceInfo(this._deviceId);
  this._deviceType = deviceInfo.type;
  
  this._manualModeButton.setEnabled(false);
  Controller.isAvailable(deviceInfo, function(isEnabled) {
    this._manualModeButton.setEnabled(isEnabled);
  }.bind(this));
  
  var schedule = Backend.getDeviceSchedule(this._deviceId);
  if (schedule == null) {
    this._programList.innerHTML = this.getLocale().UpdatingListOfProgramsLabel;
  } else if (schedule.programs.length == 0) {
    this._programList.innerHTML = this.getLocale().NoProgramsAvailableLabel;
  } else {
    this._refreshProgramList(schedule.programs);
  }
  
  Backend.getDeviceSettings(this._deviceType, function(status, deviceSettings) {
    if (status == Backend.OperationResult.SUCCESS) {
      this._triggerList.setChoices(deviceSettings.supportedProgramTriggers);
      if (schedule != null) {
        this._triggerList.selectData(schedule.trigger);
      }
    }
  }.bind(this));
  
  UIUtils.setEnabled(this._removeSelectedButton, false);
  
  this._updateModeButtons();
  
  Backend.addCacheChangeListener(this._cacheChangeListener);
}

DeviceManagementPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  Backend.removeCacheChangeListener(this._cacheChangeListener);
}

DeviceManagementPage.prototype._refreshProgramList = function() {
  var selectedItem = this._programList.getSelectedItem();
  this._programList.clear();
  UIUtils.setEnabled(this._removeSelectedButton, false);

  var schedule = Backend.getDeviceSchedule(this._deviceId);
  if (schedule == null) {
    return;
  }

  for (var i = 0; i < schedule.programs.length; i++) {
    this._addProgramToList(schedule.programs[i]);
  }
  
  this._programList.setSelectedItem(selectedItem);
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

    this._scheduleStatusPanel.innerHTML = this.getLocale().ScheduleStatusRunning;
  } else {
    UIUtils.setEnabled(this._runButton, true);
    UIUtils.setEnabled(this._stopButton, false);

    if (mode == Backend.DeviceMode.IDLE) {
      this._scheduleStatusPanel.innerHTML = this.getLocale().ScheduleStatusIdle;
    } else if (mode == Backend.DeviceMode.MANUAL) {
      this._scheduleStatusPanel.innerHTML = this.getLocale().ScheduleStatusManual;
    }
  }
}

