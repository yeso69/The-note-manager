package Application.Views;

import Application.Models.categorie;
import Application.SQLite.bdd;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Yeso on 29/11/2016.
 */
public class categoryControl {
    JPanel pAdd;
    JLabel title;
    JTextField catName;
    JButton validate;
    JDialog d;
    bdd db;
    Tree tree;
    String parentTitle;

    public categoryControl(bdd bdd, Tree tree) {
        this.db = bdd;
        this.tree = tree;
        buildPanel();
        validateAdd();
    }

    public void validateAdd(){
        //Quand on valide l'ajout d'une catégorie
        validate.addActionListener(new ActionListener()
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
                    validate.setEnabled(false);
                }
                else {
                    validate.setEnabled(true);
                }

            }
        });
    }

    private void buildPanel(){
        pAdd = new JPanel(new GridBagLayout());
        catName = new JTextField();
        title = new JLabel("Titre");
        validate = new JButton("Valider");
        validate.setEnabled(false);//because it's empty
        GridBagConstraints c = new GridBagConstraints();
        catName.setPreferredSize(new Dimension(400, 20));
        //catName.setMinimumSize(new Dimension(50, 400));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        pAdd.add(title, c);

        c.gridx++;
        pAdd.add(catName, c);

        c.gridy++;
        pAdd.add(validate, c);
    }

    public void newCat(DefaultMutableTreeNode selectedNode, JFrame frame){//Shows Jdialog to enter new Category
        JDialog d = new JDialog(frame, "Nouvelle Catégorie", true);
        if(selectedNode!=null){
            setParentTitle(selectedNode.toString());
        }else{
            setParentTitle("");
        }
        setDialog(d);
        d.add(pAdd);
        d.setLocationRelativeTo(frame);
        d.pack();
        d.setResizable(false);
        d.setVisible(true);
    }

    public void setParentTitle(String title){
        parentTitle = title;
    }

    public void setTree(Tree tree){
        this.tree = tree;
    }
    public JPanel getpAdd() {
        return pAdd;
    }
    public void setDialog(JDialog jd){
        this.d =jd;
    }
}
