Controller = {
  _discoveredDevices: {}
}

Controller.ConnectionStatus = {};
Controller.ConnectionStatus.UNKNOWN = "unknown";
Controller.ConnectionStatus.ERROR = "error";
Controller.ConnectionStatus.CONNECTED = "connected";
Controller.ConnectionStatus.OFFLINE = "offline";


Controller.startDiscovery = function(observer) {
  setTimeout(function() {
    var deviceIds = Backend.getDeviceIds();
    
    if (deviceIds == null) {
    } else {
      for (var i = 0; i < deviceIds.length; i++) {
        this._discoveredDevices[deviceIds[i]] = Controller.ConnectionStatus.CONNECTED;
        
        if (observer) {
          observer(deviceIds[i], this._discoveredDevices[deviceIds[i]]);
        }
      }
    }
  }.bind(this), 5000);
}

Controller.stopDiscovery = function() {
}

Controller.getStatus = function(deviceId, observer) {
  var status = this._discoveredDevices[deviceId];
  return status != null ? status : Controller.ConnectionStatus.UNKNOWN;
}