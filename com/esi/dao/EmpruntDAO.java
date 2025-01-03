package com.esi.dao;

import java.sql.SQLException;
import com.esi.Emprunt.Emprunt;
import java.util.List;

public interface EmpruntDAO {

    void enregistrerEmprunt(Emprunt emprunt) throws SQLException; // Accepting Emprunt object, not Etudiant
    Emprunt getEmpruntByEtudiantId(String string) throws SQLException; // Correct method name (Etudiant)
    Emprunt getEmpruntByLivreId(int id) throws SQLException; // Correct method name (Livre)
    List<Emprunt> getTousLesEmprunts() throws SQLException; // Returns all emprunts
    void deleteEmprunt(int id) throws SQLException; // Correct method name (delete Emprunt, not Etudiant)
    List<Emprunt> rechercherEmprunt(String critere) throws SQLException; // Search emprunts based on the criteria
	Emprunt getEmpruntById(int id) throws SQLException;
    void markAsReturned(int id) throws SQLException; // Mark the Emprunt with the given ID as returned
	void modifierEmprunt(Emprunt emprunt) throws SQLException;
	List <Emprunt> getEmpruntEnCours() throws SQLException;

}
