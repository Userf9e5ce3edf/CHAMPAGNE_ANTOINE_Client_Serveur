package Client;
import ClassesLib.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Client {

    // variable globale, scanner pour recuperer les saisies utilisateur
    private static Scanner scanner = new Scanner(System.in);

    // fonction pour saisir un nombre n de livre
    private static List<Livre> saisirLivres() {

        Scanner scanner = new Scanner(System.in);
        List<Livre> livres = new ArrayList<Livre>();

        System.out.print("Combien de livres voulez-vous saisir? ");
        int nombreLivres = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < nombreLivres; i++) {
            System.out.println("\n\n" + "Saisie du livre numéro " + (i+1));
            livres.add(saisirLivre());
        }

        return livres;
    }

    // fonction pour saisir un Livre
    private static Livre saisirLivre() {

        String titre, auteur, editeur;
        int anneePublication;

        System.out.print("Saisir le titre : ");
        titre = scanner.nextLine();

        System.out.print("Saisir l'auteur : ");
        auteur = scanner.nextLine();

        while (true) {
            try {
                System.out.print("Saisir l'année de publication : ");
                anneePublication = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Erreur de saisie : veuillez saisir un nombre entier");
            }
        }

        System.out.print("Saisir l'éditeur : ");
        editeur = scanner.nextLine();

        return new Livre(titre, auteur, anneePublication, editeur);
    }

    // fonction pour saisir un nombre n de Lecteur
    private static List<Lecteur> saisirLecteurs(List<Livre> livres) {

        List<Lecteur> lecteurs = new ArrayList<Lecteur>();

        System.out.print("Combien de lecteurs voulez-vous saisir? ");
        int nombreLecteurs = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < nombreLecteurs; i++) {
            System.out.println("\n\n" + "Saisie du lecteur numéro " + (i+1));
            lecteurs.add(saisirLecteur(livres));
        }

        return lecteurs;
    }

    // fonction pour saisir un Lecteur, possibilite de lui donner un Livre, et aussi de creer ce livre ou
    // d'en utiliser un deja existant
    private static Lecteur saisirLecteur(List<Livre> livres) {

        String nom, prenom, choixAjouterLivre, choixLivreExistantNouveau;
        Livre livreEnCoursDeLecture = null;

        System.out.print("Saisir le nom du lecteur : ");
        nom = scanner.nextLine();

        System.out.print("Saisir le prénom du lecteur : ");
        prenom = scanner.nextLine();

        do { // forcer l'utilisateur à entrer "O" ou "N"
            System.out.print("Voulez-vous ajouter un livre pour ce lecteur ? (O/N) : ");
            choixAjouterLivre = scanner.nextLine();
        } while (!(choixAjouterLivre.equalsIgnoreCase("O")) && !(choixAjouterLivre.equalsIgnoreCase("N")));

        if (choixAjouterLivre.equalsIgnoreCase("O")) {
            do { // forcer l'utilisateur à entrer "E" ou "N"
                System.out.print("Voulez-vous choisir un livre existant ou en saisir un nouveau ? (E/N) (E : Existant, N : Nouveau) : ");
                choixLivreExistantNouveau = scanner.nextLine();

            } while (!(choixLivreExistantNouveau.equalsIgnoreCase("E")) && !(choixLivreExistantNouveau.equalsIgnoreCase("N")));

            if (choixLivreExistantNouveau.equalsIgnoreCase("E") && !(livres.isEmpty())) {
               // le livre existe donc on va le chercher dans la liste des livres
               livreEnCoursDeLecture = SaisieLivreSupplementaire(livres);

            } else if (choixLivreExistantNouveau.equalsIgnoreCase("N")) {
                // le livre n'existe pas on en creer un nouveau
                livreEnCoursDeLecture = saisirLivre();
                // On n'ajoute pas ce nouveau livre dans la liste de tous les livres, car il est utilisé par son lecteur
                // et ne peut donc pas etre mis dans une liste de livre qui eux sont tous disponible.

            } else if ((choixAjouterLivre.equalsIgnoreCase("O")) && (livres.isEmpty())) {
                // l'utilisateur veut rajouter un livre de la liste alors que celle-ci est vide
                livreEnCoursDeLecture = SaisieLivreSuppListeVide();
            }
        }

        return new Lecteur(nom, prenom, livreEnCoursDeLecture);
    }

    // Saisie un livre alors que la liste de livre est vide,
    private static Livre SaisieLivreSuppListeVide() {

        String choixLivresListeVide;
        Livre livreEnCoursDeLecture = null;

        do {
            System.out.print("La liste de livre est vide, voulez vous saisir le livre manuellement ? (O/N) : ");
            choixLivresListeVide = scanner.nextLine();
        } while (!(choixLivresListeVide.equalsIgnoreCase("O")) && !(choixLivresListeVide.equalsIgnoreCase("N")));

        if (choixLivresListeVide.equalsIgnoreCase("O")) {
            livreEnCoursDeLecture = saisirLivre();
        }

        return livreEnCoursDeLecture;
    }

    // Demande à l'utilisateur de choisir un livre de la liste
    private static Livre SaisieLivreSupplementaire(List<Livre> livres) {

        Livre livreEnCoursDeLecture = null;
        boolean livreTrouve = false;

        System.out.println("Liste des livres :");
        // On affiche tous les Livres
        for (Livre livre : livres) {
            System.out.println(livre.getTitre());
        }

        do { // force l'utilisateur à saisir un livre est dans la liste
            System.out.print("Saisir le titre du livre : ");
            String titreLivre = scanner.nextLine();

            // trouve le livre dans la liste des livres
            Iterator<Livre> iterator = livres.iterator();
            while (iterator.hasNext()) {
                Livre livre = iterator.next();
                if (livre.getTitre().equalsIgnoreCase(titreLivre)) {
                    livreEnCoursDeLecture = livre;
                    iterator.remove(); // retire le livre trouver de la liste
                    livreTrouve = true;
                }
            }

            if (!livreTrouve) {
                System.out.print("Le livre n'est pas dans la liste, veuillez saisir un titre valide : ");
            }
        } while (!livreTrouve);

        return livreEnCoursDeLecture;
    }

    public static void main(String[] args) {

        System.out.println("Démarrage du client...");
        int nombre;
        boolean continuer = true;
        List<Livre> livres = new ArrayList<Livre>(); // liste de tous les livres
        List<Lecteur> lecteurs = new ArrayList<Lecteur>(); // liste de tous les lecteurs
        RelationServeur serveur = new RelationServeur("localhost", 1234); // objet serveur

        while (continuer) {
            try {
                System.out.print("""
                        \s
                        \s
                        Veuillez saisir un nombre entre 1 et 5 (ou 6 pour quitter)\s
                        1 pour saisir des livres\s
                        2 pour saisir des lecteurs\s
                        3 pour envoyer des livres\s
                        4 pour envoyer des lecteurs\s
                        5 pour afficher tout les livres et les lecteurs en base de donnees\s
                        >\040"""); // \040 pour faire un whitespace

                String input = scanner.nextLine();
                nombre = Integer.parseInt(input);

                if (nombre == 6) {
                    continuer = false;
                } else if (nombre < 1 || nombre > 5) {
                    System.out.println("Nombre invalide");
                } else {
                    switch (nombre) {
                        case 1 -> {
                            System.out.println("\t Saisie de livre/s");
                            livres.addAll(saisirLivres());
                        }
                        case 2 -> {
                            System.out.println("\t Saisie de lecteur/s");
                            lecteurs.addAll(saisirLecteurs(livres));
                        }
                        case 3 -> {
                            // Vérifier si la liste de livres n'est pas vide
                            if (livres.isEmpty()) {
                                System.out.println("La liste de livres est vide.");
                                break;
                            }
                            System.out.println("\t Envoie de livre/s...");
                            String requete = "Ecrire";
                            // Connection au serveur
                            serveur.Connection();
                            // Envoi de tous les livres au serveur
                            serveur.EnvoiObjet(requete); // dit au serveur que l'ont envoi des objets
                            // à mettre dans la base de donnees
                            serveur.EnvoiListeObjet(livres);
                            // Deconnection, liberation des ressources
                            serveur.Deconnection();

                            // on remet la liste de livres à 0.
                            livres.clear();
                        }
                        // La deconnection est faite par le serveur
                        case 4 -> {
                            if (lecteurs.isEmpty()) {
                                System.out.println("La liste de lecteurs est vide.");
                                break;
                            }
                            System.out.println("\t Envoie de lecteur/s..");
                            String requete = "Ecrire";

                            serveur.Connection();
                            serveur.EnvoiObjet(requete);
                            serveur.EnvoiListeObjet(lecteurs);
                            serveur.Deconnection();

                            lecteurs.clear();
                        }
                        case 5 -> {
                            String requete = "Lire";
                            List<Object> listeEnBaseDeDonnee;
                            serveur.Connection();
                            serveur.EnvoiObjet(requete);
                            serveur.EnvoiObjet(null); // signal la fin d'envoi
                            listeEnBaseDeDonnee = serveur.RecevoirListeObjet();
                            serveur.Deconnection();

                            System.out.println("Lecture de la base de donnees...\n");

                            if(listeEnBaseDeDonnee.isEmpty()) {
                                System.out.println("La base de donnees est vide\n");
                            } else {
                                for (Object obj : listeEnBaseDeDonnee) {
                                    System.out.println("\t--> " + obj.toString());
                                }
                            }

                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre valide.");
            }
        }

        scanner.close();
    }
}
