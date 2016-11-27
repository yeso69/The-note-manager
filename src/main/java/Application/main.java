package Application;

import Application.Models.categorie;
import Application.SQLite.bdd;
import Application.Views.view;

/**
 * Created by peter on 22/11/16.
 */
public class main {
    public static void main(String[] args) {
        bdd mabdd = new bdd("test.db");
        mabdd.connect();
        mabdd.remplirDB();
        view vue = new view();
        vue.menuPrincipal();

        mabdd.showAllCategories();
        mabdd.disconnect();
    }
}
