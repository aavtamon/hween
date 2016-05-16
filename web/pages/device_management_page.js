DeviceManagementPage = ClassUtils.defineClass(AbstractDataPage, function DeviceManagementPage() {
  AbstractDataPage.call(this, DeviceManagementPage.name);
  
  this._deviceId;
  
  this._cacheChangeListener = function(event) {
    if (event.type == Backend.CacheChangeEvent.TYPE_DEVICE_INFO && this._deviceId == event.objectId) {
    }
  }.bind(this);
});

DeviceManagementPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  var toolbarPanel = UIUtils.appendBlock(contentPanel, "ToolbarPanel");
  var manualModeButton = UIUtils.appendButton(toolbarPanel, "ManualModeButton", this.getLocale().ManualModeButton);
  manualModeButton.setClickListener(function() {
    Application.showPage(ManualModePage.name, {deviceId: this._deviceId});
  }.bind(this));

  var deviceSettingsButton = UIUtils.appendButton(toolbarPanel, "DeviceSettingsButton", this.getLocale().DeviceSettingsButton);
  deviceSettingsButton.setClickListener(function() {
    Application.showPage(DeviceSettingsPage.name, {deviceId: this._deviceId});
  }.bind(this));
}

DeviceManagementPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  
  console.debug(bundle)
  
  Backend.addCacheChangeListener(this._cacheChangeListener);
}

DeviceManagementPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
  
  Backend.removeCacheChangeListener(this._cacheChangeListener);
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

