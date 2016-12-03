package Application.Models;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Yeso on 01/12/2016.
 */
public class documentGenerator {
    private ArrayList<DefaultMutableTreeNode> nodesPortions;
    JFrame mainFrame;
    JFrame frame;
    JTree tree;

    public documentGenerator(ArrayList<DefaultMutableTreeNode> nodesPortions, JFrame mainFrame) {
        this.nodesPortions = nodesPortions;
        this.mainFrame = mainFrame;
        buildWindow();
        frame.setVisible(true);
        mainFrame.setVisible(false);
        buildInterface();
        onClose();
    }

    private void buildWindow(){
        frame = new JFrame("Génération de document");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);// pour eviter que l'appli se ferme quand on ferme la generation de doc
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
    }

    private void buildInterface(){
        //JDialog jd = new JDialog(frame, "Noeuds selectionés", true);
        //jd.setLayout(new FlowLayout());
        JPanel pan = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        DefaultMutableTreeNode currentNode;
        for (Iterator<DefaultMutableTreeNode> iter = nodesPortions.iterator(); iter.hasNext(); ) {
            currentNode = iter.next();
            c.gridy++;
            portion por = (portion)(currentNode.getUserObject());
            pan.add(new JTextArea(por.getText()),c);
        }
        c.anchor = GridBagConstraints.NORTH;
        frame.setLayout(new GridBagLayout());
        frame.add(pan,c);
        //jd.setLocationRelativeTo(frame);
        //jd.pack();
        //jd.setResizable(true);
        //jd.setVisible(true);
    }

    public void onClose(){

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int i=JOptionPane.showConfirmDialog(null, "Voulez-vous abandonner la génération du document ?", "Abandonner", JOptionPane.YES_NO_OPTION);
                if(i==0) {//SI OUI ALORS ENLEVER CETTE FENETRE ET REVENIR A LA FENETRE PRINCIPALE
                    mainFrame.setVisible(true);
                    frame.dispose();
                }
            }
        });
    }


}
