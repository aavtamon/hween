CreateProgramPage = ClassUtils.defineClass(AbstractDataPage, function CreateProgramPage() {
  AbstractDataPage.call(this, CreateProgramPage.name);
  
  this._deviceId;
  this._deviceInfo;
  
  this._commandList;
  this._removeCommandButton;
  this._saveButton;
  this._programPlayButton;
  this._programPauseButton;
  this._programStopButton;
  this._addCommandButton;
  
  this._playbackTimer;
  this._currentCommandIndex;
});

CreateProgramPage.prototype._PLAYBACK_SPEED = 2000;

CreateProgramPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var amimationPanel = UIUtils.appendBlock(contentPanel, "AnimationPanel");
  
  var programPanel = UIUtils.appendBlock(contentPanel, "ProgramPanel");
  var headerProgramPanel = UIUtils.appendBlock(programPanel, "ProgramHeaderPanel");
  UIUtils.appendLabel(headerProgramPanel, "ProgramLabel", this.getLocale().ProgramLabel);
  var programNameInput = UIUtils.appendTextInput(headerProgramPanel, "ProgramNameInput");
  var programPlaybackPanel = UIUtils.appendBlock(headerProgramPanel, "ProgramPlaybackPanel");
  this._programPlayButton = UIUtils.appendButton(programPlaybackPanel, "PlayButton", this.getLocale().PlayButton);
  this._programPlayButton.setClickListener(this._playProgram.bind(this));
  this._programPauseButton = UIUtils.appendButton(programPlaybackPanel, "PauseButton", this.getLocale().PauseButton);
  this._programPauseButton.setClickListener(this._pauseProgram.bind(this));
  this._programStopButton = UIUtils.appendButton(programPlaybackPanel, "StopButton", this.getLocale().StopButton);
  this._programStopButton.setClickListener(this._stopProgram.bind(this));
  
  this._commandList = UIUtils.appendList(programPanel, "ProgramCommandList", null, true);

  var footerProgramPanel = UIUtils.appendBlock(programPanel, "ProgramFooterPanel");
  this._removeCommandButton = UIUtils.appendButton(footerProgramPanel, "RemoveCommandButton", this.getLocale().RemoveCommandButton);
  this._removeCommandButton.setClickListener(function() {
    this._commandList.removeItem(this._commandList.getSelectedItem());
  }.bind(this));
  
  this._addCommandButton = UIUtils.appendExpandableButton(footerProgramPanel, "AddCommandButton", this.getLocale().AddCommandButton);
  
  this._commandList.setSelectionListener(function(selectedItem) {
    UIUtils.setEnabled(this._removeCommandButton, selectedItem != null);
  }.bind(this));
  this._commandList.setOrderListener(function(items) {
    if (this._playbackTimer == null) {
      return;
    }
    
    UIUtils.showMessage(this.getLocale().ProgramExecutionTerminatedMessage);      
    this._stopProgram();

    var items = this._commandList.getItems();
    for (var i = 0; i < items.length; i++) {
      this._stopCommandExecution(i);
    }
  }.bind(this));
  
  
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
  this._deviceInfo = Backend.getDeviceInfo(this._deviceId);
  
  var clickListener = function(command) {
    if (this._playbackTimer != null) {
      UIUtils.showMessage(this.getLocale().ProgramExecutionTerminatedMessage);      
    }
    this._stopProgram();
    
    this._addCommandToList(command);
  }.bind(this);
  
  var commands = Backend.getSupportedCommands(Backend.getDeviceInfo(this._deviceId));
  var actions = [];
  for (var i in commands) {
    var command = commands[i];
    
    var action = {display: command.display, clickListener: clickListener.bind(this, command)};
    actions.push(action);
  }
  this._addCommandButton.setExpandableActions(actions);
  
  this._commandList.clear();
  
  UIUtils.setEnabled(this._removeCommandButton, false);
  UIUtils.setEnabled(this._saveButton, false);
  
  UIUtils.setEnabled(this._programPlayButton, false);
  UIUtils.setEnabled(this._programPauseButton, false);
  UIUtils.setEnabled(this._programStopButton, false);
  
}

CreateProgramPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  this._stopProgram();
}


CreateProgramPage.prototype._addCommandToList = function(command) {
  var commandItem = document.createElement("div");
  this._commandList.addItem({element: commandItem});
  
  commandItem._command = command;
  
  UIUtils.addClass(commandItem, "command-item notselectable");

  var itemTitle = UIUtils.appendLabel(commandItem, "Title", command.display + " - " + command.description);
  UIUtils.addClass(itemTitle, "command-title");

  commandItem._activityElement = UIUtils.appendLabel(commandItem, "Activity");
  UIUtils.addClass(commandItem._activityElement, "command-activity");
}



CreateProgramPage.prototype._playProgram = function() {
  UIUtils.setEnabled(this._programPlayButton, false);
  UIUtils.setEnabled(this._programPauseButton, true);
  UIUtils.setEnabled(this._programStopButton, true);
  
  if (this._currentCommandIndex == null) {
    this._resetExecution();
    this._currentCommandIndex = 0;
  } else {
    this._stopCommandExecution(this._currentCommandIndex);
    this._currentCommandIndex++;
  }
  
  if (this._commandList.getItems().length == this._currentCommandIndex + 1) {
    this._stopProgram();
    return;
  }
  
  this._playbackTimer = setInterval(function() {
    this._stopCommandExecution(this._currentCommandIndex);
    
    if (this._commandList.getItems().length > this._currentCommandIndex + 1) {
      this._currentCommandIndex++;
      this._executeCommand(this._currentCommandIndex);
    } else {
      this._stopProgram();
    }   
  }.bind(this), CreateProgramPage.prototype._PLAYBACK_SPEED);
  
  this._executeCommand(this._currentCommandIndex);
}


CreateProgramPage.prototype._pauseProgram = function() {
  UIUtils.setEnabled(this._programPlayButton, true);
  UIUtils.setEnabled(this._programPauseButton, false);
  UIUtils.setEnabled(this._programStopButton, true);
  
  this._pauseCommandExecution(this._currentCommandIndex);
  
  clearInterval(this._playbackTimer);
}

CreateProgramPage.prototype._stopProgram = function() {
  UIUtils.setEnabled(this._programPlayButton, true);
  UIUtils.setEnabled(this._programPauseButton, false);
  UIUtils.setEnabled(this._programStopButton, false);
  
  this._stopCommandExecution(this._currentCommandIndex);
  
  clearInterval(this._playbackTimer);
  this._playbackTimer = null;
  this._currentCommandIndex = null;
}



CreateProgramPage.prototype._resetExecution = function() {
  this._commandList.scrollTop = 0;
  
//  Controller.reset(this._deviceInfo);
}

CreateProgramPage.prototype._executeCommand = function(commandIndex) {
  if (commandIndex == null) {
    return;
  }
  
  var item = this._commandList.getItems()[commandIndex].element;
  UIUtils.addClass(item, "command-executing");
  UIUtils.removeClass(item, "command-paused");
  item._activityElement.innerHTML = this.getLocale().Executing;
    
  var offset = item.parentNode.getBoundingClientRect().top - this._commandList.getBoundingClientRect().top;
  this._commandList.scrollTop = offset;
  
  
//  Controller.sendCommand(this._deviceInfo, item._command.data);
}
CreateProgramPage.prototype._pauseCommandExecution = function(commandIndex) {
  if (commandIndex == null) {
    return;
  }
  
  var item = this._commandList.getItems()[commandIndex].element;
  UIUtils.addClass(item, "command-paused");
  UIUtils.removeClass(item, "command-executing");
  item._activityElement.innerHTML = this.getLocale().Paused;
}
CreateProgramPage.prototype._stopCommandExecution = function(commandIndex) {
  if (commandIndex == null) {
    return;
  }
  
  var item = this._commandList.getItems()[commandIndex].element;
  UIUtils.removeClass(item, "command-executing");
  UIUtils.removeClass(item, "command-paused");
  item._activityElement.innerHTML = "";
}
