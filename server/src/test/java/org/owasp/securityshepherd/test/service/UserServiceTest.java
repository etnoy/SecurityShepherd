package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.Auth;
import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.repository.AuthRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthRepository authRepository;

	@Mock
	private PasswordAuthRepository passwordAuthRepository;

	@Mock
	private KeyService keyService;

	@BeforeEach
	private void setUp() {
		Hooks.onOperatorDebug();

		userService = new UserService(userRepository, authRepository, passwordAuthRepository, keyService);
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

		User mockUser = mock(User.class);
		when(mockUser.getId()).thenReturn(1);
		when(userRepository.findByDisplayName(any(String.class))).thenReturn(Mono.empty());
		when(userRepository.save(any(User.class))).thenReturn(Mono.just(mockUser));

	//	final User user = userService.create("TestUser").block();

//		assertThat(createdUser, instanceOf(User.class));
//		assertThat(createdUser, is(mockUser));

	}

	@Test
	public void createPasswordUser_DuplicateDisplayName_ThrowsException() throws Exception {

		final String displayName = "createPasswordUser_DuplicateDisplayName";
		final String loginName = "_createPasswordUser_DuplicateDisplayName_";

		final String hashedPassword = "aPasswordHash";

		final User mockUser = mock(User.class);

		when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.just(mockUser));

		StepVerifier.create(userService.createPasswordUser(displayName, loginName, hashedPassword))
				.expectError(DuplicateUserDisplayNameException.class).verify();

	}

	@Test
	public void createPasswordUser_DuplicateLoginName_ThrowsException() {

		final String displayName = "createPasswordUser_DuplicateLoginName";
		final String loginName = "_createPasswordUser_DuplicateLoginName_";

		final String password = "a_valid_password";

		final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);

		when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.just(mockPasswordAuth));
		when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

		StepVerifier.create(userService.createPasswordUser(displayName, loginName, password))
				.expectError(DuplicateUserLoginNameException.class).verify();

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

		assertThrows(NullPointerException.class,
				() -> userService.createPasswordUser(null, loginName, password).block());
		assertThrows(NullPointerException.class,
				() -> userService.createPasswordUser(displayName, null, password).block());
		assertThrows(NullPointerException.class,
				() -> userService.createPasswordUser(displayName, loginName, null).block());

	}

	@Test
	public void createPasswordUser_ValidData_Succeeds() throws Exception {

		final String displayName = "createPasswordUser_ValidData";
		final String loginName = "_createPasswordUser_ValidData_";

		final String hashedPassword = "a_valid_password";

		when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.empty());
		when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

		StepVerifier.create(userService.createPasswordUser(displayName, loginName, hashedPassword)).assertNext(user -> {
			assertThat(user.getAuth().getPassword().getLoginName(), is(loginName));
			assertThat(user.getAuth().getPassword().getHashedPassword(), is(hashedPassword));
		});

	}

	@Test
	public void get_ExistingUserIdButNoPasswordAuthEntity_ReturnsUserEntity() throws InvalidUserIdException {

		final Auth mockAuth = mock(Auth.class);
		final User mockUser = mock(User.class);

		final User mockUserWithAuth = mock(User.class);

		final int mockId = 123;

		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUser));
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(authRepository.findByUserId(mockId)).thenReturn(Mono.just(mockAuth));
		when(mockUserWithAuth.getAuth()).thenReturn(mockAuth);

		when(mockUser.withAuth(any(Auth.class))).thenReturn(mockUserWithAuth);

		StepVerifier.create(userService.get(mockId)).assertNext(user -> {
			assertThat(user, is(mockUserWithAuth));
			assertThat(user.getAuth(), is(mockAuth));
			assertThat(user.getAuth().getPassword(), is(nullValue()));

			verify(userRepository, times(1)).findById(mockId);
			verify(authRepository, times(2)).findByUserId(mockId);
			verify(passwordAuthRepository, times(1)).findByUserId(mockId);
		}).expectComplete().verify();

	}

	@Test
	public void get_ExistingUserIdButNoAuthEntity_ReturnsUserEntity() throws InvalidUserIdException {

		final User mockUser = mock(User.class);

		final int mockId = 123;

		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUser));
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(mockUser.withAuth(any(Auth.class))).thenReturn(null);

		StepVerifier.create(userService.get(mockId)).assertNext(user -> {
			assertThat(user, is(mockUser));
			assertThat(user.getAuth(), is(nullValue()));

			verify(userRepository, times(2)).findById(mockId);
			verify(authRepository, times(2)).findByUserId(mockId);
		}).expectComplete().verify();

	}

	@Test
	public void get_ExistingUserId_ReturnsUserEntity() throws InvalidUserIdException {

		final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);
		final Auth mockAuth = mock(Auth.class);
		final User mockUser = mock(User.class);

		final Auth mockAuthWithPassword = mock(Auth.class);
		final User mockUserWithAuth = mock(User.class);

		final int mockId = 123;

		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUser));
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(authRepository.findByUserId(mockId)).thenReturn(Mono.just(mockAuth));
		when(passwordAuthRepository.findByUserId(mockId)).thenReturn(Mono.just(mockPasswordAuth));
		when(mockAuthWithPassword.getPassword()).thenReturn(mockPasswordAuth);
		when(mockUserWithAuth.getAuth()).thenReturn(mockAuthWithPassword);

		when(mockAuth.withPassword(any(PasswordAuth.class))).thenReturn(mockAuthWithPassword);
		when(mockUser.withAuth(any(Auth.class))).thenReturn(mockUserWithAuth);

		StepVerifier.create(userService.get(mockId)).assertNext(user -> {
			assertThat(user, is(mockUserWithAuth));
			assertThat(user.getAuth(), is(mockAuthWithPassword));
			assertThat(user.getAuth().getPassword(), is(mockPasswordAuth));

			verify(userRepository, times(1)).findById(mockId);
			verify(authRepository, times(1)).findByUserId(mockId);
			verify(passwordAuthRepository, times(1)).findByUserId(mockId);

		}).expectComplete().verify();

	}

	@Test
	public void get_InvalidUserId_ThrowsException() throws InvalidUserIdException {

		final int mockId = 123;

		when(userRepository.existsById(mockId)).thenReturn(Mono.just(false));

		StepVerifier.create(userService.get(mockId)).expectError(UserIdNotFoundException.class).verify();

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
		final int mockId = 17;

		// Mock a test user that has a key
		final User testUserWithKey = mock(User.class);
		when(testUserWithKey.getKey()).thenReturn(testRandomBytes);

		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));
		when(userRepository.findById(mockId)).thenReturn(Mono.just(testUserWithKey));

		StepVerifier.create(userService.getKey(mockId)).assertNext(key -> {

			final InOrder order = inOrder(testUserWithKey, userRepository);

			// userService should query the repository
			order.verify(userRepository, times(1)).findById(mockId);

			// and then extract the key
			order.verify(testUserWithKey, times(1)).getKey();

			// Assert that we got the correct key
			assertThat(key, is(testRandomBytes));

		}).expectComplete().verify();

	}

	@Test
	public void getKey_NoKeyExists_GeneratesKey() throws Exception {

		// Establish a random key
		final byte[] testRandomBytes = { -108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19 };
		final int mockId = 19;

		// This user does not have a key
		final User testUserWithoutKey = mock(User.class);
		when(testUserWithoutKey.getKey()).thenReturn(null);

		// When that user is given a key, return an entity that has the key
		final User testUserWithKey = mock(User.class);
		when(testUserWithoutKey.withKey(testRandomBytes)).thenReturn(testUserWithKey);

		// Set up the mock repository
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));
		when(userRepository.findById(mockId)).thenReturn(Mono.just(testUserWithoutKey));
		when(userRepository.save(testUserWithKey)).thenReturn(Mono.just(testUserWithKey));

		// Set up the mock key service
		when(keyService.generateRandomBytes(16)).thenReturn(testRandomBytes);
		
		StepVerifier.create(userService.getKey(mockId)).assertNext(key -> {

			final InOrder order = inOrder(testUserWithoutKey, userRepository, keyService);

			order.verify(userRepository, times(1)).findById(mockId);
			order.verify(testUserWithoutKey, times(1)).getKey();
			order.verify(keyService, times(1)).generateRandomBytes(16);
			order.verify(testUserWithoutKey, times(1)).withKey(testRandomBytes);
			order.verify(userRepository, times(1)).save(testUserWithKey);

			// Assert that we got the correct key
			assertThat(key, is(testRandomBytes));

		}).expectComplete().verify();

	}

	@Test
	public void getKey_InvalidUserId_ThrowsException() {

		assertThrows(InvalidUserIdException.class, () -> userService.getKey(-1));
		assertThrows(InvalidUserIdException.class, () -> userService.getKey(-1000));
		assertThrows(InvalidUserIdException.class, () -> userService.getKey(0));

	}

	@Test
	public void setClassId_ValidClass_Succeeds() throws Exception {
		User testUser = User.builder().displayName("TestUser").id(1).classId(1).build();

		when(userRepository.save(any(User.class)))
				.thenAnswer(user -> user.getArgument(0, User.class).withClassId(1).withId(1));
		when(userRepository.findById(1)).thenReturn(Mono.just(testUser));
		// when(classService.existsById(2)).thenReturn(true);

		//final User user = userService.create("TestUser").block();

//		userService.setClassId(user.getId(), 2);
//
//		User returnedUser = userService.get(user.getId()).block();
//		assertThat(returnedUser.getClassId(), is(1));

	}

	@Test
	public void setDisplayName_ValidName_Succeeds() throws UserIdNotFoundException, InvalidUserIdException {

		String newDisplayName = "newName";

		User testUser = mock(User.class);
		when(userRepository.findById(123)).thenReturn(Mono.just(testUser));
		when(testUser.withDisplayName(newDisplayName)).thenReturn(testUser);
		when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

		userService.setDisplayName(123, newDisplayName);

		// InOrder order = inOrder(testUser, userRepository);

		// order.verify(testUser, times(1)).withDisplayName(newDisplayName);
		// order.verify(userRepository, times(1)).save(testUser);

	}

}