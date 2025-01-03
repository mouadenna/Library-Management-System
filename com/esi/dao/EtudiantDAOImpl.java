package com.esi.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esi.Etudiant.Etudiant;
import com.esi.util.DbConnection;

public class EtudiantDAOImpl implements EtudiantDAO {
    private final Connection connection;

    public EtudiantDAOImpl() throws SQLException {
        this.connection = DbConnection.getInstance().getConnection();
    }

    @Override
    public Etudiant getEtudiantById(String id) throws SQLException {
        String query = "SELECT * FROM etudiants WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEtudiantFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public void enregistrerEtudiant(Etudiant etudiant) throws SQLException {
        String query = "INSERT INTO etudiants (id, nom, prenom, email, age, filiere,numero) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        	stmt.setString(1, etudiant.getId());
            stmt.setString(2, etudiant.getNom());
            stmt.setString(3, etudiant.getPrenom());
            stmt.setString(4, etudiant.getEmail());
            stmt.setInt(5, etudiant.getAge());
            stmt.setString(6, etudiant.getFiliere());
            stmt.setString(7, etudiant.getNumero());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création d'etudiant a échoué");
            }
        }
    }

    @Override
    public List<Etudiant> getTousLesEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM etudiants";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                etudiants.add(extractEtudiantFromResultSet(rs));
            }
        }
        return etudiants;
    }

	@Override
	public void updateEtudiant(Etudiant etudiant) throws SQLException {
	    // Correct the SQL query by removing the extra comma and properly specifying the WHERE clause
	    String query = "UPDATE etudiants SET nom = ?, prenom = ?, email = ?, age = ?, filiere = ?, numero = ? WHERE id = ?";

	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        // Set the parameters for the query
	        stmt.setString(1, etudiant.getNom());
	        stmt.setString(2, etudiant.getPrenom());
	        stmt.setString(3, etudiant.getEmail());
	        stmt.setInt(4, etudiant.getAge());
	        stmt.setString(5, etudiant.getFiliere());
	        stmt.setString(6, etudiant.getNumero());
	        stmt.setString(7, etudiant.getId()); // Set the 7th parameter for the ID

	        // Execute the update
	        stmt.executeUpdate();
	    }
	}


    @Override
    public void deleteEtudiant(String id) throws SQLException {
        String query = "DELETE FROM etudiants WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Etudiant> rechercherEtudiant(String critere) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM etudiants WHERE nom LIKE ? OR prenom LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + critere + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                etudiants.add(extractEtudiantFromResultSet(rs));
            }
        }
        return etudiants;
    }

	
    private Etudiant extractEtudiantFromResultSet(ResultSet rs) throws SQLException {
        return new Etudiant(
            rs.getString("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("email"),
            rs.getInt("age"),
            rs.getString("filiere"),
            rs.getString("numero")
        );
    }



}
