package org.owasp.securityshepherd.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.owasp.securityshepherd.dao.UserDao.UserDAO;
import org.owasp.securityshepherd.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void addUser(UserEntity user) {
		// TODO Auto-generated method stub
		jdbcTemplate.update(
				"INSERT INTO core.users (userId, classId, userName, userPass, userRole, ssoName, userAddress, loginType, tempPassword, tempUsername ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ",
				user.getUserId(), user.getClassId(), user.getUserName(), user.getUserPass(), user.getUserRole(),
				user.getSsoName(), user.getUserAddress(), user.getLoginType(), user.isTempPassword(),
				user.isTempUsername());

	}

	public UserEntity getUserById(String userID) {
		// TODO Auto-generated method stub
		return null;
	}

	class UserRowMapper implements RowMapper<UserEntity> {
		@Override
		public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserEntity user = new UserEntity(rs.getString("userId"), rs.getString("classId"), rs.getString("userName"),
					rs.getString("userPass"), rs.getString("userRole"), rs.getString("ssoName"),
					rs.getInt("badLoginCount"), rs.getTimestamp("suspendedUntil"), rs.getString("userAddress"),
					rs.getString("loginType"), rs.getBoolean("tempPassword"), rs.getBoolean("tempUsername"),
					rs.getInt("userScore"), rs.getInt("goldMedalCount"), rs.getInt("silverMedalCount"),
					rs.getInt("bronzeMedalCount"), rs.getInt("badSubmissionCount"));

			return user;
		}

	}

	public List<UserEntity> listUsers() {
		return jdbcTemplate.query("select * from core.users", new UserRowMapper());
	}

	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	public void update(Integer id, Integer age) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<UserEntity> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String userID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeUserName(String userID, String userName) {
		// TODO Auto-generated method stub

	}

}