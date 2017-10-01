package basicClasses;

import java.util.ArrayList;

public class Material {
    private String color;
    private double size;
   
    public Material(String color, double size){
        this.color = color;
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