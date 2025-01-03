package com.esi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.esi.User.User;

import com.esi.util.DbConnection;

public class loginDAOImpl {
    private final Connection connection;

    public loginDAOImpl() throws SQLException {
        this.connection = DbConnection.getInstance().getConnection();
    }
	
	public boolean ValidateLogin(User user) throws SQLException{
		String query = "SELECT * FROM users WHERE username = ? AND password = ?";
		
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, user.getUsername());
		preparedStatement.setString(2, user.getPassword()); 
		try (ResultSet resultSet = preparedStatement.executeQuery()) {

			return resultSet.next(); 
        }catch (SQLException e) {
            e.printStackTrace();
            throw e; 
        }
		
		 };
	
}
