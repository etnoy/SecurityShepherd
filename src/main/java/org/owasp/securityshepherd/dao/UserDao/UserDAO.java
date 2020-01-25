package org.owasp.securityshepherd.dao.UserDao;

import java.util.List;

import org.owasp.securityshepherd.model.UserEntity;

public interface UserDAO {

	public void addUser(UserEntity user);

	public UserEntity getUserById(String userID);

	public List<UserEntity> getAllUsers();

	public void delete(String userID);

	public void changeUserName(String userID, String userName);

}