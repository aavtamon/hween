var Locale_eng = {
  "images": {
  },
  "literals": {
    "AppTitle": "Helloween Toy Controller",
    "ContactUs": "Contact Us",
    
    "ProfileItem": "Profile",
    "PreferencesItem": "Preferences",
    "LogOutItem": "Log Out",

    "LanguageEnglish": "English",
    "LanguageSpanish": "Spanish",
    
    "FrequencyAlways": "always",
    "FrequencyOften": "often",
    "FrequencyRare": "rare",
    "FrequencyOnce": "once",
    "FrequencyNever": "never",
    
    "ServerErrorMessage": "Server communication error",
    
    "CancelOperationButton": "Cancel",
    "OkButton": "OK",
    "AttachButton": "Attach",
    "ConfirmButton": "Confirm",
    "SearchButton": "Search",
    "ClearSearchButton": "Clear",
        
    "EmailLoginLabel": "Email",
    "PasswordLabel": "Password",
    "RetypePasswordLabel": "Confirm Password",
    
    "NoResultsFound": "No results found",
    
    "SerialNumber": "s/n",
    

    
    "IncorrectAttachmentMessage": "You can only attach images",
    "AttachmentTooBigMessageProvider": function(maxSize) { return "The image size should be less than " + maxSize +" MGb" },
  },
  "pages": {
    "AbstractDataPage": {
      "NoContentLabel": "This page cannot be displayed because you are not logged in",
      "LoginLink": "Login",
      "ExpiredLabel": "This page is expired",
      "CannotDisplayLabel": "The page cannot be displayed"
    },
    "LoginPage": {
      "RememberLoginLabel": "Keep me logged in",
      "SignInButton": "Log In",
      "ForgotPassowrdLink": "Forgot your password?",
      "RegisterLink": "Sign Up",
      "DownloadAppsLabel": "Download our mobile applications:",
      
      "SearchLabel": "Check it out!",
      "SearchResultsLabel": "Search what our members offer.<br>Note, that you will need to login in order to be able to rent anything from this list.",
      
      "InvalidCredentialsMessage": "Invalid login/password combination",
      "InvalidLoginMessage": "Please provide a valid email for your login",
      "ProvideLoginPasswordMessage": "Please provide login and password",
      "PasswordResetMessage": "You will receive an email shortly with a link to reset the password. You may ignore the email if you do not need to reset your password.",
      "PasswordResetRequestMessage": "The request is being sent...",
      "IncorrectEmailMessage": "Your login does not look like a valid email",
      "UnexistingEmailMessage": "Something went wrong. Please make sure that you provided a correct email address",
      
      "DescriptionHtml": "<center><h1>Welcome!</h1></center><br>This site let you control all Helloween Toys that you have at home. You will be able to download tons of different programs for your toys, create your own programs and share them with others."
    },
    "RegisterPage": {
      "SignUpLabel": "Sign Up",
      "NameLabel": "Name",
      "AccessCodeLabel": "Access Code",
      "AccessCodeExplanationTilte": "What is Access Code?",
      "AccessCodeExplanationText": "Access Code is printed on the box that you received your Helloween Toy in. If you cannot locate it, please contact our customer service and we will recover it for you",
      
      "AcceptTermsProvider": function(linkId) { return "I acknowledge that have read and agree to the <a id='" + linkId + "'>Terms And Conditions</a>"; },
      
      "LoginButton": "Login",

      "ProvideEmailMessage": "The email is not provided or does not look like a valid email address",
      "ProvideNameMessage": "You must provide a valid name. Do not use special characters",
      "ProvideAccessCodeMessage": "You must provide a valid access code",
      "ProvideCorrectPasswordMessage": "Password should be at least 5 symbols long",
      "PasswordsDoNotMatchMessage": "Passwords do not match. Please retype.",
      "MustAcceptTermsMessageProvider": function(linkId) { return "You must accept<p><a id='" + linkId + "'><b>Terms And Conditions<b></a>"; },
      "AccountCreationFailedMessage": "Failed to create an account",
      "AccountAlreadyExistsMessage": "This login (email) was already used",
    },
    "RestorePasswordPage": {
      "ChangePasswordButton": "Change password",
      "LoginLink": "Login",
      
      "PasswordChangedMessage": "Your password was successfully changed",
      "UnknownLoginOrTokenMessage": "Your recovery token expired or you provided an incorrect email",
      "ProvideLoginMessage": "The email is not provided or does not look like a valid email address",
      "ProvideCorrectPasswordMessage": "Password should be at least 5 symbols long",
      "PasswordsDoNotMatchMessage": "Passwords do not match. Please retype.",
    },
    "PageNotFoundPage": {
      "NotFoundLabel": "Page Not Found",
      "CreateRequestDialogTitle": "I Need..."
    },
    "DeviceSelectionPage": {
      "DeviceSelectionLabel": "Select the device that you want to work with:",
      "NoDevicesAvailableLabel": "You have no devices yet activated. Click <b>Add Device</b> button",
      "UpdatingDevicesLabel": "Retriving your devices, please wait...",
      "AddButton": "Add Device",
      "RemoveButton": "Remove Device",
      "RefreshButton": "Refresh"
    },
    "AddDevicePage": {
      "GoBackButton": "Go back",
      "SearchingDevicesLabel": "Searching new devices on your network...",
      "FoundNewDevicesLabel": "Found new devices:",
      "NoNewDevicesFoundLabel": "No new devices were found",
      "ServerErrorLabel": "Cannot communicate with the server. It could be a problem with your internet connection",
      "AddDevicesButton": "Add selected devices",
      "ScanButton": "Rescan",
      "AddByIdLabel": "Don't see your device in the list?",
      "AddByIdButton": "Add By Serial Number",
      "UnrecognizedDeviceIdMessage": "The Device with this s/n is not found. Please make sure the device is online",
    },
    "DeviceManagementPage": {
      "ManualModeButton": "Manual Mode",
      "DeviceSettingsButton": "Device Settings",
      "ProgramSelectionLabel": "Each program in the list runs after:",
      "RemoveSelectedButton": "Remove selected",
      "AddProgramButton": "Add program",
      "AddNewProgramButton": "New",
      "AddLibraryProgramButton": "Library",
      "AddStockProgramButton": "Stock",
      "UpdatingListOfProgramsLabel": "Please wait while we are retrieving your programs",
      "NoProgramsAvailableLabel": "You have no programs added. Click 'Add program' button to add some",
      "ManageProgramsButton": "Manage Program Library",
      "RunButton": "Run On Device",
      "StopButton": "Stop",
      "BackButton": "Back To Device Selection",
      "ScheduleStatusIdle": "Device is idle",
      "ScheduleStatusRunning": "Currently Running On Device",
      "ScheduleStatusManual": "Device is controled manually",
    },
    "ManageLibraryProgramsPage": {
      "RemoveSelectedButton": "Delete selected",
      "LoadSelectedButton": "Load selected",
      "CreateProgramButton": "Create Program",
      "UploadSelectedButton": "Upload to stock",
      "UpdatingListOfProgramsLabel": "Please wait while we are retrieving your programs",
      "NoProgramsAvailableLabel": "You have no programs added. Click 'Add program' button to add some",
      "DescriptionLabel": "Description",
    },
    "StockProgramsPage": {
      "LoadSelectedButton": "Load selected",
      "UpdatingListOfProgramsLabel": "Please wait while we are retrieving your programs",
      "NoProgramsAvailableLabel": "No matching stock programs found",
      "DescriptionLabel": "Description",
    },
    "CreateProgramPage": {
      "ProgramLabel": "Program",
      "PlayButton": "Play",
      "PauseButton": "Pause",
      "StopButton": "Stop",
      "RemoveCommandButton": "Delete",
      "AddCommandButton": "Add Command To Program",
      "SaveButton": "Save",
      "DescriptionLabel": "Description",
      
      "Executing": "executing",
      "Paused": "paused...",
      
      "ProgramExecutionTerminatedMessage": "Program Execution interrupted"
    },
    "ManualModePage": {
      "DeviceReadyForCommandLabel": "The device is ready to your commands. Just click it",
      "DeviceProcessingCommandLabel": "The device is processing your command",
      "BackButton": "Go Back",
      "ResetToInitialPositionButton": "Reset To Initial Position",
      "MoveUpButton": "Move Up",
      "MoveDownButton": "Move Down",
      "TurnLeftButton": "Turn Left",
      "TurnRightButton": "Turn Right",
      "EyeControlButtonOn": "Eyes On",
      "EyeControlButtonOff": "Eyes Off",
      "TalkButton": "Make A Noise",
      "IncorrectAudioFileMessage": "The file should be a valid audio file"
    }
  },
  
  "dialogs": {
    "AddDeviceByIdDialog": {
      "Title": "Adding Device...",
      "DescriptionLabel": "Please enter your Halloween Toy serial number. You can find it printed on the box or on the label",
      "SerialNumberLabel": "Toy serial number:",
      "VerificationLabel": "Verification code:",
      "AddButton": "Add",
      
      "IncorrectDeviceIdMessage": "Incorrect Device s/n.<br>Valid id should be a 10-digit number",
      "IncorrectVerificationCodeMessage": "The verification code is invalid.<br>It should be a 6 symbol alpha-digital case-insensitive combination.",
      "DeviceAddedMessage": "The device was successfully added",
      "UnrecognizedDeviceMessage": "The Device s/n or verification code do not match our records. Please check your input and try again"
    },
    "UploadStockProgramDialog": {
      "Title": "Upload Library Program To Stock",
      "CategoryLabel": "Choose the category that best matches this program:",
      "UploadButton": "Upload",
      "SuccessfullyUploadedMessage": "The program was successfully posted to the stock",
      "FailedToUploadMessage": "Failed to post the program"
    },
    "ConfirmDeviceRemovalDialog": {
      "Title": "Removal Confirmation",
      "TextProvider": function(deviceName) { return "Are you sure you want to remove the device " + deviceName + "?"; },
      "ConfirmRemovalButton": "Remove",
    },
    "ConfirmProgramRemovalDialog": {
      "Title": "Removal Confirmation",
      "Text": "Do you really want to remove the selected program(s)?<br>If you click Remove, the slected programs will be permanently deleted.",
      "ConfirmRemovalButton": "Remove",
    }
  }
}
