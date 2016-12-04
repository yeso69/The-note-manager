package Application.Models;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class JFileChooser extends JPanel {
    JButton go;

    javax.swing.JFileChooser chooser;
    String choosertitle;
    String selectedPath;
    JFrame originFrame;
    JFrame frame;

    public JFileChooser(JFrame originFrame) {
        frame = new JFrame("Choisissez une destination");
        this.originFrame = originFrame;
        addWindowListener();
        selectPath();
        choosertitle= "Heyyyyyyyyyyyyyyy !";
    }

    public String getSelectedPath() {
        return selectedPath;
    }

    public String selectPath() {
        chooser = new javax.swing.JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            //System.out.println("getCurrentDirectory(): "+  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " +  chooser.getSelectedFile().getPath());
            selectedPath = chooser.getSelectedFile().getPath();
        }
        else {
            System.out.println("No Selection ");
        }
        return null;
    }

    public Dimension getPreferredSize(){
        return new Dimension(200, 200);
    }


    private void addWindowListener() {
        final WindowAdapter windowAdapter1 = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
                originFrame.setVisible(true);
            }
        };
    }
}