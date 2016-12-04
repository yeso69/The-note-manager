package Application.Models;

import Application.App;
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
    JRadioButton txt;
    JRadioButton html;
    ButtonGroup group;
    JComboBox<css> cssList;
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
        comboboxListener();
        radiobuttonsListener();
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
        txt = new JRadioButton("Text");
        html = new JRadioButton("HTML");
        group = new ButtonGroup();
        cssList = new JComboBox<css>();
        cssList.addItem(new css("Blue & White","white.css"));
        cssList.addItem(new css("Dark","dark.css"));
        cssList.addItem(new css("Classic","classic.css"));
        cssList.setEnabled(false);

        ImageIcon fileIcon = Tree.createImageIcon("/img/file.png",30);
        ImageIcon htmlIcon = Tree.createImageIcon("/img/html.png",35);
        ImageIcon checkIcon = Tree.createImageIcon("/img/check.png",30);
        generate.setIcon(checkIcon);
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


        group.add(txt);
        group.add(html);
        txt.setSelected(true);
        JPanel fileFormat = new JPanel(new FlowLayout());
        fileFormat.add(new JLabel(fileIcon));
        fileFormat.add(txt);
        fileFormat.add(new JLabel(htmlIcon));
        fileFormat.add(html);

        c.gridy++;
        pan.add(fileFormat,c);

        c.gridy++;
        pan.add(new JLabel("Theme HTML (CSS)"),c);

        c.gridy++;
        pan.add(cssList,c);

        c.gridy++;
        pan.add(generate,c);

        c.anchor = GridBagConstraints.NORTH;
        frame.setLayout(new GridBagLayout());
        frame.add(pan,c);
    }

    private void writeFile(String path){
        System.out.println("writeFile");
        File file;
        if(html.isSelected()) {
            file = new File(path, docTitle.getText()+".html");
        }else{
            file = new File(path, docTitle.getText()+".txt");
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Le fichier n'existe pas et a été créer");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Un fichier du même nom existe déjà à cet emplacement.", "Impossible de créer le fichier",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

            String str = new String();
            if(txt.isSelected())
                str = generateFileContent(str,root,"");
            else{
                str+="<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "  <head>\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <title>"+docTitle.getText()+"</title>\n" +
                        "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
                        "  </head>\n" +
                        "  <body>";
                str = generateHtmlFileContent(str,root,1);
                str+="  </body>\n" +
                        "</html>";

                //CSS copy to the same destination
                css myCss = (css) cssList.getSelectedItem();
                java.net.URL cssURL = App.class.getResource(myCss.path);//important let App
                System.out.println("Dossier ressources"+App.class.getResource(""));
                System.out.println("Mon css: "+myCss.path+myCss.fileName);
                File cssFile = new File(cssURL.getPath(), myCss.fileName);
                myCss.copy(cssFile,new File(path,"style.css"));
            }
            bw.write(str);

            bw.close();
            JOptionPane.showMessageDialog(new JFrame(), "Vous trouverez le fichier dans la destination spécifiée.", "Création du document terminée",
                    JOptionPane.INFORMATION_MESSAGE);
            //Desktop.getDesktop().edit(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateHtmlFileContent(String str, DefaultMutableTreeNode node, int level) {
        for (int i=0; i< node.getChildCount();i++){
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)node.getChildAt(i);
            if(currentNode.getUserObject() instanceof categorie) {
                categorie cat = (categorie)currentNode.getUserObject();
                str+="<br/>";
                str += "<h"+level+">"+cat.getLibelle()+"</h"+level+">";//currentNode.getLevel()
                str = generateHtmlFileContent(str,currentNode,level+1);
            }
            else if(currentNode.getUserObject() instanceof portion){
                portion por = (portion) currentNode.getUserObject();
                str+="<p>"+por.getText()+"</p>"+System.getProperty("line.separator");;
            }
        }
        return str;
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
                JFileChooser fileChooser = new JFileChooser(frame);
                if(fileChooser.getSelectedPath()!= null)//if user chose a destination
                    writeFile(fileChooser.getSelectedPath());

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

    public void comboboxListener(){
        cssList.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                css myCss = (css)cssList.getSelectedItem();
                System.out.println("CE CSS EST SELECTED "+myCss);
            }
        });
    }

    public void radiobuttonsListener(){
        txt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                cssList.setEnabled(false);
            }
        });

        html.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                cssList.setEnabled(true);
            }
        });
    }


}
