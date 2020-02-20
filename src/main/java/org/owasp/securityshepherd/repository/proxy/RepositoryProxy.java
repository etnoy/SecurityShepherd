package org.owasp.securityshepherd.repository.proxy;

import java.util.Optional;

public interface RepositoryProxy<T> {

	public T save(T entity);

	public Iterable<T> saveAll(Iterable<T> entities);

	public Optional<T> findById(Integer id);

	public boolean existsById(Integer id);

	public Iterable<T> findAll();

	public Iterable<T> findAllById(Iterable<Integer> ids);

	public long count();

	public void deleteById(Integer id);

	public void delete(T entity);

	public void deleteAll(Iterable<? extends T> entities);

	public void deleteAll();

}