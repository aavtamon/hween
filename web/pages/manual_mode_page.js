ManualModePage = ClassUtils.defineClass(AbstractDataPage, function ManualModePage() {
  AbstractDataPage.call(this, ManualModePage.name);
  
  this._deviceId;
  this._deviceInfo;
  
  this._resetButton;
  this._moveUpButton;
  this._moveDownButton;
  this._turnLeftButton;
  this._turRghtButton;
});

ManualModePage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  UIUtils.appendLabel(contentPanel, "CommandsLabel", this.getLocale().CommandsLabel);
  
  var commandsPanel = UIUtils.appendBlock(contentPanel, "CommandsPanel");
  
  this._resetButton = UIUtils.appendButton(commandsPanel, "ResetButton", this.getLocale().ResetToInitialPositionButton);
  this._resetButton.setClickListener(function() {
    Controller.reset(this._deviceInfo);
  }.bind(this));

  this._moveUpButton = UIUtils.appendButton(commandsPanel, "MoveUpButton", this.getLocale().MoveUpButton);
  this._moveUpButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, Backend.DeviceCommand.MOVE_UP, function() {
      this._enableActions();
    });
  }.bind(this));

  this._moveDownButton = UIUtils.appendButton(commandsPanel, "MoveDownButton", this.getLocale().MoveDownButton);
  this._moveDownButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, Backend.DeviceCommand.MOVE_DOWN, function() {
      this._enableActions();
    });
  }.bind(this));

  
  this._turnLeftButton = UIUtils.appendButton(commandsPanel, "TurnLeftButton", this.getLocale().TurnLeftButton);
  this._turnLeftButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, Backend.DeviceCommand.TURN_LEFT, function() {
      this._enableActions();
    });
  }.bind(this));
  
  this._turnRightButton = UIUtils.appendButton(commandsPanel, "TurnRightButton", this.getLocale().TurnRightButton);
  this._turnRightButton.setClickListener(function() {
    this._disableActions();
    Controller.sendCommand(this._deviceInfo, Backend.DeviceCommand.TURN_RIGHT, function() {
      this._enableActions();
    });
  }.bind(this));
}

ManualModePage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  this._deviceInfo = Backend.getDeviceInfo(this._deviceId);
  
  this._enableActions();
}

ManualModePage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
}


ManualModePage.prototype._enableActions = function() {
  var deviceCommands = Backend.getSupportedCommands(this._deviceInfo);
  
  UIUtils.setEnabled(this._resetButton, true);
  UIUtils.setEnabled(this._moveUpButton, GeneralUtils.containsInArray(deviceCommands, Backend.DeviceCommand.MOVE_UP));
  UIUtils.setEnabled(this._moveDownButton, GeneralUtils.containsInArray(deviceCommands, Backend.DeviceCommand.MOVE_DOWN));
  UIUtils.setEnabled(this._turnLeftButton, GeneralUtils.containsInArray(deviceCommands, Backend.DeviceCommand.TURN_LEFT));
  UIUtils.setEnabled(this._turnRightButton, GeneralUtils.containsInArray(deviceCommands, Backend.DeviceCommand.TURN_RIGHT));
  
}
ManualModePage.prototype._disableActions = function() {
  UIUtils.setEnabled(this._resetButton, false);
  UIUtils.setEnabled(this._moveUpButton, false);
  UIUtils.setEnabled(this._moveUpButton, false);
  UIUtils.setEnabled(this._turnLeftButton, false);
  UIUtils.setEnabled(this._turnRightButton, false);
}