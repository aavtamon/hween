DeviceSelectionPage = ClassUtils.defineClass(AbstractPage, function DeviceSelectionPage() {
  AbstractPage.call(this, DeviceSelectionPage.name);
  
  this._noDevicesAvailableLabel;
  this._updatingDevicesLabel;
  this._deviceSelectionPanel;
  this._deviceSelector;
  
  this._devices = {};
});

DeviceSelectionPage.prototype.definePageContent = function(root) {
  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  this._noDevicesAvailableLabel = UIUtils.appendLabel(contentPanel, "NoDevicesAvailableLabel", this.getLocale().NoDevicesAvailableLabel);
  UIUtils.setVisible(this._noDevicesAvailableLabel, false);
  
  this._updatingDevicesLabel = UIUtils.appendLabel(contentPanel, "UpdatingDevicesLabel", this.getLocale().UpdatingDevicesLabel);
  
  this._deviceSelectionPanel = UIUtils.appendBlock(contentPanel, "DeviceSelectionPanel");
  UIUtils.appendLabel(this._deviceSelectionPanel, "DeviceSelectionLabel", this.getLocale().DeviceSelectionLabel);
  this._deviceSelector = UIUtils.appendGallery(this._deviceSelectionPanel, "DeviceSelector");
  
  UIUtils.setVisible(this._deviceSelectionPanel, false);
  
  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var addButton = UIUtils.appendButton(buttonsPanel, "AddButton", this.getLocale().AddButton);
}

DeviceSelectionPage.prototype.onShow = function() {
  Controller.startDiscovery(function(deviceId, status) {
    var deviceItem = this._devices[deviceId];
    if (deviceItem != null) {
      if (status == Controller.ConnectionStatus.UNKNOWN) {
        UIUtils.removeClass(deviceItem._indicator, "device-connected");
        UIUtils.removeClass(deviceItem._indicator, "device-offline");
        UIUtils.addClass(deviceItem._indicator, "device-inactive");
      } else if (status == Controller.ConnectionStatus.CONNECTED) {
        UIUtils.removeClass(deviceItem._indicator, "device-inactive");
        UIUtils.removeClass(deviceItem._indicator, "device-offline");
        UIUtils.addClass(deviceItem._indicator, "device-connected");
      } else if (status == Controller.ConnectionStatus.OFFLINE) {
        UIUtils.removeClass(deviceItem._indicator, "device-inactive");
        UIUtils.removeClass(deviceItem._indicator, "device-connected");
        UIUtils.addClass(deviceItem._indicator, "device-offline");
      }
    }
  }.bind(this));
  
  Backend.getDeviceIds(function(status, ids) {
    if (status == Backend.OperationResult.SUCCESS) {
      UIUtils.setVisible(this._updatingDevicesLabel, false);
      
      if (ids.length == 0) {
        UIUtils.setVisible(this._noDevicesAvailableLabel, true);
        return;
      }
      
      UIUtils.setVisible(this._deviceSelectionPanel, true);
      
      for (var i = 0; i < ids.length; i++) {
        Backend.getDeviceInfo(ids[i], function(status, info) {
          if (status == Backend.OperationResult.SUCCESS) {
            this._addDevice(info);
          }
        }.bind(this)); 
      }
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