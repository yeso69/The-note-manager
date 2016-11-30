package Application;

import Application.SQLite.bdd;
import Application.Views.Tree;
import Application.Views.categoryControl;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Yeso on 29/11/2016.
 */
public class App {
    private JFrame frame;
    private JButton addCategory;
    private JButton delete;
    private JButton addPortion;
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
        treeListeners();
        addCategoryListener();
        deleteCategorieListener();
        catControl = new categoryControl(db,catTree);

        frame.setVisible(true);
    }

    private void buildWindow() {
        frame = new JFrame("OSZ gen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        //this.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    //Building windows
    private void buildInterface() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        jp = new JPanel(gridBagLayout);
        addCategory = new JButton("Ajouter Categorie");
        addPortion = new JButton("Ajouter Portion");
        searchBar = new JTextField();
        search = new JLabel("Recherche");
        delete = new JButton("Supprimer");
        GridBagConstraints c = new GridBagConstraints();

        // **** add here:

        //c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        //jp.add(addCategory, c);

        JPanel pContainer = new JPanel();
        pContainer.setLayout(new FlowLayout());
        pContainer.add(addCategory); pContainer.add(addPortion);
        c.anchor=GridBagConstraints.NORTHWEST;
        c.gridy = 3; c.gridx = 0;
        c.gridwidth = 1; c.gridheight = 1;
        gridBagLayout.setConstraints(pContainer, c);
        jp.add(pContainer,c);


        c.weightx = 1.0;
        c.gridx++;
        //jp.add(addPortion, c);

        c.gridy++;
        c.gridx--;
        c.gridwidth = 2;//takes two cells
        jp.add(search,c);

        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        jp.add(searchBar, c);

        catTree = new Tree(db);
        tree = catTree.getTree();
        c.gridy++;
        jp.add(tree, c);

        c.gridy++;
        jp.add(delete,c);
        delete.setEnabled(false);

        frame.setContentPane(jp);
    }

    private void treeListeners() {
        // JTREE ----> Quand un élément de l'arbre est sélectionner
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent se) {
                //we get selected node + root
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();

                if (selectedNode != null) {//if something is selected we can delete
                    delete.setEnabled(true);
                    if (selectedNode.isRoot()) {//Can't delete root
                        delete.setEnabled(false);
                    }
                }

                //if the tree's depth is smaller than 4 level we can add new cat in every way
                if (root.getDepth() - 1 < 4) {// -1 because root doesn't count
                    addCategory.setEnabled(true);
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
        });
    }
        public void addCategoryListener() {
            //Quand le bouton Ajouter Catégorie est cliqué on affiche un Jdialog d'ajout
            addCategory.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    //System.out.println(selectedNode.getParent().toString().getClass());
                    // Popup avec formulaire d'ajout de catégorie
                    catControl.newCat(selectedNode, frame);

                }
            });
        }

        public void deleteCategorieListener(){
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
}



