package com.esi.dao;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import com.esi.livre.Livre;
import com.esi.util.DbConnection;

public class LivreDAOImpl implements LivreDAO {
    private final Connection connection;

    public LivreDAOImpl() throws SQLException {
        this.connection = DbConnection.getInstance().getConnection();
    }

    @Override
    public void enregistrerLivre(Livre livre) throws SQLException {
        String query = "INSERT INTO livres (titre, auteur, nbExemplaires, editeur, datePub) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setInt(3, livre.getExemplairesDisponibles());
            stmt.setString(4, livre.getEditeur());
            stmt.setDate(5, livre.getDatePublication());


            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du livre a échoué");
            }
        }
    }

    @Override
    public Livre getLivreById(int id) throws SQLException {
        String query = "SELECT * FROM livres WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractLivreFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Livre> getTousLesLivres() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String query = "SELECT * FROM livres";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
        }
        return livres;
    }

    @Override
    public void updateLivre(Livre livre) throws SQLException {
        String query = "UPDATE livres SET titre = ?, auteur = ?, nbExemplaires = ?, editeur = ?, datePub = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setInt(3, livre.getExemplairesDisponibles());
            stmt.setString(4, livre.getEditeur());
            stmt.setDate(5, livre.getDatePublication());
            stmt.setInt(6, livre.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteLivre(int id) throws SQLException {
        String query = "DELETE FROM livres WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Livre> rechercherLivres(String critere) throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String query = "SELECT * FROM livres WHERE titre LIKE ? OR auteur LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + critere + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
        }
        return livres;
    }

    private Livre extractLivreFromResultSet(ResultSet rs) throws SQLException {

    	java.sql.Date datePub = rs.getDate("datePub");



        return new Livre(
                rs.getInt("id"),
                rs.getString("titre"),
                rs.getString("auteur"),
                rs.getInt("nbExemplaires"),
                rs.getString("editeur"),
                datePub // Use the LocalDate
        );
    }

}
