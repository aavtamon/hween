Toy = ClassUtils.defineClass(Object, function Toy(id, type) {
  this._id = id;
  this._type = type;
  
  this._state;
});

Toy.prototype.append = function(container) {
  this._canvas = UIUtils.appendElement(container, "canvas", this._id);
  this._canvas.setAttribute("width", getComputedStyle(this._canvas).width);
  this._canvas.setAttribute("height", getComputedStyle(this._canvas).height);

  this.reset();
}

Toy.prototype.reset = function() {
  this._state = {};
  this.initializeState(this._state);
  
  this._clear();
  this.drawState();
}

Toy.prototype.performCommand = function(command) {
  if (this.changeState(this._state, command)) {
    this._clear();
    this.drawState(this._state);
  }
}


Toy.prototype.changeState = function(state, command) {
  throw "Not Implemented";
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

