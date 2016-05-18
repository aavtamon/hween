Backend.OperationResult = {};
Backend.OperationResult.SUCCESS = "success";
Backend.OperationResult.FAILURE = "failure";
Backend.OperationResult.ERROR = "error";

Backend.CacheChangeEvent.TYPE_DEVICE_IDS = "device_ids";
Backend.CacheChangeEvent.TYPE_DEVICE_INFO = "device_info";

Backend.CacheChangeEvent.TYPE_DEVICE_PROGRAMS = "device_programs";


Backend.DeviceType = {};
Backend.DeviceType.STUMP_GHOST = "stump_ghost";

Backend.Status = {};
Backend.Status.UNKNOWN = "unknown";
Backend.Status.CONNECTED = "connected";
Backend.Status.OFFLINE = "offline";
Backend.Status.DISCOVERED = "discovered";


Backend.Program = {};
Backend.Program.FREQUENCY_NEVER;
Backend.Program.FREQUENCY_ONCE;
Backend.Program.FREQUENCY_RARE;
Backend.Program.FREQUENCY_OFTER;
Backend.Program.FREQUENCY_ALWAYS;




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



// Program Management
Backend.getPrograms = function(deviceId, operationCallback) {
  var devicePrograms = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_PROGRAMS, deviceId);
  
  if (devicePrograms == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_PROGRAMS, deviceId);
    
    this._pullDevicePrograms(deviceId, operationCallback);
  } else if (operationCallback) {
    operationCallback(Backend.OperationResult.SUCCESS, devicePrograms);
  }
  
  return devicePrograms;
}
Backend._pullDevicePrograms = function(deviceId, operationCallback) {
  //TODO
  setTimeout(function() {
    var devicePrograms = [{
      id: 1,
      title: "Roar",
      frequency: Backend.Program.FREQUENCY_ONCE
    }, {
      id: 2,
      title: "Loud Roar",
      frequency: Backend.Program.FREQUENCY_ONCE
    }]
    Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_PROGRAMS, deviceId, devicePrograms);

    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, devicePrograms);
    }
  }, 1000);
}
