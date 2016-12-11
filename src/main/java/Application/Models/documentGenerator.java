package Application.Models;

import Application.App;
import Application.Views.Tree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
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
    JPanel panMain;
    JPanel rightPanel;
    JLabel title;
    JTextField docTitle;
    JButton generate;
    JButton up;
    JButton down;
    ArrayList<categorie> cats;
    ArrayList<portion> portions;
    JScrollPane preview;
    Tree tree;
    JTree jTree;
    JRadioButton txt;
    JRadioButton html;
    ButtonGroup group;
    JComboBox<css> cssList;
    JComboBox maxLevelList;
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
        onClose();
        treeListeners();
        upDownListener();

        //frame.pack();

        frame.setVisible(true);
        mainFrame.setVisible(false);

    }

    private void buildWindow(){
        frame = new JFrame("Génération de document");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);// pour eviter que l'appli se ferme quand on ferme la generation de doc
        //frame.setMinimumSize(new Dimension(600, 400));
        frame.setMinimumSize(new Dimension(700,650));
        frame.setLocationRelativeTo(null);
    }

    private void buildTree(){
        categorie racine = new categorie(0,"Contenu",0);
        this.root = new DefaultMutableTreeNode(racine);
        tree = new Tree(null,cats,portions,root,4);
        tree.showTree(root);
        jTree = tree.getTree();
    }

    private void buildInterface(){
        JPanel panTopLabels = new JPanel(new BorderLayout());
        JPanel panBottom = new JPanel(new GridBagLayout());
        JLabel themeLabel = new JLabel("Theme HTML (CSS) ");
        JLabel formatLabel = new JLabel("Format");
        panMain = new JPanel(new BorderLayout());
        title = new JLabel("Nom du fichier");
        docTitle= new JTextField();
        generate = new JButton("Générer le fichier");
        up = new JButton();
        down = new JButton();
        txt = new JRadioButton("Text");
        html = new JRadioButton("HTML");
        group = new ButtonGroup();
        cssList = new JComboBox<css>();
        //cssList.addItem(new css("ODZ Theme","odz.css"));
        cssList.addItem(new css("Blue & White","white.css"));
        cssList.addItem(new css("Dark","dark.css"));
        cssList.addItem(new css("Classic","classic.css"));
        cssList.setEnabled(true);



        ImageIcon fileIcon = Tree.createImageIcon("/img/file.png",30);
        ImageIcon htmlIcon = Tree.createImageIcon("/img/html.png",35);
        ImageIcon checkIcon = Tree.createImageIcon("/img/check.png",30);
        ImageIcon upIcon = Tree.createImageIcon("/img/up.png",20);
        ImageIcon downIcon = Tree.createImageIcon("/img/down.png",20);
        ImageIcon previewIcon = Tree.createImageIcon("/img/preview.png",30);
        ImageIcon themeIcon = Tree.createImageIcon("/img/theme.png",30);
        ImageIcon titleIcon = Tree.createImageIcon("/img/title.png",30);
        ImageIcon formatIcon = Tree.createImageIcon("/img/format.png",30);

        formatLabel.setIcon(formatIcon);
        themeLabel.setIcon(themeIcon);
        up.setIcon(upIcon);
        down.setIcon(downIcon);
        title.setIcon(titleIcon);

        generate.setIcon(checkIcon);
        generate.setEnabled(false);


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;

        //LABELS OF THE TOP PANEL
        panTopLabels.add(title,BorderLayout.NORTH);
        panTopLabels.add(formatLabel,BorderLayout.CENTER);
        panTopLabels.add(themeLabel,BorderLayout.SOUTH);


        //FIELDS OF THE TOP PANEL
        JPanel panTopFields = new JPanel(new BorderLayout());
        panTopFields.add(docTitle,BorderLayout.NORTH);

        group.add(txt);
        group.add(html);
        html.setSelected(true);

        JPanel fileFormat = new JPanel(new FlowLayout());
        fileFormat.add(new JLabel(fileIcon));
        fileFormat.add(txt);
        fileFormat.add(new JLabel(htmlIcon));
        fileFormat.add(html);

        panTopFields.add(fileFormat,BorderLayout.CENTER);
        panTopFields.add(cssList,BorderLayout.SOUTH);

        //ADDING LABELS ANDS FIELDS IN A TOP PANEL P
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panTopLabels,BorderLayout.WEST);
        panelTop.add(panTopFields,BorderLayout.CENTER);



        panMain.add(panelTop,BorderLayout.NORTH);

        //LEFT PANEL ANS SUB PANELS
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel upDownPanel = new JPanel(new BorderLayout());
        upDownPanel.add(down,BorderLayout.WEST);
        upDownPanel.add(up,BorderLayout.EAST);
        JLabel moveLabel = new JLabel("Déplacer");
        upDownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        upDownPanel.add(moveLabel,BorderLayout.CENTER);
        leftPanel.add(upDownPanel,BorderLayout.SOUTH);
        JScrollPane scroll = new JScrollPane(tree.getTree());

        leftPanel.add(scroll,BorderLayout.CENTER);

        maxLevelList = new JComboBox<>();
        for(int i = 2; i< 8;i++){
            maxLevelList.addItem(i);
        }
        maxLevelList.setSelectedIndex(2);
        leftPanel.add(maxLevelList,BorderLayout.NORTH);


        panMain.add(leftPanel, BorderLayout.WEST);

        rightPanel = new JPanel(new BorderLayout());
        JLabel previewLabel = new JLabel("Aperçu du document");
        previewLabel.setIcon(previewIcon);
        rightPanel.add(previewLabel, BorderLayout.NORTH);
        showPreview();
        panMain.add(rightPanel,BorderLayout.CENTER);

        //MARGINS
        rightPanel.setBorder(BorderFactory.createEmptyBorder(
                5, //top
                5,     //left
                5, //bottom
                0));   //right
        leftPanel.setBorder(BorderFactory.createEmptyBorder(
                5, //top
                0,     //left
                5, //bottom
                5));   //right
        panMain.setBorder(BorderFactory.createEmptyBorder(
                5, //top
                5,     //left
                5, //bottom
                5));   //right
        panMain.add(generate,BorderLayout.SOUTH);
        frame.setLayout(new BorderLayout());
        //frame.add(panTop,BorderLayout.NORTH);
        frame.add(panMain,BorderLayout.CENTER);

    }

    private void writeFile(String path){
        File file;
        if(html.isSelected()) {
            file = new File(path, docTitle.getText()+".html");
        }else{
            file = new File(path, docTitle.getText()+".txt");
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Un fichier du même nom existe déjà à cet emplacement.", "Impossible de créer le fichier",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(),false));



            bw.write(getCompiledContent());
            bw.close();
            //str = str.replace(/(?:\r\n|\r|\n)/g, '<br />');

            //CSS copy to the same destination
            css myCss = (css) cssList.getSelectedItem();
            java.net.URL cssURL = App.class.getResource(myCss.path);//important let App
            //System.out.println("Dossier ressources"+App.class.getResource(""));
            //System.out.println("Mon css: "+myCss.path+myCss.fileName);
            File cssFile = new File(cssURL.getPath(), myCss.fileName);
            myCss.copy(cssFile,new File(path,"style.css"));
            JOptionPane.showMessageDialog(new JFrame(), "Vous trouverez le fichier dans la destination spécifiée.", "Création du document terminée",
                    JOptionPane.INFORMATION_MESSAGE);
            //Desktop.getDesktop().edit(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCompiledContent(){
        String top = new String();
        String content = new String();
        String bottom = new String();
        String document = new String();
        if(txt.isSelected()) {
            document = generateFileContent(top, root, "");
            //replace all line separators by \n
            document.replaceAll(System.getProperty("line.separator"), "\n");
        } else{
            top="<!DOCTYPE html>\n" +
                    "<html lang=\"fr\">\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <title>"+docTitle.getText()+"</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
                    "  </head>\n" +
                    "  <body>";
            content = generateHtmlFileContent(new String(),root,1);
            bottom ="  </body>\n" +
                    "</html>";

            document = top+content+bottom;

        }
        return document;
    }

    private void showPreview(){
        if(html.isSelected()) {
            // create a JEditorPane
            JEditorPane jEditorPane = new JEditorPane();

            // make it read-only
            jEditorPane.setEditable(false);

            // add a HTMLEditorKit to the editor pane
            HTMLEditorKit kit = new HTMLEditorKit();
            jEditorPane.setEditorKit(kit);

            // now add it to a scroll pane by first removing the old one
            if (preview != null)
                rightPanel.remove(preview);
            preview = new JScrollPane(jEditorPane);

            // add some styles to the html
            css myCss = (css) cssList.getSelectedItem();
            java.net.URL cssURL = App.class.getResource(myCss.path);//important let App
            File cssFile = new File(cssURL.getPath(), myCss.fileName);
            FileReader f = null;
            StyleSheet styleSheet = kit.getStyleSheet();
            try {
                f = new FileReader(cssFile);
                styleSheet.loadRules(f, cssURL);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            kit.setStyleSheet(styleSheet);

            // create a document, set it on the jeditorpane, then add the html
            Document doc = kit.createDefaultDocument();
            jEditorPane.setDocument(doc);
            jEditorPane.setText(getCompiledContent());
            jEditorPane.setCaretPosition(0);
//            JDialog d = new JDialog(frame, "Preview", true);
//            d.add(jEditorPane);
//            d.setVisible(true);

        }
        else{//if TEXT is selected
            if (preview != null)
                rightPanel.remove(preview);
            JTextArea content = new JTextArea(getCompiledContent());
            content.setEditable(false);
            preview = new JScrollPane(content);
        }
        //ADD THE PREVIEW TO THE RIGHT PART OF THE FRAME
        rightPanel.add(preview,BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();

    }

    private String generateHtmlFileContent(String str, DefaultMutableTreeNode node, int level) {
        for (int i=0; i< node.getChildCount();i++){
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)node.getChildAt(i);
            String tmp = new String();
            if(currentNode.getUserObject() instanceof categorie) {
                categorie cat = (categorie)currentNode.getUserObject();
                str += "<h"+level+">"+cat.getLibelle()+"</h"+level+">"+System.getProperty("line.separator");
                str = generateHtmlFileContent(str,currentNode,level+1);
            }
            else if(currentNode.getUserObject() instanceof portion){
                portion por = (portion) currentNode.getUserObject();
                tmp = por.getText().replaceAll("(\\r|\\n|\\r\\n)+",  "<br />"+System.getProperty("line.separator"));
                str+="<p>"+tmp+"</p>"+System.getProperty("line.separator");
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
                if(fileChooser.getSelectedPath()!= null) {//if user chose a destination
                    writeFile(fileChooser.getSelectedPath());
                    frame.dispose();
                    mainFrame.setVisible(true);
                }

            }
        });
    }

    public void upDownListener() {
        up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.moveNode(true);
                showPreview();
            }
        });

        down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.moveNode(false);
                showPreview();
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
                //css myCss = (css)cssList.getSelectedItem();
                showPreview();
            }
        });

        maxLevelList.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                tree.setMaxLevel((int)maxLevelList.getSelectedItem());
                showPreview();
            }
        });

    }

    public void radiobuttonsListener(){
        txt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                cssList.setEnabled(false);
                showPreview();
            }
        });

        html.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                cssList.setEnabled(true);
                showPreview();
            }
        });
    }



    private void treeListeners() {
        // JTREE ----> Quand un élément de l'arbre est sélectionner
        TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                System.out.println("Dans listener");
                JTree treeSource = (JTree) treeSelectionEvent.getSource();
                //Getting paths of all selected nodes
                TreePath[] treePaths = treeSource.getSelectionPaths();

                if(treePaths == null){
                    return;
                }

                //we get selected node + root
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree.getModel().getRoot();

                if (selectedNode != null) {//if something is selected we can delete
                    if (selectedNode.isRoot()) {
                        up.setEnabled(false);
                        down.setEnabled(false);
                    }
                    else{
                        up.setEnabled(true);
                        down.setEnabled(true);
                    }
                }

            }
        };
        jTree.addTreeSelectionListener(treeSelectionListener);
    }

}
