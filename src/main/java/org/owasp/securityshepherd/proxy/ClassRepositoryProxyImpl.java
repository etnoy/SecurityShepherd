package org.owasp.securityshepherd.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.repository.ClassRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ClassRepositoryProxyImpl implements ClassRepositoryProxy {

	private final ClassRepository classRepository;

	@Override
	public ClassEntity save(final ClassEntity classEntity) {
		return classRepository.save(classEntity);
	}

	@Override
	public Iterable<ClassEntity> saveAll(final Iterable<ClassEntity> classEntity) {
		return classRepository.saveAll(classEntity);
	}

	@Override
	public Optional<ClassEntity> findById(Integer id) {
		return classRepository.findById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return classRepository.existsById(id);
	}

	@Override
	public Iterable<ClassEntity> findAll() {
		return classRepository.findAll();
	}

	@Override
	public Iterable<ClassEntity> findAllById(Iterable<Integer> ids) {
		return classRepository.findAllById(ids);
	}

	@Override
	public long count() {
		return classRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		classRepository.deleteById(id);
	}

	@Override
	public void delete(ClassEntity classEntity) {
		classRepository.delete(classEntity);
	}

	@Override
	public void deleteAll(Iterable<? extends ClassEntity> classEntity) {
		classRepository.deleteAll(classEntity);
	}

	@Override
	public void deleteAll() {
		classRepository.deleteAll();
	}

}