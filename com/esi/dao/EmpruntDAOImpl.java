package com.esi.dao;

import com.esi.Emprunt.Emprunt;
import com.esi.livre.Livre;
import com.esi.Etudiant.Etudiant;
import com.esi.util.DbConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAOImpl implements EmpruntDAO {
    private final Connection connection;
    private final LivreDAOImpl livreDao;
    private final EtudiantDAOImpl etudiantDao;

    public EmpruntDAOImpl() throws SQLException {
        this.connection = DbConnection.getInstance().getConnection();
        this.livreDao = new LivreDAOImpl();
        this.etudiantDao = new EtudiantDAOImpl();
    }
    
    @Override
    public void enregistrerEmprunt(Emprunt emprunt) throws SQLException {
        String query = "INSERT INTO emprunts (etudiant_id, livre_id, date_emprunt, date_retour_prevue, date_retour_effective, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, emprunt.getEtudiant().getId());
            stmt.setInt(2, emprunt.getLivre().getId());
            stmt.setDate(3, java.sql.Date.valueOf(emprunt.getDateEmprunt()));
            stmt.setDate(4, java.sql.Date.valueOf(emprunt.getDateRetourPrevue()));
            stmt.setDate(5, emprunt.getDateRetourEffective() != null ? emprunt.getDateRetourEffective(): null);
            stmt.setString(6, emprunt.getStatut().getDisplayName());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création de l'emprunt a échoué");
            }
        }
    }

    @Override
    public Emprunt getEmpruntById(int id) throws SQLException {
        String query = "SELECT * FROM emprunts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEmpruntFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Emprunt> getTousLesEmprunts() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String query = "SELECT * FROM emprunts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
        }
        return emprunts;
    }
    public List<Emprunt> getEmpruntEnCours() throws SQLException{
    	
    	List<Emprunt> emprunts = new ArrayList<>();
    	String query = "SELECT * FROM emprunts WHERE Status='en cours'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
        }
        return emprunts;
    	
    	
    }

    @Override
    public void deleteEmprunt(int id) throws SQLException {
        String query = "DELETE FROM emprunts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Emprunt> rechercherEmprunt(String critere) throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String query = "SELECT * FROM emprunts WHERE livre_id LIKE ? OR etudiant_id LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + critere + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
        }
        return emprunts;
    }

    private Emprunt extractEmpruntFromResultSet(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(rs.getInt("id"));
        
        Etudiant etudiant = etudiantDao.getEtudiantById(rs.getString("etudiant_id"));
        emprunt.setEtudiant(etudiant);

        Livre livre = livreDao.getLivreById(rs.getInt("livre_id"));
        emprunt.setLivre(livre);

        emprunt.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        emprunt.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());
        
        Date dateRetourEffective = rs.getDate("date_retour_effective");
        if (dateRetourEffective != null) {
            emprunt.setDateRetourEffective(dateRetourEffective);
        }
        
        String status = rs.getString("status");
        emprunt.setStatut(Emprunt.Status.fromDisplayName(status));

        return emprunt;
    }

    @Override
    public Emprunt getEmpruntByEtudiantId(String id) throws SQLException {
        String sql = "SELECT * FROM emprunts WHERE etudiant_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEmpruntFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public Emprunt getEmpruntByLivreId(int id) throws SQLException {
        String sql = "SELECT * FROM emprunts WHERE livre_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEmpruntFromResultSet(rs);
            }
        }
        return null;
    }
    
    @Override
    public void markAsReturned(int id) throws SQLException {
        String query = "UPDATE emprunts SET status = ?, date_retour_effective = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Emprunt.Status.RETOURNE.getDisplayName());
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            statement.setInt(3, id);
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("No Emprunt found with the given ID.");
            }
        }
    }

    @Override
    public void modifierEmprunt(Emprunt emprunt) throws SQLException {
        String query = "UPDATE emprunts SET livre_id = ?, etudiant_id = ?, date_emprunt = ?, date_retour_prevue = ?, date_retour_effective = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, emprunt.getLivre().getId());
            stmt.setString(2, emprunt.getEtudiant().getId());
            stmt.setDate(3, java.sql.Date.valueOf(emprunt.getDateEmprunt()));
            stmt.setDate(4, java.sql.Date.valueOf(emprunt.getDateRetourPrevue()));
            
            if (emprunt.getDateRetourEffective() != null) {
                stmt.setDate(5, emprunt.getDateRetourEffective());

            } else {
                stmt.setNull(5, Types.DATE);
            }
            System.out.println(emprunt.getDateRetourEffective());

            
            stmt.setString(6, emprunt.getStatut().getDisplayName());
            stmt.setInt(7, emprunt.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("No Emprunt found with the given ID.");
            }
        }
    }
}