public class Square extends Rectangle {

	// initialize this Square's length, width to the constructor's argument
	public Square(double size) {
		super(size, size); // invoke superclass constructor
	}

	// over-ride toString() method inherited from Rectangle
	// When toString() called on a Square object, this version executed
	public String toString() {
		return "Square[side = " + this.getLength() + "]";
	}
}