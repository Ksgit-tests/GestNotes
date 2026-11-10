package com.gestnotes.listener;

import com.gestnotes.dao.ConnexionBD;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebListener
public class InitBaseListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(InitBaseListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection cn = ConnexionBD.getConnexion(); Statement st = cn.createStatement()) {
            executerScript(st, "/schema.sql");
            int nombre;
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM etudiant")) {
                rs.next();
                nombre = rs.getInt(1);
            }
            if (nombre == 0) {
                executerScript(st, "/donnees.sql");
                LOG.info("Données de démonstration insérées.");
            }
        } catch (SQLException | IOException e) {
            LOG.log(Level.SEVERE, "Initialisation de la base impossible", e);
        }
    }

    /** Lit un script du classpath et exécute chaque instruction séparée par ';'. */
    private void executerScript(Statement st, String ressource) throws IOException, SQLException {
        try (InputStream in = getClass().getResourceAsStream(ressource)) {
            if (in == null) {
                throw new IOException("Ressource introuvable : " + ressource);
            }
            String contenu = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            for (String instruction : contenu.split(";")) {
                if (!instruction.trim().isEmpty()) {
                    st.execute(instruction);
                }
            }
        }
    }
}