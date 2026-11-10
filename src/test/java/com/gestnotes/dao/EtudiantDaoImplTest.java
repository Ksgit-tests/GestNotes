package com.gestnotes.dao;

import com.gestnotes.model.Etudiant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Test d'intégration : le DAO contre la vraie base H2 en mémoire. */
class EtudiantDaoImplTest {

    private final EtudiantDao dao = new EtudiantDaoImpl();

    @BeforeAll
    static void creerTable() throws Exception {
        try (Connection cn = ConnexionBD.getConnexion(); Statement st = cn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS etudiant ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(100) NOT NULL, "
                    + "filiere VARCHAR(50) NOT NULL, note_math DOUBLE NOT NULL, "
                    + "note_info DOUBLE NOT NULL, note_anglais DOUBLE NOT NULL)");
            st.execute("DELETE FROM etudiant");
        }
    }

    @Test
    void cycle_complet_ajouter_trouver_modifier_rechercher_supprimer() {
        Etudiant e = dao.ajouter(new Etudiant("Zoulikha Test", "Robotique", 12, 14, 16));
        assertTrue(e.getId() > 0);

        Etudiant lu = dao.trouver(e.getId());
        assertNotNull(lu);
        assertEquals("Zoulikha Test", lu.getNom());
        assertEquals(14.0, lu.getNoteInfo(), 1e-9);

        lu.setNoteInfo(18);
        assertTrue(dao.modifier(lu));
        assertEquals(18.0, dao.trouver(e.getId()).getNoteInfo(), 1e-9);

        List<Etudiant> trouves = dao.rechercher("ROBOT");
        assertEquals(1, trouves.size());
        assertTrue(dao.rechercher("introuvable-xyz").isEmpty());

        assertFalse(dao.lister().isEmpty());

        assertTrue(dao.supprimer(e.getId()));
        assertNull(dao.trouver(e.getId()));
        assertFalse(dao.supprimer(e.getId()));
    }
}