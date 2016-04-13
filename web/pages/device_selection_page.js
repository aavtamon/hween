DeviceSelectionPage = ClassUtils.defineClass(AbstractPage, function DeviceSelectionPage() {
  AbstractPage.call(this, DeviceSelectionPage.name);
  
  this._deviceSelecionLabel;
  this._noDevicesAvailableLabel;
  this._updatingDevicesLabel;
  this._deviceSelector;
});

DeviceSelectionPage.prototype.definePageContent = function(root) {
  var deviceSelecionPanel = UIUtils.appendBlock(root, "DeviceSelectionPanel");
  
  this._deviceSelecionLabel = UIUtils.appendLabel(deviceSelecionPanel, "DeviceSelectionLabel", this.getLocale().DeviceSelectionLabel);
  UIUtils.setVisible(this._deviceSelecionLabel, false);
  
  this._noDevicesAvailableLabel = UIUtils.appendLabel(deviceSelecionPanel, "NoDevicesAvailableLabel", this.getLocale().NoDevicesAvailableLabel);
  UIUtils.setVisible(this._noDevicesAvailableLabel, false);
  
  this._updatingDevicesLabel = UIUtils.appendLabel(deviceSelecionPanel, "UpdatingDevicesLabel", this.getLocale().UpdatingDevicesLabel);
  
  this._deviceSelector = UIUtils.appendGallery(deviceSelecionPanel, "DeviceSelector");
}

DeviceSelectionPage.prototype.onShow = function() {
  Backend.getDeviceIds(function(status, ids) {
    if (status == Backend.OperationResult.SUCCESS) {
      UIUtils.setVisible(this._updatingDevicesLabel, false);
      
      if (ids.length == 0) {
        UIUtils.setVisible(this._noDevicesAvailableLabel, true);
        return;
      }
      
      UIUtils.setVisible(this._deviceSelecionLabel, true);
      
      for (var i = 0; i < ids.length; i++) {
        Backend.getDeviceInfo(ids[i], function(status, info) {
          if (status == Backend.OperationResult.SUCCESS) {
            this._deviceSelector.addItem({id: info.id, label: info.label, icon: info.icon, function(id) {
              console.debug("Selected device " + id);
            }});
          }
        }.bind(this)); 
      }
    }
  }.bind(this));
}

DeviceSelectionPage.prototype.onHide = function() {
}

