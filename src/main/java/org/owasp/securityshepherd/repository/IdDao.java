package org.owasp.securityshepherd.repository;

import java.util.List;

public interface IdDao<T> {

	public boolean containsId(String id);

	public int count();

	public void create(T entity);

	public void deleteAll();

	public void deleteById(String id);

	public List<T> getAll();

	public T getById(String id);

}