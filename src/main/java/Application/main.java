package Application;

import Application.SQLite.bdd;
import Application.Views.view;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by peter on 22/11/16.
 */
public class main {
    public static void main(String[] args) {
        bdd mabdd = new bdd("test.db");
        mabdd.connect();

        if(!mabdd.exist())
            mabdd.remplirDB();

        App fen = new App(mabdd);
        mabdd.showAllCategories();
        view vue = new view();
        vue.menuPrincipal();
        mabdd.disconnect();
    }
}
