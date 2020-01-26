package org.owasp.securityshepherd.dao.UserDao;

import java.util.List;

import org.owasp.securityshepherd.model.User;

public interface UserDAO {

	public void addUser(User user);

	public User getUserById(String userID);

	public List<User> getAllUsers();

	public void delete(String userID);

	public void changeUserName(String userID, String userName);

}