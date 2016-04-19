AddDevicePage = ClassUtils.defineClass(AbstractPage, function AddDevicePage() {
  AbstractPage.call(this, AddDevicePage.name);
  
  this._progressIndicator;
  this._devices = {};
});

AddDevicePage.prototype.definePageContent = function(root) {
  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var devicesPanel = UIUtils(contentPanel, "DevicesPanel");
  
  UIUtils.appendLabel(devicesPanel, "FoundDevicesLabel", this.getLocale().NoDevicesAvailableLabel);
  
  UIUtils.appendList(devicesPanel, "DevicesList");
  
  var buttonsPanel = UIUtils.appendBlock(devicesPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().literals.CancelOperationButton);
  var addButton = UIUtils.appendButton(buttonsPanel, "AddDevicesButton", this.getLocale().AddDevicesButton);
  
  var progressPanel = UIUtils(contentPanel, "ProgressPanel");
  this._progressIndicator = UIUtils.appendBlock(progressPanel, "DiscoveryInProgress");
  UIUtils.setVisible(this._progressIndicator, false);
}

AddDevicePage.prototype.onShow = function() {
  UIUtils.setVisible(this._progressIndicator, false);
  
  Backend.getDeviceIds(function(status, ids) {
    if (status != Backend.OperationResult.SUCCESS) {
      return;
    }
    
    UIUtils.setVisible(this._updatingDevicesLabel, false);

    if (ids.length == 0) {
      UIUtils.setVisible(this._noDevicesAvailableLabel, true);
      return;
    }

    UIUtils.setVisible(this._deviceSelectionPanel, true);

    for (var i = 0; i < ids.length; i++) {
      Backend.getDeviceInfo(ids[i], function(result, info) {
        if (result == Backend.OperationResult.SUCCESS) {
          this._addDevice(info);

          if (info.status == Backend.Status.OFFLINE) {
            this._setDeviceConnectionStatus(info, Backend.Status.OFFLINE);
          } else {
            this._setDeviceConnectionStatus(info, Backend.Status.UNKNOWN);

            Controller.isAvailable(info, function(isAvailable) {
              this._setDeviceConnectionStatus(info, isAvailable ? Backend.Status.CONNECTED : Backend.Status.OFFLINE);
            }.bind(this));
          }
        }
      }.bind(this)); 
    }
  }.bind(this));
}

DeviceSelectionPage.prototype.onHide = function() {
  Controller.stopDiscovery();
}


DeviceSelectionPage.prototype._addDevice = function(deviceInfo) {
  var deviceItem = document.createElement("div");
  deviceItem._info = deviceInfo;
  
  this._devices[deviceInfo.id] = deviceItem;
  
  
  this._deviceSelector.appendItem(deviceItem);
  UIUtils.addClass(deviceItem, "device");
  
  var itemIcon = UIUtils.appendBlock(deviceItem, "Icon");
  UIUtils.addClass(itemIcon, "device-icon");
  itemIcon.style.backgroundImage = deviceInfo.icon;

  var activityIndicator = UIUtils.appendBlock(itemIcon, "ActivityIndicator");
  UIUtils.addClass(activityIndicator, "device-indicator");
  UIUtils.addClass(activityIndicator, "device-inactive");
  
  deviceItem._indicator = activityIndicator;

  var itemLabel = UIUtils.appendLabel(deviceItem, "Label", deviceInfo.name);
  UIUtils.addClass(itemLabel, "device-");

  UIUtils.setClickListener(deviceItem, function() {
    console.debug("Clicked element " + deviceInfo.id);
  });
}

DeviceSelectionPage.prototype._setDeviceConnectionStatus = function(deviceInfo, status) {
  var deviceItem = this._devices[deviceInfo.id];
  
  if (status == Backend.Status.UNKNOWN) {
    UIUtils.removeClass(deviceItem._indicator, "device-connected");
    UIUtils.removeClass(deviceItem._indicator, "device-offline");
    UIUtils.addClass(deviceItem._indicator, "device-inactive");
  } else if (status == Backend.Status.CONNECTED) {
    UIUtils.removeClass(deviceItem._indicator, "device-inactive");
    UIUtils.removeClass(deviceItem._indicator, "device-offline");
    UIUtils.addClass(deviceItem._indicator, "device-connected");
  } else if (status == Backend.Status.OFFLINE) {
    UIUtils.removeClass(deviceItem._indicator, "device-inactive");
    UIUtils.removeClass(deviceItem._indicator, "device-connected");
    UIUtils.addClass(deviceItem._indicator, "device-offline");
  }
}