Dialogs = {
  _processing: false
}


Dialogs.showAddDeviceByIdDialog = function() {
  var deviceSnInput;
  var verificationInput;
  
  var dialog = UIUtils.showDialog("AddDeviceByIdDialog", I18n.getLocale().dialogs.AddDeviceByIdDialog.Title, function(contentPanel) {
    UIUtils.appendLabel(contentPanel, "DescriptionLabel", I18n.getLocale().dialogs.AddDeviceByIdDialog.DescriptionLabel);
    UIUtils.appendLabel(contentPanel, "SnLabel", I18n.getLocale().dialogs.AddDeviceByIdDialog.SerialNumberLabel);
    deviceSnInput = UIUtils.appendTextInput(contentPanel, "DeviceSnInput", 10, ValidationUtils.NUMBER_REGEXP);
    
    UIUtils.appendBlock(contentPanel);
    
    UIUtils.appendLabel(contentPanel, "VerificationLabel", I18n.getLocale().dialogs.AddDeviceByIdDialog.VerificationLabel);
    verificationInput = UIUtils.appendTextInput(contentPanel, "VerificationInput", 6, ValidationUtils.NUMBER_REGEXP);
    
    deviceSnInput.focus();
  }, {
    ok: {
      display: I18n.getLocale().dialogs.AddDeviceByIdDialog.AddButton,
      listener: function() {
        if (Dialogs._processing) {
          return;
        }

        if (deviceSnInput.getValue() == null || deviceSnInput.getValue().length != 10) {
          UIUtils.indicateInvalidInput(deviceSnInput);
          UIUtils.showMessage(I18n.getLocale().dialogs.AddDeviceByIdDialog.IncorrectDeviceIdMessage);
          return;
        }
        if (verificationInput.getValue() == null || verificationInput.getValue().length != 6) {
          UIUtils.indicateInvalidInput(verificationInput);
          UIUtils.showMessage(I18n.getLocale().dialogs.AddDeviceByIdDialog.IncorrectVerificationCodeMessage);
          return;
        }
        
        Dialogs._processing = true;
        Backend.registerDevice(deviceSnInput.getValue(), verificationInput.getValue(), function(status) {
          if (status == Backend.OperationResult.SUCCESS) {
            dialog.close();
            UIUtils.showMessage(I18n.getLocale().dialogs.AddDeviceByIdDialog.DeviceAddedMessage);
            Application.showPage(DeviceSelectionPage.name);
          } else if (status == Backend.OperationResult.FAILURE) {
            UIUtils.showMessage(I18n.getLocale().dialogs.AddDeviceByIdDialog.UnrecognizedDeviceMessage);
          } else {
            UIUtils.showMessage(I18n.getLocale().literal.ServerErrorMessage);
          }
        });
      }
    },
    cancel: {
      display: I18n.getLocale().literals.CancelOperationButton,
      alignment: "left"
    }
  });
}
