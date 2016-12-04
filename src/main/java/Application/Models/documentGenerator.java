package Application.Models;

import Application.Views.Tree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Yeso on 01/12/2016.
 */
public class documentGenerator {
    private ArrayList<DefaultMutableTreeNode> nodesPortions;
    JFrame mainFrame;
    JFrame frame;
    JLabel title;
    JTextField docTitle;
    JButton generate;
    ArrayList<categorie> cats;
    ArrayList<portion> portions;
    Tree tree;
    JTree jTree;
    DefaultMutableTreeNode root;

    public documentGenerator(ArrayList<categorie> cats, ArrayList<portion> portions, JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.cats = cats;
        this.portions = portions;
        buildWindow();
        buildTree();
        buildInterface();
        textListener();
        generateDocListener();
        frame.setVisible(true);
        mainFrame.setVisible(false);
        onClose();
    }

    private void buildWindow(){
        frame = new JFrame("Génération de document");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);// pour eviter que l'appli se ferme quand on ferme la generation de doc
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
    }

    private void buildTree(){
        categorie racine = new categorie(0,"Contenu",0);
        this.root = new DefaultMutableTreeNode(racine);
        tree = new Tree(null,cats,portions,root);
        //tree.addBastardsToRoot();
        tree.showTree(root);
    }

    private void buildInterface(){
        JPanel pan = new JPanel(new GridBagLayout());
        title = new JLabel("Titre du document");
        docTitle= new JTextField();
        generate = new JButton("Générer le fichier");
        ImageIcon fileIcon = Tree.createImageIcon("/img/file.png",25);
        generate.setIcon(fileIcon);
        generate.setEnabled(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        pan.add(title,c);

        c.gridy++;
        pan.add(docTitle,c);

        c.gridy++;
        pan.add(tree.getTree(),c);
        //tree.showTree(root);

        c.gridy++;
        pan.add(generate,c);

        c.anchor = GridBagConstraints.NORTH;
        frame.setLayout(new GridBagLayout());
        frame.add(pan,c);
    }

    private void witeFile(String path){
        System.out.println("writeFile");
        File file = new File(path, docTitle.getText()+".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Le chier n'existe pas et a été créer");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

            String str = new String();
            str = generateFileContent(str,root,"");
            bw.write(str);

            bw.close();
            Desktop.getDesktop().edit(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFileContent(String str, DefaultMutableTreeNode node, String level){
        int catCount =0;
        for (int i=0; i< node.getChildCount();i++){

            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)node.getChildAt(i);
            if(currentNode.getUserObject() instanceof categorie) {
                catCount++;
                categorie cat = (categorie)currentNode.getUserObject();
                str+=System.getProperty("line.separator");
                str += level+catCount+" "+cat.getLibelle()+System.getProperty("line.separator");//currentNode.getLevel()
                str = generateFileContent(str,currentNode,level+catCount+".");
            }
            else if(currentNode.getUserObject() instanceof portion){
                portion por = (portion) currentNode.getUserObject();
                str+= por.getText()+System.getProperty("line.separator");;
            }
        }
        return str;
    }

    public void onClose(){

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int i=JOptionPane.showConfirmDialog(null, "Voulez-vous abandonner la génération du document ?", "Abandonner", JOptionPane.YES_NO_OPTION);
                if(i==0) {//SI OUI ALORS ENLEVER CETTE FENETRE ET REVENIR A LA FENETRE PRINCIPALE
                    root.removeAllChildren();
                    mainFrame.setVisible(true);
                    frame.dispose();
                }
            }
        });
    }

    public void generateDocListener() {
        //Quand le bouton Ajouter Catégorie est cliqué on affiche un Jdialog d'ajout
        generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //JFrame frame = new JFrame("");
                DemoJFileChooser fileChooser = new DemoJFileChooser(frame);
                System.out.println("Chemin du test "+fileChooser.getSelectedPath());
                if(fileChooser.getSelectedPath()!= null)//if user chose a destination
                    witeFile(fileChooser.getSelectedPath());

            }
        });
    }

    public void textListener(){
        docTitle.getDocument().addDocumentListener(new DocumentListener() {

            public void changed() {
                if (docTitle.getText().equals("")){
                    generate.setEnabled(false);
                }
                else {
                    generate.setEnabled(true);
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


}
