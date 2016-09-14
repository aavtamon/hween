StockProgramsPage = ClassUtils.defineClass(AbstractDataPage, function StockProgramsPage() {
  AbstractDataPage.call(this, StockProgramsPage.name);
  
  this._deviceId;
  this._deviceType;
  
  this._programList;
  this._loadSelectedButton;
  this._removeSelectedButton;
  this._uploadProgramButton;
  
  this._cacheChangeListener = function(event) {
    if (event.type == Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS && this._deviceType == event.objectId) {
      this._refreshProgramList();
    }
  }.bind(this);
});

StockProgramsPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var programSelectionPanel = UIUtils.appendBlock(contentPanel, "ProgramSelectionPanel");
  this._programList = UIUtils.appendList(programSelectionPanel, "ProgramSelectionList");
  var descriptionPanel = UIUtils.appendBlock(programSelectionPanel, "DescriptionPanel");
  var descriptionLabel = UIUtils.appendBlock(descriptionPanel, "DescriptionPanelLabel");
  var descriptionLabel = UIUtils.appendLabel(descriptionPanel, "DescriptionPanelLabel", this.getLocale().DescriptionLabel);
  var descriptionText = UIUtils.appendBlock(descriptionPanel, "DescriptionPanelText");
  
  this._programList.setSelectionListener(function(selectedItem) {
    descriptionText.innerHTML = selectedItem != null && selectedItem.element != null && selectedItem.element._program != null ? selectedItem.element._program.description : "";
  });
  
  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().CancelOperationButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._loadSelectedButton = UIUtils.appendButton(buttonsPanel, "LoadSelectedButton", this.getLocale().LoadSelectedButton);
  this._loadSelectedButton.setClickListener(function() {
    var programs = [];
    
    var selectedPrograms = this._getSelectedPrograms();
    for (var i in selectedPrograms) {
      var program = selectedPrograms[i];
      programs.push(Backend.convertToDeviceProgram(program));
    }
    
    Backend.addDevicePrograms(this._deviceId, programs, function(status) {
      if (status == Backend.OperationResult.SUCCESS) {
        Application.showPage(DeviceManagementPage.name, {deviceId: this._deviceId});
      }
    });
  }.bind(this));
}

StockProgramsPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceId = bundle.deviceId;
  this._deviceType = Backend.getDeviceInfo(this._deviceId).type;
  
  var programs = Backend.getStockPrograms(this._deviceType);
  if (programs == null) {
    this._programList.innerHTML = this.getLocale().UpdatingListOfProgramsLabel;
  } else if (programs.length == 0) {
    this._programList.innerHTML = this.getLocale().NoProgramsAvailableLabel;
  } else {
    this._refreshProgramList();
  }
  
  UIUtils.setEnabled(this._loadSelectedButton, false);
  
  Backend.addCacheChangeListener(this._cacheChangeListener);
}

StockProgramsPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  Backend.removeCacheChangeListener(this._cacheChangeListener);
}

StockProgramsPage.prototype._refreshProgramList = function() {
  this._programList.clear();
  UIUtils.setEnabled(this._loadSelectedButton, false);

  var programs = Backend.getStockPrograms(this._deviceType);
  if (programs == null) {
    return;
  }
  
  Backend.getDeviceSettings(this._deviceType, function(status, deviceSettings) {
    if (status == Backend.OperationResult.SUCCESS) {
      var categories = deviceSettings.categories;
      for (var categoryIndex in categories) {
        var category = categories[categoryIndex];
        this._addCategoryToList(category);

        for (var progId in programs) {
          var program = programs[progId];
          if (program.category == category.data) {
            this._addProgramToList(program);
          }
        }
      }
    }
  }.bind(this));
}


StockProgramsPage.prototype._addCategoryToList = function(category) {
  var categoryItem = document.createElement("div");
  this._programList.addItem({element: categoryItem});
  
  categoryItem._category = category;  
  
  UIUtils.addClass(categoryItem, "category-item notselectable");
  categoryItem.innerHTML = category.display;
}

StockProgramsPage.prototype._addProgramToList = function(program) {
  var programItem = document.createElement("div");
  this._programList.addItem({element: programItem});
  
  programItem._program = program;
  program._item = programItem;
  
  UIUtils.addClass(programItem, "program-item notselectable");

  var selectionBox = UIUtils.appendCheckbox(programItem, "Selection");
  UIUtils.addClass(selectionBox, "program-selection");
  programItem._selectionBox = selectionBox;
  selectionBox.setChangeListener(function() {
    UIUtils.setEnabled(this._loadSelectedButton, this._getSelectedPrograms().length > 0);
  }.bind(this));
  
  var itemTitle = UIUtils.appendLabel(programItem, "Title", program.title);
  UIUtils.addClass(itemTitle, "program-title");
}

StockProgramsPage.prototype._getSelectedPrograms = function() {
  var selectedPrograms = [];

  var programItems = this._programList.getItems();
  for (var i = 0; i < programItems.length; i++) {
    if (programItems[i].element._selectionBox != null && programItems[i].element._selectionBox.isChecked()) {
      selectedPrograms.push(programItems[i].element._program);
    }
  }
  
  return selectedPrograms;
}
