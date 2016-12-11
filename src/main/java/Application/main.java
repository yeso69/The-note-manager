package Application;

import Application.SQLite.bdd;
import Application.Views.view;

/**
 * Created by peter on 22/11/16.
 */
public class main {
    public static void main(String[] args) {
//        BufferedReader In=null;
//
//        try{
//            In = new BufferedReader(new FileReader("db/test.db"));
//            System.out.println("test");
//            //si le fichier existe, les instructions qui suivent seront exécutées.
//        } catch (FileNotFoundException fnfe) {
//            System.out.println(fnfe);
//        }
        bdd mabdd = new bdd("test.db");
        mabdd.connect();
//        mabdd.remplirDB();
        App fen = new App(mabdd);
        mabdd.showAllCategories();
        view vue = new view();
        vue.menuPrincipal();
        mabdd.disconnect();
    }
}
