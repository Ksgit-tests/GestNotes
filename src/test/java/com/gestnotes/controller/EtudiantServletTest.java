package com.gestnotes.controller;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Teste la conversion des saisies, sans serveur. */
class EtudiantServletTest {

    @Test
    void lireNote_accepte_la_virgule_et_le_point() {
        List<String> erreurs = new ArrayList<>();
        assertEquals(12.5, EtudiantServlet.lireNote("12,5", "Maths", erreurs), 1e-9);
        assertEquals(12.5, EtudiantServlet.lireNote(" 12.5 ", "Maths", erreurs), 1e-9);
        assertTrue(erreurs.isEmpty());
    }

    @Test
    void lireNote_vide_ou_invalide_renvoie_NaN_avec_message() {
        List<String> erreurs = new ArrayList<>();
        assertTrue(Double.isNaN(EtudiantServlet.lireNote("", "Maths", erreurs)));
        assertTrue(Double.isNaN(EtudiantServlet.lireNote(null, "Maths", erreurs)));
        assertTrue(Double.isNaN(EtudiantServlet.lireNote("abc", "Maths", erreurs)));
        assertEquals(3, erreurs.size());
        assertTrue(erreurs.get(2).contains("n'est pas un nombre"));
    }

    @Test
    void lireId_renvoie_null_si_absent_ou_invalide() {
        assertNull(EtudiantServlet.lireId(null));
        assertNull(EtudiantServlet.lireId(""));
        assertNull(EtudiantServlet.lireId("abc"));
        assertEquals(3L, EtudiantServlet.lireId("3"));
    }
}