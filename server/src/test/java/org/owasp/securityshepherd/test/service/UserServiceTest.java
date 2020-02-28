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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.Auth;
import org.owasp.securityshepherd.persistence.model.Module;
import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.repository.AuthRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
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
	private ClassService classService;

	@Mock
	private KeyService keyService;

	@Test
	public void count_ReturnsNumberOfUsers() throws Exception {

		final long mockedUserCount = 11L;

		when(userRepository.count()).thenReturn(Mono.just(mockedUserCount));

		StepVerifier.create(userService.count()).assertNext(count -> {

			assertThat(count, is(mockedUserCount));
			verify(userRepository, times(1)).count();

		}).expectComplete().verify();

	}

	@Test
	public void create_DisplayNameAlreadyExists_ThrowsException() {

		final String displayName = "createPasswordUser_DuplicateDisplayName";

		final User mockUser = mock(User.class);

		when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.just(mockUser));

		StepVerifier.create(userService.create(displayName))
				.expectError(DuplicateUserDisplayNameException.class).verify();

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

		final String displayName = "TestUser";
		final int mockId = 22;

		when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

		when(userRepository.save(any(User.class)))
				.thenAnswer(user -> Mono.just(user.getArgument(0, User.class).withId(mockId)));

		StepVerifier.create(userService.create(displayName)).assertNext(user -> {

			assertThat(user.getDisplayName(), is(displayName));

			verify(userRepository, times(1)).findByDisplayName(displayName);
			verify(userRepository, times(1)).save(any(User.class));

		}).expectComplete().verify();

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
				.expectError(DuplicateClassNameException.class).verify();

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

		final String hashedPassword = "a_valid_password";

		final int mockId = 199;

		when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.empty());
		when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

		when(userRepository.save(any(User.class)))
				.thenAnswer(user -> Mono.just(user.getArgument(0, User.class).withId(mockId)));

		StepVerifier.create(userService.createPasswordUser(displayName, loginName, hashedPassword)).assertNext(user -> {

			assertThat(user.getAuth().getPassword().getLoginName(), is(loginName));
			assertThat(user.getAuth().getPassword().getHashedPassword(), is(hashedPassword));

			verify(passwordAuthRepository, times(1)).findByLoginName(loginName);
			verify(userRepository, times(1)).findByDisplayName(displayName);

			verify(userRepository, times(1)).save(any(User.class));
			verify(authRepository, times(1)).save(any(Auth.class));
			verify(passwordAuthRepository, times(1)).save(any(PasswordAuth.class));

		}).expectComplete().verify();

	}

	@Test
	public void getById_ExistingUserId_ReturnsUserEntity() throws InvalidUserIdException {

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

		StepVerifier.create(userService.getById(mockId)).assertNext(user -> {
			assertThat(user, is(mockUserWithAuth));
			assertThat(user.getAuth(), is(mockAuthWithPassword));
			assertThat(user.getAuth().getPassword(), is(mockPasswordAuth));

			verify(userRepository, times(1)).findById(mockId);
			verify(authRepository, times(1)).findByUserId(mockId);
			verify(passwordAuthRepository, times(1)).findByUserId(mockId);

		}).expectComplete().verify();

	}

	@Test
	public void getById_ExistingUserIdButNoAuthEntity_ReturnsUserEntity() throws InvalidUserIdException {

		final User mockUser = mock(User.class);

		final int mockId = 123;

		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUser));
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(mockUser.withAuth(any(Auth.class))).thenReturn(null);

		StepVerifier.create(userService.getById(mockId)).assertNext(user -> {
			assertThat(user, is(mockUser));
			assertThat(user.getAuth(), is(nullValue()));

			verify(userRepository, times(2)).findById(mockId);
			verify(authRepository, times(2)).findByUserId(mockId);
		}).expectComplete().verify();

	}

	@Test
	public void getById_ExistingUserIdButNoPasswordAuthEntity_ReturnsUserEntity() throws InvalidUserIdException {

		final Auth mockAuth = mock(Auth.class);
		final User mockUser = mock(User.class);

		final User mockUserWithAuth = mock(User.class);

		final int mockId = 123;

		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUser));
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(authRepository.findByUserId(mockId)).thenReturn(Mono.just(mockAuth));
		when(mockUserWithAuth.getAuth()).thenReturn(mockAuth);

		when(mockUser.withAuth(any(Auth.class))).thenReturn(mockUserWithAuth);

		StepVerifier.create(userService.getById(mockId)).assertNext(user -> {
			assertThat(user, is(mockUserWithAuth));
			assertThat(user.getAuth(), is(mockAuth));
			assertThat(user.getAuth().getPassword(), is(nullValue()));

			verify(userRepository, times(1)).findById(mockId);
			verify(authRepository, times(2)).findByUserId(mockId);
			verify(passwordAuthRepository, times(1)).findByUserId(mockId);
		}).expectComplete().verify();

	}

	@Test
	public void getById_InvalidUserId_ThrowsException() throws InvalidUserIdException {

		StepVerifier.create(Flux.just(-1, -1000, 0, -99999).next().flatMap(userService::getById))
				.expectError(InvalidUserIdException.class).verify();

	}

	@Test
	public void getById_NonExistentUserId_ThrowsException() throws InvalidUserIdException {

		final int mockId = 123;

		when(userRepository.existsById(mockId)).thenReturn(Mono.just(false));

		StepVerifier.create(userService.getById(mockId)).expectError(UserIdNotFoundException.class).verify();

	}

	@Test
	public void getByLoginName_EmptyLoginName_ThrowsException() throws InvalidUserIdException {

		assertThrows(IllegalArgumentException.class, () -> userService.getByLoginName(""));

	}

	@Test
	public void getByLoginName_NullLoginName_ThrowsException() throws InvalidUserIdException {

		assertThrows(NullPointerException.class, () -> userService.getByLoginName(null));

	}

	@Test
	public void getByLoginName_PasswordAuthExistButNotUser_ThrowsException() throws InvalidUserIdException {

		final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);

		final String loginName = "MockUser";
		final int mockId = 117;

		when(mockPasswordAuth.getUser()).thenReturn(mockId);
		when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.just(mockPasswordAuth));

		when(userRepository.findById(mockId)).thenReturn(Mono.empty());
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(false));

		StepVerifier.create(userService.getByLoginName(loginName)).expectError(UserIdNotFoundException.class).verify();

	}

	@Test
	public void getByLoginName_UserExists_ReturnsUser() throws InvalidUserIdException {

		final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);
		final Auth mockAuth = mock(Auth.class);
		final User mockUser = mock(User.class);

		final Auth mockAuthWithPassword = mock(Auth.class);
		final User mockUserWithAuth = mock(User.class);

		final String loginName = "MockUser";
		final int mockId = 117;

		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUser));
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(authRepository.findByUserId(mockId)).thenReturn(Mono.just(mockAuth));
		when(passwordAuthRepository.findByUserId(mockId)).thenReturn(Mono.just(mockPasswordAuth));
		when(mockAuthWithPassword.getPassword()).thenReturn(mockPasswordAuth);
		when(mockUserWithAuth.getAuth()).thenReturn(mockAuthWithPassword);

		when(mockAuth.withPassword(any(PasswordAuth.class))).thenReturn(mockAuthWithPassword);
		when(mockUser.withAuth(any(Auth.class))).thenReturn(mockUserWithAuth);

		when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.just(mockPasswordAuth));
		when(mockPasswordAuth.getUser()).thenReturn(mockId);

		StepVerifier.create(userService.getByLoginName(loginName)).assertNext(user -> {
			assertThat(user, is(mockUserWithAuth));
			assertThat(user.getAuth(), is(mockAuthWithPassword));
			assertThat(user.getAuth().getPassword(), is(mockPasswordAuth));

			verify(userRepository, times(1)).findById(mockId);
			verify(authRepository, times(1)).findByUserId(mockId);
			verify(passwordAuthRepository, times(1)).findByUserId(mockId);

		}).expectComplete().verify();

	}

	@Test
	public void getByLoginName_UserIdDoesNotExist_ThrowsException() throws InvalidUserIdException {

		final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);

		final String loginName = "MockUser";
		final int mockId = 117;

		when(mockPasswordAuth.getUser()).thenReturn(mockId);
		when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.just(mockPasswordAuth));

		when(userRepository.findById(mockId)).thenReturn(Mono.empty());
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(false));

		StepVerifier.create(userService.getByLoginName(loginName)).expectError(UserIdNotFoundException.class).verify();

	}

	@Test
	public void getKeyById_InvalidUserId_ThrowsException() {

		StepVerifier.create(Flux.just(-1, -1000, 0).next().flatMap(userService::getKeyById))
				.expectError(InvalidUserIdException.class).verify();

	}

	@Test
	public void getKeyById_KeyExists_ReturnsKey() throws Exception {

		// Establish a random key
		final byte[] testRandomBytes = { -108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19 };
		final int mockId = 17;

		// Mock a test user that has a key
		final User mockUserWithKey = mock(User.class);
		when(mockUserWithKey.getKey()).thenReturn(testRandomBytes);

		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));
		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUserWithKey));

		StepVerifier.create(userService.getKeyById(mockId)).assertNext(key -> {

			final InOrder order = inOrder(mockUserWithKey, userRepository);

			// userService should query the repository
			order.verify(userRepository, times(1)).findById(mockId);

			// and then extract the key
			order.verify(mockUserWithKey, times(1)).getKey();

			// Assert that we got the correct key
			assertThat(key, is(testRandomBytes));

		}).expectComplete().verify();

	}

	@Test
	public void getKeyById_NoKeyExists_GeneratesKey() throws Exception {

		// Establish a random key
		final byte[] testRandomBytes = { -108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19 };
		final int mockId = 19;

		// This user does not have a key
		final User mockUserWithoutKey = mock(User.class);
		when(mockUserWithoutKey.getKey()).thenReturn(null);

		// When that user is given a key, return an entity that has the key
		final User mockUserWithKey = mock(User.class);
		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUserWithoutKey));
		when(mockUserWithoutKey.getKey()).thenReturn(null);

		when(mockUserWithoutKey.withKey(testRandomBytes)).thenReturn(mockUserWithKey);

		// Set up the mock repository
		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));
		when(userRepository.save(mockUserWithKey)).thenReturn(Mono.just(mockUserWithKey));

		// Set up the mock key service
		when(keyService.generateRandomBytes(16)).thenReturn(Mono.just(testRandomBytes));

		when(mockUserWithKey.getKey()).thenReturn(testRandomBytes);

		StepVerifier.create(userService.getKeyById(mockId)).assertNext(key -> {

			// Assert that we got the correct key
			assertThat(key, is(testRandomBytes));

			ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

			verify(userRepository, times(1)).findById(mockId);
			verify(mockUserWithoutKey, times(1)).getKey();
			verify(keyService, times(1)).generateRandomBytes(16);
			verify(mockUserWithoutKey, times(1)).withKey(testRandomBytes);
			verify(mockUserWithKey, times(1)).getKey();
			verify(userRepository, times(1)).save(argument.capture());
			assertThat(argument.getValue(), is(mockUserWithKey));
			assertThat(argument.getValue().getKey(), is(testRandomBytes));

		}).expectComplete().verify();

	}

	@Test
	public void setClassId_ClassIdDoesNotExist_ThrowsException() throws Exception {

		int userId = 12;
		int classId = 84;

		User mockUser = mock(User.class);

		when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
		when(userRepository.findById(userId)).thenReturn(Mono.just(mockUser));
		when(classService.existsById(classId)).thenReturn(Mono.just(false));

		StepVerifier.create(userService.setClassId(userId, classId)).expectError(ClassIdNotFoundException.class)
				.verify();

	}

	@Test
	public void setClassId_InvalidClassId_ThrowsException() throws Exception {

		assertThrows(InvalidClassIdException.class, () -> userService.setClassId(10, -3));

	}

	@Test
	public void setClassId_InvalidUserId_ThrowsException() throws Exception {

		assertThrows(InvalidUserIdException.class, () -> userService.setClassId(-5, 61));

	}

	@Test
	public void setClassId_ValidClassId_Succeeds() throws Exception {

		int userId = 13;
		int classId = 94;

		User mockUser = mock(User.class);
		User mockUserWithClass = mock(User.class);

		when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
		when(userRepository.findById(userId)).thenReturn(Mono.just(mockUser));
		when(classService.existsById(classId)).thenReturn(Mono.just(true));

		when(mockUser.withClassId(classId)).thenReturn(mockUserWithClass);
		when(userRepository.save(mockUserWithClass)).thenReturn(Mono.just(mockUserWithClass));
		when(mockUserWithClass.getClassId()).thenReturn(classId);

		StepVerifier.create(userService.setClassId(userId, classId)).assertNext(user -> {

			assertThat(user.getClassId(), is(classId));

		}).expectComplete().verify();

	}

	@Test
	public void setDisplayName_EmptyDisplayName_ThrowsException() throws InvalidUserIdException {

		assertThrows(IllegalArgumentException.class, () -> userService.setDisplayName(1, ""));

	}

	@Test
	public void setDisplayName_InvalidUserId_ThrowsException() throws InvalidUserIdException {

		assertThrows(InvalidUserIdException.class, () -> userService.setDisplayName(-1, "displayName"));
		assertThrows(InvalidUserIdException.class, () -> userService.setDisplayName(0, "displayName"));
		assertThrows(InvalidUserIdException.class, () -> userService.setDisplayName(-1000, "displayName"));

	}

	@Test
	public void setDisplayName_NullDisplayName_ThrowsException() throws InvalidUserIdException {

		assertThrows(NullPointerException.class, () -> userService.setDisplayName(1, null));

	}

	@Test
	public void setDisplayName_UserIdDoesNotExist_ThrowsException() throws InvalidUserIdException {

		String newDisplayName = "newName";

		int mockId = 3;

		when(userRepository.existsById(mockId)).thenReturn(Mono.just(false));

		StepVerifier.create(userService.setDisplayName(mockId, newDisplayName))
				.expectError(UserIdNotFoundException.class).verify();

	}

	@Test
	public void setDisplayName_ValidName_Succeeds() throws Exception {

		User mockUser = mock(User.class);
		String newDisplayName = "newName";

		int mockId = 3;

		when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));
		when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUser));
		when(userRepository.findByDisplayName(newDisplayName)).thenReturn(Mono.empty());

		when(mockUser.withDisplayName(newDisplayName)).thenReturn(mockUser);
		when(userRepository.save(any(User.class))).thenReturn(Mono.just(mockUser));
		when(mockUser.getDisplayName()).thenReturn(newDisplayName);

		StepVerifier.create(userService.setDisplayName(mockId, newDisplayName)).assertNext(user -> {

			assertThat(user.getDisplayName(), is(newDisplayName));

			InOrder order = inOrder(mockUser, userRepository);

			order.verify(mockUser, times(1)).withDisplayName(newDisplayName);
			order.verify(userRepository, times(1)).save(mockUser);

		}).expectComplete().verify();

	}

	@BeforeEach
	private void setUp() {
		// Print more verbose errors if something goes wrong
		Hooks.onOperatorDebug();

		// Set up userService to use our mocked repos and services
		userService = new UserService(userRepository, authRepository, passwordAuthRepository, classService, keyService);
	}

}