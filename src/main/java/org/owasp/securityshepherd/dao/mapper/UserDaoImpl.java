package org.owasp.securityshepherd.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.owasp.securityshepherd.dao.UserDao.UserDAO;
import org.owasp.securityshepherd.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void addUser(User user) {
		// TODO Auto-generated method stub
		jdbcTemplate.update(
				"INSERT INTO core.users (userId, classId, userName, userPass, userRole, ssoName, badLoginCount, suspendedUntil, userAddress, loginType, tempPassword, tempUsername, userScore, goldMedalCount, silverMedalCount, bronzeMedalCount, badSubmissionCount ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ",
				user.getId(), user.getClassId(), user.getName(), user.getPassword(), user.getRole(), user.getSsoId(),
				user.getBadLoginCount(), user.getSuspendedUntil(), user.getEmail(), user.getLoginType(),
				user.isTemporaryPassword(), user.isTemporaryUsername(), user.getScore(), user.getGoldMedalCount(),
				user.getSilverMedalCount(), user.getBronzeMedalCount(), user.getBadSubmissionCount());

	}

	public User getUserById(String userID) {
		// TODO Auto-generated method stub
		return null;
	}

	class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return User.builder().id(rs.getString("userId")).classId(rs.getString("classId"))
					.name(rs.getString("userName")).password(rs.getString("userPass")).role(rs.getString("userRole"))
					.ssoId(rs.getString("ssoName")).badLoginCount(rs.getInt("badLoginCount"))
					.suspendedUntil(rs.getTimestamp("suspendedUntil")).email(rs.getString("userAddress"))
					.loginType(rs.getString("loginType")).temporaryPassword(rs.getBoolean("tempPassword"))
					.temporaryPassword(rs.getBoolean("tempUsername")).score(rs.getInt("userScore"))
					.goldMedalCount(rs.getInt("goldMedalCount")).silverMedalCount(rs.getInt("silverMedalCount"))
					.bronzeMedalCount(rs.getInt("bronzeMedalCount")).badSubmissionCount(rs.getInt("badSubmissionCount"))
					.build();

		}

	}

	public List<User> listUsers() {
		return jdbcTemplate.query("select * from core.users", new UserRowMapper());
	}

	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	public void update(Integer id, Integer age) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<User> getAllUsers() {
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