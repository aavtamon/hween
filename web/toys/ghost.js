Ghost = ClassUtils.defineClass(Toy, function Ghost(id) {
  Toy.call(this, id, Backend.DeviceType.STUMP_GHOST);
});

Ghost.EYES = "eyes";
Ghost.HEAD = "head";

Ghost._HEAD_HIGH_POSITION = 10;


Ghost.prototype.changeState = function(state, command) {
  if (command == Backend.DeviceCommand.MOVE_UP) {
    if (state[Ghost.HEAD].position + 1 <= Ghost._HEAD_HIGH_POSITION) {
      state[Ghost.HEAD].postion++;
      return true;
    }
  } else if (command == Backend.DeviceCommand.MOVE_DOWN) {
    if (state[Ghost.HEAD].position >= 1) {
      state[Ghost.HEAD].position--;
      return true;
    }
  }
  
  return false;
}

Ghost.prototype.initializeState = function(state) {
  state[Ghost.EYES] = {on: false};
  state[Ghost.HEAD] = {position: 0};
}

Ghost.prototype.drawState = function(state) {
  // draw stump
  this.addLine(40, 60, 60, 60);
  this.addLine(60, 60, 60, 95);
  this.addLine(60, 95, 40, 95);
  this.addLine(40, 95, 40, 60);
  
  this.getCanvasContext().stroke();
}
