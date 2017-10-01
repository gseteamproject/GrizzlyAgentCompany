package basicClasses;


import java.util.ArrayList;
 
 
public class Storage {
    private ArrayList<Material> materiallist = new ArrayList<Material>();
   
    public boolean add(Material mat){
        return this.materiallist.add(mat);
    }
    public boolean remove(Material mat){
        return this.materiallist.remove(mat);
    }
    public int getAmountByColor(String color){
        int count=0;
        for (int a=0; a<this.materiallist.size(); a++){
            if (this.materiallist.get(a).getColor()==color){
                count++;
            }
        }
        return count;
    }
}
