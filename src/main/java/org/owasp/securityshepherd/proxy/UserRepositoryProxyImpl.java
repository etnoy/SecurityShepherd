package org.owasp.securityshepherd.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserRepositoryProxyImpl implements UserRepositoryProxy {

	private final UserRepository userRepository;

	@Override
	public User save(final User user) {
		return userRepository.save(user);
	}

	@Override
	public Iterable<User> saveAll(final Iterable<User> users) {
		return userRepository.saveAll(users);
	}

	@Override
	public Optional<User> findById(Integer id) {
		return userRepository.findById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return userRepository.existsById(id);
	}

	@Override
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Iterable<User> findAllById(Iterable<Integer> ids) {
		return userRepository.findAllById(ids);
	}

	@Override
	public long count() {
		return userRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		userRepository.deleteById(id);
	}

	@Override
	public void delete(User user) {
		userRepository.delete(user);
	}

	@Override
	public void deleteAll(Iterable<? extends User> users) {
		userRepository.deleteAll(users);
	}

	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}

	@Override
	public boolean existsByDisplayName(String displayName) {
		return userRepository.existsByDisplayName(displayName);
	}

	@Override
	public boolean existsByLoginName(String loginName) {
		return userRepository.existsByLoginName(loginName);
	}

	@Override
	public Optional<User> findByLoginName(String loginName) {
		return userRepository.findByLoginName(loginName);
	}

}