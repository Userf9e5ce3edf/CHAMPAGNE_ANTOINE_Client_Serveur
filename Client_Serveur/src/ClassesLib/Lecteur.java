package ClassesLib;

import java.io.Serializable;

public class Lecteur implements Serializable {
    // Les attributs
    private String nom;
    private String prenom;
    private Livre livreEnCoursDeLecture;

    // Constructeurs
    public Lecteur(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public Lecteur(String nom, String prenom, Livre livre) {
        this.nom = nom;
        this.prenom = prenom;
        this.livreEnCoursDeLecture = livre;
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Livre getLivreEnCoursDeLecture() {
        return livreEnCoursDeLecture;
    }

    // Methode

    public void changerLivreEnCoursDeLecture(Livre livre) {
        this.livreEnCoursDeLecture = livre;
    }

    @Override
    public String toString() {
        return "Lecteur{" + "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", livreEnCoursDeLecture=" + livreEnCoursDeLecture +
                '}';
    }
}

