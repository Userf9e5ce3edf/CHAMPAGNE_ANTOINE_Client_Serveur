package Serveur;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {

    // Creer les 2 tables si celles ci n'exites pas deja
    public static void BDDPremierConnection() {
        RelationBaseDeDonnees baseDeDonnees = new RelationBaseDeDonnees();
        baseDeDonnees.createTables();
        baseDeDonnees.close();
    }

    public static void main(String[] args) {
        System.out.println("Démarrage du serveur...");
        BDDPremierConnection();

        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while (true) {
                System.out.println("Attente de connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connecté a " + clientSocket.getInetAddress().getHostName());

                // Create a RelationClient instance for this client
                RelationAvecClient client = new RelationAvecClient(clientSocket);

                // Receive objects from the client until the connection is closed
                Object objetRecu;
                while ((objetRecu = client.RecevoirObjet()) != null) {
                    System.out.println("Objet reçu : " + objetRecu);

                    String requeteRecu = (String)objetRecu;
                    client.GereRequetes(requeteRecu);
                }

                // Deconnection du client, restitution des ressources
                client.Deconnection();
            }
        } catch (IOException e) {
            System.err.println("Erreur dans le main du serveur : " + e.getMessage());
        }
    }
}