DeviceSelectionPage = ClassUtils.defineClass(AbstractPage, function DeviceSelectionPage() {
  AbstractPage.call(this, DeviceSelectionPage.name);
  
  this._noDevicesAvailableLabel;
  this._updatingDevicesLabel;
  this._deviceSelectionPanel;
  this._deviceSelector;
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
            this._deviceSelector.addItem({id: info.id, label: info.name, icon: info.icon, clickListener: function(id) {
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

