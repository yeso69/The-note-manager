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
                newPor.replaceAll(System.getProperty("line.separator"), "\n");
                int idCat = selectedCat.getId();
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
                if (db.existCategorie(newCat)){ //Vérification de l'unicité du libélé
                    JOptionPane.showMessageDialog(null,
                            "Une catégorie du même nom existe déjà. Veuillez en saisir une autre.", "Création impossible",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println("Existe déja !");
                }
                else {
                    int idParent = 0;
                    if(!parentTitle.equals("")){
                        idParent = db.getId(parentTitle);
                    }
                    categorie cat = new categorie(0,newCat,idParent);
                    db.addCategorie(cat);//Add the new category to the db
                    tree.refresh();
                    d.dispose();//Close dialog
                    catName.setText("");//clear textfield
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
        jpAddCat = new JPanel(new BorderLayout());
        catName = new JTextField();
        catTitle = new JLabel("Libéllé de la catégorie");
        validateCat = new JButton("Ajouter la catégorie");
        ImageIcon icon = Tree.createImageIcon("/img/folder-close.png",25);
        validateCat.setIcon(icon);
        validateCat.setEnabled(false);//because it's empty
        GridBagConstraints c = new GridBagConstraints();
        catName.setPreferredSize(new Dimension(600, 20));

        jpAddCat.add(catTitle, BorderLayout.NORTH);

        jpAddCat.add(catName, BorderLayout.CENTER);

        jpAddCat.add(validateCat, BorderLayout.SOUTH);
    }

    private void buildPanelAddPor(){
        jpAddPor = new JPanel(new BorderLayout());
        portionText = new JTextArea();
        porTitle = new JLabel("Contenu de la portion de texte");
        validatePor = new JButton("Ajouter la portion de texte");


        ImageIcon icon = Tree.createImageIcon("/img/p.png",25);
        validatePor.setIcon(icon);
        validatePor.setEnabled(false);//because it's empty
        JScrollPane scroll = new JScrollPane(portionText);


        jpAddPor.add(porTitle, BorderLayout.NORTH);

        jpAddPor.add(scroll, BorderLayout.CENTER);

        jpAddPor.add(validatePor, BorderLayout.SOUTH);
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
        d.setMinimumSize(new Dimension(400, 400));
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
