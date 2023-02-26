package Client;

import ClassesLib.Lecteur;
import ClassesLib.Livre;
import Serveur.RelationBaseDeDonnees;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class RelationServeur {

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    public RelationServeur(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void Connection() {
        System.out.println("Connection au serveur...");

        try {
            socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.flush(); // flush directement apres la creation, pour des raisons qui me sont inconnus
            // sans cela la creation du InputStream crash
            InputStream inputStream = socket.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            objectInputStream = new ObjectInputStream(bufferedInputStream);
        } catch (UnknownHostException e) {
            System.err.println("Hote inconnue " + host);
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation du socket : " + e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.err.println("Erreur lors de la connection au serveur : " + e.getMessage());
            System.exit(-1);
        }

        System.out.println("Serveur connecté avec succès.");
    }


    // Envoi d'un objet
    public void EnvoiObjet(Object object) {

        System.out.println("Envoi d'objet");

        try {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi d'un object : " + e.getMessage());
        }
    }

    // Envoi d'une liste objet
    public <T> void EnvoiListeObjet(List<T> objetAEnvoyer) {

        try {
            for (T objet : objetAEnvoyer) {
                objectOutputStream.writeObject(objet);
                System.out.println("Envoi de : " + objet.getClass());
            }
            objectOutputStream.writeObject(null); // l'envoi d'un null arrête la boucle de reception du serveur
            objectOutputStream.writeObject(null); // sans ça, une exception est lever sur le serveur

            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi d'un object : " + e.getMessage());
        }
    }

    // recevoir une liste d'objet
    // le try catch est là car : un objet null signal la fin de transmission or si une exception est lever dans
    // recevoirObjet l'objet null ne sera jamais recu le programme continura de penser que la transmission n'est pas fini
    // ce try catch permet donc de sortir de la boucle au cas où une exception serait lever dans RecevoirObjet
    public List<Object> RecevoirListeObjet() {
        Object objetRecu;
        List<Object> listeObjetRecu = new ArrayList<>();
        while (true) {
            try {
                objetRecu = RecevoirObjet();
                if (objetRecu == null) {
                    break;
                }
                listeObjetRecu.add(objetRecu);
            } catch (Exception e) {
                System.err.println("Erreur lors de la reception de l'objet : " + e.getMessage());
                break;
            }
        }
        return listeObjetRecu;
    }


    // recevoir un objet
    public Object RecevoirObjet() {
        Object objetRecu = null;
        try {
            objetRecu = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors de la reception de l'objet : " + e.getMessage());
        }
        return objetRecu;
    }

    // Deconnection, libere les ressources
    public void Deconnection() {

        System.out.println("Deconnection du serveur...");

        try {
            objectInputStream.close();
            objectOutputStream.close();
            bufferedInputStream.close();
            bufferedOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la resitution des ressources socket input/output streams : " + e.getMessage());
        }

        System.out.println("Serveur deconnecter avec succès.");
    }
}
