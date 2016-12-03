package Application.Views;

import Application.Models.categorie;
import Application.Models.portion;
import Application.SQLite.bdd;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Yeso on 29/11/2016.
 */
public class categoryControl {
    JPanel jpAddCat;
    JPanel jpAddPor;
    JLabel catTitle;
    JLabel porTitle;
    JTextField catName;
    JButton validateCat;
    JButton validatePor;
    JDialog d;
    bdd db;
    Tree tree;
    String parentTitle;
    private JTextArea portionText;
    private portion selectedPor;
    private categorie selectedCat;

    public categoryControl(bdd bdd, Tree tree) {
        this.db = bdd;
        this.tree = tree;
        buildPanelAddCat();
        buildPanelAddPor();
        validateAddCat();
        validateAddPor();
        setTextListeners();
    }

    private void validateAddPor() {

        //Quand on valide l'ajout d'une portion
        validatePor.addActionListener(new ActionListener()
        {
            String newPor = "";
            public void actionPerformed(ActionEvent e)
            {
                newPor = portionText.getText();
                int idCat = selectedCat.getId();
                System.out.println("ID du futur papa "+idCat);
                portion por = new portion(0, newPor,idCat);
                db.addPortion(por);//Add the new category to the db
                tree.refresh();
                d.dispose();//Close dialog
                portionText.setText("");//clear textfield
            }

        });
}



    public void validateAddCat(){
        //Quand on valide l'ajout d'une catégorie
        validateCat.addActionListener(new ActionListener()
        {
            String newCat = "";
            public void actionPerformed(ActionEvent e)
            {
                newCat = catName.getText();
                if (db.existCategorie(newCat)){
                    JOptionPane.showMessageDialog(null,
                            "Une catégorie du même nom existe déjà. Veuillez en saisir une autre.", "Création impossible",
                            JOptionPane.ERROR_MESSAGE);
                }
                if(!db.existCategorie(newCat)){//Vérification de l'unicité du libélé
                    int idParent = 0;
                    if(!parentTitle.equals("")){
                        idParent = db.getId(parentTitle);
                    }
                    System.out.println("ID du futur papa "+idParent);
                    categorie cat = new categorie(0,newCat,idParent);
                    db.addCategorie(cat);//Add the new category to the db
                    cat.setId(db.getId(newCat));//getting and setting the real id from db after insertion
                    tree.addCatNode(parentTitle, cat);//Add the new category to the tree
                    //db.showAllCategories();
                    d.dispose();//Close dialog
                    catName.setText("");//clear textfield
                }
                else{
                    System.out.println("Existe déja !");
                }
            }
        });

    }

    public void setTextListeners(){

        catName.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (catName.getText().equals("")){
                    validateCat.setEnabled(false);
                }
                else {
                    validateCat.setEnabled(true);
                }

            }
        });

        portionText.getDocument().addDocumentListener(new DocumentListener() {

            public void changed() {
                if (portionText.getText().equals("")){
                    validatePor.setEnabled(false);
                }
                else {
                    validatePor.setEnabled(true);
                }
            }
            //Other Function I must keep (interface)
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }
        });
    }

    private void buildPanelAddCat(){
        jpAddCat = new JPanel(new GridBagLayout());
        catName = new JTextField();
        catTitle = new JLabel("Libéllé de la catégorie");
        validateCat = new JButton("Ajouter la catégorie");
        ImageIcon icon = Tree.createImageIcon("img/folder-close.png",20);
        validateCat.setIcon(icon);
        validateCat.setEnabled(false);//because it's empty
        GridBagConstraints c = new GridBagConstraints();
        catName.setPreferredSize(new Dimension(600, 20));
        //catName.setMinimumSize(new Dimension(50, 400));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        jpAddCat.add(catTitle, c);

        c.gridy++;
        jpAddCat.add(catName, c);

        c.gridy++;
        jpAddCat.add(validateCat, c);
    }

    private void buildPanelAddPor(){
        jpAddPor = new JPanel(new GridBagLayout());
        portionText = new JTextArea();
        porTitle = new JLabel("Contenu de la portion de texte");
        validatePor = new JButton("Ajouter la portion de texte");

        ImageIcon icon = Tree.createImageIcon("img/p.png",20);
        validatePor.setIcon(icon);
        validatePor.setEnabled(false);//because it's empty
        GridBagConstraints c = new GridBagConstraints();
        portionText.setPreferredSize(new Dimension(580, 400));

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        jpAddPor.add(porTitle, c);

        c.gridy++;
        jpAddPor.add(portionText, c);

        c.gridy++;
        jpAddPor.add(validatePor, c);
    }

    public void newCat(DefaultMutableTreeNode selectedNode, JFrame frame){//Shows Jdialog to enter new Category
        this.selectedCat = (categorie)selectedNode.getUserObject();
        JDialog d = new JDialog(frame, "Nouvelle Catégorie", true);
        if(selectedNode!=null){
            setParentTitle(selectedNode.toString());
        }else{
            setParentTitle("");
        }
        //JDialog constraints to display WELL and fill space
        d.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor=GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy= 0;

        d.add(jpAddCat,gbc);
        setDialog(d);
        d.pack();
        d.setLocationRelativeTo(frame);
        d.setResizable(true);
        d.setVisible(true);
    }

    public void setParentTitle(String title){
        parentTitle = title;
    }

    public void setTree(Tree tree){
        this.tree = tree;
    }
    public JPanel getJpAddCat() {
        return jpAddCat;
    }
    public void setDialog(JDialog jd){
        this.d =jd;
    }

    public void newPor(DefaultMutableTreeNode selectedNode, JFrame frame) {
        this.selectedCat = (categorie) selectedNode.getUserObject();
        JDialog d = new JDialog(frame, "Nouvelle Portion de texte", true);
        if(selectedNode!=null){
            setParentTitle(selectedNode.toString());
        }else{
            setParentTitle("");
        }
        //JDialog constraints to display WELL and fill space
        d.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor=GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy= 0;

        jpAddPor.setForeground(Color.BLUE);
        d.add(jpAddPor,gbc);
        setDialog(d);
        //d.add(jpAddPor);
        d.pack();
        d.setLocationRelativeTo(frame);
        d.setResizable(true);
        d.setVisible(true);
    }
}
