Toy = ClassUtils.defineClass(Object, function Toy(id, type) {
  this._id = id;
  this._type = type;
  
  this._state;
});

Toy.createToy = function(type, id) {
  if (type == Backend.DeviceType.STUMP_GHOST) {
    return new Ghost(id);    
  } else {
    return null;
  }
}

Toy.prototype.append = function(container) {
  this._canvas = UIUtils.appendElement(container, "canvas", this._id);
  this._canvas.setAttribute("width", getComputedStyle(this._canvas).width);
  this._canvas.setAttribute("height", getComputedStyle(this._canvas).height);

  this.reset();
}

Toy.prototype.remove = function() {
  if (this._canvas == null) {
    return;
  }
  
  var parent = this._canvas.parentNode;
  this._canvas.parentNode.removeChild(this._canvas);
  this._canvas = null;
}

Toy.prototype.reset = function() {
  if (this._state == null) {
    this._state = {};
    this.initializeState(this._state);
    
    this._clear();
    this.drawState(this._state);
  } else if (!this.performCommand({data: Backend.DeviceCommand.MOVE_DOWN})) {
    this.initializeState(this._state);
    
    this._clear();
    this.drawState(this._state);
  } else {
    setTimeout(function() {
      this.reset();
    }.bind(this), 100);
  }
}

Toy.prototype.performCommand = function(command) {
  if (this.changeState(this._state, command)) {
    this._clear();
    this.drawState(this._state);
    return true;
  } else {
    return false;
  }
}


Toy.prototype.changeState = function(state, command) {
  if (command.data == Backend.DeviceCommand.RESET) {
    this.reset();
  }
}

Toy.prototype.drawState = function(state) {
  throw "Not Implemented";
}

Toy.prototype.initializeState = function(state) {
  throw "Not Implemented";
}


Toy.prototype.getCanvasContext = function() {
  return this._canvas.getContext("2d");
}

Toy.prototype.getScaledPosition = function(x, y) {
  return {x: x * this._canvas.clientWidth / 100, y: y * this._canvas.clientHeight / 100};
}

Toy.prototype._clear = function() {
  this.getCanvasContext().clearRect(0, 0, this._canvas.clientWidth, this._canvas.clientHeight);
}

Toy.prototype.addLine = function(begX, begY, endX, endY) {
  var ctx = this.getCanvasContext();

  var begPosition = this.getScaledPosition(begX, begY);
  ctx.moveTo(begPosition.x, begPosition.y);
  var endPosition = this.getScaledPosition(endX, endY);
  ctx.lineTo(endPosition.x, endPosition.y);
}

Toy.prototype.addArc = function(x, y, radius, startAngle, endAngle, anticlock) {
  var ctx = this.getCanvasContext();

  var position = this.getScaledPosition(x, y);
  var scaledRadious = this.getScaledPosition(radius, radius);
  ctx.arc(position.x, position.y, scaledRadious.x, startAngle * Math.PI, endAngle * Math.PI, anticlock);
}

Toy.prototype.fillRect = function(x, y, width, height, fillStyle) {
  var ctx = this.getCanvasContext();
  
  ctx.fillStyle = fillStyle;
  var position = this.getScaledPosition(x, y);
  var size = this.getScaledPosition(width, height);
  ctx.fillRect(position.x, position.y, size.x, size.y);
}

