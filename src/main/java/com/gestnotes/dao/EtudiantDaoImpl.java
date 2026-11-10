package com.gestnotes.dao;

import com.gestnotes.model.Etudiant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDaoImpl implements EtudiantDao {

    private static final String COLONNES = "id, nom, filiere, note_math, note_info, note_anglais";

    @Override
    public List<Etudiant> lister() {
        return requete("SELECT " + COLONNES + " FROM etudiant ORDER BY nom", null);
    }

    @Override
    public List<Etudiant> rechercher(String motCle) {
        String motif = "%" + motCle.toLowerCase() + "%";
        return requete("SELECT " + COLONNES + " FROM etudiant "
                + "WHERE LOWER(nom) LIKE ? OR LOWER(filiere) LIKE ? ORDER BY nom", motif);
    }

    private List<Etudiant> requete(String sql, String motif) {
        List<Etudiant> liste = new ArrayList<>();
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (motif != null) {
                ps.setString(1, motif);
                ps.setString(2, motif);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(convertir(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Impossible de lire les étudiants", e);
        }
        return liste;
    }

    @Override
    public Etudiant trouver(long id) {
        String sql = "SELECT " + COLONNES + " FROM etudiant WHERE id = ?";
        try (Connection cn = ConnexionBD.getConnexion(); 
        		PreparedStatement ps = cn.prepareStatement(sql)
        				) 
        {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? convertir(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Impossible de lire l'étudiant " + id, e);
        }
    }

    @Override
    public Etudiant ajouter(Etudiant e) {
        String sql = "INSERT INTO etudiant(nom, filiere, note_math, note_info, note_anglais) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNom().trim());
            ps.setString(2, e.getFiliere().trim());
            ps.setDouble(3, e.getNoteMath());
            ps.setDouble(4, e.getNoteInfo());
            ps.setDouble(5, e.getNoteAnglais());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (cles.next()) {
                    e.setId(cles.getLong(1));
                }
            }
            return e;
        } catch (SQLException ex) {
            throw new DaoException("Impossible d'ajouter l'étudiant", ex);
        }
    }

    @Override
    public boolean modifier(Etudiant e) {
        String sql = "UPDATE etudiant SET nom = ?, filiere = ?, note_math = ?, note_info = ?, note_anglais = ? WHERE id = ?";
        try (Connection cn = ConnexionBD.getConnexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNom().trim());
            ps.setString(2, e.getFiliere().trim());
            ps.setDouble(3, e.getNoteMath());
            ps.setDouble(4, e.getNoteInfo());
            ps.setDouble(5, e.getNoteAnglais());
            ps.setLong(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Impossible de modifier l'étudiant " + e.getId(), ex);
        }
    }

    @Override
    public boolean supprimer(long id) {
        String sql = "DELETE FROM etudiant WHERE id = ?";
        try (Connection cn = ConnexionBD.getConnexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Impossible de supprimer l'étudiant " + id, e);
        }
    }

    /** Une ligne du ResultSet -> un objet Etudiant. */
    private Etudiant convertir(ResultSet rs) throws SQLException {
        Etudiant e = new Etudiant();
        e.setId(rs.getLong("id"));
        e.setNom(rs.getString("nom"));
        e.setFiliere(rs.getString("filiere"));
        e.setNoteMath(rs.getDouble("note_math"));
        e.setNoteInfo(rs.getDouble("note_info"));
        e.setNoteAnglais(rs.getDouble("note_anglais"));
        return e;
    }
}