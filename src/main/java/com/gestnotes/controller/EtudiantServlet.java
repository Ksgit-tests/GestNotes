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

    private static final String VUE_LISTE = "/WEB-INF/views/liste.jsp";

    private final EtudiantDao dao = new EtudiantDaoImpl(); // sans état : partageable entre threads

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
}