package model;

import java.io.Serializable;

public class Dico implements Serializable {

    private String mot_creole;
    private String traduction;


    public Dico() {
        this.mot_creole = mot_creole;
        this.traduction = traduction;
    }



    public String getMot_creole() {
        return mot_creole;
    }

    public void setMot_creole(String mot_creole) {
        this.mot_creole = mot_creole;
    }

    public String getTraduction() {
        return traduction;
    }

    public void setTraduction(String traduction) {
        this.traduction = traduction;
    }

    public String toFullString() {
        return mot_creole.toString()+" "+traduction.toString();
    }

}
