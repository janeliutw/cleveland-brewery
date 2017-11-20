package com.techelevator.clebrews.model;

import javax.sql.DataSource;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.clebrews.security.PasswordHasher;



@Component
public class JDBCUserDAO implements UserDAO {

	private JdbcTemplate jdbcTemplate;
	private PasswordHasher passwordHasher;

	@Autowired
	public JDBCUserDAO(DataSource dataSource, PasswordHasher passwordHasher) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.passwordHasher = passwordHasher;
	}
	
	@Override
	public int saveUser(User newUser) {
		byte[] salt = passwordHasher.generateRandomSalt();
		String hashedPassword = passwordHasher.computeHash(newUser.getPassword(), salt);
		String saltString = new String(Base64.encode(salt));
		int userId = jdbcTemplate.queryForObject("INSERT INTO users(username, password, salt, email, role_id) VALUES (?, ?, ?,?, 2) RETURNING user_id", Integer.class, newUser.getUserName(), hashedPassword, saltString, newUser.getEmail());
		//TODO brewery account (role_id = 2)
		return userId;
	}

	@Override
	public boolean searchForUsernameAndPassword(String userName, String password) {
		String sqlSearchForUser = "SELECT * "+
							      "FROM users "+
							      "WHERE UPPER(username) = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchForUser, userName.toUpperCase());
		if(results.next()) {
			String storedSalt = results.getString("salt");
			String storedPassword = results.getString("password");
			String hashedPassword = passwordHasher.computeHash(password, Base64.decode(storedSalt));
			return storedPassword.equals(hashedPassword);
		} else {
			return false;
		}
	}

	@Override
	public boolean searchForUsername(String userName) {
		String sqlSearchForUser = "SELECT * "+
								  "FROM users "+
			      				  "WHERE UPPER(username) = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchForUser, userName.toUpperCase());
		if(results.next()) {
			return true;
		} else {
		return false;
		}
	}
	
	@Override
	public void updatePassword(String userName, String password) {
		byte[] salt = passwordHasher.generateRandomSalt();
		String hashedPassword = passwordHasher.computeHash(password, salt);
		String saltString = new String(Base64.encode(salt));
		jdbcTemplate.update("UPDATE users SET password = ?, salt = ? WHERE username = ?", hashedPassword, saltString, userName);
	}

	@Override
	public User getUserByUsername(String userName) {
		User user = new User();
		String sqlgetUserByUsername = "SELECT * FROM users WHERE UPPER(username) = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlgetUserByUsername, userName.toUpperCase());
		if(results.next()) {
			user.setId(results.getInt("user_id")); 
			user.setUserName(results.getString("username"));
			user.setPassword(results.getString("password"));
			user.setEmail(results.getString("email"));
			user.setRoleId(results.getInt("role_id"));
			user.setActive(results.getBoolean("is_active"));
		}
		return user;
	}

}
