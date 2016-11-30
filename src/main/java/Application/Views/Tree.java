package Application.Views;

import Application.Models.categorie;
import Application.SQLite.bdd;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by Yeso on 29/11/2016.
 */
public class Tree{

    protected DefaultMutableTreeNode root;
    protected bdd db;
    JTree tree;

    public Tree(bdd bdd) {
        this.db = bdd;
        root = new DefaultMutableTreeNode("Catégories");
        categorie racine = new categorie(0,"Catégories",0);
        root.setUserObject(racine);
        buildCategoryTree();
        tree = new JTree(root);
        setListener();
    }

    public JTree getTree(){
        return tree;
    }

    private void setListener(){
        //root.getDepth();
    }



    //Building windows
    private void buildCategoryTree() {
        //Getting categories from db
        ArrayList<categorie> cats = db.getCategories();
        DefaultMutableTreeNode bastard;// For categories without parents, bastard you can say

        for (categorie cat : cats) {
            if (cat.getId_parent() == 0) {//si pas de parents alors c'est un gros batard
                System.out.println("NOUVEAU NOEUD BATARD "+cat.getLibelle());
                bastard =  new DefaultMutableTreeNode(cat.getLibelle());
                bastard.setUserObject(cat);
                bastard = addChilds(cat, cats, bastard, 1);
                root.add(bastard);
            }
        }
        //this = new JTree(root); <-- A voir

    }

    public void addCatNode(String parent, categorie cat){
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        //Création du nouveau noeud
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(cat.getLibelle());
        newNode.setUserObject(cat);

        if(cat.getId_parent() == 0){//Si ajout a la racine
            root.add(newNode);
            //comparer les id des cat et non les libelle
        }
        while (e.hasMoreElements()) {//Sinon parcours des noeuds jusqu'à trouver le père
            DefaultMutableTreeNode node = e.nextElement();
            if (node.toString().equalsIgnoreCase(parent)) {//When the right node parent is found
                System.out.println("Bon noeud trouve n"+node.toString());
                node.add(newNode);

            }
        }
        model.reload(root);
    }

    //Function that determines if node is portion or category and treat with delPortion and delCat
    public void deleteNode(DefaultMutableTreeNode node) {
        if (JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cet élément et tous ses descendants ?", "Suppression",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // yes option
            //if node is a category call category delete function
            if (node.getUserObject().getClass().getSimpleName().equals("categorie")) {
                delCatNode(node);
            } else if (node.getUserObject().getClass().getSimpleName().equals("portion")) {
                delPortionNode(node);
            }
        }
    }

    public void delPortionNode(DefaultMutableTreeNode node){
        System.out.println("DelPortion");
        //Supression des descendants d'une catégorie
        //Ajouter les portions dans l'arbre
        //Gérer l'ajout et la supression des portions
    }

    public void delCatNode(DefaultMutableTreeNode catNode){
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        categorie delCat = (categorie) catNode.getUserObject();

        int delId = delCat.getId();
        int parentId = delCat.getId_parent();

        //All the child of deleted category will gave the id_parent of the deleted category
        db.updateValue("UPDATE categorie SET id_parent="+parentId+" WHERE id_parent="+delId+";");
        db.removeCategorie(delCat.getLibelle());//Delete the category
        root.removeAllChildren();//Remove the old tree model except root we need to keep
        buildCategoryTree();//Build The tree with the updated ids
        model.reload(root);//refresh the tree
    }
    private DefaultMutableTreeNode addChilds(categorie cat, ArrayList<categorie> cats, DefaultMutableTreeNode papa, int level) {
        if (level <= 4) { // 4 levels only
            int id = cat.getId(); //id of current node
            DefaultMutableTreeNode child; //child node
            categorie currentCat;

            //Adding direct childs of papa
            for (Iterator<categorie> iter = cats.iterator(); iter.hasNext(); ) {
                currentCat = iter.next();
                if (currentCat.getId_parent() == id) {//when child is found
                    System.out.println("Pere " + cat.getLibelle() + " ---> Fils " + currentCat.getLibelle()+" Level "+level);
                    child = new DefaultMutableTreeNode(currentCat.getLibelle());
                    child.setUserObject(currentCat);
                    child = addChilds(currentCat, cats, child, level+1);
                    papa.add(child);//Adding the child and its childs to the parents papa
                }
            }
        }//end of if level<4

        return papa;
    }
}
