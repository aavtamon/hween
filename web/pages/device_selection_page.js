DeviceSelectionPage = ClassUtils.defineClass(AbstractDataPage, function DeviceSelectionPage() {
  AbstractDataPage.call(this, DeviceSelectionPage.name);
  
  this._initialUpdatePanel;
  this._addByIdButton;
  this._deviceSelectionLabel;
  this._deviceSelectionPanel;
  this._deviceSelector;
  this._addButton;
  this._removeButton;
  this._refreshButton;
  
  this._devices = {};
  
  this._cacheChangeListener = function(event) {
    if (event.type == Backend.CacheChangeEvent.TYPE_DEVICE_IDS) {
      this._updateDeviceSelector();
    } else if (event.type == Backend.CacheChangeEvent.TYPE_DEVICE_INFO) {
      this._updateDevice(event.objectId);
    }
  }.bind(this);
});

DeviceSelectionPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  this._initialUpdatePanel = UIUtils.appendBlock(contentPanel, "InitialUpdatePanel");
  this._initialStatusLabel = UIUtils.appendLabel(this._initialUpdatePanel, "StatusLabel");
  var buttonsPanel = UIUtils.appendBlock(this._initialUpdatePanel, "ButtonsPanel");
  this._addByIdButton = UIUtils.appendButton(buttonsPanel, "AddButton", this.getLocale().AddButton);
  this._addByIdButton.setClickListener(Dialogs.showAddDeviceByIdDialog.bind(Dialogs));
  

  this._deviceSelectionPanel = UIUtils.appendBlock(contentPanel, "DeviceSelectionPanel");
  this._refreshButton = UIUtils.appendButton(this._deviceSelectionPanel, "RefreshButton", this.getLocale().RefreshButton);
  this._refreshButton.setClickListener(function() {
    this._refreshDevices();
  }.bind(this));
  
  this._statusLabel = UIUtils.appendLabel(this._deviceSelectionPanel, "DeviceSelectionLabel", this.getLocale().DeviceSelectionLabel);
  this._deviceSelector = UIUtils.appendGallery(this._deviceSelectionPanel, "DeviceSelector");
  this._deviceSelector.setClickListener(function(item) {
    console.debug(item._info);
  });

  
  var buttonsPanel = UIUtils.appendBlock(this._deviceSelectionPanel, "ButtonsPanel");

  this._removeButton = UIUtils.appendButton(buttonsPanel, "RemoveButton", this.getLocale().RemoveButton);
  this._removeButton.setClickListener(function() {
    if (this._deviceSelector.getSelectedItem() != null) {
      Dialogs.showConfirmDeviceRemovalDialog(this._deviceSelector.getSelectedItem()._info.id);
    }
  }.bind(this));

  this._addButton = UIUtils.appendButton(buttonsPanel, "AddButton", this.getLocale().AddButton);
  this._addButton.setClickListener(function() {
    Application.showPage(AddDevicePage.name);
  });
}

DeviceSelectionPage.prototype.onShow = function() {
  AbstractDataPage.prototype.onShow.call(this);
  
  UIUtils.setVisible(this._initialUpdatePanel, true);
  UIUtils.setEnabled(this._addByIdButton, false);
  this._initialStatusLabel.innerHTML = this.getLocale().UpdatingDevicesLabel;
  
  UIUtils.setVisible(this._deviceSelectionPanel, false);
  
  Backend.getRegisteredDeviceIds(function(status, ids) {
    if (ids.length == 0) {
      this._initialStatusLabel.innerHTML = this.getLocale().NoDevicesAvailableLabel;
      UIUtils.setEnabled(this._addByIdButton, true);
      return;
    } else {
      UIUtils.setVisible(this._initialUpdatePanel, false);
      UIUtils.setVisible(this._deviceSelectionPanel, true);
      this._updateDeviceSelector();
    }

    Backend.addCacheChangeListener(this._cacheChangeListener);
  }.bind(this));
}

DeviceSelectionPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  Backend.removeCacheChangeListener(this._cacheChangeListener);
}


DeviceSelectionPage.prototype._refreshDevices = function() {
  Backend.removeCacheChangeListener(this._cacheChangeListener);
  
  UIUtils.setEnabled(this._addButton, false);
  UIUtils.setEnabled(this._refreshButton, false);
  this._statusLabel.innerHTML = this.getLocale().UpdatingDevicesLabel;
  
  this._deviceSelector.setEnabled(false);
  
  Backend.getRegisteredDeviceIds(function(status, ids) {
    UIUtils.setEnabled(this._addButton, true);
    UIUtils.setEnabled(this._refreshButton, true);
    
    if (status == Backend.OperationResult.SUCCESS) {
      this._statusLabel.innerHTML = this.getLocale().DeviceSelectionLabel;
      this._updateDeviceSelector();
    }
    Backend.addCacheChangeListener(this._cacheChangeListener);
  }.bind(this), true);
}

DeviceSelectionPage.prototype._updateDeviceSelector = function() {
  this._deviceSelector.clear();
  this._devices = {};
  
  this._deviceSelector.setEnabled(true);

  var ids = Backend.getRegisteredDeviceIds();
  for (var i = 0; i < ids.length; i++) {
    Backend.getDeviceInfo(ids[i], function(result, info) {
      if (result == Backend.OperationResult.SUCCESS) {
        this._addDevice(info);
        
        Controller.reportToServer(info, function(success) {
          // success == true means that the device was contacted successfully
        });

        if (info.status == Backend.Status.UNKNOWN) {
          Backend.getDeviceInfo(ids[i], true); // we force the system to pull an update to let the backend find if the device is really connected
        }
      }
    }.bind(this)); 
  }
}

DeviceSelectionPage.prototype._addDevice = function(deviceInfo) {
  var deviceItem = document.createElement("div");
  deviceItem._info = deviceInfo;
  
  this._devices[deviceInfo.id] = deviceItem;
  
  this._deviceSelector.appendItem(deviceItem);
  UIUtils.addClass(deviceItem, "device");

  var activityIndicator = UIUtils.appendBlock(deviceItem, "ActivityIndicator");
  UIUtils.addClass(activityIndicator, "device-indicator");
  UIUtils.addClass(activityIndicator, "device-inactive");
  
  var itemIcon = UIUtils.appendBlock(deviceItem, "Icon");
  UIUtils.addClass(itemIcon, "device-icon");
  itemIcon.style.backgroundImage = deviceInfo.icon;

  deviceItem._indicator = activityIndicator;
  this._setDeviceConnectionStatus(deviceInfo.id);

  var itemLabel = UIUtils.appendLabel(deviceItem, "Label", deviceInfo.name);
  UIUtils.addClass(itemLabel, "device-name");

  var idLabel = UIUtils.appendLabel(deviceItem, "Id", I18n.getLocale().literals.SerialNumber + " " + deviceInfo.serial_number);
  UIUtils.addClass(idLabel, "device-id");
}

DeviceSelectionPage.prototype._updateDevice = function(deviceId) {
  var deviceItem = this._devices[deviceId];
  if (deviceItem == null) {
    return;
  }
  
  deviceItem._info = Backend.getDeviceInfo(deviceId);
  
  this._setDeviceConnectionStatus(deviceId);
}

DeviceSelectionPage.prototype._setDeviceConnectionStatus = function(deviceId) {
  var deviceItem = this._devices[deviceId];
  var deviceInfo = deviceItem._info;
  
  if (deviceInfo.status == Backend.Status.UNKNOWN) {
    UIUtils.removeClass(deviceItem._indicator, "device-connected");
    UIUtils.removeClass(deviceItem._indicator, "device-offline");
    UIUtils.addClass(deviceItem._indicator, "device-inactive");
  } else if (deviceInfo.status == Backend.Status.CONNECTED) {
    UIUtils.removeClass(deviceItem._indicator, "device-inactive");
    UIUtils.removeClass(deviceItem._indicator, "device-offline");
    UIUtils.addClass(deviceItem._indicator, "device-connected");
  } else if (deviceInfo.status == Backend.Status.OFFLINE) {
    UIUtils.removeClass(deviceItem._indicator, "device-inactive");
    UIUtils.removeClass(deviceItem._indicator, "device-connected");
    UIUtils.addClass(deviceItem._indicator, "device-offline");
  }
}