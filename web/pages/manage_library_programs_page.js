ManageLibraryProgramsPage = ClassUtils.defineClass(AbstractDataPage, function ManageLibraryProgramsPage() {
  AbstractDataPage.call(this, ManageLibraryProgramsPage.name);
  
  this._deviceId;
  
  this._programList;
  this._loadSelectedButton;
  this._removeSelectedButton;
  this._uploadProgramButton;
  
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
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().CancelOperationButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._removeSelectedButton = UIUtils.appendButton(buttonsPanel, "RemoveSelectedButton", this.getLocale().RemoveSelectedButton);
  this._removeSelectedButton.setClickListener(function() {
    if (this._getSelectedPrograms().length > 0) {
      Dialogs.showConfirmProgramRemovalDialog(function() {
        var selectedPrograms = this._getSelectedPrograms();
        for (var i in selectedPrograms) {
          var program = selectedPrograms[i];
          this._programList.removeItem(program._item);
          Backend.removeLibraryProgram(this._deviceId, program);
        }
      }.bind(this));
    }
    
  }.bind(this));
  
  this._loadSelectedButton = UIUtils.appendButton(buttonsPanel, "LoadSelectedButton", this.getLocale().LoadSelectedButton);
  this._loadSelectedButton.setClickListener(function() {
    var programs = [];
    
    var selectedPrograms = this._getSelectedPrograms();
    for (var i in selectedPrograms) {
      var program = selectedPrograms[i];
      programs.push(this._convertLibraryToDeviceProgram(program));
    }
    
    Backend.addDevicePrograms(this._deviceId, programs, function(status) {
      if (status == Backend.OperationResult.SUCCESS) {
        Application.showPage(DeviceManagementPage.name, {deviceId: this._deviceId});
      }
    });
  }.bind(this));


  this._uploadSelectedButton = UIUtils.appendButton(buttonsPanel, "UploadSelectedButton", this.getLocale().UploadSelectedButton);
  this._uploadSelectedButton.setClickListener(function() {
    var selectedProgram = this._getSelectedPrograms()[0];
    Dialogs.showUploadStockProgramDialog(this._deviceId, selectedProgram);
  }.bind(this));
  
  this._createProgramButton = UIUtils.appendButton(buttonsPanel, "CreateProgramButton", this.getLocale().CreateProgramButton);
  this._createProgramButton.setClickListener(function() {
    Application.showPage(CreateProgramPage.name, {deviceId: this._deviceId});
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
  UIUtils.setEnabled(this._uploadSelectedButton, false);
  
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
  UIUtils.setEnabled(this._uploadSelectedButton, false);

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
    description: stockProgram.description,
    frequency: Backend.Program.FREQUENCY_NEVER
  }
}
