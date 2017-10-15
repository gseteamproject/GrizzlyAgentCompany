package basicClasses;

public class Material {
	private String color;
	private double size;

	// material for product (stone with color)
	public Material(String color, double size) {
		this.color = color;
		this.size = size;
	}

	// material for paint
	public Material(String color) {
		this.color = color;
	}

	// material for raw stone
	public Material(double size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}
}