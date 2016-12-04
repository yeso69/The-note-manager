package Application;

import Application.Models.categorie;
import Application.Models.documentGenerator;
import Application.Models.portion;
import Application.SQLite.bdd;
import Application.Views.Tree;
import Application.Views.categoryControl;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by Yeso on 29/11/2016.
 */
public class App {
    private JFrame frame;
    private JButton addCategory;
    private JButton delete;
    private JButton addPortion;
    private JButton generateDoc;
    private JTextField searchBar;
    private JTree tree;
    private JLabel search;
    private JPanel jp;
    private categoryControl catControl;
    Tree catTree;

    bdd db;

    public App(bdd db) {
        this.db = db;
        buildWindow();
        buildInterface();

        //listeners
        treeListeners();
        addCategoryListener();
        addPortionListener();
        deleteListener();
        generateDocumentListener();


        catControl = new categoryControl(db,catTree);

        frame.setVisible(true);
    }

    private void buildWindow() {
        frame = new JFrame("OSZ gen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        //this.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    //Building windows
    private void buildInterface() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        jp = new JPanel(gridBagLayout);
        addCategory = new JButton("Ajouter Categorie");
        addPortion = new JButton("Ajouter Portion");
        generateDoc = new JButton("Générer un document");
        searchBar = new JTextField();
        search = new JLabel("Recherche");
        delete = new JButton("Supprimer");
        GridBagConstraints c = new GridBagConstraints();
        ImageIcon catIcon = Tree.createImageIcon("/img/folder-close.png",25);;
        ImageIcon porIcon = Tree.createImageIcon("/img/p.png",25);;
        ImageIcon fileIcon = Tree.createImageIcon("/img/file.png",25);
        ImageIcon removeIcon = Tree.createImageIcon("/img/remove.png",25);
        addCategory.setIcon(catIcon);
        addPortion.setIcon(porIcon);
        generateDoc.setIcon(fileIcon);
        delete.setIcon(removeIcon);

        // **** add here:

        //c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        //jp.add(addCategory, c);


        //BUTTONS
        JPanel pContainer = new JPanel();
        FlowLayout fl = new FlowLayout();
        pContainer.setLayout(fl);
        pContainer.add(addCategory);
        pContainer.add(addPortion);
        //pContainer.add(generateDoc);

        generateDoc.setEnabled(false);
        c.gridy = 3; c.gridx = 0;
        c.gridwidth = 1; c.gridheight = 1;
        gridBagLayout.setConstraints(pContainer, c);
        jp.add(pContainer,c);


        c.weightx = 1.0;
        c.gridx++;
        c.anchor = GridBagConstraints.NORTHEAST;
        jp.add(generateDoc,c);

        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy++;
        c.gridx--;
        c.gridwidth = 2;//takes two cells
        jp.add(search,c);

        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        jp.add(searchBar, c);



        catTree = new Tree(db,null,null,null);
        tree = catTree.getTree();
        System.out.println(tree.toString());
        c.gridy++;
        jp.add(tree, c);

        c.gridy++;
        jp.add(delete,c);
        delete.setEnabled(false);

        //FRAME constraints to display WELL
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor=GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy= 0;

    jp.setForeground(Color.BLUE);
        frame.add(jp,gbc);


//        frame.setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.anchor=GridBagConstraints.NORTHWEST;
//        frame.add(jp,gbc);
    }

    private void treeListeners() {
        // JTREE ----> Quand un élément de l'arbre est sélectionner
        TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                JTree treeSource = (JTree) treeSelectionEvent.getSource();
                //Getting paths of all selected nodes
                TreePath[] treePaths = treeSource.getSelectionPaths();
                //System.out.println("NB selected Nodes: "+treePaths.length);

                if(treePaths == null){
                    return;
                }
                //------------------------- SINGLE NODE SELECTED
                else if(treePaths.length == 1){//if only one node is selected
                    System.out.println("Une seule noeud selectioné !");
                    //we get selected node + root
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();

                    if (selectedNode != null) {//if something is selected we can delete
                        delete.setEnabled(true);
                        if(selectedNode.getUserObject() instanceof categorie){
                            addPortion.setEnabled(true);
                            generateDoc.setEnabled(false);

                            System.out.println(((categorie) selectedNode.getUserObject()).getLibelle());
                            if (selectedNode.getLevel() - 1 == 4) {//Si un élément de profondeur max est selectionné on désactive l'ajout
                                addCategory.setEnabled(false);
                            } else {
                                addCategory.setEnabled(true);//sinon on peut (quand on veut)
                            }
                        }
                        else if(selectedNode.getUserObject() instanceof portion){
                            System.out.println(((portion) selectedNode.getUserObject()).getText());
                            addCategory.setEnabled(false);
                            addPortion.setEnabled(false);
                            generateDoc.setEnabled(true);
                        }
                        else{

                        }
                        if (selectedNode.isRoot()) {
                            delete.setEnabled(false);//Can't delete root
                            addPortion.setEnabled(true);//Can't add a portion without a parent category
                        }
                    }
                    //if the tree is at the max depth we make sure it's not possible to add a new level
                    else if (root.getDepth() - 1 == 4) {//Cas ou on ne doit plus pouvoir rajouter d'enfants plus profond (niveau 4 de profondeur)
                        if (selectedNode != null) {//Dans le cas ou rien n'est encore sélectioné on ajoute à la racine
                            if (selectedNode.getLevel() - 1 == 4) {//Si un élément de profondeur max est selectionné
                                addCategory.setEnabled(false);
                            } else {
                                addCategory.setEnabled(true);
                            }
                        }
                    }
                }

                //------------------------- MULTIPLE NODES SELECTED
                else if(treePaths.length > 1){
                    System.out.println("Plusieurs noeuds selectionés !");
                    addCategory.setEnabled(false);
                    addPortion.setEnabled(false);

                    //CREATING ARRAYLIST OF ALL SELECTED NODES
                    boolean rootIn = false;
                    for(int i =0; i<treePaths.length;i++){
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePaths[i].getLastPathComponent();
                        //CHECKING IF root is in the selected nodes
                        if(node.isRoot()){
                            generateDoc.setEnabled(false);
                            rootIn=true;
                            break;
                        }
                        System.out.println(node.getUserObject());
                    }
                    if(!rootIn){
                        generateDoc.setEnabled(true);
                    }
                    //HERE ALL SELECTED NODES ARE CATS AND PORTIONS (not root)


                }


            }

        };
        tree.addTreeSelectionListener(treeSelectionListener);
    }

    public void addCategoryListener() {
        //Quand le bouton Ajouter Catégorie est cliqué on affiche un Jdialog d'ajout
        addCategory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null){//Avoid null pointer exception if nothing is selected
                    return;
                }
                if(selectedNode.getUserObject() instanceof categorie) {
                    catControl.newCat(selectedNode, frame);
                }
            }
        });
    }

    public void addPortionListener() {
        //Quand le bouton Ajouter Catégorie est cliqué on affiche un Jdialog d'ajout
        addPortion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if(selectedNode ==null){}//avoid nullPointer
                else if(selectedNode.getUserObject() instanceof categorie) {
                    catControl.newPor(selectedNode, frame);
                }

            }
        });
    }

    public void deleteListener(){
        //Quand l'utilisateur supprime un élément
        delete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if(selectedNode!=null){
                    catTree.deleteNode(selectedNode);
                }
                delete.setEnabled(false);
            }
        });
    }

    public void generateDocumentListener(){


        //System.out.println("NB selected Nodes: "+treePaths.length);

        generateDoc.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Getting paths of all selected nodes
                TreePath[] treePaths = tree.getSelectionPaths();
                ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
                ArrayList<categorie> cats = new ArrayList<categorie>();
                ArrayList<portion> portions = new ArrayList<portion>();

                //temporary variables
                categorie cat;
                portion por;
                for(int i =0; i<treePaths.length;i++){
                    //System.out.println(treePaths[i].getPathComponent(i));
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePaths[i].getLastPathComponent();
                    if(node.getUserObject() instanceof categorie){
                        cat = (categorie)node.getUserObject();
                        ///cat = cat.clone();//we clone the categorie because we will modify it on doc genation
                        cats.add(cat);
                    }
                    else if(node.getUserObject() instanceof portion){
                            por = (portion) node.getUserObject();
                            ///cat = cat.clone();//we clone the categorie because we will modify it on doc genation
                            portions.add(por);
                            //System.out.println("Portion "+por+"!!!!!!!!!!!!!!!!!!!!!!!");
                    }

                }
                new documentGenerator(cats,portions, frame);
            }
        });
    }

}

