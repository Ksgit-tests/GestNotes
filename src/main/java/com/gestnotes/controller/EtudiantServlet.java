package com.gestnotes.controller;

import com.gestnotes.dao.DaoException;
import com.gestnotes.dao.EtudiantDao;
import com.gestnotes.dao.EtudiantDaoImpl;
import com.gestnotes.model.Etudiant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "EtudiantServlet", urlPatterns = {"/etudiants"})
public class EtudiantServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VUE_LISTE = "/WEB-INF/views/liste.jsp";
    private static final String VUE_FORMULAIRE = "/WEB-INF/views/formulaire.jsp";

    private final EtudiantDao dao = new EtudiantDaoImpl(); // sans état : partageable entre threads

    // ---------- GET : affichages ----------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("nouveau".equals(action)) {
            afficherFormulaire(request, response, new Etudiant());
        } else if ("modifier".equals(action)) {
            Long id = lireId(request.getParameter("id"));
            Etudiant etudiant = (id == null) ? null : dao.trouver(id);
            if (etudiant == null) {          // id absent, invalide ou inconnu
                redirigerVersListe(request, response);
                return;
            }
            afficherFormulaire(request, response, etudiant);
        } else {
            lister(request, response);
        }
    }

    // ---------- POST : actions qui modifient les données ----------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // AVANT le premier getParameter
        if ("enregistrer".equals(request.getParameter("action"))) {
            enregistrer(request, response);
        } else {
            redirigerVersListe(request, response);
        }
    }

    private void lister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = request.getParameter("q");
        List<Etudiant> etudiants;
        try {
            etudiants = (q == null || q.trim().isEmpty()) ? dao.lister() : dao.rechercher(q.trim());
        } catch (DaoException e) {
            log("Erreur d'accès aux données", e);
            etudiants = new ArrayList<>();
            request.setAttribute("erreurBase", "La base de données est indisponible.");
        }
        request.setAttribute("etudiants", etudiants);
        request.setAttribute("q", q);
        request.getRequestDispatcher(VUE_LISTE).forward(request, response);
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response, Etudiant etudiant)
            throws ServletException, IOException {
        request.setAttribute("etudiant", etudiant);
        request.getRequestDispatcher(VUE_FORMULAIRE).forward(request, response);
    }

    private void enregistrer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Etudiant etudiant = new Etudiant();
        Long id = lireId(request.getParameter("id"));
        if (id != null) {
            etudiant.setId(id);
        }
        etudiant.setNom(request.getParameter("nom"));
        etudiant.setFiliere(request.getParameter("filiere"));

        // 1. Conversion (rôle du contrôleur)
        List<String> erreursConversion = new ArrayList<>();
        etudiant.setNoteMath(lireNote(request.getParameter("noteMath"), "Mathématiques", erreursConversion));
        etudiant.setNoteInfo(lireNote(request.getParameter("noteInfo"), "Informatique", erreursConversion));
        etudiant.setNoteAnglais(lireNote(request.getParameter("noteAnglais"), "Anglais", erreursConversion));

        // 2. Règles métier (rôle du modèle)
        List<String> erreurs = new ArrayList<>(etudiant.validerIdentite());
        erreurs.addAll(erreursConversion);
        if (erreursConversion.isEmpty()) {
            erreurs.addAll(etudiant.validerNotes());
        }

        if (!erreurs.isEmpty()) {
            request.setAttribute("erreurs", erreurs);
            afficherFormulaire(request, response, etudiant); // forward : on garde la saisie
            return;
        }

        if (id == null) {
            dao.ajouter(etudiant);
        } else {
            dao.modifier(etudiant);
        }
        redirigerVersListe(request, response); // PRG
    }

    /** Texte -> note. Virgule acceptée. En cas d'échec : message + Double.NaN. */
    static double lireNote(String saisie, String module, List<String> erreurs) {
        if (saisie == null || saisie.trim().isEmpty()) {
            erreurs.add("La note de " + module + " est obligatoire.");
            return Double.NaN;
        }
        try {
            return Double.parseDouble(saisie.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            erreurs.add("La note de " + module + " n'est pas un nombre.");
            return Double.NaN;
        }
    }

    /** Texte -> id, ou null si absent / invalide. */
    static Long lireId(String saisie) {
        if (saisie == null || saisie.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(saisie.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void redirigerVersListe(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/etudiants");
    }
}