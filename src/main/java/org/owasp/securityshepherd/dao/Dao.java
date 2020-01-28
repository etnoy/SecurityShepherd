package org.owasp.securityshepherd.dao;

import java.util.List;

public interface Dao<T> {

	public void create(T entity);

	public T getById(String id);

	public T getByName(String name);

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