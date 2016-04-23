AddDevicePage = ClassUtils.defineClass(AbstractDataPage, function AddDevicePage() {
  AbstractPage.call(this, AddDevicePage.name);
  
  this._statusLabel;
  this._progressIndicator;
  this._addButton;
  this._rescanButton;
  this._deviceList;
  this._devices = {};
});

AddDevicePage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);
  
  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var devicesPanel = UIUtils.appendBlock(contentPanel, "DevicesPanel");
  
  var scanStatusPanel = UIUtils.appendBlock(devicesPanel, "ScanStatusPanel");
  this._statusLabel = UIUtils.appendLabel(scanStatusPanel, "SearchStatusLabel");
  this._rescanButton = UIUtils.appendButton(scanStatusPanel, "ScanButton", this.getLocale().ScanButton);
  this._rescanButton.setClickListener(this._scanNewDevices.bind(this));
  
  this._deviceList = UIUtils.appendBlock(devicesPanel, "DevicesList");
  
  var buttonsPanel = UIUtils.appendBlock(devicesPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().literals.CancelOperationButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._addButton = UIUtils.appendButton(buttonsPanel, "AddDevicesButton", this.getLocale().AddDevicesButton);
  this._addButton.setClickListener(function() {
    Backend.registerDevices(this._getSelectedDeviceIds(), function() {
      Application.goBack();
    });
  }.bind(this));
  UIUtils.setEnabled(this._addButton, false);
  
  var progressPanel = UIUtils.appendBlock(contentPanel, "ProgressPanel");
  this._progressIndicator = UIUtils.appendBlock(progressPanel, "DiscoveryInProgress");
  UIUtils.setVisible(this._progressIndicator, false);
}

AddDevicePage.prototype.onShow = function() {
  AbstractDataPage.prototype.onShow.call(this);
 
  this._scanNewDevices();
}

AddDevicePage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
}


AddDevicePage.prototype._scanNewDevices = function() {
  UIUtils.setVisible(this._progressIndicator, true);
  this._statusLabel.innerHTML = this.getLocale().SearchingDevicesLabel;
  UIUtils.setEnabled(this._rescanButton, false);
  
  this._devices = {};
  UIUtils.emptyContainer(this._deviceList);
  
  Backend.getNewDeviceIds(function(status, ids) {
    if (status != Backend.OperationResult.SUCCESS) {
      this._statusLabel.innerHTML = this.getLocale().ServerErrorLabel;
      return;
    }
    
    if (ids.length == 0) {
      UIUtils.setVisible(this._progressIndicator, false);
      this._statusLabel.innerHTML = this.getLocale().NoNewDevicesFoundLabel;
      return;
    }

    for (var i = 0; i < ids.length; i++) {
      this._devices[ids[i]] = null;
      
      Backend.getDeviceInfo(ids[i], function(result, info) {
        if (result == Backend.OperationResult.SUCCESS) {
          if (info.status == Backend.Status.DISCOVERED) {
            Controller.isAvailable(info, function(isAvailable) {
              if (isAvailable) {
                this._devices[info.id] = info;
                
                this._addDeviceElement(info.id);
                this._stopProgressIndicationIfDiscoveryCompleted();
              }
            }.bind(this));
          } else {
            delete this._devices[info.id];
            this._stopProgressIndicationIfDiscoveryCompleted();
          }
        } else {
          delete this._devices[info.id];
          this._stopProgressIndicationIfDiscoveryCompleted();
        }
      }.bind(this)); 
    }
  }.bind(this));
}


AddDevicePage.prototype._stopProgressIndicationIfDiscoveryCompleted = function() {
  for (var id in this._devices) {
    if (this._devices[id] == null) {
      return;
    }
  }
  
  UIUtils.setVisible(this._progressIndicator, false);
  
  if (Object.keys(this._devices).length == 0) {
    this._statusLabel.innerHTML = this.getLocale().NoNewDevicesFoundLabel;
  } else {
    this._statusLabel.innerHTML = this.getLocale().FoundNewDevicesLabel;
  }
  
  UIUtils.setEnabled(this._rescanButton, true);
}

AddDevicePage.prototype._addDeviceElement = function(id) {
  var info = this._devices[id];
  var deviceItem = UIUtils.appendBlock(this._deviceList, info.id);
  deviceItem._info = info;
  this._devices[id] = deviceItem;
  
  UIUtils.addClass(deviceItem, "discovered-device");

  var selectionBox = UIUtils.appendCheckbox(deviceItem, "Selection");
  UIUtils.addClass(selectionBox, "device-selection");
  deviceItem._selectionBox = selectionBox;
  selectionBox.setChangeListener(function() {
    UIUtils.setEnabled(this._addButton, this._getSelectedDeviceIds().length > 0);
  }.bind(this));
                       
  var itemIcon = UIUtils.appendBlock(deviceItem, "Icon");
  UIUtils.addClass(itemIcon, "device-icon");
  itemIcon.style.backgroundImage = info.icon;

  var itemLabel = UIUtils.appendLabel(deviceItem, "Label", info.name);
  UIUtils.addClass(itemLabel, "device-name");
}

AddDevicePage.prototype._getSelectedDeviceIds = function() {
  var selectedIds = [];

  for (var id in this._devices) {
    if (this._devices[id]._selectionBox.isChecked()) {
      selectedIds.push(id);
    }
  }
  
  return selectedIds;
}
