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
        BufferedReader In=null;

        try{
            In = new BufferedReader(new FileReader("db/test.db"));
            System.out.println("test");
            //si le fichier existe, les instructions qui suivent seront exécutées.
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        }
        bdd mabdd = new bdd("test.db");
        mabdd.connect();
        mabdd.remplirDB();
        //Frame frame = new Frame(mabdd);
        App fen = new App(mabdd);
        //new TreeWithMultiDiscontiguousSelections();
        mabdd.showAllCategories();
        view vue = new view();
        vue.menuPrincipal();
        mabdd.disconnect();
    }
}
