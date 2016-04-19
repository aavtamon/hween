Backend.OperationResult = {};
Backend.OperationResult.SUCCESS = "success";
Backend.OperationResult.FAILURE = "failure";
Backend.OperationResult.ERROR = "error";

Backend.CacheChangeEvent.TYPE_DEVICE_IDS = "device_ids";
Backend.CacheChangeEvent.TYPE_DEVICE_INFO = "device_info";


Backend.DeviceType = {};
Backend.DeviceType.STUMP_GHOST = "stump_ghost";

Backend.Status = {};
Backend.Status.UNKNOWN = "unknown";
Backend.Status.CONNECTED = "connected";
Backend.Status.OFFLINE = "offline";
Backend.Status.DISCOVERED = "discovered";




// Device Management

Backend.getDeviceIds = function(operationCallback) {
  var deviceIds = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
  
  if (deviceIds == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0);
    
    //TODO
    setTimeout(function() {
      var deviceIds = [1001, 1002, 1003];
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_IDS, 0, deviceIds);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, deviceIds);
      }
    }, 5000);
  } else {
    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, deviceIds);
    }
  }
  
  return deviceIds;
}

Backend.getDeviceInfo = function(deviceId, operationCallback) {
  var deviceInfo = Backend.Cache.getObject(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId);
  
  if (deviceInfo == null) {
    Backend.Cache.markObjectInUpdate(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId);
    
    //TODO
    setTimeout(function() {
      var deviceInfo = {
        id: deviceId,
        type: Backend.DeviceType.STUMP_GHOST,
        version: 1,
        operations: ["up", "down", "rotate-clockwise", "rotate-couterclockwise", "eyes_on", "eyes_off"],
        name: "Ghost",
        icon: null,
        status: Backend.Status.CONNECTED,
        ip_address: "192.168.0.1"
      }
      Backend.Cache.setObject(Backend.CacheChangeEvent.TYPE_DEVICE_INFO, deviceId, deviceInfo);

      if (operationCallback) {
        operationCallback(Backend.OperationResult.SUCCESS, deviceInfo);
      }
    }, 1000);
  } else {
    if (operationCallback) {
      operationCallback(Backend.OperationResult.SUCCESS, deviceInfo);
    }
  }
  
  return deviceInfo;
}



// Program Management