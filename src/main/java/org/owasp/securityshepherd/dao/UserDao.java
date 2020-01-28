package org.owasp.securityshepherd.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao implements Dao<User> {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	class UserRowMapper implements RowMapper<User> {
		
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserBuilder rowMapBuilder = User.builder();

			rowMapBuilder.id(rs.getString("id"));
			rowMapBuilder.name(rs.getString("name"));
			rowMapBuilder.classId(rs.getString("classId"));
			rowMapBuilder.password(rs.getString("password"));
			rowMapBuilder.role(rs.getString("role"));
			rowMapBuilder.suspendedUntil(rs.getTimestamp("suspendedUntil"));
			rowMapBuilder.email(rs.getString("email"));
			rowMapBuilder.loginType(rs.getString("loginType"));
			rowMapBuilder.temporaryPassword(rs.getBoolean("tempPassword"));
			rowMapBuilder.temporaryUsername(rs.getBoolean("tempUsername"));
			rowMapBuilder.score(rs.getInt("score"));
			rowMapBuilder.goldMedals(rs.getInt("goldMedals"));
			rowMapBuilder.silverMedals(rs.getInt("silverMedals"));
			rowMapBuilder.bronzeMedals(rs.getInt("bronzeMedals"));
			rowMapBuilder.badSubmissionCount(rs.getInt("badSubmissionCount"));
			rowMapBuilder.badLoginCount(rs.getInt("badLoginCount"));

			return rowMapBuilder.build();

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

		String createQuery = "INSERT INTO core.users (id, name, classId, password, role, suspendedUntil, email, loginType, tempPassword, tempUsername, score, goldMedals, silverMedals, bronzeMedals, badSubmissionCount, badLoginCount) VALUES (:id, :name, :classId, :password, :role, :suspendedUntil, :email, :loginType, :temporaryPassword, :temporaryUsername, :score, :goldMedals, :silverMedals, :bronzeMedals, :badSubmissionCount, :badLoginCount)";

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("id", user.getId());
		namedParameters.addValue("name", user.getName());
		namedParameters.addValue("classId", user.getClassId());
		namedParameters.addValue("password", user.getPassword());
		namedParameters.addValue("role", user.getRole());
		namedParameters.addValue("suspendedUntil", user.getSuspendedUntil());
		namedParameters.addValue("email", user.getEmail());
		namedParameters.addValue("loginType", user.getLoginType());
		namedParameters.addValue("temporaryPassword", user.isTemporaryPassword());
		namedParameters.addValue("temporaryUsername", user.isTemporaryUsername());
		namedParameters.addValue("score", user.getScore());
		namedParameters.addValue("goldMedals", user.getGoldMedals());
		namedParameters.addValue("silverMedals", user.getSilverMedals());
		namedParameters.addValue("bronzeMedals", user.getBronzeMedals());
		namedParameters.addValue("badSubmissionCount", user.getBadSubmissionCount());
		namedParameters.addValue("badLoginCount", user.getBadLoginCount());

		namedParameterJdbcTemplate.update(createQuery, namedParameters);

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
	
		return namedParameterJdbcTemplate.query("SELECT * FROM core.users", new MapSqlParameterSource(),
				new UserRowMapper());
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