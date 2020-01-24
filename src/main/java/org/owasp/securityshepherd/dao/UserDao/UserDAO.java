package org.owasp.securityshepherd.dao.UserDao;

import java.util.List;

import org.owasp.securityshepherd.model.UserEntity;

public interface UserDAO {

	public void create(UserEntity user);

	public UserEntity getUser(String userID);

	public List<UserEntity> getAllUsers();

	public void delete(String userID);

	public void changeUserName(String userID, String userName);

}