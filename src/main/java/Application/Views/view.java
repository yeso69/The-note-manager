package Application.Views;
import Application.Models.categorie;
import Application.SQLite.bdd;

import java.util.Objects;

/**
 * Created by peter on 27/11/16.
 */
public class view {

    private bdd mabdd;
    public view() {
        this.mabdd = new bdd("test.db");
        mabdd.connect();
    }

    public void menuPrincipal()
    {
        int sousmenu = 0;
        boolean arret = false;
        while (!arret){
            System.out.println("--Menu de navigation --");
            System.out.println("Catégorie : tapez 1");
            System.out.println("Portion de texte : tapez 2");
            System.out.println("quitter : tapez 9");
            sousmenu = Lire.i();
            switch(sousmenu)
            {
                case 1 :{
                    int choix = 0;
                    System.out.println("--- Catégorie ---");
                    System.out.println("Ajouter une catégorie : tapez 1");
                    System.out.println("Voir une catégorie : tapez 2");
                    System.out.println("Supprimer une catégorie : tapez 3");
                    System.out.println("Menu principal : tapez 6");
                    choix = Lire.i();
                    switch(choix){
                        case 1 : arret = true; menuAjoutCategorie(); break;
                        case 2 : arret = true; menuVoirCategorie(); break;
                        case 3 : arret = true; menuSupprimerCategorie(); break;
                        case 6 : arret = true; menuPrincipal(); break;
                        default : System.out.println("entrez un choix entre 1 et 3"); break;
                    }
                } break;
                case 2 :{
                    int choix = 0;
                    System.out.println("--- Portion de texte ---");
                    System.out.println("Ajouter un texte : tapez 1");
                    System.out.println("Voir un texte : tapez 2");
                    System.out.println("Menu principal : tapez 3");
                    System.out.println("quitter : tapez 9");
                    choix = Lire.i();
                    switch(choix){
                        case 1 : System.out.println("Sous menu 1-1"); break;
                        case 2 : System.out.println("Sous menu 1-2"); break;
                        case 3 : menuPrincipal(); break;
                        case 9 : arret = true; break;
                        default : System.out.println("entrez un choix entre 1 et 2"); break;
                    }
                } break;
                case 9 : arret = true; break;
                default : System.out.println("entrez un choix entre 1 et 3"); break;
            }
        }
    }

    private void menuAjoutCategorie()
    {
        String sousmenu = "";
        String nom ="";
        String id_parent="";
        System.out.println("Veuillez saisir le nom de la catégorie : (tapez 9 pour quitter)");
        sousmenu = Lire.S();
        if(Objects.equals(sousmenu, "9"))
        {
            this.menuPrincipal();
        }
        else
        {
            if(sousmenu != "")
            {
                categorie cat = new categorie(null,sousmenu,0);
                mabdd.addCategorie(cat);
                this.menuAjoutCategorie();
            }
            else
            {
                System.out.println("Veuillez saisir une chaine de caractère");
                menuAjoutCategorie();
            }
        }
    }

    private void menuVoirCategorie()
    {
        String sousmenu = "";
        String nom ="";
        String id_parent="";
        System.out.println("Veuillez saisir le nom de la catégorie : (tapez 9 pour quitter)");
        sousmenu = Lire.S();
        if(Objects.equals(sousmenu, "9"))
        {
            this.menuPrincipal();
        }
        else
        {
            if(sousmenu != "")
            {
                mabdd.showCategorie(sousmenu);
                menuVoirCategorie();
            }
            else
            {
                System.out.println("Veuillez saisir une chaine de caractère");
                menuVoirCategorie();
            }
        }
    }

    private void menuSupprimerCategorie()
    {
        String sousmenu = "";
        String nom ="";
        String id_parent="";
        System.out.println("Veuillez saisir le nom de la catégorie : (tapez 9 pour quitter)");
        sousmenu = Lire.S();
        if(Objects.equals(sousmenu, "9"))
        {
            this.menuPrincipal();
        }
        else
        {
            if(sousmenu != "")
            {
                mabdd.removeCategorie(sousmenu);
                menuVoirCategorie();
            }
            else
            {
                System.out.println("Veuillez saisir une chaine de caractère");
                menuVoirCategorie();
            }
        }
    }
}
