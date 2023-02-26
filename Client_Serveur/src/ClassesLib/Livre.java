package ClassesLib;

import java.io.Serializable;

public class Livre implements Serializable {
    // Les attributs
    private String titre;
    private String auteur;
    private int anneePublication;
    private String editeur;

    // Constructeur
    public Livre(String titre, String auteur, int anneePublication, String editeur) {
        this.titre = titre;
        this.auteur = auteur;
        this.anneePublication = anneePublication;
        this.editeur = editeur;
    }

    // Getters
    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public String getEditeur() {
        return editeur;
    }

    // Methode


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Livre{");
        sb.append("titre='").append(titre).append('\'');
        sb.append(", auteur='").append(auteur).append('\'');
        sb.append(", anneePublication=").append(anneePublication);
        sb.append(", editeur='").append(editeur).append('\'');
        sb.append('}');
        return sb.toString();
    }
}