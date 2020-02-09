package org.owasp.securityshepherd.test.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void existsById_ExistingId_ReturnsTrue() {

		User existsByIdExistingIdUser = User.builder().displayName("existsById_ExistingId").build();

		User returnedUser = userRepository.save(existsByIdExistingIdUser);

		assertNotNull(returnedUser.getId());
		assertTrue(userRepository.existsById(returnedUser.getId()));

	}

	@Test
	public void existsById_NonExistentId_ReturnsFalse() {

		assertFalse(userRepository.existsById(1234567890L));

	}

	@Test
	public void count_KnownNumberOfUsers_ReturnsCorrectNumber() {

		userRepository.deleteAll();
		assertEquals(0, userRepository.count());

		userRepository.save(User.builder().displayName("count_KnownNumberOfUsers1").build());
		assertEquals(1, userRepository.count());

		userRepository.save(User.builder().displayName("count_KnownNumberOfUsers2").build());
		assertEquals(2, userRepository.count());

		userRepository.save(User.builder().displayName("count_KnownNumberOfUsers3").build());
		assertEquals(3, userRepository.count());

		userRepository.save(User.builder().displayName("count_KnownNumberOfUsers4").build());
		assertEquals(4, userRepository.count());

		userRepository.save(User.builder().displayName("count_KnownNumberOfUsers5").build());
		assertEquals(5, userRepository.count());

	}

	@Test
	public void save_DuplicateUserName_ThrowsException() {

		User duplicateUserName1 = User.builder().displayName("duplicateUserName").build();
		User duplicateUserName2 = User.builder().displayName("duplicateUserName").build();

		userRepository.save(duplicateUserName1);

		assertThrows(DbActionExecutionException.class, () -> {
			userRepository.save(duplicateUserName2);
		});

	}

	@Test
	public void save_ValidUser_ContainedInAllUsers() {

		User validUser1 = User.builder().displayName("save_ValidUser1").build();

		User validUser2 = User.builder().classId(3L).displayName("save_ValidUser2").email("me@example.com").build();

		User validUser3 = User.builder().classId(500L).displayName("save_ValidUser3").email("").build();

		validUser1 = userRepository.save(validUser1);
		validUser2 = userRepository.save(validUser2);
		validUser3 = userRepository.save(validUser3);

		List<User> allUsers = (List<User>) userRepository.findAll();

		assertTrue(allUsers.contains(validUser1), "List of users should contain added users");
		assertTrue(allUsers.contains(validUser2), "List of users should contain added users");
		assertTrue(allUsers.contains(validUser3), "List of users should contain added users");

	}

	@Test
	public void deleteAll_ExistingUsers_DeletesAll() {

		assertEquals(0, userRepository.count());

		userRepository.save(User.builder().displayName("deleteAll_DeletesAll_user1").build());

		assertEquals(1, userRepository.count());

		userRepository.deleteAll();

		assertEquals(0, userRepository.count());

		userRepository.save(User.builder().displayName("deleteAll_DeletesAll_user2").build());
		userRepository.save(User.builder().displayName("deleteAll_DeletesAll_user3").build());
		userRepository.save(User.builder().displayName("deleteAll_DeletesAll_user4").build());
		userRepository.save(User.builder().displayName("deleteAll_DeletesAll_user5").build());

		assertEquals(4, userRepository.count());

		userRepository.deleteAll();

		assertEquals(0, userRepository.count());

	}

	@Test
	public void deleteAll_NoUsers_DoesNothing() {

		assertEquals(0, userRepository.count());

		userRepository.deleteAll();

		assertEquals(0, userRepository.count());

	}

	@Test
	public void deleteById_ValidId_DeletesUser() {

		User returnedUser = userRepository.save(User.builder().displayName("deleteById_ValidId").build());

		userRepository.deleteById(returnedUser.getId());

		assertFalse(userRepository.existsById(returnedUser.getId()));

	}

	@Test
	public void findAll_ReturnsUsers() {

		userRepository.deleteAll();

		assertTrue(userRepository.count() == 0);

		User findAll_ReturnsUsers_user1 = userRepository
				.save(User.builder().displayName("findAll_ReturnsUsers_user1").build());
		User findAll_ReturnsUsers_user2 = userRepository
				.save(User.builder().displayName("findAll_ReturnsUsers_user2").build());
		User findAll_ReturnsUsers_user3 = userRepository
				.save(User.builder().displayName("findAll_ReturnsUsers_user3").build());
		User findAll_ReturnsUsers_user4 = userRepository
				.save(User.builder().displayName("findAll_ReturnsUsers_user4").build());

		assertTrue(userRepository.existsById(findAll_ReturnsUsers_user1.getId()));
		assertTrue(userRepository.existsById(findAll_ReturnsUsers_user2.getId()));
		assertTrue(userRepository.existsById(findAll_ReturnsUsers_user3.getId()));
		assertTrue(userRepository.existsById(findAll_ReturnsUsers_user4.getId()));

		List<User> users = (List<User>) userRepository.findAll();

		assertEquals(4, users.size());

		assertTrue(users.contains(findAll_ReturnsUsers_user1));
		assertTrue(users.contains(findAll_ReturnsUsers_user2));
		assertTrue(users.contains(findAll_ReturnsUsers_user3));
		assertTrue(users.contains(findAll_ReturnsUsers_user4));

	}

	@Test
	public void findById_NonExistentId_ThrowsException() {

		assertFalse(userRepository.findById(123456789L).isPresent());

	}

	@Test
	public void findById_ValidId_CanFindUser() {

		User findUserById_validId_User = userRepository.save(User.builder().displayName("findById_ValidId").build());

		Optional<User> returnedUser = userRepository.findById(findUserById_validId_User.getId());

		assertTrue(returnedUser.isPresent());

		assertEquals(returnedUser.get(), findUserById_validId_User);

	}

}