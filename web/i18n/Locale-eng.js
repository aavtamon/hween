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
      "AddButton": "Add Device"
    },
    "AddDevicePage": {
      "SearchingDevicesLabel": "Searching new devices on your network...",
      "FoundNewDevicesLabel": "Found new devices:",
      "NoNewDevicesFoundLabel": "No new devices were found",
      "ServerErrorLabel": "Cannot communicate with the server. It could be a problem with your internet connection",
      "AddDevicesButton": "Add Selected Devices",
      "ScanButton": "Rescan",
      "AddByIdLabel": "Don't see your device in the list?",
      "AddByIdButton": "Add By ID",
      "UnrecognizedDeviceIdMessage": "The Device Id is not recognized. Please make sure the device is online"
    }
  },
  
  "dialogs": {
    "AddDeviceByIdDialog": {
      "Title": "Adding Device...",
      "DescriptionLabel": "Please enter your Halloween Toy id. You can find it printed on the box or on the label:",
      "AddButton": "Add",
      
      "IncorrectDeviceIdMessage": "Incorrect Device Id.<br>Valid id should be a 10-digit number",
      "UnrecognizedDeviceIdMessage": "The Device Id is not recognized. Please make sure the device is online"
    }
  }
}
