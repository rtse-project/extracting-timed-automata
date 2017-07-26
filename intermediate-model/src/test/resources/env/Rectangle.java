public class Rectangle extends Shape {
	private double length;
	private double width;

	public Rectangle(double len, double wth) {
		length = len;
		width = wth;
	}

	public double getWidth() {
		return width;
	}

	public double getLength() {
		return length;
	}

	public double area() {
		return length*width;
	}

	public void setLength(double length) {
		if(length >= 0) this.length = length;
	}

	public void setWidth(double width) {
		if(width >= 0) this.width = width;
	}

	public String toString() {
		// return String representation of rectangle
		String repr = "Rectangle[width = " + width + ", length = " + length + "]";
		return repr;
	}
}