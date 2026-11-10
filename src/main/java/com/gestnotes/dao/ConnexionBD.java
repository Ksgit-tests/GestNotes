package com.gestnotes.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnexionBD {
    private static final String PILOTE = "org.h2.Driver";
    private static final String URL = "jdbc:h2:mem:gestnotes;DB_CLOSE_DELAY=-1";
    private static final String UTILISATEUR = "sa";
    private static final String MOT_DE_PASSE = "";

    static {
        try {
            Class.forName(PILOTE);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Pilote JDBC introuvable : " + e.getMessage());
        }
    }

    private ConnexionBD() { }

    /** Connexion à fermer par l'appelant (try-with-resources). */
    public static Connection getConnexion() throws SQLException {
        return DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
    }
}