package Serveur;

import ClassesLib.Lecteur;
import ClassesLib.Livre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RelationBaseDeDonnees {
    // url vers la bdd
    private final String url = "jdbc:mysql://localhost:3306/mabd";
    private final String user = "root";
    private final String password = "root";
    private Connection conn;

    // Initialisation de la variable de connection a la bdd + driver
    public RelationBaseDeDonnees() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.err.println("Erreur ClassNotFound sur le driver : " + e.getMessage());
        }

        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e){
            System.err.println("Erreur SQL lors de la connection a la base : " + e.getMessage());
        }
    }

    // Methode pour créer les tables uniquement si elles n'existent pas deja
    public boolean createTables() {
        String queryLecteurs = """
                CREATE TABLE IF NOT EXISTS Lecteurs (
                    id INT NOT NULL AUTO_INCREMENT,
                    nom VARCHAR(255) NOT NULL,
                    prenom VARCHAR(255) NOT NULL,
                    livre_en_cours_de_lecture INT,
                    PRIMARY KEY (id),
                    FOREIGN KEY (livre_en_cours_de_lecture) REFERENCES Livres(id)
                );
                """;
        String queryLivres = """
                CREATE TABLE IF NOT EXISTS Livres (
                    id INT NOT NULL AUTO_INCREMENT,
                    titre VARCHAR(255) NOT NULL,
                    auteur VARCHAR(255) NOT NULL,
                    annee_publication INT,
                    editeur VARCHAR(255),
                    PRIMARY KEY (id)
                );""";

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(queryLivres);
            stmt.executeUpdate(queryLecteurs);
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur SQl lors de la creation des tables : " + e.getMessage());
            return false;
        }
    }

    // Methode pour insérer un Lecteur dans la bdd, qu'il possède un Livre ou non
    public boolean insertLecteur(Lecteur lecteur) {

        try {
            // Statement pour insérer le Lecteur dans la table lecteur
            String query = "INSERT INTO Lecteurs (nom, prenom, livre_en_cours_de_lecture) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, lecteur.getNom());
            pstmt.setString(2, lecteur.getPrenom());

            Livre livreEnCoursDeLecture = lecteur.getLivreEnCoursDeLecture();
            // Si le lecteur possède un livre
            if (livreEnCoursDeLecture != null) {
                // Insère le livre que le lecteur possède dans la table Livre
                String livreQuery = "INSERT INTO Livres (titre, auteur, annee_publication, editeur) VALUES (?, ?, ?, ?)";
                PreparedStatement livreStmt = conn.prepareStatement(livreQuery, Statement.RETURN_GENERATED_KEYS);
                livreStmt.setString(1, livreEnCoursDeLecture.getTitre());
                livreStmt.setString(2, livreEnCoursDeLecture.getAuteur());
                livreStmt.setInt(3, livreEnCoursDeLecture.getAnneePublication());
                livreStmt.setString(4, livreEnCoursDeLecture.getEditeur());
                livreStmt.executeUpdate();

                // On récupère l'ID du Livre
                ResultSet livreResult = livreStmt.getGeneratedKeys();
                if (livreResult.next()) {
                    int livreId = livreResult.getInt(1);
                    // On insère l'ID du livre dans la colonne livreEnCourDeLecture pour ce lecteur
                    pstmt.setInt(3, livreId);
                }
            } else {
                // Si le lecteur n'a pas de livre
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur SQl lors de l'insertion d'un lecteur : " + e.getMessage());
            return false;
        }
    }

    // Methode pour insérer un Livre dans la bdd
    public boolean insertLivre(Livre livre) {

        try {
            String query = "INSERT INTO Livres (titre, auteur, annee_publication, editeur) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setInt(3, livre.getAnneePublication());
            pstmt.setString(4, livre.getEditeur());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur SQl lors de l'insertion d'un livre : " + e.getMessage());
            return false;
        }
    }

    // Methode pour faire un Select * d'une table passer en paramètre
    public ResultSet SelectAll(String table) {
        String query = "SELECT * FROM " + table;

        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Erreur SQl lors du Select * d'une table : " + e.getMessage());
            return null;
        }
    }

    // Methode pour récupérer tous les Lecteurs qui sont dans la bdd, elle gère les Lecteurs avec et sans Livre
    public List<Lecteur> GetLecteursBD() {

        List<Lecteur> lecteurs = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(
                    "SELECT l.id, l.nom, l.prenom, li.titre, li.auteur, li.annee_publication, li.editeur " +
                            "FROM lecteurs l " +
                            "LEFT OUTER JOIN livres li ON l.livre_en_cours_de_lecture = li.id"
            );
            rs = stmt.executeQuery();

            while (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String titreLivre = rs.getString("titre");
                String auteurLivre = rs.getString("auteur");
                int anneePublication = rs.getInt("annee_publication");
                String editeurLivre = rs.getString("editeur");

                Livre livre = null;
                Lecteur lecteur = null;

                if (titreLivre != null) {
                    livre = new Livre(titreLivre, auteurLivre, anneePublication, editeurLivre);
                    lecteur = new Lecteur(nom, prenom, livre);
                } else {
                    lecteur = new Lecteur(nom, prenom);
                }

                lecteurs.add(lecteur);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recuperation des lecteurs : " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture des ressources GetLecteurBD : " + e.getMessage());
            }
        }

        return lecteurs;
    }

    // Methode pour récupérer tous les Livres qui sont dans la bdd.
    public List<Livre> GetLivresBD() {
        List<Livre> livres = new ArrayList<>();
        try (ResultSet rs = SelectAll("livres")) {
            while (rs.next()) {
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                int anneePublication = rs.getInt("annee_publication");
                String editeur = rs.getString("editeur");
                Livre livre = new Livre(titre, auteur, anneePublication, editeur);
                livres.add(livre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recuperation des livres : " + e.getMessage());
        }
        return livres;
    }

    // Methode pour se deconnecter de la base de donnees
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la fermeture de la connection a la base de donnee : " + e.getMessage());
        }
    }
}

