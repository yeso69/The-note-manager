package Application.SQLite;

import Application.Models.categorie;
import Application.Models.portion;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Cette classe fait l'interface avec la base de données.
 * @author Leucistic
 *
 */
public class bdd

{
    private String      dbName;
    private  Connection  connection;
    private Statement   requete;

    /**
     * Constructeur de la classe Database
     * @param dbName Le nom de la base de données
     */
    public bdd (String dbName)
    {
        // Charge le driver sqlite JDBC en utilisant le class loader actuel
        try
        {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e1)
        {
            System.err.println(e1.getMessage());
        }

        this.dbName     = dbName;
        this.connection = null;
    }

    public String getDbName() {
        return dbName;
    }

    /**
     * Ouvre la base de données spécifiée
     * @return True si la connection à été réussie. False sinon.
     */
    public boolean connect ()
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:db/"+this.dbName);
            requete = connection.createStatement();

            requete.executeUpdate("PRAGMA synchronous = OFF;");
            requete.setQueryTimeout(30);

            return true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ferme la connection à la base de données
     * @return True si la connection a bien été fermée. False sinon.
     */
    public boolean disconnect ()
    {
        try
        {
            if(connection != null)
                connection.close();

            return true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param requete La requête SQL (avec un ";" à la fin)
     * @return Un ResultSet contenant le résultat de la requête
     */
    public ResultSet getResultOf (String requete)
    {
        try
        {
            return this.requete.executeQuery(requete);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param requete La requete SQL de modification
     */
    public void updateValue (String requete)
    {
        try
        {
            this.requete.executeUpdate(requete);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  Permet de remplir la base de donnée
     */
    public void remplirDB()
    {
        String path="db/data.sql";
        String sql = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        try {
            while ((line = in.readLine()) != null)
            {
               sql+= line;
            }
            String str[] = sql.toString().split(";");

            for(int i = 0; i < str.length; i++)
            {
                System.out.println("REQUETE >> "+str[i]);
                updateValue(str[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateValue(sql);
    }

    public void saveDB(){
        /*
        ATTACH DATABASE '/the/path/to/the/other/db.sqlite' AS otherdb;
        CREATE TABLE otherdb.theTableName (...);
        INSERT INTO otherdb.theTableName SELECT * FROM main.theTableName;
        */
    }

    public void addCategorie(categorie cat)
    {
        if(!this.existCategorie(cat.getLibelle()))
        {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("INSERT INTO categorie (libelle,id_parent) VALUES(?,?)");
                preparedStatement.setString(1, cat.getLibelle());
                preparedStatement.setInt(2, cat.getId_parent());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Ajout en BDD de la catégorie : "+cat.getLibelle());
        }else
        {
            System.out.println("La catégorie existe déjà !");
        }

    }

    public void addPortion(portion por)
    {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("INSERT INTO portion (message,id_categorie,keywords) VALUES(?,?,?)");
                preparedStatement.setString(1, por.getText());
                preparedStatement.setInt(2, por.getIdCat());
                preparedStatement.setString(3, por.getKeywords());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Ajout en BDD de la portion : "+por.toString());
    }

    public void updatePortion(portion por)
    {

        String requete = "UPDATE portion SET message=\'"+por.getText()+"\', keywords=\'"+por.getKeywords()+"\'";
        requete+= " WHERE id=\'"+por.getId()+"\'";
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE portion SET message=?,id_categorie=?,keywords=? WHERE id = ?");
            preparedStatement.setString(1, por.getText());
            preparedStatement.setInt(2, por.getIdCat());
            preparedStatement.setString(3, por.getKeywords());
            preparedStatement.setInt(4,por.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Ajout en BDD de la portion : "+por.toString());
    }



    public void showAllCategories()
    {
        java.sql.ResultSet rs = null;
        try
        {
            java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from categorie");
            rs = pstatement.executeQuery();
            System.out.println();
            System.out.println("      AFFICHAGE DES CATEGORIES     ");
            System.out.println("-----------------------------------");
            while (rs.next())
            {
                int id = rs.getInt(1);
                String libelle = rs.getString(2);
                int id_parent = rs.getInt(3);
                System.out.println("ID : " + id);
                System.out.println("Libelle : " + libelle);
                System.out.println("ID_PARENT : " + id_parent);
                System.out.println();
            }
            rs.close();
            System.out.println("-----------------------------------");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void removePortion(int id)
    {
        if(!existPortion(id))
        {
            System.out.println("La portion avec l'id "+id+" n'existe pas !");
        }
        else
        {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("DELETE FROM portion WHERE id = ?");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Supression de la portion ayant pour id: "+id);
        }
    }

    public void removeCategorie(String lib)
    {
        if(!existCategorie(lib))
        {
            System.out.println("La catégorie "+lib+" n'existe pas !");
        }
        else
        {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("DELETE FROM categorie WHERE libelle = ?");
                preparedStatement.setString(1, lib);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Supression de la catégorie : "+lib);
        }
    }

    public void showCategorie(String lib)
    {
        if(!existCategorie(lib))
        {
            System.out.println("La catégorie "+lib+" n'existe pas !");
        }
        else
        {
            java.sql.ResultSet rs = null;
            try
            {
                java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from categorie WHERE libelle= ?");
                pstatement.setString(1,lib);
                rs = pstatement.executeQuery();

                int id = rs.getInt(1);
                String libelle = rs.getString(2);
                int id_parent = rs.getInt(3);
                System.out.println("-----------------------------------");
                System.out.println("ID : " + id);
                System.out.println("Libelle : " + libelle);
                System.out.println("ID_PARENT : " + id_parent);
                System.out.println("-----------------------------------");
                rs.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void showPortion(int id)
    {
        if(!existPortion(id))
        {
            System.out.println("La catégorie ayant l'id "+id+" n'existe pas !");
        }
        else
        {
            java.sql.ResultSet rs = null;
            try
            {
                java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from portion WHERE id= ?");
                pstatement.setInt(1,id);
                rs = pstatement.executeQuery();

                int idPortion = rs.getInt(1);
                String message = rs.getString(2);
                int id_categorie = rs.getInt(3);
                String clef = rs.getString(4);
                System.out.println("-----------------------------------");
                System.out.println("ID : " + id);
                System.out.println("Libelle : " + message);
                System.out.println("ID_CATEGORIE : " + id_categorie);
                System.out.println("Mots-clés : " + clef);
                System.out.println("-----------------------------------");
                rs.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean existCategorie(String lib)
    {
        boolean res = false;
        try {

            java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from categorie WHERE libelle= ?");
            pstatement.setString(1,lib);

            try (ResultSet rs = pstatement.executeQuery()) {
                // Only expecting a single result
                if (rs.next()) {
                    boolean found = rs.getBoolean(1); // "found" column
                    if (found) {
                        res = true;
                    } else {
                        res = false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean existPortion(int id)
    {
        boolean res = false;
        try {

            java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from portion WHERE id= ?");
            pstatement.setInt(1,id);

            try (ResultSet rs = pstatement.executeQuery()) {
                // Only expecting a single result
                if (rs.next()) {
                    boolean found = rs.getBoolean(1); // "found" column
                    if (found) {
                        res = true;
                    } else {
                        res = false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public ArrayList<categorie> getCategories() {
        java.sql.ResultSet rs = null;
        ArrayList<categorie> cats = new ArrayList<categorie>();
        try {
            java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from categorie");
            rs = pstatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String libelle = rs.getString(2);
                int id_parent = rs.getInt(3);
                cats.add(new categorie(id, libelle, id_parent));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cats;
    }

    public ArrayList<portion> getPortions() {
        java.sql.ResultSet rs = null;
        ArrayList<portion> portions = new ArrayList<portion>();
        try {
            java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from portion");
            rs = pstatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String text = rs.getString(2);
                int idCat = rs.getInt(3);
                String keywords = rs.getString(4);
                portions.add(new portion(id, text, idCat, keywords));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return portions;
    }

    public ArrayList<portion> getPortions(String[] kWords) {


//
//        kWords.replace("!", "!!")
//                .replace("%", "!%")
//                .replace("_", "!_")
//                .replace("[", "![");
//        PreparedStatement pstmt = con.prepareStatement(
//                "SELECT * FROM analysis WHERE notes LIKE ? ESCAPE '!'");
//        pstmt.setString(1, notes + "%");

        java.sql.ResultSet rs = null;
        ArrayList<portion> portions = new ArrayList<portion>();
        try {
            for(int i=0; i<kWords.length;i++){

                    kWords[i] = kWords[i].replace("!", "!!")
                            .replace("%", "!%")
                            .replace("_", "!_")
                            .replace("[", "![");

                    java.sql.PreparedStatement PSTATEMENT = connection.prepareStatement("Select * from portion where keywords like ? ESCAPE '!'");
                    PSTATEMENT.setString(1,'%'+kWords[i]+'%');
                    rs = PSTATEMENT.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String text = rs.getString(2);
                        int idCat = rs.getInt(3);
                        String keywords = rs.getString(4);
                        portions.add(new portion(id, text, idCat, keywords));
                    }
            }
            rs.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return portions;
    }


    public int getId(String lib){
        int id = 0;
        if(!existCategorie(lib))
        {
            System.out.println("La catégorie "+lib+" n'existe pas !");
        }
        else
        {
            java.sql.ResultSet rs = null;
            try
            {
                java.sql.PreparedStatement pstatement = connection.prepareStatement("Select * from categorie WHERE libelle= ?");
                pstatement.setString(1,lib);
                rs = pstatement.executeQuery();

                id = rs.getInt(1);
                rs.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return id;
    }


}
