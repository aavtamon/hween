DeviceSettingsPage = ClassUtils.defineClass(AbstractDataPage, function DeviceSettingsPage() {
  AbstractDataPage.call(this, DeviceSettingsPage.name);
  
  this._deviceId;
});

DeviceSettingsPage.prototype.definePageContent = function(root) {
  AbstractDataPage.prototype.definePageContent.call(this, root);

  var contentPanel = UIUtils.appendBlock(root, "ContentPanel");
  
  UIUtils.appendLabel(contentPanel, "SettingsLabel", this.getLocale().NoSettings);
  

  var buttonsPanel = UIUtils.appendBlock(contentPanel, "ButtonsPanel");
  var cancelButton = UIUtils.appendButton(buttonsPanel, "CancelButton", I18n.getLocale().CancelOperationButton);
  cancelButton.setClickListener(Application.goBack.bind(Application));
  
  var saveButton = UIUtils.appendButton(buttonsPanel, "SaveButton", this.getLocale().SaveButton);
  saveButton.setClickListener(function() {
  });
  UIUtils.setEnabled(saveButton, false);
}

DeviceSettingsPage.prototype.onShow = function(root, bundle) {
  AbstractDataPage.prototype.onShow.call(this);
  this._deviceInfo = Backend.getDeviceInfo(this._deviceId);
}

DeviceSettingsPage.prototype.onHide = function() {
  AbstractDataPage.prototype.onHide.call(this);
}
