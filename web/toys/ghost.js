Ghost = ClassUtils.defineClass(Toy, function Ghost(id) {
  Toy.call(this, id, Backend.DeviceType.STUMP_GHOST);
  
  this._talker = null;
});

Ghost.EYES = "eyes";
Ghost.HEAD = "head";

Ghost._HEAD_HIGH_POSITION = 10;
Ghost._HEAD_TURN_ANGLE = 45;


Ghost.prototype.changeState = function(state, command) {
  Toy.prototype.changeState.call(this, state, command);

  if (command.data == Backend.DeviceCommand.MOVE_UP) {
    if (state[Ghost.HEAD].position + 1 <= Ghost._HEAD_HIGH_POSITION) {
      state[Ghost.HEAD].position++;
      return true;
    }
  } else if (command.data == Backend.DeviceCommand.MOVE_DOWN) {
    if (state[Ghost.HEAD].position >= 1) {
      state[Ghost.HEAD].position--;
      return true;
    }
  } else if (command.data == Backend.DeviceCommand.TURN_LEFT) {
    if (state[Ghost.HEAD].angle - Ghost._HEAD_TURN_ANGLE < 0) {
      state[Ghost.HEAD].angle = state[Ghost.HEAD].angle - Ghost._HEAD_TURN_ANGLE + 360;
    } else {
      state[Ghost.HEAD].angle -= Ghost._HEAD_TURN_ANGLE;
    }
    return true;
  } else if (command.data == Backend.DeviceCommand.TURN_RIGHT) {
    if (state[Ghost.HEAD].angle + Ghost._HEAD_TURN_ANGLE >= 360) {
      state[Ghost.HEAD].angle = state[Ghost.HEAD].angle + Ghost._HEAD_TURN_ANGLE - 360;
    } else {
      state[Ghost.HEAD].angle += Ghost._HEAD_TURN_ANGLE;
    }
    return true;
  } else if (command.data == Backend.DeviceCommand.EYES_ON) {
    state[Ghost.EYES].on = true;
    return true;
  } else if (command.data == Backend.DeviceCommand.EYES_OFF) {
    state[Ghost.EYES].on = false;
    return true;
  } else if (command.data == Backend.DeviceCommand.TALK) {
    state[Ghost.HEAD].talk = command.arg;
    return true;
  }
  
  return false;
}

Ghost.prototype.initializeState = function(state) {
  state[Ghost.EYES] = {on: false};
  state[Ghost.HEAD] = {position: 0, angle: 0, talk: null};
}

Ghost.prototype.drawState = function(state) {
  // draw stump
  this.getCanvasContext().beginPath();
  this.addArc(50, 48, 14, 0.25, 0.75);
  this.addArc(50, 73.5, 14, -0.75, -0.25);
  this.addLine(60, 60, 60, 90);
  this.addArc(50, 77, 14, 0.25, 0.75);
  this.addLine(40, 90, 40, 60);
  this.getCanvasContext().stroke();

  
  // draw ghost head
  this.getCanvasContext().beginPath();
  var headPosition = 60 - 3 * state[Ghost.HEAD].position;
  this.addArc(50, headPosition, 5, 0, 2);
  this.getCanvasContext().fillStyle = "#CDAA9B";
  this.getCanvasContext().fill();
  this.getCanvasContext().stroke();
  
  // draw ghost eyes
  this.getCanvasContext().beginPath();
  var eyePosition = headPosition - 1;
  this.addArc(47, eyePosition, 1, 0, 2);
  this.getCanvasContext().fillStyle = state[Ghost.EYES].on ? "#FF0000" : "#C8BEB9";
  this.getCanvasContext().fill();
  this.getCanvasContext().stroke();
  this.getCanvasContext().beginPath();
  this.addArc(53, eyePosition, 1, 0, 2);
  this.getCanvasContext().fill();
  this.getCanvasContext().stroke();
  
  // draw ghost mouth
  var mouthPosition = headPosition + 3;
  this.getCanvasContext().beginPath();
  if (state[Ghost.HEAD].talk) {
    //this.addArc(50, headPosition + 4, 1, 0, 2);    
    this.addArc(50, headPosition + 1 - 3, 5, 0.3, 0.7);
    this.addArc(50, headPosition + 1 + 8, 5, 1.3, 1.7);
    
    if (this._talker != null) {
      this._talker.pause();
    }
    this._talker = new Audio(state[Ghost.HEAD].talk);
    this._talker.play();
    this._talker.addEventListener("ended", function() {
      state[Ghost.HEAD].talk = null;
      this._talker = null;
      this.drawState(state);
    }.bind(this));
  } else {
    this.addArc(50, headPosition + 1 - 3, 5, 0.3, 0.7);
    if (this._talker != null) {
      this._talker.pause();
    }
  }
  
  
  // draw ghost neck
  this.fillRect(48.25, headPosition + 6, 3.5, 66 - (headPosition + 6), "#CDAA9B");
  this.addLine(48, headPosition + 6, 48, 66);
  this.addLine(52, headPosition + 6, 52, 66);
  this.getCanvasContext().stroke();
}
