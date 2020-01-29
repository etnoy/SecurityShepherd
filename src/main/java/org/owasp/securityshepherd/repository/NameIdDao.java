package org.owasp.securityshepherd.repository;

public interface NameIdDao<T> extends IdDao<T> {

	public boolean containsName(String name);

	public void deleteByName(String name);

	public T getByName(String name);

	public void renameById(String id, String newName);

	public void renameByName(String name, String newName);

}