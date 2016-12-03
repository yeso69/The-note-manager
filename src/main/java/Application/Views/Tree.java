package Application.Views;

import Application.App;
import Application.Models.categorie;
import Application.Models.portion;
import Application.SQLite.bdd;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by Yeso on 29/11/2016.
 */
public class Tree {

    protected DefaultMutableTreeNode root;
    protected bdd db;
    protected JTree tree;
    protected Font fontCat;
    protected Font fontPortion;

    public Tree(bdd bdd) {
        this.db = bdd;
        setDesign();
        buildTree();
        tree = new JTree(root);
        setListener();
        //On permet la selection multiple de noeuds pour la génération de documents
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        int treeSelectedRows[] = {0};//ROOT IS SELECTED WHEN TREE IS CREATED
        tree.setSelectionRows(treeSelectedRows);
        tree.setCellRenderer(new MyRenderer());
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path, int size) {
        java.net.URL imgURL = App.class.getResource(path);//important let App
        System.out.println("ressource: "+ App.class.getResource(""));
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage() ;
            Image newimg = img.getScaledInstance( size, size,  java.awt.Image.SCALE_SMOOTH ) ;
            icon = new ImageIcon( newimg );
            return icon;
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }

    }

    public JTree getTree(){
        return tree;
    }

    private void setListener(){
        //root.getDepth();
    }

    private void setDesign(){
        fontCat = new Font("Helvetica", Font.BOLD, 12);
        fontPortion = new Font("Helvetica", Font.PLAIN, 12);
    }

    //Building windows
    private void buildTree() {
        //Getting categories from db
        ArrayList<categorie> cats = db.getCategories();
        ArrayList<portion> portions = db.getPortions();
        categorie racine = new categorie(0, "Accueil", 0);
        if(root == null) {//If ROOT IS NOT SET (1st time) WE CREATE IT
            root = new DefaultMutableTreeNode(racine);
        }
        addChilds(racine, cats, portions, root, 1);


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
        if (JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cet élément ?", "Suppression",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // yes option
            //if node is a category call category delete function
            if (node.getUserObject() instanceof categorie) {
                delCatNode(node);
            } else if (node.getUserObject() instanceof portion) {
                delPortionNode(node);
            }
        }
    }

    public void delPortionNode(DefaultMutableTreeNode node){
        System.out.println("DelPortion");
        //Gérer l'ajout et la supression des portions

        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        portion por = (portion) node.getUserObject();

        int porId = por.getId();
        db.removePortion(porId);//Delete the category
        root.removeAllChildren();//Remove the old tree model except root we need to keep
        buildTree();//Build The tree with the updated ids
        model.reload(root);//refresh the tree
    }

    public void delCatNode(DefaultMutableTreeNode catNode){
        categorie delCat = (categorie) catNode.getUserObject();
        int delId = delCat.getId();
        int parentId = delCat.getId_parent();

        //All the child of deleted category will gave the id_parent of the deleted category
        db.updateValue("UPDATE categorie SET id_parent="+parentId+" WHERE id_parent="+delId+";");
        db.updateValue("UPDATE portion SET id_categorie="+parentId+" WHERE id_categorie="+delId+";");
        db.removeCategorie(delCat.getLibelle());//Delete the category
        refresh();
    }

    public void refresh(){
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        root.removeAllChildren();//Remove the old tree model except root we need to keep
        buildTree();//Build The tree with the updated ids
        model.reload(root);//refresh the tree
    }

    private DefaultMutableTreeNode addChilds(categorie cat, ArrayList<categorie> cats, ArrayList<portion> portions, DefaultMutableTreeNode papa, int level) {
        if (level <= 4) { // 4 levels only
            int id = cat.getId(); //id of current node
            DefaultMutableTreeNode child; //child node
            categorie currentCat;
            portion currentPortion;

            for(Iterator<portion> iter2 = portions.iterator(); iter2.hasNext();){
                currentPortion = iter2.next();
                if(currentPortion.getIdCat() == id){
                    //Creating the new node for this portion
                    child = new DefaultMutableTreeNode(currentPortion, false);//false because it does not allow children
                    child.setUserObject(currentPortion);
                    //child.
                    papa.add(child);
                }
            }

            //Adding category childs of papa
            for (Iterator<categorie> iter = cats.iterator(); iter.hasNext(); ) {
                currentCat = iter.next();
                if (currentCat.getId_parent() == id) {//when child is found
                    System.out.println("Pere " + cat.getLibelle() + " ---> Fils " + currentCat.getLibelle()+" Level "+level);
                    child = new DefaultMutableTreeNode(currentCat.getLibelle());
                    child.setUserObject(currentCat);
                    child = addChilds(currentCat, cats, portions, child, level+1);
                    papa.add(child);//Adding the child and its childs to the parents papa
                }

            }


        }//end of if level<4

        return papa;
    }


    private class MyRenderer extends DefaultTreeCellRenderer {
        ImageIcon portionIcon = createImageIcon("/img/p.png",20);;
        ImageIcon catOpenIcon = createImageIcon("/img/folder-open.png",25);
        ImageIcon catClosedIcon = createImageIcon("/img/folder-close.png",25);
        ImageIcon rootIcon = createImageIcon("/img/house.png",25);

        public MyRenderer() {
            if (portionIcon != null && catOpenIcon != null) {
                //IT is OK
            } else {
                System.err.println("Tutorial icon missing; using default.");
            }
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if(node.isRoot()){
                setIcon(rootIcon);
            }
            else if (node.getUserObject() instanceof categorie) {
                setOpenIcon(catOpenIcon);
                setClosedIcon(catClosedIcon);
                setFont(fontCat);
                if(node.isLeaf()){
                    setIcon(catOpenIcon);
                }
                //System.out.println(("Style appliqué à la catégorie"+node.toString()));
            } else if(node.getUserObject() instanceof portion){
                setIcon(portionIcon);
                //System.out.println("Style appliqué à la portion"+node.toString());
                setFont(fontPortion);
            }

            return this;
        }

    }


}


