CreateProgramPage = ClassUtils.defineClass(AbstractDataPage, function CreateProgramPage() {
  AbstractDataPage.call(this, CreateProgramPage.name);
  
  this._deviceId;
  this._deviceInfo;
  this._program;
  this._addToDevice;
  this._deviceProgramId;
  this._addToLibrary;
  this._libraryProgramId;
  
  this._commandList;
  this._removeCommandButton;
  this._saveButton;
  this._programPlayButton;
  this._programPauseButton;
  this._programStopButton;
  this._addCommandButton;
  this._programNameInput;
  this._descriptionInput;
  
  this._playbackTimer;
  this._currentCommandIndex;
  
  this._toy;
});

CreateProgramPage.prototype._PLAYBACK_SPEED = 2000;

CreateProgramPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");

  this._toyPanel = UIUtils.appendBlock(contentPanel, "AnimationPanel");
  
  var programPanel = UIUtils.appendBlock(contentPanel, "ProgramPanel");
  var headerProgramPanel = UIUtils.appendBlock(programPanel, "ProgramHeaderPanel");
  UIUtils.appendLabel(headerProgramPanel, "ProgramLabel", this.getLocale().ProgramLabel);
  this._programNameInput = UIUtils.appendTextInput(headerProgramPanel, "ProgramNameInput");
  this._programNameInput.setChangeListener(function(value) {
    UIUtils.setEnabled(this._saveButton, (value != null && value.trim() != ""));
  }.bind(this));
  
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
    this._stopProgram();
  }.bind(this));
  
  this._addCommandButton = UIUtils.appendExpandableButton(footerProgramPanel, "AddCommandButton", this.getLocale().AddCommandButton);
  
  this._commandList.setSelectionListener(function(selectedItem) {
    UIUtils.setEnabled(this._removeCommandButton, selectedItem != null);
  }.bind(this));
  this._commandList.setPreorderListener(function(items) {
    if (this._playbackTimer == null) {
      return;
    }
    
    UIUtils.showMessage(this.getLocale().ProgramExecutionTerminatedMessage);      
    this._stopProgram();
  }.bind(this));
  
  UIUtils.appendLabel(contentPanel, "DescriptionLabel", this.getLocale().DescriptionLabel);
  this._descriptionInput = UIUtils.appendTextInput(contentPanel, "DescriptionInput");
  
  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().CancelOperationButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._saveButton = UIUtils.appendButton(buttonsPanel, "SaveButton", this.getLocale().SaveButton);
  this._saveButton.setClickListener(function() {
    this._program.title = this._programNameInput.getValue();
    this._program.description = this._descriptionInput.getValue();
    this._program.commands = [];

    var items = this._commandList.getItems();
    for (var index in items) {
      this._program.commands.push(items[index].element._command);
    }
    
    if (this._addToLibrary) {
      if (this._libraryProgramId != null) {
        //TODO: Not yet supported. Requires to add updateLibraryProgram to Backend apis
      } else {
        Backend.addLibraryProgram(this._deviceId, this._program, function(status, libProgram) {
          if (status == Backend.OperationResult.SUCCESS) {
            if (this._addToDevice) {
              Backend.addDevicePrograms(this._deviceId, Backend.convertLibraryToDeviceProgram(libProgram), function(status) {
                if (status == Backend.OperationResult.SUCCESS) {
                  Application.goBack();
                }
              }.bind(this));
            } else {
              Application.goBack();
            }
          }
        }.bind(this));
      }
    } else if (this._addToDevice) {
      if (this._deviceProgramId != null) {
        Backend.updateDevicePrograms(this._deviceId, this._program, function(status) {
          if (status == Backend.OperationResult.SUCCESS) {
            Application.goBack();
          }
        }.bind(this));
      } else {
        Backend.addDevicePrograms(this._deviceId, Backend.convertLibraryToDeviceProgram(this._program), function(status) {
          if (status == Backend.OperationResult.SUCCESS) {
            Application.goBack();
          }
        }.bind(this));
      }
    }
  }.bind(this));
}

CreateProgramPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  this._deviceProgramId = bundle.deviceProgramId;
  this._addToDevice = this._deviceProgramId != null || bundle.addToDevice || false;
  this._addToLibrary = bundle.addToLibrary || false;
  this._deviceInfo = Backend.getDeviceInfo(this._deviceId);
  
  this._toy = Toy.createToy(this._deviceInfo.type, "Toy");
  this._toy.append(this._toyPanel);

  
  this._commandList.clear();
  this._programNameInput.setValue("");
  this._descriptionInput.setValue("");
  
  this._program = null;
  if (this._deviceProgramId != null) {
    var schedule = Backend.getDeviceSchedule(this._deviceId);
    if (schedule != null && schedule.programs != null) {
      for (var index in schedule.programs) {
        var program = schedule.programs[index]
        if (program.id == this._deviceProgramId) {
          this._program = program;
          break;
        }
      }
    } else {
      console.error("Incorrect situation: provided device program id " + this._deviceProgramId + " is out of sync")
    }
  } else if (this._libraryProgramId != null) {
    var libraryPrograms = Backend.getLibraryPrograms(this._deviceId); 
    if (libraryPrograms != null && libraryPrograms.length > this._libraryProgramId) {
      this._program = libraryPrograms[this._libraryProgramId];
    } else {
      console.error("Incorrect situation: provided library program id " + this._libraryProgramId + " is out of sync")
    }
  }

  if (this._program != null) {
    for (var command in program.commands) {
      this._addCommandToList(command);
    }
    this._programNameInput.setValue(program.title);
    this._descriptionInput.setValue(program.description);
  } else {
    this._program = {};
  }
  
  
  Backend.getDeviceSettings(this._deviceInfo.type, function(status, deviceSettings) {
    if (status == Backend.OperationResult.SUCCESS) {
      var commands = deviceSettings.supportedCommands;
      this._addCommandButton.setExpandableActions(this._getCommandActions(deviceSettings.supportedCommands));
      
      for (var i in commands) {
        if (commands[i].data == Backend.DeviceCommand.RESET) {
          this._addCommandToList(commands[i]);
          break;
        }
      }
    }
  }.bind(this));
  
  UIUtils.setEnabled(this._removeCommandButton, false);
  UIUtils.setEnabled(this._saveButton, false);
  
  this._stopProgram();
  this._toy.reset();
}

CreateProgramPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  this._stopProgram();
  
  this._toy.remove();
}


CreateProgramPage.prototype._getCommandActions = function(supportedCommands) {
  var clickListener = function(command) {
    if (this._playbackTimer != null) {
      UIUtils.showMessage(this.getLocale().ProgramExecutionTerminatedMessage);      
    }
    
    this._addCommandToList(command);
    this._stopProgram();
  }.bind(this);
  
  
  var actions = [];
  for (var i in supportedCommands) {
    var command = supportedCommands[i];
    
    var clickAction;
    if (command.data == Backend.DeviceCommand.TALK) {
      clickAction = function(command) {
        var fileChooser = UIUtils.appendFileChooser(this._addCommandButton);

        fileChooser.open(function(files) {
          UIUtils.remove(fileChooser);
          
          if (files == null || files.length == 0) {
            return;
          }
          var selectedFile = files[0];
          if (FileUtils.isAudio(selectedFile)) {
            FileUtils.loadFile(selectedFile, function(file, dataUrl) {
              command.description = file.name;
              command.arg = dataUrl;
              clickListener(command);
            });
          } else {
            UIUtils.showMessage(this.getLocale().IncorrectAudioFileMessage);
          }
        }.bind(this));
      }.bind(this, command);
    } else {
      clickAction = clickListener.bind(this, command);
    }
    var action = {display: command.display, clickListener: clickAction};
    actions.push(action);
  }
  
  return actions;
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
    this._currentCommandIndex = 0;
  } else {
    this._stopCommandExecution(this._currentCommandIndex);
    this._currentCommandIndex++;
  }
  
  if (this._commandList.getItems().length == this._currentCommandIndex) {
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
  UIUtils.setEnabled(this._programPlayButton, this._commandList.getItems().length > 0);
  UIUtils.setEnabled(this._programPauseButton, false);
  UIUtils.setEnabled(this._programStopButton, false);
  
  this._stopCommandExecution(this._currentCommandIndex);
  
  clearInterval(this._playbackTimer);
  this._playbackTimer = null;
  this._currentCommandIndex = null;
  
  this._commandList.scrollToItem(this._commandList.getItems()[0]);
}



CreateProgramPage.prototype._executeCommand = function(commandIndex) {
  if (commandIndex == null) {
    return;
  }
  
  var item = this._commandList.getItems()[commandIndex].element;
  UIUtils.addClass(item, "command-executing");
  UIUtils.removeClass(item, "command-paused");
  item._activityElement.innerHTML = this.getLocale().Executing;
  
  this._commandList.scrollToItem(this._commandList.getItems()[commandIndex]);
  
  this._toy.performCommand(item._command);
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
