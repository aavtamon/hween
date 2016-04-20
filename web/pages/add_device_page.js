AddDevicePage = ClassUtils.defineClass(AbstractDataPage, function AddDevicePage() {
  AbstractPage.call(this, AddDevicePage.name);
  
  this._progressIndicator;
  this._devices = {};
});

AddDevicePage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);
  
  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var devicesPanel = UIUtils.appendBlock(contentPanel, "DevicesPanel");
  
  UIUtils.appendLabel(devicesPanel, "FoundDevicesLabel", this.getLocale().NoDevicesAvailableLabel);
  
  UIUtils.appendList(devicesPanel, "DevicesList");
  
  var buttonsPanel = UIUtils.appendBlock(devicesPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().literals.CancelOperationButton);
  var addButton = UIUtils.appendButton(buttonsPanel, "AddDevicesButton", this.getLocale().AddDevicesButton);
  
  var progressPanel = UIUtils.appendBlock(contentPanel, "ProgressPanel");
  this._progressIndicator = UIUtils.appendBlock(progressPanel, "DiscoveryInProgress");
  UIUtils.setVisible(this._progressIndicator, false);
}

AddDevicePage.prototype.onShow = function() {
  AbstractDataPage.prototype.onShow.call(this);
  
  UIUtils.setVisible(this._progressIndicator, true);
  
  
  Backend.getDeviceIds(function(status, ids) {
    if (status != Backend.OperationResult.SUCCESS) {
      return;
    }
    
    if (ids.length == 0) {
      UIUtils.setVisible(this._progressIndicator, false);
      return;
    }

    for (var i = 0; i < ids.length; i++) {
      this._devices[ids[i]] = null;
      
      Backend.getDeviceInfo(ids[i], function(result, info) {
        if (result == Backend.OperationResult.SUCCESS) {
          if (info.status == Backend.Status.DISCOVERED) {
            Controller.isAvailable(info, function(isAvailable) {
              if (isAvailable) {
                this._devices[ids[i]] = info;
                this._addDeviceElement(ids[i]);
                this._stopProgressIndicationIfDiscoveryCompleted();
              }
            }.bind(this));
          } else {
            this._devices[ids[i]] = {};
            this._stopProgressIndicationIfDiscoveryCompleted();
          }
        } else {
          this._devices[ids[i]] = {};
          this._stopProgressIndicationIfDiscoveryCompleted();
        }
      }.bind(this)); 
    }
  }.bind(this));
}

AddDevicePage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
}

AddDevicePage.prototype._stopProgressIndicationIfDiscoveryCompleted = function() {
  for (var id in this._devices) {
    if (this._devices[id] == null) {
      return;
    }
  }
  
  UIUtils.setVisible(this._progressIndicator, false);
}

AddDevicePage.prototype._addDeviceElement = function(id) {
  var deviceItem = document.createElement("div");
  UIUtils.addClass(deviceItem, "discovered-device");
  
  var info = this._devices[id];
  this._devices[id] = {element: deviceItem, dislay: info.name, info: info};

  var selectionBox = UIUtils.appendCheckbox(deviceItem, "Selection");
  UIUtils.addClass(itemIcon, "device-selection");
                       
  var itemIcon = UIUtils.appendBlock(deviceItem, "Icon");
  UIUtils.addClass(itemIcon, "device-icon");
  itemIcon.style.backgroundImage = info.icon;

  var itemLabel = UIUtils.appendLabel(deviceItem, "Label", deviceInfo.name);
  UIUtils.addClass(itemLabel, "device-name");

  UIUtils.setClickListener(deviceItem, function() {
    console.debug("Clicked element " + info.id);
  });
}
