package it.polimi.Gallery.Dao;


import it.polimi.Gallery.Beans.User;

import java.sql.*;
import java.util.ArrayList;

public class UserDAO {
	private final Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}



	public User checkUser(String username, String password) throws SQLException {
		String query = "SELECT * FROM db_Gallery_TIW2020.user WHERE username=? AND password=?";
		
		// try-catch with resources
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			
			try (ResultSet result = preparedStatement.executeQuery()) {
				if (result.next()) return initUser(result);
				else return null;
			}
			catch (SQLException e){
				connection.rollback();
				// statement.rollback
				return null;
			}
		}
	}

	private User initUser(ResultSet result) throws SQLException{

		User user = new User();

		user.setUsername(result.getString("username"));
		user.setPassword(result.getString("password"));
		user.setName(result.getString("name"));
		user.setSurname(result.getString("surname"));
		user.setEmail(result.getString("email"));

		return user;
	}

	public ArrayList<User> getUsers() throws SQLException {
		String query = "SELECT * FROM db_Gallery_TIW2020.user";
		ArrayList<User> users = new ArrayList<>();
		
		// try-catch with resources
		try (Statement statement = connection.createStatement()) {
			try (ResultSet resultSet = statement.executeQuery(query)) {
				while (resultSet.next()){

					User user = initUser(resultSet);
					users.add(user);
				}
			}
			catch (SQLException e){
				connection.rollback();
				// statement.rollback
				return null;
			}
		}
		
		return users;
	}

	public User registerUser(String username, String email,  String password) throws SQLException{
		String query = "INSERT INTO db_Gallery_TIW2020.user VALUES (?, ?, ?)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, password);

			try (ResultSet result = preparedStatement.executeQuery()) {
				// che risultato da una INSERT INTO?
				if (result.next()) return initUser(result);
				else return null;
			}
			catch (SQLException e){
				connection.rollback();
				// statement.rollback
				return null;
			}
		}
	}
}
