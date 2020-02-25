package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private KeyService keyService;

	@BeforeEach
	private void setUp() {
		userService = new UserService(userRepository, keyService);
	}

	@Test
	public void create_EmptyArgument_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> userService.create(""));

	}

	@Test
	public void create_NullArgument_ThrowsException() {

		assertThrows(NullPointerException.class, () -> userService.create(null));

	}

	@Test
	public void create_ValidDisplayName_CreatesUser() throws Exception {

		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(1);
		when(userRepository.findByDisplayName(any(String.class))).thenReturn(Mono.empty());
		when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

		final User createdUser = userService.create("TestUser").block();

		assertThat(createdUser, instanceOf(User.class));
		assertThat(createdUser, is(testUser));

	}

	@Test
	public void createPasswordUser_DuplicateDisplayName_ThrowsException() throws Exception {

		final String displayName = "createPasswordUser_ValidData";
		final String loginName = "_createPasswordUser_ValidData_";

		// String "createPasswordUser_ValidData" bcrypted
		final String hashedPassword = "$2y$04$2zPOzxj77Ul5amFcsnsyjenMBGpRgEApYsJXyK76dcX2wK7asi7.6";

		//when(userRepository.existsByDisplayName(displayName)).thenReturn(Mono.just(true));

		assertThrows(DuplicateUserDisplayNameException.class,
				() -> userService.createPasswordUser(displayName, loginName, hashedPassword));

		// Validate method calls
		//verify(userRepository, times(1)).existsByDisplayName(displayName);

	}

	@Test
	public void createPasswordUser_DuplicateLoginName_ThrowsException() {

		final String displayName = "createPasswordUser_ValidData";
		final String loginName = "_createPasswordUser_ValidData_";

		// String "createPasswordUser_ValidData" bcrypted
		final String hashedPassword = "$2y$04$2zPOzxj77Ul5amFcsnsyjenMBGpRgEApYsJXyK76dcX2wK7asi7.6";

		//when(userRepository.existsByLoginName(loginName)).thenReturn(Mono.just(true));

		assertThrows(DuplicateUserLoginNameException.class,
				() -> userService.createPasswordUser(displayName, loginName, hashedPassword));

		// Validate method calls
		//verify(userRepository, times(1)).existsByLoginName(loginName);

	}

	@Test
	public void createPasswordUser_EmptyArgument_ThrowsException() {

		String displayName = "createPasswordUser_NullArgument";
		String loginName = "_createPasswordUser_NullArgument_";
		String password = "aprettyweakpassword_with_null";

		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser("", loginName, password));
		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser(displayName, "", password));
		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser(displayName, loginName, ""));

	}

	@Test
	public void createPasswordUser_NullArgument_ThrowsException() {

		String displayName = "createPasswordUser_NullArgument";
		String loginName = "_createPasswordUser_NullArgument_";
		String password = "aprettyweakpassword_with_null";

		assertThrows(NullPointerException.class, () -> userService.createPasswordUser(null, loginName, password));
		assertThrows(NullPointerException.class, () -> userService.createPasswordUser(displayName, null, password));
		assertThrows(NullPointerException.class, () -> userService.createPasswordUser(displayName, loginName, null));

	}

	@Test
	public void createPasswordUser_ValidData_Succeeds() throws Exception {

		final String displayName = "createPasswordUser_ValidData";
		final String loginName = "_createPasswordUser_ValidData_";

		// String "createPasswordUser_ValidData" bcrypted
		final String hashedPassword = "$2y$04$2zPOzxj77Ul5amFcsnsyjenMBGpRgEApYsJXyK76dcX2wK7asi7.6";

		//when(userRepository.existsByDisplayName(displayName)).thenReturn(Mono.just(false));

		// When saving an user, just return the user back
		when(userRepository.save(any(User.class))).thenAnswer(user -> (user.getArgument(0)));

		final User createdUser = userService.createPasswordUser(displayName, loginName, hashedPassword).block();

		assertThat(createdUser.getDisplayName(), is(displayName));
		// assertThat(createdUser.block().getAuth().getPassword().getLoginName(),
		// is(loginName));
		// assertThat(createdUser.block().getAuth().getPassword().getHashedPassword(),
		// is(hashedPassword));

		// Validate method calls.
		final InOrder order1 = inOrder(userRepository);
		final InOrder order2 = inOrder(userRepository);

		// save() should be called after displayname and loginname checks, but we don't
		// care if loginname or displayname is checked first.
		// TODO
		// order1.verify(userRepository, times(1)).existsByDisplayName(displayName);
		// order1.verify(userRepository, times(1)).save(createdUser.withId(0));

		// order2.verify(userRepository, times(1)).existsByLoginName(loginName);
		// order2.verify(userRepository, times(1)).save(createdUser.withId(0));

	}

	@Test
	public void get_ValidUser_CallsRepository() throws InvalidUserIdException {

		User testUser = mock(User.class);
		when(userRepository.findById(123)).thenReturn(Mono.just(testUser));

		userService.get(123);

		verify(userRepository, times(1)).findById(123);

	}

	@Test
	@Disabled
	public void get_InvalidUserId_ThrowsException() {

		assertThrows(InvalidUserIdException.class, () -> userService.get(-1));
		assertThrows(InvalidUserIdException.class, () -> userService.get(-1000));
		assertThrows(InvalidUserIdException.class, () -> userService.get(0));

	}

	@Test
	public void get_NonExistentUserId_NotPresent() throws InvalidUserIdException {

//		assertThat(userService.count(), is(0L));
//		assertThat(userService.get(1).isPresent(), is(false));
//		assertThat(userService.get(1000).isPresent(), is(false));

	}

	@Test
	public void getKey_KeyExists_ReturnsKey() throws Exception {

		// Establish a random key
		final byte[] testRandomBytes = { -108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19 };
		final int userId = 17;

		// Mock a test user that has a key
		final User testUserWithKey = mock(User.class);
		when(testUserWithKey.getKey()).thenReturn(testRandomBytes);
		when(userRepository.findById(userId)).thenReturn(Mono.just(testUserWithKey));

		// Perform the test
		final byte[] userKey = userService.getKey(Mono.just(userId)).block();

		// final InOrder order = inOrder(testUserWithKey, userRepository);

		// userService should query the repository
		// order.verify(userRepository, times(1)).findById(userId);

		// and then extract the key
		// order.verify(testUserWithKey, times(1)).getKey();

		// Assert that we got the correct key
		assertThat(userKey, is(testRandomBytes));

	}

	@Test
	public void getKey_NoKeyExists_GeneratesKey() throws Exception {

		// Establish a random key
		final byte[] testRandomBytes = { -108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19 };
		final int userId = 19;

		// This user does not have a key
		final User testUserWithoutKey = mock(User.class);
		when(testUserWithoutKey.getKey()).thenReturn(null);

		// When that user is given a key, return an entity that has the key
		final User testUserWithKey = mock(User.class);
		when(testUserWithoutKey.withKey(testRandomBytes)).thenReturn(testUserWithKey);

		// Set up the mock repository
		when(userRepository.findById(userId)).thenReturn(Mono.just(testUserWithoutKey));
		when(userRepository.save(testUserWithKey)).thenReturn(Mono.just(testUserWithKey));

		// Set up the mock key service
		when(keyService.generateRandomBytes(16)).thenReturn(testRandomBytes);

		// Perform the test
		final Mono<byte[]> userKey = userService.getKey(Mono.just(userId));

		// Validate method calls
		final InOrder order = inOrder(testUserWithoutKey, userRepository, keyService);

		order.verify(userRepository, times(1)).findById(userId);
		order.verify(testUserWithoutKey, times(1)).getKey();
		order.verify(keyService, times(1)).generateRandomBytes(16);
		order.verify(testUserWithoutKey, times(1)).withKey(testRandomBytes);
		order.verify(userRepository, times(1)).save(testUserWithKey);

		assertThat(userKey, is(testRandomBytes));

	}

	@Test
	public void getKey_InvalidUserId_ThrowsException() {

		assertThrows(InvalidUserIdException.class, () -> userService.getKey(Mono.just(-1)));
		assertThrows(InvalidUserIdException.class, () -> userService.getKey(Mono.just(-1000)));
		assertThrows(InvalidUserIdException.class, () -> userService.getKey(Mono.just(0)));

	}

	@Test
	public void setClassId_ValidClass_Succeeds() throws Exception {
		User testUser = User.builder().displayName("TestUser").id(1).classId(1).build();

		when(userRepository.save(any(User.class)))
				.thenAnswer(user -> user.getArgument(0, User.class).withClassId(1).withId(1));
		when(userRepository.findById(1)).thenReturn(Mono.just(testUser));
		// when(classService.existsById(2)).thenReturn(true);

		int userId = userService.create("TestUser").block().getId();

		userService.setClassId(Mono.just(userId), Mono.just(2));

		User returnedUser = userService.get(userId).block();
		assertThat(returnedUser.getClassId(), is(1));

	}

	@Test
	public void setDisplayName_ValidName_Succeeds() throws UserIdNotFoundException, InvalidUserIdException {

		String newDisplayName = "newName";

		User testUser = mock(User.class);
		when(userRepository.findById(123)).thenReturn(Mono.just(testUser));
		when(testUser.withDisplayName(newDisplayName)).thenReturn(testUser);
		when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

		userService.setDisplayName(Mono.just(123), Mono.just(newDisplayName));

		// InOrder order = inOrder(testUser, userRepository);

		// order.verify(testUser, times(1)).withDisplayName(newDisplayName);
		// order.verify(userRepository, times(1)).save(testUser);

	}

}