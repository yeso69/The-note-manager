package Application.Models;

/**
 * Created by peter on 22/11/16.
 */
public class categorie implements Cloneable{
    private Integer id;
    private String libelle;
    private Integer id_parent;

    public categorie(Integer id, String libelle, Integer id_parent) {
        this.id = id;
        this.libelle = libelle;
        this.id_parent = id_parent;
    }

//    //Clone constructor for doc generation
//    public categorie(categorie cat) {
//        this.dummy = another.dummy; // you can access
//    }

    @Override
    public categorie clone(){
        return new categorie(this.id,this.libelle,this.id_parent);
    }

    @Override
    public String toString() {
        return getLibelle();
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getId_parent() {
        return id_parent;
    }

    public void setId_parent(Integer id_parent) {
        this.id_parent = id_parent;
    }

}
