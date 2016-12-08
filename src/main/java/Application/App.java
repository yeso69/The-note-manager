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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton saveChanges;
    private JButton deleteKeywords;
    private JTextField searchBar;
    private JTextField newKeyword;
    JTextArea contenuPortion;
    private JTree tree;
    private JLabel search;
    private JPanel jp;
    private JPanel rightPanel;
    private JPanel pCenter;
    private JPanel pkey;
    private JPanel keywordsPanel;
    private categoryControl catControl;
    JScrollPane rightScroll;
    JScrollPane treeScroll;
    Tree catTree;
    ArrayList<DefaultMutableTreeNode> selectedNodes;

    bdd db;

    public App(bdd db) {
        this.db = db;
        selectedNodes = new ArrayList<>();
        buildWindow();
        buildInterface();

        //listeners
        treeListeners();
        addCategoryListener();
        addPortionListener();
        deleteListener();
        generateDocumentListener();


        catControl = new categoryControl(db,catTree);
        frame.setMinimumSize(new Dimension(600,400));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void buildWindow() {
        frame = new JFrame("OSZ gen");
        //frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 400));
        //this.setResizable(false);
        frame.setLocationRelativeTo(null);

    }

    private void buildInterface() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        jp = new JPanel(gridBagLayout);
        keywordsPanel = new JPanel();//will be built dynamicaly
        rightPanel = new JPanel(new BorderLayout());
        addCategory = new JButton("Ajouter Categorie");
        addPortion = new JButton("Ajouter Portion");
        generateDoc = new JButton("Générer un document");
        saveChanges = new JButton("Sauvegarder");
        deleteKeywords = new JButton(Tree.createImageIcon("/img/trash.png",25));
        searchBar = new JTextField();
        newKeyword = new JTextField("");
        search = new JLabel("Recherche");
        delete = new JButton("Supprimer");

        //KEYWORD PANEL
        pkey = new JPanel(new BorderLayout());
        //rightPanel.setMinimumSize(new Dimension(900,0));

        GridBagConstraints c = new GridBagConstraints();
        ImageIcon catIcon = Tree.createImageIcon("/img/folder-close.png",25);;
        ImageIcon porIcon = Tree.createImageIcon("/img/p.png",25);;
        ImageIcon editIcon = Tree.createImageIcon("/img/edit.png",25);;
        ImageIcon fileIcon = Tree.createImageIcon("/img/file.png",25);
        ImageIcon removeIcon = Tree.createImageIcon("/img/trash.png",25);
        saveChanges.setIcon(Tree.createImageIcon("/img/save.png",25));
        addCategory.setIcon(catIcon);
        addPortion.setIcon(porIcon);
        generateDoc.setIcon(fileIcon);
        delete.setIcon(removeIcon);
        delete.setEnabled(false);
        generateDoc.setEnabled(false);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1;//To take all available space !XTRA IMPORTANT!
        gbc.weighty=1;
        frame.getContentPane().add(jp,gbc);

        //ADDING TOP BUTTON TO THE TOP OF JFRAME
        JPanel addContainer = new JPanel();
        FlowLayout fl = new FlowLayout();
        addContainer.setLayout(fl);
        addContainer.add(addCategory);
        addContainer.add(addPortion);

        c.gridy = 0;
        c.gridx = 0;
        c.weightx=1;
        c.weighty=1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridwidth = 1;
        c.gridheight = 1;
        jp.add(addContainer,c);

        c.gridx++;
        c.anchor = GridBagConstraints.NORTHEAST;
        jp.add(generateDoc,c);


        //ADDIND TREE IN A SCROLL PANE
        catTree = new Tree(db,null,null,null);
        tree = catTree.getTree();
        JPanel leftPanel = new JPanel(new BorderLayout());
        treeScroll = new JScrollPane(tree);
        leftPanel.add(treeScroll, BorderLayout.CENTER);
        leftPanel.add(delete,BorderLayout.SOUTH);



        //KEYWORDS PANEL
        JPanel keywordsTop = new JPanel(new BorderLayout());
        JLabel addKeywordLabel = new JLabel("Ajouter un mot-clé");
        JLabel motsCles = new JLabel("Mots-clés");
        keywordsTop.add(addKeywordLabel,BorderLayout.NORTH);
        keywordsTop.add(newKeyword,BorderLayout.CENTER);
        keywordsTop.add(motsCles,BorderLayout.SOUTH);
        pkey.add(keywordsTop,BorderLayout.NORTH);

        //CONTENT PANEL
        JLabel contenu = new JLabel("Contenu");
        contenuPortion = new JTextArea();
       //contenuPortion.setMinimumSize(new Dimension(400,400));
        JPanel editPortionPanel = new JPanel(new GridBagLayout());
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        editPortionPanel.add(contenu,c);
        c.gridy++;
        editPortionPanel.add(contenuPortion,c);

        JPanel editButtonsPanel = new JPanel(new BorderLayout());
        editButtonsPanel.add(deleteKeywords, BorderLayout.EAST);
        editButtonsPanel.add(saveChanges, BorderLayout.CENTER);

        rightPanel.add(editPortionPanel,BorderLayout.NORTH);
        rightPanel.add(pkey,BorderLayout.CENTER);
        rightPanel.add(editButtonsPanel,BorderLayout.SOUTH);
        rightScroll = new JScrollPane(rightPanel);

        treeScroll.getVerticalScrollBar().setValue(0);
        rightScroll.getHorizontalScrollBar().setValue(0);
        c.gridy=0;
        c.gridx=0;

        c.anchor=GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;

        //PANEL CENTER WITH --> TREE AND KEYWORD PANELS IN IT
        pCenter = new JPanel(new GridBagLayout());
        pCenter.add(leftPanel,c);
        c.gridx++;
        pCenter.add(rightScroll, c);
        c.gridx--;


        c.gridwidth=2;
        c.insets = new Insets(50, 0, 0, 0);

        //ADDING CENTER PANEL AND DELETE BUTTON ON PANEL CONTAINER (FlowLayout)
        JPanel main = new JPanel(new BorderLayout());
        main.add(pCenter,BorderLayout.CENTER);
        //main.add(delete,BorderLayout.SOUTH);
        jp.add(main, c);
    }

    private void treeListeners() {
        // JTREE ----> Quand un élément de l'arbre est sélectionner
        TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                JTree treeSource = (JTree) treeSelectionEvent.getSource();
                //Getting paths of all selected nodes
                TreePath[] treePaths = treeSource.getSelectionPaths();

                if(treePaths == null){
                    return;
                }
                //---------------------------------------------------------- SINGLE NODE SELECTED
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
                            pkey.setVisible(false);
                            contenuPortion.setText(selectedNode.getUserObject().toString());
                            System.out.println(((categorie) selectedNode.getUserObject()).getLibelle());
                            if (selectedNode.getLevel() - 1 == 4) {//Si un élément de profondeur max est selectionné on désactive l'ajout
                                //addCategory.setEnabled(false);
                            } else {
                                addCategory.setEnabled(true);//sinon on peut (quand on veut)
                            }
                        }
                        else if(selectedNode.getUserObject() instanceof portion){
                            pkey.setVisible(true);
                            System.out.println(((portion) selectedNode.getUserObject()).getText());
                            addCategory.setEnabled(false);
                            addPortion.setEnabled(false);
                            generateDoc.setEnabled(true);
                            showKeyWords((portion)selectedNode.getUserObject());
                            contenuPortion.setText(((portion) selectedNode.getUserObject()).getText());
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
                                //addCategory.setEnabled(false);
                            } else {
                                addCategory.setEnabled(true);
                            }
                        }
                    }
                }

                //------------------------------------------------------------------ MULTIPLE NODES SELECTED
                else if(treePaths.length > 1){
                    System.out.println("Plusieurs noeuds selectionés !");
                    addCategory.setEnabled(false);
                    addPortion.setEnabled(false);

                    //CREATING ARRAYLIST OF ALL SELECTED NODES
                    boolean rootIn = false;
                    selectedNodes.clear();
                    DefaultMutableTreeNode node;
                    for(int i =0; i<treePaths.length;i++){
                        node = (DefaultMutableTreeNode)treePaths[i].getLastPathComponent();

                        //CHECKING IF root is in the selected nodes
                        if(node.isRoot()){
                            generateDoc.setEnabled(false);
                            rootIn=true;
                        }else{
                            selectedNodes.add(node);
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

    private void showKeyWords(portion por) {
        newKeyword.setText("tkt");
        pkey.remove(keywordsPanel);
        keywordsPanel = por.getKeywordsPanel();
        pkey.add(keywordsPanel, BorderLayout.CENTER);
        //JOptionPane.showMessageDialog(frame, keywordsPanel);
        pkey.setEnabled(true);
        pkey.repaint();
        pkey.revalidate();
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


                //Getting paths of all selected nodes
                TreePath[] treePaths = tree.getSelectionPaths();

                if(treePaths == null){
                    return;
                }
                //------------------------- SINGLE NODE SELECTED
                else if(treePaths.length == 1){//if only one node is selected
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if(selectedNode!=null){
                        catTree.deleteNode(selectedNode);
                    }
                    delete.setEnabled(false);
                }
                //------------------------- MULTIPLE NODES SELECTED
                else if(treePaths.length > 1){
                    //OPEN ARE YOU SURE POPUP
                    if (JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer ces éléments ?", "Suppression", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // yes option
                        catTree.deleteNodes(selectedNodes);
                    }
                }
            }
        });
    }

    public void generateDocumentListener(){

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
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePaths[i].getLastPathComponent();
                    if(node.getUserObject() instanceof categorie){
                        cat = (categorie)node.getUserObject();
                        ///cat = cat.clone();//we clone the categorie because we will modify it on doc genation
                        cats.add(cat);
                    }
                    else if(node.getUserObject() instanceof portion){
                            por = (portion) node.getUserObject();
                            portions.add(por);
                    }

                }
                new documentGenerator(cats,portions, frame);
            }
        });
    }

}

