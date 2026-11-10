package com.gestnotes.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EtudiantTest {

    @Test
    void moyenne_ponderee() {
        Etudiant e = new Etudiant("Test", "Info", 12, 16, 14); // (12*3 + 16*3 + 14*2) / 8 = 14
        assertEquals(14.0, e.getMoyenne(), 1e-9);
    }

    @Test
    void mentions() {
        assertEquals("Très bien", new Etudiant("a", "f", 16, 16, 16).getMention());
        assertEquals("Bien", new Etudiant("a", "f", 14, 14, 14).getMention());
        assertEquals("Assez bien", new Etudiant("a", "f", 12, 12, 12).getMention());
        assertEquals("Passable", new Etudiant("a", "f", 10, 10, 10).getMention());
        assertEquals("Ajourné", new Etudiant("a", "f", 9, 9, 9).getMention());
    }

    @Test
    void admis_a_partir_de_10() {
        assertTrue(new Etudiant("a", "f", 10, 10, 10).isAdmis());
        assertFalse(new Etudiant("a", "f", 9.99, 9.99, 9.99).isAdmis());
    }

    @Test
    void validation_detecte_les_erreurs() {
        Etudiant invalide = new Etudiant(" ", "", 21, -1, Double.NaN);
        assertEquals(5, invalide.valider().size());
        assertTrue(new Etudiant("Sara", "Info", 12, 13, 14).valider().isEmpty());
    }
    
    @Test
    void moyenne_arrondie_half_up_comme_le_cahier_des_charges() {
        assertEquals(16.63, new Etudiant("a", "f", 18, 15, 17).getMoyenneArrondie(), 1e-9); // 16,625
        assertEquals(9.63, new Etudiant("a", "f", 10, 9, 10).getMoyenneArrondie(), 1e-9);   // 9,625
        assertEquals(9.13, new Etudiant("a", "f", 9, 8, 11).getMoyenneArrondie(), 1e-9);    // 9,125
        assertEquals(13.56, new Etudiant("a", "f", 12, 13.5, 16).getMoyenneArrondie(), 1e-9); // 13,5625 : pas un cas « pile 5 »
    }
    
    @Test
    void validation_separee_identite_et_notes() {
        Etudiant e = new Etudiant("", " ", 25, 10, 10);
        assertEquals(2, e.validerIdentite().size());
        assertEquals(1, e.validerNotes().size());
        assertEquals(3, e.valider().size());
    }

    /* Réactivés à l'étape 7 (classe Statistiques) :
    @Test void statistiques() { ... }
    @Test void statistiques_liste_vide() { ... }
    */
}