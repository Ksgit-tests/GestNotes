package com.gestnotes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
// import pour faciliter gerer les arrondi des données du types double
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Etudiant implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final double COEF_MATH = 3;
    public static final double COEF_INFO = 3;
    public static final double COEF_ANGLAIS = 2;

    private long id;
    private String nom;
    private String filiere;
    private double noteMath;
    private double noteInfo;
    private double noteAnglais;

    public Etudiant() { }

    public Etudiant(String nom, String filiere, double noteMath, double noteInfo, double noteAnglais) {
        this.nom = nom;
        this.filiere = filiere;
        this.noteMath = noteMath;
        this.noteInfo = noteInfo;
        this.noteAnglais = noteAnglais;
    }

    // --- getters / setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public double getNoteMath() { return noteMath; }
    public void setNoteMath(double noteMath) { this.noteMath = noteMath; }
    public double getNoteInfo() { return noteInfo; }
    public void setNoteInfo(double noteInfo) { this.noteInfo = noteInfo; }
    public double getNoteAnglais() { return noteAnglais; }
    public void setNoteAnglais(double noteAnglais) { this.noteAnglais = noteAnglais; }

    // --- logique métier ---
    public double getMoyenne() {
        double somme = noteMath * COEF_MATH + noteInfo * COEF_INFO + noteAnglais * COEF_ANGLAIS;
        return somme / (COEF_MATH + COEF_INFO + COEF_ANGLAIS);
    }

    public String getMention() {
        double m = getMoyenne();
        if (m >= 16) return "Très bien";
        if (m >= 14) return "Bien";
        if (m >= 12) return "Assez bien";
        if (m >= 10) return "Passable";
        return "Ajourné";
    }

    public boolean isAdmis() {
        return getMoyenne() >= 10;
    }

    public List<String> valider() {
        List<String> erreurs = new ArrayList<>();
        if (nom == null || nom.trim().isEmpty()) {
            erreurs.add("Le nom est obligatoire.");
        } else if (nom.trim().length() > 100) {
            erreurs.add("Le nom ne doit pas dépasser 100 caractères.");
        }
        if (filiere == null || filiere.trim().isEmpty()) {
            erreurs.add("La filière est obligatoire.");
        }
        verifierNote(noteMath, "Mathématiques", erreurs);
        verifierNote(noteInfo, "Informatique", erreurs);
        verifierNote(noteAnglais, "Anglais", erreurs);
        return erreurs;
    }
    
    /** Moyenne arrondie à 2 décimales, arrondi scolaire (16,625 -> 16,63). Pour l'affichage. */
    public double getMoyenneArrondie() {
        return BigDecimal.valueOf(getMoyenne()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static void verifierNote(double note, String module, List<String> erreurs) {
        if (Double.isNaN(note) || note < 0 || note > 20) {
            erreurs.add("La note de " + module + " doit être comprise entre 0 et 20.");
        }
    }
}