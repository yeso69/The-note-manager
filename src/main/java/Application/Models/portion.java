package Application.Models;

/**
 * Created by Yeso on 30/11/2016.
 */
public class portion {
    private int id;
    private String text;
    private int idCat;

    public portion(int id, String text, int idCat) {
        this.id = id;
        this.text = text;
        this.idCat = idCat;
    }
    @Override
    public portion clone(){
        return new portion(this.id,this.text,this.idCat);
    }



    @Override //function used by the Jtree to set a node title
    public String toString() {
        //Getting the first characters of the portion text to display it on the tree as title of the node
        String nodeTitle = new String("");
        if(this.getText().length() > 50){
            nodeTitle = this.getText().substring(0,25) + "...";
            return nodeTitle;
        }
        return this.text;
    }

    //----------------------------GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIdCat() {
        return idCat;
    }

    public void setIdCat(int idCat) {
        this.idCat = idCat;
    }
    //--------------------------------
}
