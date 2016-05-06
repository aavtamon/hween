AddDevicePage = ClassUtils.defineClass(AbstractDataPage, function AddDevicePage() {
  AbstractPage.call(this, AddDevicePage.name);
  
  this._statusLabel;
  this._progressIndicator;
  this._addByIdButton;
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
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", this.getLocale().GoBackButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  this._addButton = UIUtils.appendButton(buttonsPanel, "AddDevicesButton", this.getLocale().AddDevicesButton);
  this._addButton.setClickListener(function() {
    Backend.addRegisterDevices(this._getSelectedDeviceIds(), function(status) {
      if (status == Backend.OperationResult.SUCCESS) {
        Application.goBack();
      } else {
        UIUtuls.showMessage(this.getLocale().UnrecognizedDeviceIdMessage);
      }
    }.bind(this));
  }.bind(this));
  UIUtils.setEnabled(this._addButton, false);
  
  
  var rightPanel = UIUtils.appendBlock(contentPanel, "RightPanel");
  
  this._progressIndicator = UIUtils.appendBlock(rightPanel, "DiscoveryInProgressIndicator");
  this._progressIndicator.start = function() {
    this._progressIndicator.style.backgroundImage = "url(../../shared_tools/web/imgs/ajax-loader.gif)";
  }.bind(this);
  this._progressIndicator.stop = function() {
    this._progressIndicator.style.backgroundImage = "url(../../shared_tools/web/imgs/close.png)";
  }.bind(this);
  
  
  var addByIdPanel = UIUtils.appendBlock(rightPanel, "AddByIdPanel");
  UIUtils.appendLabel(addByIdPanel, "AddByIdLabel", this.getLocale().AddByIdLabel);
  this._addByIdButton = UIUtils.appendButton(addByIdPanel, "AddByIdButton", this.getLocale().AddByIdButton);
  this._addByIdButton.setClickListener(function() {
    Dialogs.showAddDeviceByIdDialog();
  });
}

AddDevicePage.prototype.onShow = function() {
  AbstractDataPage.prototype.onShow.call(this);
 
  this._scanNewDevices();
}

AddDevicePage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
}


AddDevicePage.prototype._scanNewDevices = function() {
  this._progressIndicator.start();
  this._statusLabel.innerHTML = this.getLocale().SearchingDevicesLabel;
  UIUtils.setEnabled(this._rescanButton, false);
  UIUtils.setEnabled(this._addByIdButton, false);
  
  this._devices = {};
  UIUtils.emptyContainer(this._deviceList);
  
  Backend.getUnregisteredDeviceIds(function(status, ids) {
    if (status != Backend.OperationResult.SUCCESS) {
      this._statusLabel.innerHTML = this.getLocale().ServerErrorLabel;
      return;
    }
    
    if (ids.length == 0) {
      this._progressIndicator.stop();
      UIUtils.setEnabled(this._addByIdButton, true);
      this._statusLabel.innerHTML = this.getLocale().NoNewDevicesFoundLabel;
      return;
    }

    for (var i = 0; i < ids.length; i++) {
      this._devices[ids[i]] = null;
      
      Backend.getDeviceInfo(ids[i], function(result, info) {
        if (result == Backend.OperationResult.SUCCESS) {
          this._devices[info.id] = info;

          this._addDeviceElement(info.id);
          this._stopProgressIndicationIfDiscoveryCompleted();
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
  
  this._progressIndicator.stop();
  UIUtils.setEnabled(this._addByIdButton, true);
  
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

  var itemLabel = UIUtils.appendLabel(deviceItem, "NameLabel", info.name);
  UIUtils.addClass(itemLabel, "device-name");

  var idLabel = UIUtils.appendLabel(deviceItem, "IdLabel", I18n.getLocale().literals.SerialNumber + " " + info.serial_number);
  UIUtils.addClass(idLabel, "device-id");
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
