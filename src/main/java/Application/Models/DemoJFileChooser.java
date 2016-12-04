package Application.Models;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class DemoJFileChooser extends JPanel {
    JButton go;

    JFileChooser chooser;
    String choosertitle;
    String selectedPath;
    JFrame originFrame;
    JFrame frame;

    public DemoJFileChooser(JFrame originFrame) {
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
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
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