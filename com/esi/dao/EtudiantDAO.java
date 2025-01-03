package com.esi.dao;
import java.sql.SQLException;
import com.esi.Etudiant.Etudiant;
import java.util.List;


public interface EtudiantDAO {

    void enregistrerEtudiant(Etudiant etudiant) throws SQLException;
    Etudiant getEtudiantById(String id) throws SQLException;
    List<Etudiant> getTousLesEtudiants() throws SQLException;
    void updateEtudiant(Etudiant etudiant) throws SQLException;
    void deleteEtudiant(String string) throws SQLException;
    List<Etudiant> rechercherEtudiant(String critere) throws SQLException;
}
