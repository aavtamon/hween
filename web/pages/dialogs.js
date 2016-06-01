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
        Backend.addNewDevice(deviceSnInput.getValue(), verificationInput.getValue(), function(status) {
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


Dialogs.showUploadStockProgramDialog = function(deviceId, libraryProgram) {
  var categoryChooser;
  
  var dialog = UIUtils.showDialog("UploadStockProgramDialog", I18n.getLocale().dialogs.UploadStockProgramDialog.Title, function(contentPanel) {
    UIUtils.appendLabel(contentPanel, "CategoryLabel", I18n.getLocale().dialogs.UploadStockProgramDialog.CategoryLabel);
    categoryChooser = UIUtils.appendDropList(contentPanel, "CategoryChooser", Backend.getStockCategories());
  }, {
    ok: {
      display: I18n.getLocale().dialogs.UploadStockProgramDialog.UploadButton,
      listener: function() {
        if (Dialogs._processing) {
          return;
        }

        Dialogs._processing = true;
        
        var stockProgram = {
          title: libraryProgram.title,
          category: categoryChooser.getValue()
        }
        
        Backend.addStockProgram(deviceId, stockProgram, function(status) {
          if (status == Backend.OperationResult.SUCCESS) {
            dialog.close();
            UIUtils.showMessage(I18n.getLocale().dialogs.UploadStockProgramDialog.SuccessfullyUploadedMessage);
          } else if (status == Backend.OperationResult.FAILURE) {
            UIUtils.showMessage(I18n.getLocale().dialogs.UploadStockProgramDialog.FailedToUploadMessage);
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


Dialogs.showConfirmDeviceRemovalDialog = function(deviceId) {
  var dialog = UIUtils.showDialog("ConfirmDeviceRemovalDialog", I18n.getLocale().dialogs.ConfirmDeviceRemovalDialog.Title, I18n.getLocale().dialogs.ConfirmDeviceRemovalDialog.TextProvider(Backend.getDeviceInfo(deviceId).name), {
    ok: {
      display: I18n.getLocale().dialogs.ConfirmDeviceRemovalDialog.ConfirmRemovalButton,
      listener: function() {
        Backend.unregisterDevices([deviceId], function(status) {
          if (status == Backend.OperationResult.SUCCESS) {
            dialog.close();
          }
        })
      }
    },
    cancel: {
      display: I18n.getLocale().literals.CancelOperationButton,
      alignment: "left"
    }
  });
}


Dialogs.showConfirmProgramRemovalDialog = function(successCallback) {
  var dialog = UIUtils.showDialog("ConfirmProgramRemovalDialog", I18n.getLocale().dialogs.ConfirmProgramRemovalDialog.Title, I18n.getLocale().dialogs.ConfirmProgramRemovalDialog.Text, {
    ok: {
      display: I18n.getLocale().dialogs.ConfirmProgramRemovalDialog.ConfirmRemovalButton,
      listener: function() {
        dialog.close();
        if (successCallback) {
          successCallback();
        }
      }
    },
    cancel: {
      display: I18n.getLocale().literals.CancelOperationButton,
      alignment: "left"
    }
  });
}
