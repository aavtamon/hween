Dialogs = {
  _processing: false
}


Dialogs.showAddDeviceByIdDialog = function() {
  var deviceIdInput;
  
  var dialog = UIUtils.showDialog("AddDeviceByIdDialog", I18n.getLocale().dialogs.AddDeviceByIdDialog.Title, function(contentPanel) {
    UIUtils.appendLabel(contentPanel, "DescriptionLabel", I18n.getLocale().dialogs.AddDeviceByIdDialog.DescriptionLabel);
    deviceIdInput = UIUtils.appendTextInput(contentPanel, "DeviceIdInput", 10, ValidationUtils.NUMBER_REGEXP);
    
    deviceIdInput.focus();
  }, {
    ok: {
      display: I18n.getLocale().dialogs.AddDeviceByIdDialog.AddButton,
      listener: function() {
        if (Dialogs._processing) {
          return;
        }

        if (deviceIdInput.getValue() == null || deviceIdInput.getValue().length != 10) {
          UIUtils.indicateInvalidInput(deviceIdInput);
          UIUtils.showMessage(I18n.getLocale().dialogs.AddDeviceByIdDialog.IncorrectDeviceIdMessage);
          return;
        }
        
        Dialogs._processing = true;
        dialog.close();
        Application.showPage(DeviceSelectionPage.name);
      }
    },
    cancel: {
      display: I18n.getLocale().literals.CancelOperationButton,
      alignment: "left"
    }
  });
}
