Backend.OperationResult = {};
Backend.OperationResult.SUCCESS = "success";
Backend.OperationResult.FAILURE = "failure";
Backend.OperationResult.ERROR = "error";

Backend.CacheChangeEvent.TYPE_DEVICE_IDS = "device_ids";
Backend.CacheChangeEvent.TYPE_DEVICE_INFO = "device_info";

Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE = "device_schedule";
Backend.CacheChangeEvent.TYPE_LIBRARY_PROGRAMS = "livrary_programs";
Backend.CacheChangeEvent.TYPE_STOCK_PROGRAMS = "stock_programs";
Backend.CacheChangeEvent.TYPE_DEVICE_MODE = "device_mode";


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
Backend.DeviceCommand.MOVE_UP = "up";
Backend.DeviceCommand.MOVE_DOWN = "down";
Backend.DeviceCommand.TURN_LEFT = "left";
Backend.DeviceCommand.TURN_RIGHT = "right";
Backend.DeviceCommand.EYES_ON = "eyes on";
Backend.DeviceCommand.EYES_OFF = "eyes off";
Backend.DeviceCommand.TALK = "talk";
Backend.DeviceCommand.PAUSE = "pause";



Backend.DeviceMode = {};
Backend.DeviceMode.IDLE = "idle";
Backend.DeviceMode.RUNNING_SCHEDULE = "running";
Backend.DeviceMode.MANUAL = "manual";



// User Management - temporary

Backend._communicate = function(resource, method, data, isJsonResponse, headers, callback) {
  if (resource.indexOf("user?login") == 0) {
    callback.success({user_id: 1}, 200);
  } else if (resource.indexOf("user/1") == 0) {
    callback.success({}, 200);
  } else if (resource.indexOf("user/1/settings") == 0) {
    callback.success({}, 200);
  }
}



// Device Type Management

Backend.getStockCategories = function(deviceType) {
  return [ {data: "fun", display: "Fun"}, {data: "scary", display: "Scary"} ];
}

Backend.getSupportedCommands = function(deviceType) {
  return [ {data: Backend.DeviceCommand.RESET, display: "Reset", description: "Sets toy to the initial position"}, {data: Backend.DeviceCommand.MOVE_UP, display: "Move Up", description: "Move toy up one inch"}, {data: Backend.DeviceCommand.MOVE_DOWN, display: "Move Down", description: "Move toy down one inch"}, {data: Backend.DeviceCommand.TURN_LEFT, display: "Turn Left", description: "Turn left a bit"}, {data: Backend.DeviceCommand.TURN_RIGHT, display: "Turn Right", description: "Turn right a bit"}, {data: Backend.DeviceCommand.EYES_ON, display: "Turn Eyes On", description: "Turn eyes on"}, {data: Backend.DeviceCommand.EYES_OFF, display: "Turn Eyes Off", description: "Turn eyes off"}, {data: Backend.DeviceCommand.TALK, display: "Speak", description: "Say something"}, {data: Backend.DeviceCommand.PAUSE, display: "Do nothing", description: "Do nothing for a bit"} ];
}

Backend.getSupportedProgramTriggers = function(deviceType) {
  return [ {data: Backend.Program.TRIGGER_IMMEDIATELY, display: "Previous"}, {data: Backend.Program.TRIGGER_DELAY, display: "Delay"}, {data: Backend.Program.TRIGGER_MOTION, display: "Motion"} ];
}



// Device Management

Backend.getRegisteredDeviceIds = function(operationCallback, forceUpdate) {
  var deviceIds = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);

  if (deviceIds == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
    
    this._pullRegisteredDeviceIds(operationCallback);
  } else if (forceUpdate) {
    this._pullRegisteredDeviceIds(operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceIds);
  }
  
  return deviceIds;
}
Backend._pullRegisteredDeviceIds = function(operationCallback) {
  //TODO
  setTimeout(function() {
    var deviceIds = [1001, 1002, 1003, 1004];
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, deviceIds);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, deviceIds);
    }
  }, 5000);
}

Backend.getUnregisteredDeviceIds = function(operationCallback) {
  //TODO
  setTimeout(function() {
    var deviceIds = [1004, 1005, 1006];

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, deviceIds);
    }
  }, 5000);
}

Backend.getDeviceInfo = function(deviceId, operationCallback, forceUpdate) {
  var deviceInfo = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId);
  
  if (deviceInfo == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId);
    
    this._pullDeviceInfo(deviceId, operationCallback);
  } else if (forceUpdate) {
    this._pullDeviceInfo(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceInfo);
  }
  
  return deviceInfo;
}
Backend._pullDeviceInfo = function(deviceId, operationCallback) {
  //TODO
  setTimeout(function() {
    var deviceInfo = {
      id: deviceId,
      type: Backend.DeviceType.STUMP_GHOST,
      version: 1,
      operations: ["up", "down", "rotate-clockwise", "rotate-couterclockwise", "eyes_on", "eyes_off"],
      name: "Ghost",
      icon: null,
      serial_number: "000000" + deviceId,
      status: Backend.Status.CONNECTED,
      ip_address: "192.168.0.1"
    }
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId, deviceInfo);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, deviceInfo);
    }
  }, 1000);
}


Backend.registerDevices = function(ids, operationCallback) {
  var deviceIds = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  setTimeout(function() {
    for (var i in ids) {
      deviceIds.push(ids[i]);
    }
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, deviceIds);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 5000);
}

Backend.unregisterDevices = function(ids, operationCallback) {
  var deviceIds = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  setTimeout(function() {
    for (var i in ids) {
      GeneralUtils.removeFromArray(deviceIds, ids[i]);
    }
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, deviceIds);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 5000);
}

Backend.addNewDevice = function(deviceId, verificationCode, operationCallback) {
  var deviceIds = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  setTimeout(function() {
    deviceIds.push(deviceId);
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, deviceIds);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 5000);
}



// Device Program Management
Backend.getDeviceSchedule = function(deviceId, operationCallback) {
  var deviceSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  if (deviceSchedule == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
    
    this._pullDeviceSchedule(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceSchedule);
  }
  
  return deviceSchedule;
}
Backend._pullDeviceSchedule = function(deviceId, operationCallback) {
  //TODO
  setTimeout(function() {
    var deviceSchedule = {
      trigger: Backend.Program.TRIGGER_MOTION,
      programs: [{
        id: 1,
        title: "Roar",
        frequency: Backend.Program.FREQUENCY_ONCE
      }, {
        id: 2,
        title: "Loud Roar",
        frequency: Backend.Program.FREQUENCY_ONCE
      }],      
    }
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, deviceSchedule);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, deviceSchedule);
    }
  }, 1000);
}

Backend.setDeviceSchedule = function(deviceId, schedule, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  setTimeout(function() {
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, schedule);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 3000);
}

Backend.addDevicePrograms = function(deviceId, programs, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  setTimeout(function() {
    var currentSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
    currentSchedule.programs = currentSchedule.programs.concat(programs);
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, currentSchedule);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 3000);
}

Backend.removeDevicePrograms = function(deviceId, programs, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  setTimeout(function() {
    var currentSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
    currentSchedule.programs = GeneralUtils.removeFromArray(currentSchedule.programs, programs);
    
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, currentPrograms);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 3000);
}

Backend.updateDeviceProgram = function(deviceId, program, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);
  
  setTimeout(function() {
    var currentSchedule = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId);

    for (var index in currentSchedule.programs) {
      if (currentSchedule.programs[index].id == program.id) {
        currentSchedule.programs[index] = program;
        break;
      }
    }
    
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_SCHEDULE, deviceId, currentSchedule);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 3000);
}




Backend.getDeviceMode = function(deviceId, operationCallback) {
  var deviceMode = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId);
  
  if (deviceMode == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId);
    
    this._pullDeviceMode(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, deviceMode);
  }
  
  return deviceMode;
}
Backend._pullDeviceMode = function(deviceId, operationCallback) {
  //TODO
  setTimeout(function() {
    var deviceMode = Backend.DeviceMode.IDLE;
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId, deviceMode);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, deviceMode);
    }
  }, 1000);
}


Backend.setDeviceMode = function(deviceId, mode, operationCallback) {
  Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId);
  
  setTimeout(function() {
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_MODE, deviceId, mode);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS);
    }
  }, 1000);
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
