package org.owasp.securityshepherd.dao.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.owasp.securityshepherd.dao.Dao;
import org.owasp.securityshepherd.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao implements Dao<User> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return User.builder().id(rs.getString("id")).classId(rs.getString("classId")).name(rs.getString("name"))
					.password(rs.getString("password")).role(rs.getString("role"))
					.suspendedUntil(rs.getTimestamp("suspendedUntil")).email(rs.getString("email"))
					.loginType(rs.getString("loginType")).temporaryPassword(rs.getBoolean("tempPassword"))
					.temporaryUsername(rs.getBoolean("tempUsername")).score(rs.getInt("score"))
					.goldMedals(rs.getInt("goldMedals")).silverMedals(rs.getInt("silverMedals"))
					.bronzeMedals(rs.getInt("bronzeMedals")).badSubmissionCount(rs.getInt("badSubmissionCount"))
					.badLoginCount(rs.getInt("badLoginCount")).build();

		}

	}

	@Override
	public boolean containsId(String id) {
		User.validateId(id);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject("SELECT count(id) FROM core.users WHERE id = :id",
				namedParameters, Integer.class) > 0;
	}

	@Override
	public boolean containsName(String name) {
		User.validateName(name);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name);

		return namedParameterJdbcTemplate.queryForObject("SELECT count(name) FROM core.users WHERE name = :name",
				namedParameters, Integer.class) > 0;
	}

	@Override
	public int count() {
		SqlParameterSource namedParameters = new MapSqlParameterSource();

		return namedParameterJdbcTemplate.queryForObject("SELECT count(id) FROM core.users", namedParameters,
				Integer.class);
	}

	public void create(User user) {
		jdbcTemplate.update(
				"INSERT INTO core.users (id, classId, name, password, role, suspendedUntil, email, loginType, tempPassword, tempUsername, score, goldMedals, silverMedals, bronzeMedals, badSubmissionCount, badLoginCount) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ",
				user.getId(), user.getClassId(), user.getName(), user.getPassword(), user.getRole(),
				user.getSuspendedUntil(), user.getEmail(), user.getLoginType(), user.isTemporaryPassword(),
				user.isTemporaryUsername(), user.getScore(), user.getGoldMedals(), user.getSilverMedals(),
				user.getBronzeMedals(), user.getBadSubmissionCount(), user.getBadLoginCount());

	}

	@Override
	public void deleteAll() {

		namedParameterJdbcTemplate.update("DELETE FROM core.users ", new MapSqlParameterSource());
	}

	@Override
	public void deleteById(String id) {
		User.validateId(id);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

		namedParameterJdbcTemplate.update("DELETE FROM core.users WHERE id = :id", namedParameters);

	}

	@Override
	public void deleteByName(String name) {
		User.validateName(name);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name);

		namedParameterJdbcTemplate.update("DELETE FROM core.users WHERE name = :name", namedParameters);
	}

	public User getById(String id) {

		User.validateId(id);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject("SELECT * FROM core.users WHERE id = :id", namedParameters,
				new UserRowMapper());

	}

	@Override
	public User getByName(String name) {
		User.validateName(name);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name);

		return namedParameterJdbcTemplate.queryForObject("SELECT * FROM core.users WHERE name = :name", namedParameters,
				new UserRowMapper());
	}

	@Override
	public List<User> getAll() {
		return jdbcTemplate.query("select * from core.users", new UserRowMapper());
	}

	@Override
	public void renameByName(String name, String newName) {
		User.validateName(name);

		User.validateName(newName);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name).addValue("newName",
				newName);

		String renameQuery = "UPDATE core.users SET name=:newName WHERE name = :name";

		int rowsAffected = namedParameterJdbcTemplate.update(renameQuery, namedParameters);

		if (rowsAffected != 1) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(renameQuery, 1, rowsAffected);
		}

	}

	@Override
	public void renameById(String id, String newName) {
		User.validateId(id);

		User.validateName(newName);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id).addValue("name", newName);

		String renameQuery = "UPDATE core.users SET name=:name WHERE id = :id";

		int rowsAffected = namedParameterJdbcTemplate.update(renameQuery, namedParameters);

		if (rowsAffected != 1) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(renameQuery, 1, rowsAffected);
		}

	}

}