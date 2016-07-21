Backend._SERVER_BASE_URL = "http://localhost:8080/HweenToy/";


Backend.OperationResult = {};
Backend.OperationResult.SUCCESS = "success";
Backend.OperationResult.FAILURE = "failure";
Backend.OperationResult.ERROR = "error";

Backend.CacheChangeEvent.TYPE_DEVICE_IDS = "device_ids";
Backend.CacheChangeEvent.TYPE_DEVICE_INFO = "device_info";

Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE = "device_schedule";
Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS = "library_programs";
Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS = "stock_programs";
Backend.CacheChangeEvent.TYPE_DEVICE_MODE = "device_mode";

Backend.CacheChangeEvent.TYPE_DEVICE_SETTINGS = "device_settings";


Backend.DeviceType = {};
Backend.DeviceType.STUMP_GHOST = "stump_ghost";

Backend.Status = {};
Backend.Status.UNKNOWN = "unknown";
Backend.Status.CONNECTED = "connected";
Backend.Status.OFFLINE = "offline";
Backend.Status.DISCOVERED = "discovered";


Backend.Program = {};
Backend.Program.FREQUENCY_NEVER = "never";
Backend.Program.FREQUENCY_ONCE = "once";
Backend.Program.FREQUENCY_RARE = "rare";
Backend.Program.FREQUENCY_OFTEN = "often";
Backend.Program.FREQUENCY_ALWAYS = "always";
Backend.Program.TRIGGER_IMMEDIATELY = "immediately";
Backend.Program.TRIGGER_DELAY = "delay";
Backend.Program.TRIGGER_MOTION = "motion";


Backend.DeviceCommand = {};
Backend.DeviceCommand.RESET = "reset";
Backend.DeviceCommand.MOVE_UP = "move_up";
Backend.DeviceCommand.MOVE_DOWN = "move_down";
Backend.DeviceCommand.TURN_LEFT = "turn_left";
Backend.DeviceCommand.TURN_RIGHT = "turn_right";
Backend.DeviceCommand.EYES_ON = "eyes_on";
Backend.DeviceCommand.EYES_OFF = "eyes_off";
Backend.DeviceCommand.TALK = "talk";
Backend.DeviceCommand.PAUSE = "pause";



Backend.DeviceMode = {};
Backend.DeviceMode.IDLE = "idle";
Backend.DeviceMode.RUNNING_SCHEDULE = "running";
Backend.DeviceMode.MANUAL = "manual";



// Device Type Management

Backend.getDeviceSettings = function(deviceType, operationCallback) {
  var categories = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SETTINGS, deviceType);

  if (categories == null) {
    this._pullDeviceSettings(deviceType, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, categories);
  }
  
  return categories;
}
Backend._pullDeviceSettings = function(deviceType, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SETTINGS, deviceType);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SETTINGS, deviceType, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SETTINGS, deviceType, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("settings/device/" + deviceType, "GET", null, true, this._getAuthenticationHeader(), communicationCallback);
}



// Device Management

Backend.getDeviceIds = function(operationCallback, forceUpdate) {
  var deviceIds = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);

  if (deviceIds == null) {
    this._pullDeviceIds(operationCallback);
  } else if (forceUpdate) {
    this._pullDeviceIds(operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceIds);
  }
  
  return deviceIds;
}
Backend._pullDeviceIds = function(operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices", "GET", null, true, this._getAuthenticationHeader(), communicationCallback);
}

Backend.getDeviceInfo = function(deviceId, operationCallback, forceUpdate) {
  var deviceInfo = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId);
  
  if (deviceInfo == null) {
    this._pullDeviceInfo(deviceId, operationCallback);
  } else if (forceUpdate) {
    this._pullDeviceInfo(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceInfo);
  }
  
  return deviceInfo;
}
Backend._pullDeviceInfo = function(deviceId, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices/" + deviceId, "GET", null, true, this._getAuthenticationHeader(), communicationCallback);
}


Backend.registerDevices = function(ids, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }

  //TODO: Aggregation callback
  
  for (var index in ids) {
    this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices/" + ids[index], "PUT", {registered: true}, true, this._getAuthenticationHeader(), communicationCallback);
  }
}

Backend.unregisterDevice = function(deviceId, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices/" + deviceId, "PUT", {registered: false}, true, this._getAuthenticationHeader(), communicationCallback);
}

Backend.addNewDevice = function(deviceSn, verificationCode, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 403 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices", "POST", {serial_number: deviceSn, verification_code: verificationCode}, true, this._getAuthenticationHeader(), communicationCallback);
}



// Device Program Management
Backend.getDeviceSchedule = function(deviceId, operationCallback) {
  var deviceSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  if (deviceSchedule == null) {
    this._pullDeviceSchedule(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceSchedule);
  }
  
  return deviceSchedule;
}
Backend._pullDeviceSchedule = function(deviceId, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 403 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices/" + deviceId + "/schedule", "GET", null, true, this._getAuthenticationHeader(), communicationCallback);
  
//  //TODO
//  setTimeout(function() {
//    var deviceSchedule = {
//      trigger: Backend.Program.TRIGGER_MOTION,
//      programs: [{
//        id: 1,
//        title: "Roar",
//        frequency: Backend.Program.FREQUENCY_ONCE
//      }, {
//        id: 2,
//        title: "Loud Roar",
//        frequency: Backend.Program.FREQUENCY_ONCE
//      }],      
//    }
//    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, deviceSchedule);
//
//    if (operationCallback) {
//      operationCallback(Backend.OperationResult.SUCCESS, deviceSchedule);
//    }
//  }, 1000);
}

Backend.setDeviceSchedule = function(deviceId, schedule, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, data);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 403 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices/" + deviceId + "/schedule", "PUT", schedule, true, this._getAuthenticationHeader(), communicationCallback);
}

Backend.addDevicePrograms = function(deviceId, programs, operationCallback) {
  var currentSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  currentSchedule.programs = currentSchedule.programs.concat(programs);
  
  Backend.setDeviceSchedule(deviceId, currentSchedule, operationCallback);
}

Backend.removeDevicePrograms = function(deviceId, programs, operationCallback) {
  var currentSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  currentSchedule.programs = GeneralUtils.removeFromArray(currentSchedule.programs, programs);
    
  Backend.setDeviceSchedule(deviceId, currentSchedule, operationCallback);
}

Backend.updateDeviceProgram = function(deviceId, program, operationCallback) {
  var currentSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);

  for (var index in currentSchedule.programs) {
    if (currentSchedule.programs[index].id == program.id) {
      currentSchedule.programs[index] = program;
      break;
    }
  }
  
  Backend.setDeviceSchedule(deviceId, currentSchedule, operationCallback);
}




Backend.getDeviceMode = function(deviceId, operationCallback) {
  var deviceMode = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId);
  
  if (deviceMode == null) {
    this._pullDeviceMode(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceMode);
  }
  
  return deviceMode;
}
Backend._pullDeviceMode = function(deviceId, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId, data.mode);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, data.mode);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 403 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices/" + deviceId + "/mode", "GET", null, true, this._getAuthenticationHeader(), communicationCallback);
}


Backend.setDeviceMode = function(deviceId, mode, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId);
  
  var communicationCallback = {
    success: function(data, status, xhr) {
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId, data.mode);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS);
      }
    },
    error: function(xhr, status, error) {
      Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId, false);
      if (operationCallback) {
        if (xhr.status == 401 || xhr.status == 403 || xhr.status == 404) {
          operationCallback(Backend.OperationResult.FAILURE);
        } else {
          operationCallback(Backend.OperationResult.ERROR);
        }
      }
    }
  }
  
  this._communicate("devices/user/" + Backend.getUserProfile().user_id + "/devices/" + deviceId + "/mode", "PUT", {mode: mode}, true, this._getAuthenticationHeader(), communicationCallback);
}







// Library Program Management

Backend.getLibraryPrograms = function(deviceId, operationCallback) {
  var libraryPrograms = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId);
  
  if (libraryPrograms == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId);
    
    this._pullLibraryPrograms(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, libraryPrograms);
  }
  
  return libraryPrograms;
}
Backend._pullLibraryPrograms = function(deviceId, operationCallback) {
  //TODO
  setTimeout(function() {
    var libraryPrograms = [{
      id: 1,
      title: "Giiii",
      description: "Just giiii"
    }, {
      id: 2,
      title: "Loud Giiii",
      description: "Very loud giii"
    }]
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId, libraryPrograms);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, libraryPrograms);
    }
  }, 1000);
}


Backend.addLibraryProgram = function(deviceId, program, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId);
  
  setTimeout(function() {
    var programs = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId);
    programs.push(program);
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId, programs);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 2000);
}

Backend.removeLibraryProgram = function(deviceId, program, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId);
  
  setTimeout(function() {
    var programs = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId);
    GeneralUtils.removeFromArray(programs, program);
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS, deviceId, programs);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 2000);
}


// Stock Program Manger

Backend.getStockPrograms = function(deviceId, operationCallback) {
  var stockPrograms = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId);
  
  if (stockPrograms == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId);
    
    this._pullStockPrograms(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, stockPrograms);
  }
  
  return stockPrograms;
}
Backend._pullStockPrograms = function(deviceId, operationCallback) {
  //TODO
  setTimeout(function() {
    var stockPrograms = [{
      id: 1,
      title: "Jopppa",
      description: "Just jopaaa",
      category: "fun"
    }, {
      id: 2,
      title: "Loud JOOOOPA",
      description: "Very loud jopa",
      category: "scary"
    }]
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId, stockPrograms);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, stockPrograms);
    }
  }, 1000);
}


Backend.addStockProgram = function(deviceId, program, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId);
  
  setTimeout(function() {
    var programs = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId);
    if (programs == null) {
      programs = [];
    }
    programs.push(program);
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId, programs);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 2000);
}

Backend.removeStockProgram = function(deviceId, program, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId);
  
  setTimeout(function() {
    var programs = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId);
    GeneralUtils.removeFromArray(programs, program);
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS, deviceId, programs);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 2000);
}
