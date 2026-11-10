package com.gestnotes.dao;

import com.gestnotes.model.Etudiant;
import java.util.List;

public interface EtudiantDao {
    List<Etudiant> lister();
    /** Nom ou filière contenant le mot-clé (insensible à la casse). */
    List<Etudiant> rechercher(String motCle);
    /** @return l'étudiant, ou null s'il n'existe pas */
    Etudiant trouver(long id);
    /** Insère et renseigne l'id généré. */
    Etudiant ajouter(Etudiant etudiant);
    boolean modifier(Etudiant etudiant);
    boolean supprimer(long id);
}