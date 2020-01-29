package org.owasp.securityshepherd.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface NameIdDao<T> extends PagingAndSortingRepository<T, String> {

	public boolean containsName(String name);

	public void deleteByName(String name);

	public T getByName(String name);

	public void renameById(String id, String newName);

	public void renameByName(String name, String newName);

}