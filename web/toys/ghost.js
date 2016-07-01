Ghost = ClassUtils.defineClass(Toy, function Ghost(id) {
  Toy.call(this, id, Backend.DeviceType.STUMP_GHOST);
});

Ghost.EYES = "eyes";
Ghost.HEAD = "head";

Ghost._HEAD_HIGH_POSITION = 10;


Ghost.prototype.changeState = function(state, command) {
  Toy.prototype.changeState.call(this, state, command);
  
  if (command == Backend.DeviceCommand.MOVE_UP) {
    if (state[Ghost.HEAD].position + 1 <= Ghost._HEAD_HIGH_POSITION) {
      state[Ghost.HEAD].position++;
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
  console.debug("CALLED with " + JSON.stringify(state))
  
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
  this.getCanvasContext().stroke();
  
  // draw ghost eyes
  this.getCanvasContext().beginPath();
  var eyePosition = headPosition - 1;
  this.addArc(47, eyePosition, 1, 0, 2);
  this.getCanvasContext().stroke();
  this.getCanvasContext().beginPath();
  this.addArc(53, eyePosition, 1, 0, 2);
  this.getCanvasContext().stroke();
  
  // draw ghost mouth
  var mouthPosition = headPosition + 3;
  this.getCanvasContext().beginPath();
  this.addArc(50, headPosition + 1 - 10, 10, 0.45, 0.55);
  
  
  // draw ghost neck
  this.addLine(48, headPosition + 6, 48, 66);
  this.addLine(52, headPosition + 6, 52, 66);
  this.getCanvasContext().stroke();
  this.fillRect(48.25, 53, 3.5, 3, "#FFFFFF");
  
  

  
}
