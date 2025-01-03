package com.esi.dao;

import com.esi.Emprunt.Emprunt;
import com.esi.Etudiant.Etudiant;
import com.esi.livre.Livre;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DAOMain {

    public static void main(String[] args) throws SQLException {
        // Create instances of the DAOs (replace with your actual implementations)
        EmpruntDAO empruntDAO = new EmpruntDAOImpl(); // Replace with your actual implementation
        EtudiantDAO etudiantDAO = new EtudiantDAOImpl(); // Replace with your actual implementation
        LivreDAO livreDAO = new LivreDAOImpl(); // Replace with your actual implementation



        // Test fetching all emprunts
        List<Emprunt> allEmprunts = empruntDAO.getTousLesEmprunts();
        System.out.println("All emprunts: " + allEmprunts);

        // Test fetching all Etudiants
        List<Etudiant> allEtudiants = etudiantDAO.getTousLesEtudiants();
        System.out.println("All etudiants: " + allEtudiants);


        // Test fetching all Livres
        List<Livre> allLivres = livreDAO.getTousLesLivres();
        System.out.println("All livres: " + allLivres);


        
    }
}