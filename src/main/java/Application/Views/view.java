package Application.Views;
import Application.Models.categorie;
import Application.Models.portion;
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
            System.out.println("-- Menu de navigation --");
            System.out.println("Catégorie : tapez 1");
            System.out.println("Portion de texte : tapez 2");
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
                    System.out.println("Ajouter une portion : tapez 1");
                    System.out.println("Voir une portion : tapez 2");
                    System.out.println("Supprimer une portion : tapez 3");
                    System.out.println("Menu principal : tapez 4");
                    System.out.println("quitter : tapez 9");
                    choix = Lire.i();
                    switch(choix){
                        case 1 : arret = true; menuAjoutTexte(); break;
                        case 2 : arret = true; menuVoirTexte(); break;
                        case 3 : arret = true; menuSupprimerTexte(); break;
                        case 4 : menuPrincipal(); break;
                        case 9 : arret = true; break;
                        default : System.out.println("entrez un choix entre 1 et 3"); break;
                    }
                } break;
                case 9 : arret = true; break;
                default : System.out.println("entrez un choix entre 1, 2 et 9"); break;
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

    private void menuAjoutTexte()
    {
        String sousmenu = "";
        System.out.println("Veuillez saisir le message de la portion : (tapez 9 pour quitter)");
        sousmenu = Lire.S();
        if(Objects.equals(sousmenu, "9"))
        {
            this.menuPrincipal();
        }
        else
        {
            if(sousmenu != "")
            {
                String libellePor = "";
                System.out.println("Veuillez saisir le libelle de la categorie : ");
                libellePor = Lire.S();
                if(mabdd.existCategorie(libellePor))
                {
                    int id_categorie = mabdd.getId(libellePor);
                    String clef = "";
                    System.out.println("Veuillez saisir les mots clefs : ");
                    clef = Lire.S();
                    portion port = new portion(null,sousmenu,id_categorie,clef);
                    mabdd.addPortion(port);
                    this.menuAjoutTexte();
                }
                else
                {
                    System.out.println("Ma catégorie n'existe pas");
                    menuAjoutTexte();
                }
            }
            else
            {
                System.out.println("Veuillez saisir une chaine de caractère");
                menuAjoutTexte();
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

    private void menuVoirTexte()
    {
        int sousmenu = 0;
        String nom ="";
        String id_parent="";
        System.out.println("Veuillez saisir l'id de la portion : (tapez 9 pour quitter)");
        sousmenu = Lire.i();
        if(Objects.equals(sousmenu, 9))
        {
            this.menuPrincipal();
        }
        else
        {
            if(sousmenu != 0)
            {
                mabdd.showPortion(sousmenu);
                menuVoirTexte();
            }
            else
            {
                System.out.println("Veuillez saisir un chiffre");
                menuVoirTexte();
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

    private void menuSupprimerTexte()
    {
        int sousmenu = 0;
        String nom ="";
        String id_parent="";
        System.out.println("Veuillez saisir l'identifiant de la portion : (tapez 9 pour quitter)");
        sousmenu = Lire.i();
        if(Objects.equals(sousmenu, 9))
        {
            this.menuPrincipal();
        }
        else
        {
            if(sousmenu != 9)
            {
                mabdd.removePortion(sousmenu);
                menuSupprimerTexte();
            }
            else
            {
                System.out.println("Veuillez saisir un chiffre");
                menuSupprimerTexte();
            }
        }
    }
}
