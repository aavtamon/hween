ManualModePage = ClassUtils.defineClass(AbstractDataPage, function ManualModePage() {
  AbstractDataPage.call(this, ManualModePage.name);
  
  this._deviceId;
  this._deviceInfo;
  
  this._deviceStatusLabel;
  this._resetButton;
  this._moveUpButton;
  this._moveDownButton;
  this._turnLeftButton;
  this._turnRghtButton;
  this._eyeControlButton;
  this._talkButton;
});

ManualModePage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  this._deviceStatusLabel = UIUtils.appendLabel(contentPanel, "CommandsLabel");
  
  var commandsPanel = UIUtils.appendBlock(contentPanel, "CommandsPanel");
  
  this._moveUpButton = UIUtils.appendButton(commandsPanel, "MoveUpButton", this.getLocale().MoveUpButton);
  this._moveUpButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, {data: Backend.DeviceCommand.MOVE_UP}, function() {
      this._enableActions();
    }.bind(this));
  }.bind(this));

  this._moveDownButton = UIUtils.appendButton(commandsPanel, "MoveDownButton", this.getLocale().MoveDownButton);
  this._moveDownButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, {data: Backend.DeviceCommand.MOVE_DOWN}, function() {
      this._enableActions();
    }.bind(this));
  }.bind(this));

  
  this._turnLeftButton = UIUtils.appendButton(commandsPanel, "TurnLeftButton", this.getLocale().TurnLeftButton);
  this._turnLeftButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, {data: Backend.DeviceCommand.TURN_LEFT}, function() {
      this._enableActions();
    }.bind(this));
  }.bind(this));
  
  this._turnRightButton = UIUtils.appendButton(commandsPanel, "TurnRightButton", this.getLocale().TurnRightButton);
  this._turnRightButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, {data: Backend.DeviceCommand.TURN_RIGHT}, function() {
      this._enableActions();
    }.bind(this));
  }.bind(this));

  
  this._eyeControlButton = UIUtils.appendToggleButton(commandsPanel, "EyeControlButton", this.getLocale().EyeControlButtonOn, this.getLocale().EyeControlButtonOff);
  this._eyeControlButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, {data: Backend.DeviceCommand.EYES_ON}, function() {
      this._enableActions();
      this._eyeControlButton.setSelected(!this._eyeControlButton.isSelected());
    }.bind(this));
  }.bind(this));
  
  
  this._talkButton = UIUtils.appendButton(commandsPanel, "TalkButton", this.getLocale().TalkButton);
  this._talkButton.setClickListener(function() {
    var fileChooser = UIUtils.appendFileChooser(commandsPanel);
    
    fileChooser.open(function(files) {
      UIUtils.remove(fileChooser);
      
      if (files == null || files.lenght == 0) {
        return;
      }
      var selectedFile = files[0];

      if (FileUtils.isAudio(selectedFile)) {
        FileUtils.loadFile(selectedFile, function(file, dataUrl) {
          this._disableActions();
          Controller.sendCommand(this._deviceInfo, {data: Backend.DeviceCommand.TALK, arg: dataUrl}, function() {
            this._enableActions();
          }.bind(this));
        }.bind(this));
      } else {
        UIUtils.showMessage(I18n.getLocale().IncorrectAudioFileMessage);
      }
    }.bind(this));
  }.bind(this));

  
  
  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", this.getLocale().BackButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._resetButton = UIUtils.appendButton(buttonsPanel, "ResetButton", this.getLocale().ResetToInitialPositionButton);
  this._resetButton.setClickListener(function() {
    this._disableActions();
    Controller.reset(this._deviceInfo, function() {
      this._enableActions();
    }.bind(this));
  }.bind(this));
}

ManualModePage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  this._deviceInfo = Backend.getDeviceInfo(this._deviceId);
  
  Backend.getDeviceSettings(this._deviceInfo.type, function(status, deviceSettings) {
    if (status == Backend.OperationResult.SUCCESS) {
      this._enableActions(deviceSettings.supportedCommands);
    }
  }.bind(this));
}

ManualModePage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
}


ManualModePage.prototype._enableActions = function(deviceCommands) {
  this._deviceStatusLabel.innerHTML = this.getLocale().DeviceReadyForCommandLabel;
  
  UIUtils.setEnabled(this._resetButton, true);
  UIUtils.setEnabled(this._moveUpButton, Application.Configuration.findConfigurationItem(deviceCommands, Backend.DeviceCommand.MOVE_UP) != null);
  UIUtils.setEnabled(this._moveDownButton, Application.Configuration.findConfigurationItem(deviceCommands, Backend.DeviceCommand.MOVE_DOWN) != null);
  UIUtils.setEnabled(this._turnLeftButton, Application.Configuration.findConfigurationItem(deviceCommands, Backend.DeviceCommand.TURN_LEFT) != null);
  UIUtils.setEnabled(this._turnRightButton, Application.Configuration.findConfigurationItem(deviceCommands, Backend.DeviceCommand.TURN_RIGHT) != null);
  UIUtils.setEnabled(this._eyeControlButton, Application.Configuration.findConfigurationItem(deviceCommands, Backend.DeviceCommand.EYES_ON) != null);
  UIUtils.setEnabled(this._talkButton, Application.Configuration.findConfigurationItem(deviceCommands, Backend.DeviceCommand.TALK) != null);
}
ManualModePage.prototype._disableActions = function() {
  this._deviceStatusLabel.innerHTML = this.getLocale().DeviceProcessingCommandLabel;
  
  UIUtils.setEnabled(this._resetButton, false);
  UIUtils.setEnabled(this._moveUpButton, false);
  UIUtils.setEnabled(this._moveDownButton, false);
  UIUtils.setEnabled(this._turnLeftButton, false);
  UIUtils.setEnabled(this._turnRightButton, false);
  UIUtils.setEnabled(this._eyeControlButton, false);
  UIUtils.setEnabled(this._talkButton, false);
}
