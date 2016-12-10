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
    protected bdd db = null;
    protected JTree tree;
    protected Font fontCat;
    protected Font fontPortion;
    ArrayList<categorie> cats = null;
    ArrayList<portion> portions = null;
    protected ArrayList<categorie> bastardCats;
    protected ArrayList<portion> bastardPors;
    JScrollPane scroll;


    public Tree(bdd bdd, ArrayList<categorie> cats, ArrayList<portion> portions,DefaultMutableTreeNode root) {
        this.db = bdd;

        if (cats == null && portions == null){//Si le contenu n'a pas été donné en paramètre on le recupère depuis la BDD
            this.cats = db.getCategories();
            this.portions = db.getPortions();
            buildTree();
        }
        else{
            this.root = root;
            this.portions = portions;
            this.cats = cats;
            buildTree(this.cats,this.portions,this.root);
            addBastardsToRoot();
        }

        setDesign();
        tree = new JTree(this.root);
        scroll = new JScrollPane(tree);
        setListener();
        //On permet la selection multiple de noeuds pour la génération de documents
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        int treeSelectedRows[] = {0};//ROOT IS SELECTED WHEN TREE IS CREATED
        tree.setSelectionRows(treeSelectedRows);
        tree.setCellRenderer(new MyRenderer());
        expandAllNodes(tree, 0, tree.getRowCount());

        //showTree(root);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path, int size) {
        java.net.URL imgURL = App.class.getResource(path);//important let App
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

    public void showTree(DefaultMutableTreeNode node){
        for (int i=0; i< node.getChildCount();i++){
            System.out.println("Noeud "+node.toString()+"Pere de "+node.getChildAt(i).toString());
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

    private void buildTree(ArrayList<categorie> cats, ArrayList<portion> portions, DefaultMutableTreeNode root){
        categorie racine =(categorie)root.getUserObject();
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
            node.removeFromParent();
            if (node.getUserObject() instanceof categorie) {
                delCatNode(node);
                refresh();
            } else if (node.getUserObject() instanceof portion) {
                delPortionNode(node);
                reloadTreeOnly();
            }
            tree.setSelectionRow(0);
        }
    }

    public void deleteNodes(ArrayList<DefaultMutableTreeNode> delNodes) {
           // DELETE ALL NODES
            DefaultMutableTreeNode node;
            for(int i =0;i<delNodes.size();i++) {
                node=delNodes.get(i);
                if (node.getUserObject() instanceof categorie) {
                    delCatNode(node);
                } else if (node.getUserObject() instanceof portion) {
                    delPortionNode(node);
                }
                node.removeFromParent();
            }
            tree.setSelectionRow(0);
    }

    public void delPortionNode(DefaultMutableTreeNode node){
        //Gérer l'ajout et la supression des portions

        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        portion por = (portion) node.getUserObject();
        int porId = por.getId();
        db.removePortion(porId);//Delete the category
    }

    public void delCatNode(DefaultMutableTreeNode catNode){
        categorie delCat = (categorie) catNode.getUserObject();
        int delId = delCat.getId();
        int parentId = delCat.getId_parent();

        //All the child of deleted category will gave the id_parent of the deleted category
        db.updateValue("UPDATE categorie SET id_parent="+parentId+" WHERE id_parent="+delId+";");
        db.updateValue("UPDATE portion SET id_categorie="+parentId+" WHERE id_categorie="+delId+";");
        db.removeCategorie(delCat.getLibelle());//Delete the category
    }

    public void refresh(){
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        int[] selected = tree.getSelectionRows();
        root.removeAllChildren();//Remove the old tree model except root we need to keep
        buildTree();//Build The tree with the updated ids
        model.reload(root);//refresh the tree
        expandAllNodes(tree,0,tree.getRowCount());
        tree.setSelectionRows(selected);
    }

    public void reloadTreeOnly(){
        int[] selected = tree.getSelectionRows();
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.reload(root);//refresh the tree
        expandAllNodes(tree,0,tree.getRowCount());
        tree.setSelectionRows(selected);
    }

    private DefaultMutableTreeNode addChilds(categorie cat, ArrayList<categorie> cats, ArrayList<portion> portions, DefaultMutableTreeNode papa, int level) {
        //if (level <= 4) { // 4 levels only
            int id = cat.getId(); //id of current node
            DefaultMutableTreeNode child; //child node
            categorie currentCat;
            portion currentPortion;
            boolean bastard = true;


            for(Iterator<portion> iter = portions.iterator(); iter.hasNext();){
                currentPortion = iter.next();
                if(currentPortion.getIdCat() == id){//If cat of this portion is found
                    //Creating the new node for this portion
                    child = new DefaultMutableTreeNode(currentPortion, false);//false because it does not allow children
                    child.setUserObject(currentPortion);
                    papa.add(child);
                }
            }


            //Adding category childs of papa
            for (Iterator<categorie> iter2 = cats.iterator(); iter2.hasNext();) {
                currentCat = iter2.next();
                if (currentCat.getId_parent() == id) {//when child is found
//                    if(bastardCats!=null)
//                        bastardCats.remove(currentCat);
                    child = new DefaultMutableTreeNode(currentCat.getLibelle());
                    child.setUserObject(currentCat);
                    child = addChilds(currentCat, cats, portions, child, level+1);
                    papa.add(child);//Adding the child and its childs to the parents papa

                }

            }


        //}//end of if level<4

        return papa;
    }

    public void addBastardsToRoot(){

        //ADDING BASTARD CATEGORIES TO ROOT
        int currentId;
        int parentId;
        boolean bastardCat;
        for(int i=0;i<cats.size();i++){
            parentId = cats.get(i).getId_parent();
            currentId = cats.get(i).getId();
            bastardCat = true;
            for(int j=0;j<cats.size();j++){
                if(parentId == cats.get(j).getId()) {
                    bastardCat = false;
                }
            }
            if (bastardCat){
                if(parentId != 0) {//If root is parent it's already in the tree
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(cats.get(i));
                    addChilds(cats.get(i),cats, portions,newNode,3);
                    root.add(newNode);
                }
            }
        }

        boolean isBastard;

        //ADDING BASTARD PORTIONS TO ROOT
        for(int i=0;i<portions.size();i++){
            isBastard = true;
            if( portions.get(i).getIdCat() == 0) {
                isBastard = false;
            }
            else {
                for (int j = 0; j < cats.size(); j++) {
                    if (portions.get(i).getIdCat() == cats.get(j).getId()) {
                        isBastard = false;
                    }
                }
            }

            if(isBastard){
                root.add(new DefaultMutableTreeNode(portions.get(i)));

            }
        }

    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private class MyRenderer extends DefaultTreeCellRenderer {
        ImageIcon portionIcon = createImageIcon("/img/p.png",20);;
        ImageIcon catOpenIcon = createImageIcon("/img/folder-open.png",25);
        ImageIcon catClosedIcon = createImageIcon("/img/folder-close.png",25);
        ImageIcon rootIcon = createImageIcon("/img/house.png",25);

        public MyRenderer() {
            if (portionIcon != null && catOpenIcon != null) {
                //IT is OK
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
            } else if(node.getUserObject() instanceof portion){
                setIcon(portionIcon);
                setFont(fontPortion);
            }

            return this;
        }

    }

    public void moveNode(boolean up){//true = UP false=DOWN
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        DefaultMutableTreeNode previousNode = selectedNode.getPreviousSibling();
        DefaultMutableTreeNode nextNode = selectedNode.getNextSibling();
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();

        if(selectedNode == null)
            return;

        if(up == true && previousNode != null){//-----------------> UP
            if(selectedNode.getUserObject() instanceof categorie){//-----> CAT
                if(previousNode.getUserObject() instanceof  categorie) {
                    parent.insert(selectedNode,parent.getIndex(selectedNode)-1);
                    tree.setSelectionRow(tree.getRowForPath(tree.getSelectionPath()));
                    reloadTreeOnly();
                }
            }
            else if (selectedNode.getUserObject() instanceof portion){
                if(previousNode.getUserObject() instanceof portion){
                    portion tmp1 = (portion)previousNode.getUserObject();
                    portion tmp2 = (portion)selectedNode.getUserObject();
                    previousNode.setUserObject(tmp2);
                    selectedNode.setUserObject(tmp1);
                    tree.setSelectionRow(tree.getRowForPath(tree.getSelectionPath()));
                    reloadTreeOnly();
                }
            }
        }
        else if(up == false && nextNode != null){//------------ DOWN
            if(selectedNode.getUserObject() instanceof categorie){//-----> CAT
                if(nextNode.getUserObject() instanceof  categorie) {
                    parent.insert(selectedNode,parent.getIndex(selectedNode)+1);
                    tree.setSelectionRow(tree.getRowForPath(tree.getSelectionPath()));
                    reloadTreeOnly();
                }
            }
            else if (selectedNode.getUserObject() instanceof portion){
                if(nextNode.getUserObject() instanceof portion){
                    portion tmp1 = (portion)nextNode.getUserObject();
                    portion tmp2 = (portion)selectedNode.getUserObject();
                    nextNode.setUserObject(tmp2);
                    selectedNode.setUserObject(tmp1);
                    tree.setSelectionRow(tree.getRowForPath(tree.getSelectionPath()));
                    reloadTreeOnly();
                }
            }
        }
    }

    public DefaultMutableTreeNode getSelectedNode(){
        return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    }

    public JScrollPane getScrollableTree(){
        return scroll;
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }
}


