Controller = {
}

Controller.Command = {}
Controller.Command.CONNECT_TO_BACKEND = "connect_to_backend";

Controller.isAvailable = function(deviceInfo, observer) {
  if (observer != null) {
    setTimeout(observer.bind(this, true), 2000);
  }
}

Controller.reportToServer = function(deviceInfo, observer) {
  if (deviceInfo.ip_address == null || deviceInfo.ip_address == "" || deviceInfo.port == null || deviceInfo.port == -1) {
    return;
  }
  
  var request = new XMLHttpRequest();
  request.onreadystatechange = function() {
    if (request.readyState == 4 && request.status == 200) {
      if (observer != null) {
        observer();
      }
    }
  }
  
  var data = JSON.stringify({command: Controller.Command.CONNECT_TO_BACKEND});

  request.open("PUT", "http://" + deviceInfo.ip_address + ":" + deviceInfo.port, true);
  
  request.setRequestHeader("Content-Type", "application/json");
  request.setRequestHeader("Accept", "application/json");
  
  request.send(data);
}

Controller.reset = function(deviceInfo, observer) {
    setTimeout(observer.bind(this, true), 2000);
}

// command: {data: Controller.Command, arg: <any data>}
Controller.sendCommand = function(deviceInfo, command, observer) {
    setTimeout(observer.bind(this, true), 2000);
}
