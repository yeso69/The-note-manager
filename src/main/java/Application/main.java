package Application;

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
        //Frame frame = new Frame(mabdd);
        App fen = new App(mabdd);
        //new TreeWithMultiDiscontiguousSelections();
        mabdd.showAllCategories();
        view vue = new view();
        vue.menuPrincipal();
        mabdd.showAllCategories();
        mabdd.disconnect();


    }
}
