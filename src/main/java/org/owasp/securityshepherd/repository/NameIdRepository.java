package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface NameIdRepository<T> extends PagingAndSortingRepository<T, String> {

	public boolean existsByName(String name);

	public void deleteByName(String name);

	public Optional<T> findByName(String name);

}