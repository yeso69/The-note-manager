package Application;

import Application.Categorie.categorie;
import Application.SQLite.bdd;

/**
 * Created by peter on 22/11/16.
 */
public class main {
    public static void main(String[] args) {
        bdd mabdd = new bdd("test.db");
        mabdd.connect();
        mabdd.remplirDB();
        categorie cat = new categorie(null,"Chaussure",0);
        mabdd.addCategorie(cat);
        categorie cat2 = new categorie(null,"Pantalon",0);
        mabdd.addCategorie(cat2);
        categorie cat3 = new categorie(null,"Pull",0);
        mabdd.addCategorie(cat3);

        mabdd.showCategorie(cat3.getLibelle());


    }
}
