Controller = {
}

Controller.Command = {}
Controller.Command.CONNECT_TO_BACKEND = "connect_to_backend";

Controller.isAvailable = function(deviceInfo, observer) {
  setTimeout(observer.bind(this, true), 2000);
}

Controller.reportToServer = function(deviceInfo, observer) {
  setTimeout(observer.bind(this, true), 2000);
}

// command: {id: Controller.Command, arg: <any data>}
Controller.sendCommand = function(deviceInfo, command, observer) {
  //  setTimeout(observer.bind(this, true), 2000);
}

Controller.sendProgram = function(deviceInfo, commands, observer) {
  //  setTimeout(observer.bind(this, true), 2000);
}