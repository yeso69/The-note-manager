package Application.Models;

import Application.Views.categoryControl;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yeso on 30/11/2016.
 */
public class portion {
    private Integer id;
    private String text;
    private int idCat;
    private String keywords;
    List<JCheckBox> checkboxes;

    public portion(Integer id, String text, int idCat, String keywords) {
        this.id = id;
        this.text = text;
        this.idCat = idCat;
        this.keywords = keywords;
    }
    @Override
    public portion clone(){
        return new portion(this.id,this.text,this.idCat, keywords);
    }

    @Override //function used by the Jtree to set a node title
    public String toString() {
        //Getting the first characters of the portion text to display it on the tree as title of the node
        String nodeTitle = new String("");
        if(this.getText().length() > 50){
            nodeTitle = this.getText().substring(0,25) + "...";
            return nodeTitle;
        }
        return this.text;
    }

    //----------------------------GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIdCat() {
        return idCat;
    }

    public void setIdCat(int idCat) {
        this.idCat = idCat;
    }

    public JPanel getKeywordsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        checkboxes = new ArrayList<>();

        String[] keyTrimed = keywords.split(",");
        if(keyTrimed.length == 1 && keyTrimed[0].equals(""))//if it's emptu don't show an empty box
            return panel;
        for(int i =0; i<keyTrimed.length;i++){
            JCheckBox box = new JCheckBox(keyTrimed[i]);
            checkboxes.add(box);
            box.setSelected(true);
            panel.add(box);
            System.out.println("Mot clé --> "+keyTrimed[i]);
        }

        return panel;
    }

    public String getKeywords() {
        return keywords;
    }

    public void addKeywords(String keywords){
        this.keywords+=keywords;
    }

    public void setKeywords(String keywords) {

    }

    public boolean updateKeywords(boolean add) {//if true we add the selected keywords if false we delete them
        String updatedKeywords = new String();
        String keyword = new String();


        if(add){//SAVE CHANGES
            int yesNo=JOptionPane.showConfirmDialog(null, "Les mot-clés non sélectionnés ne seront pas sauvegardé.\nVoulez-vous poursuivre l'enregistrement ?", "Sauvegarde", JOptionPane.YES_NO_OPTION);
            if(yesNo==0) {//SI OUI
                for(int i = 0; i<checkboxes.size();i++) {//concat all keyword with "," after each

                    keyword = checkboxes.get(i).getText();
                    System.out.println("Size= "+checkboxes.size()+"Keyword: "+keyword);
                    if (checkboxes.get(i).isSelected()) {
                            updatedKeywords += keyword+ ",";
                    }
                }
            }
            else return false;//IF NO JUST RETURN
        }
       else {//DELETE KEYWORDS
            int yesNo = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer tous les mots-clés séléctionés ?", "Supprimer les mots-clés", JOptionPane.YES_NO_OPTION);
            if (yesNo == 0) {//SI OUI
                for (int i = 0; i < checkboxes.size(); i++) {
                    keyword = checkboxes.get(i).getText();
                    if (!checkboxes.get(i).isSelected())//it's a delete we just keep not selected keywords
                        updatedKeywords += keyword + ",";
                }
            } else return false; //IF NO JUST RETURN
        }
        if(updatedKeywords.length()>=1)
            updatedKeywords = updatedKeywords.substring(0,updatedKeywords.length()-1);//to remove the last ","
        System.out.println("updated keywords = "+updatedKeywords);
        //Finally we update the database thanks to the control
        this.keywords = updatedKeywords;
        categoryControl.updatePortion(this);
        return true;
    }
}
