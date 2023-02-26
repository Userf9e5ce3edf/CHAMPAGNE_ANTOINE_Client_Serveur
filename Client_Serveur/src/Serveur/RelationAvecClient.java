package Serveur;
import ClassesLib.Lecteur;
import ClassesLib.Livre;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RelationAvecClient {
    private Socket clientSocket;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;


    // Instanciation de objectInputStream et objectOutputStream
    public RelationAvecClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
            objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.flush(); // flush directement apres la creation, pour des raisons qui me sont inconnus
            // sans cela la creation du InputStream crash
            objectInputStream = new ObjectInputStream(bufferedInputStream);

        } catch (IOException e) {
            System.err.println("Erreur lors de la creation input/output streams: " + e.getMessage());
            System.exit(-1);
        }
    }


    // Methode pour recevoir un objet
    public Object RecevoirObjet() {
        Object objetRecu = null;
        try {
            objetRecu = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors de la reception d'objet : " + e.getMessage());
        }
        return objetRecu;
    }

    // Metode pour gerer les requetes, Au depart deux possibilités, Soit le client veux Lire
    // dans quel cas on lui revoit tous les objets Livre et Lecteur de la base,
    // soit le client veux envoyer des objets Livre ou Lecteur donc Ecrire des objets dans la base de donnees.
    public void GereRequetes(String requete) {

        if (Objects.equals(requete, "Lire")) {
            List<Object> objetAEnvoyer = new ArrayList<Object>();
            // On récupère tous les objets Livre et Lecteur la base
            RelationBaseDeDonnees baseDeDonnees = new RelationBaseDeDonnees();
            objetAEnvoyer.addAll(baseDeDonnees.GetLivresBD());
            objetAEnvoyer.addAll(baseDeDonnees.GetLecteursBD());
            baseDeDonnees.close();
            // On les envoie
            EnvoiListeObjet(objetAEnvoyer);

        } else if (Objects.equals(requete, "Ecrire")) {
            // reception + écriture en base
            EcrireObjetsEnBase();
        } else {
            System.out.println("requete inconnue envoyer par le client");
        }
    }

    // Methode pour envoyer une liste d'objet au Client
    public <T> void EnvoiListeObjet(List<T> objetAEnvoyer) {

        try {
            for (T objet : objetAEnvoyer) {
                objectOutputStream.writeObject(objet);
                System.out.println("Envoi de : " + objet.getClass());
            }
            objectOutputStream.writeObject(null); // l'envoi d'un null arrête la boucle de reception du client
            // sans ça tout se bloque à cause d'une boucle infinit

            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Erreur a l'envoi des objets : " + e.getMessage());
        }
    }

    // Methode pour envoyer des objets dans la base de données
    private void EcrireObjetsEnBase() {
        Object objetRecu;

        try {
            // Connection a la base de donnees
            RelationBaseDeDonnees baseDeDonnees = new RelationBaseDeDonnees();
            while ((objetRecu = RecevoirObjet()) != null) {
                System.out.println("Objet reçu : " + objetRecu);

                // Mettre l'objet en base selon si c'est un livre ou un lecteur
                if (objetRecu instanceof Lecteur) {
                    baseDeDonnees.insertLecteur((Lecteur) objetRecu);
                } else if (objetRecu instanceof Livre) {
                    baseDeDonnees.insertLivre((Livre) objetRecu);
                } else { // objet recu n'est ni un lecteur ni un livre cela ne devrait JAMAIS arriver
                    System.err.println("L'objet recu est invalide");
                }
            }

            baseDeDonnees.close(); // fermeture de la connection a la base de donnees
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise en base de donnees : " + e.getMessage());
        }
    }

    // Deconnection, Restitution des ressources
    public void Deconnection() {

        System.out.println("Deconnection du client...");

        try {
            objectInputStream.close();
            objectOutputStream.close();
            bufferedInputStream.close();
            bufferedOutputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la resitution des ressources socket input/output streams : " + e.getMessage());
        }

        System.out.println("client deconnecter avec succès.");
    }
}
