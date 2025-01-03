package com.esi.dao;

import java.sql.SQLException;
import java.util.List;
import com.esi.livre.Livre;

public interface LivreDAO {
    void enregistrerLivre(Livre livre) throws SQLException;
    Livre getLivreById(int id) throws SQLException;
    List<Livre> getTousLesLivres() throws SQLException;
    void updateLivre(Livre livre) throws SQLException;
    void deleteLivre(int id) throws SQLException;
    List<Livre> rechercherLivres(String critere) throws SQLException;
}