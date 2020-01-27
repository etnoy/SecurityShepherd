package org.owasp.securityshepherd.dao;

import java.util.List;

import org.owasp.securityshepherd.model.User;

public interface Dao<T> {

	public void create(User user);

	public User getById(String id);

	public User getByName(String name);

	public List<T> getAll();

	public void deleteById(String id);

	public void deleteByName(String name);

	public void deleteAll();

	public void renameById(String id, String newName);

	public void renameByName(String id, String newName);

	public int count();

	public boolean containsId(String id);

	public boolean containsName(String name);

}