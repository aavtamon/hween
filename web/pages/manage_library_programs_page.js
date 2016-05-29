ManageLibraryProgramsPage = ClassUtils.defineClass(AbstractDataPage, function ManageLibraryProgramsPage() {
  AbstractDataPage.call(this, ManageLibraryProgramsPage.name);
  
  this._deviceId;
  
  this._programList;
  this._loadSelectedButton;
  this._removeSelectedButton;
  
  this._cacheChangeListener = function(event) {
    if (event.type == Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS && this._deviceId == event.objectId) {
      this._refreshProgramList();
    }
  }.bind(this);
});

ManageLibraryProgramsPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var programSelectionPanel = UIUtils.appendBlock(contentPanel, "ProgramSelectionPanel");
  this._programList = UIUtils.appendList(programSelectionPanel, "ProgramSelectionList");
  var descriptionPanel = UIUtils.appendBlock(programSelectionPanel, "DescriptionPanel");
  var descriptionLabel = UIUtils.appendBlock(descriptionPanel, "DescriptionPanelLabel");
  var descriptionLabel = UIUtils.appendLabel(descriptionPanel, "DescriptionPanelLabel", this.getLocale().DescriptionLabel);
  var descriptionText = UIUtils.appendBlock(descriptionPanel, "DescriptionPanelText");
  
  this._programList.setSelectionListener(function(selectedItem) {
    descriptionText.innerHTML = selectedItem != null ? selectedItem.element._program.description : "";
  });
  
  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().literals.CancelOperationButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._removeSelectedButton = UIUtils.appendButton(buttonsPanel, "RemoveSelectedButton", this.getLocale().RemoveSelectedButton);
  this._removeSelectedButton.setClickListener(function() {
    var hasSelected = false;
    var items = this._programList.getItems();
    for (var i in items) {
      var item = items[i];
      if (item.element._selectionBox.isChecked()) {
        hasSelected = true;
        break;
      }
    }
    
    if (hasSelected) {
      Dialogs.showConfirmProgramRemovalDialog(function() {
        var items = this._programList.getItems();
        for (var i in items) {
          var item = items[i];
          if (item.element._selectionBox.isChecked()) {
            this._programList.removeItem(item);
            Backend.removeLibraryProgram(this._deviceId, item._program);
          }
        }
      }.bind(this));
    }
    
  }.bind(this));
  
  this._loadSelectedButton = UIUtils.appendButton(buttonsPanel, "LoadSelectedButton", this.getLocale().LoadSelectedButton);
  this._loadSelectedButton.setClickListener(function() {
    var programs = Backend.getPrograms(this._deviceId);
    
    var items = this._programList.getItems();
    for (var i in items) {
      var item = items[i];
      if (item.element._selectionBox.isChecked()) {
        programs.push(this._convertLibraryToDeviceProgram(item.element._program));
      }
    }
    
    Backend.setPrograms(this._deviceId, programs, function(status) {
      if (status == Backend.OperationResult.SUCCESS) {
        Application.showPage(DeviceManagementPage.name, {deviceId: this._deviceId});
      }
    });
  }.bind(this));
}

ManageLibraryProgramsPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  
  var programs = Backend.getLibraryPrograms(this._deviceId);
  if (programs == null) {
    this._programList.innerHTML = this.getLocale().UpdatingListOfProgramsLabel;
  } else if (programs.length == 0) {
    this._programList.innerHTML = this.getLocale().NoProgramsAvailableLabel;
  } else {
    this._refreshProgramList(programs);
  }
  
  UIUtils.setEnabled(this._removeSelectedButton, false);
  UIUtils.setEnabled(this._loadSelectedButton, false);
  
  Backend.addCacheChangeListener(this._cacheChangeListener);
}

ManageLibraryProgramsPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  Backend.removeCacheChangeListener(this._cacheChangeListener);
}

ManageLibraryProgramsPage.prototype._refreshProgramList = function() {
  this._programList.clear();
  UIUtils.setEnabled(this._removeSelectedButton, false);
  UIUtils.setEnabled(this._loadSelectedButton, false);

  var programs = Backend.getLibraryPrograms(this._deviceId);
  if (programs == null) {
    return;
  }
  
  for (var i = 0; i < programs.length; i++) {
    this._addProgramToList(programs[i]);
  }
}

ManageLibraryProgramsPage.prototype._addProgramToList = function(program) {
  var programItem = document.createElement("div");
  this._programList.addItem({element: programItem});
  
  programItem._program = program;
  
  UIUtils.addClass(programItem, "program-item notselectable");

  var selectionBox = UIUtils.appendCheckbox(programItem, "Selection");
  UIUtils.addClass(selectionBox, "program-selection");
  programItem._selectionBox = selectionBox;
  selectionBox.setChangeListener(function() {
    var hasSelection = this._getSelectedPrograms().length > 0;
    
    UIUtils.setEnabled(this._removeSelectedButton, hasSelection);
    UIUtils.setEnabled(this._loadSelectedButton, hasSelection);
  }.bind(this));
  
  var itemTitle = UIUtils.appendLabel(programItem, "Title", program.title);
  UIUtils.addClass(itemTitle, "program-title");
}

ManageLibraryProgramsPage.prototype._getSelectedPrograms = function() {
  var selectedPrograms = [];

  var programItems = this._programList.getItems();
  for (var i = 0; i < programItems.length; i++) {
    if (programItems[i].element._selectionBox.isChecked()) {
      selectedPrograms.push(programItems[i].element._program);
    }
  }
  
  return selectedPrograms;
}


ManageLibraryProgramsPage.prototype._convertLibraryToDeviceProgram = function(libraryProgram) {
  return {
    title: libraryProgram.title,
    frequency: Backend.Program.FREQUENCY_NEVER
  }
}
