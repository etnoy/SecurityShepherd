package org.owasp.securityshepherd.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.owasp.securityshepherd.dao.Dao;
import org.owasp.securityshepherd.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDao implements Dao<Group> {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	class GroupRowMapper implements RowMapper<Group> {
		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			return Group.builder().id(rs.getString("id")).name(rs.getString("name")).build();

		}

	}

	@Override
	public boolean containsId(String id) {
		Group.validateId(id);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject("SELECT count(id) FROM core.groups WHERE id = :id",
				namedParameters, Integer.class) > 0;
	}

	@Override
	public boolean containsName(String name) {
		Group.validateName(name);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name);

		return namedParameterJdbcTemplate.queryForObject("SELECT count(name) FROM core.groups WHERE name = :name",
				namedParameters, Integer.class) > 0;
	}

	@Override
	public int count() {
		return namedParameterJdbcTemplate.queryForObject("SELECT count(id) FROM core.groups",
				new MapSqlParameterSource(), Integer.class);
	}

	public void create(Group group) {

		String createQuery = "INSERT INTO core.groups (id, name) VALUES (:id, :name)";

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("id", group.getId());
		namedParameters.addValue("name", group.getName());

		namedParameterJdbcTemplate.update(createQuery, namedParameters);

	}

	@Override
	public void deleteAll() {

		namedParameterJdbcTemplate.update("DELETE FROM core.groups ", new MapSqlParameterSource());
	}

	@Override
	public void deleteById(String id) {
		Group.validateId(id);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

		namedParameterJdbcTemplate.update("DELETE FROM core.groups WHERE id = :id", namedParameters);

	}

	@Override
	public void deleteByName(String name) {
		Group.validateName(name);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name);

		namedParameterJdbcTemplate.update("DELETE FROM core.groups WHERE name = :name", namedParameters);
	}

	public Group getById(String id) {

		Group.validateId(id);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject("SELECT * FROM core.groups WHERE id = :id", namedParameters,
				new GroupRowMapper());

	}

	@Override
	public Group getByName(String name) {
		Group.validateName(name);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name);

		return namedParameterJdbcTemplate.queryForObject("SELECT * FROM core.groups WHERE name = :name",
				namedParameters, new GroupRowMapper());
	}

	@Override
	public List<Group> getAll() {
		return namedParameterJdbcTemplate.query("SELECT * FROM core.groups", new MapSqlParameterSource(),
				new GroupRowMapper());
	}

	@Override
	public void renameByName(String name, String newName) {
		Group.validateName(name);

		Group.validateName(newName);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", name).addValue("newName",
				newName);

		String renameQuery = "UPDATE core.groups SET name=:newName WHERE name = :name";

		int rowsAffected = namedParameterJdbcTemplate.update(renameQuery, namedParameters);

		if (rowsAffected != 1) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(renameQuery, 1, rowsAffected);
		}

	}

	@Override
	public void renameById(String id, String newName) {
		Group.validateId(id);

		Group.validateName(newName);

		SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id).addValue("name", newName);

		String renameQuery = "UPDATE core.groups SET name=:name WHERE id = :id";

		int rowsAffected = namedParameterJdbcTemplate.update(renameQuery, namedParameters);

		if (rowsAffected != 1) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(renameQuery, 1, rowsAffected);
		}

	}

}