package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.owasp.securityshepherd.model.UserAuth;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.AuthRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.UserDatabaseClient;
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
@DisplayName("UserService unit test")
public class UserServiceTest {

  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserDatabaseClient userDatabaseClient;

  @Mock
  private AuthRepository authRepository;

  @Mock
  private PasswordAuthRepository passwordAuthRepository;

  @Mock
  private ClassService classService;

  @Mock
  private KeyService keyService;

  @Test
  @DisplayName("count() must return number of users")
  public void count_FiniteNumberOfUsers_ReturnsCount() throws Exception {
    final long mockedUserCount = 11L;

    when(userRepository.count()).thenReturn(Mono.just(mockedUserCount));

    StepVerifier.create(userService.count()).expectNext(mockedUserCount).expectComplete().verify();

    verify(userRepository, times(1)).count();
  }

  @Test
  @DisplayName("create() must throw exception if display name already exists")
  public void create_DisplayNameAlreadyExists_ThrowsException() {
    final String displayName = "createPasswordUser_DuplicateDisplayName";

    final User mockUser = mock(User.class);

    when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.just(mockUser));

    StepVerifier.create(userService.create(displayName))
        .expectError(DuplicateUserDisplayNameException.class).verify();

    verify(userRepository, times(1)).findByDisplayName(displayName);
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
  public void create_ValidDisplayName_CreatesUser() {
    final String displayName = "TestUser";
    final int mockUserId = 651;

    when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

    when(userRepository.save(any(User.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, User.class).withId(mockUserId)));

    StepVerifier.create(userService.create(displayName)).expectNext(mockUserId).expectComplete()
        .verify();

    verify(userRepository, times(1)).findByDisplayName(displayName);
    verify(userRepository, times(1)).save(any(User.class));

    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(argument.capture());
    assertThat(argument.getValue().getDisplayName(), is(displayName));
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

    verify(userRepository, times(1)).findByDisplayName(displayName);
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

    verify(passwordAuthRepository, times(1)).findByLoginName(loginName);
    verify(userRepository, times(1)).findByDisplayName(displayName);
  }

  @Test
  public void createPasswordUser_EmptyArgument_ThrowsException() {
    final String displayName = "createPasswordUser_NullArgument";
    final String loginName = "_createPasswordUser_NullArgument_";
    final String password = "aprettyweakpassword_with_null";

    assertThrows(IllegalArgumentException.class,
        () -> userService.createPasswordUser("", loginName, password));
    assertThrows(IllegalArgumentException.class,
        () -> userService.createPasswordUser(displayName, "", password));
    assertThrows(IllegalArgumentException.class,
        () -> userService.createPasswordUser(displayName, loginName, ""));
  }

  @Test
  public void createPasswordUser_NullArgument_ThrowsException() {
    final String displayName = "createPasswordUser_NullArgument";
    final String loginName = "_createPasswordUser_NullArgument_";
    final String password = "aprettyweakpassword_with_null";

    assertThrows(NullPointerException.class,
        () -> userService.createPasswordUser(null, loginName, password));
    assertThrows(NullPointerException.class,
        () -> userService.createPasswordUser(displayName, null, password));
    assertThrows(NullPointerException.class,
        () -> userService.createPasswordUser(displayName, loginName, null));
  }

  @Test
  public void createPasswordUser_ValidData_Succeeds() throws Exception {
    final String displayName = "createPasswordUser_ValidData";
    final String loginName = "_createPasswordUser_ValidData_";

    final String hashedPassword = "a_valid_password";

    final int mockUserId = 199;

    when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.empty());
    when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

    when(userRepository.save(any(User.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, User.class).withId(mockUserId)));

    when(authRepository.save(any(UserAuth.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, UserAuth.class).withId(mockUserId)));

    when(passwordAuthRepository.save(any(PasswordAuth.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, PasswordAuth.class).withId(mockUserId)));

    StepVerifier.create(userService.createPasswordUser(displayName, loginName, hashedPassword))
        .expectNext(mockUserId).expectComplete().verify();

    verify(passwordAuthRepository, times(1)).findByLoginName(loginName);
    verify(userRepository, times(1)).findByDisplayName(displayName);

    verify(userRepository, times(1)).save(any(User.class));
    verify(authRepository, times(1)).save(any(UserAuth.class));
    verify(passwordAuthRepository, times(1)).save(any(PasswordAuth.class));

    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getId(), is(nullValue()));
    assertThat(userArgumentCaptor.getValue().getDisplayName(), is(displayName));

    ArgumentCaptor<UserAuth> userAuthArgumentCaptor = ArgumentCaptor.forClass(UserAuth.class);
    verify(authRepository, times(1)).save(userAuthArgumentCaptor.capture());
    assertThat(userAuthArgumentCaptor.getValue().getUserId(), is(mockUserId));

    ArgumentCaptor<PasswordAuth> passwordAuthArgumentCaptor =
        ArgumentCaptor.forClass(PasswordAuth.class);
    verify(passwordAuthRepository, times(1)).save(passwordAuthArgumentCaptor.capture());
    assertThat(passwordAuthArgumentCaptor.getValue().getUserId(), is(mockUserId));
    assertThat(passwordAuthArgumentCaptor.getValue().getLoginName(), is(loginName));
    assertThat(passwordAuthArgumentCaptor.getValue().getHashedPassword(), is(hashedPassword));
  }

  @Test
  public void demote_InvalidUserId_ThrowsException() {
    StepVerifier.create(userService.demote(-1)).expectError(InvalidUserIdException.class).verify();
    StepVerifier.create(userService.demote(-1000)).expectError(InvalidUserIdException.class)
        .verify();
    StepVerifier.create(userService.demote(0)).expectError(InvalidUserIdException.class).verify();
    StepVerifier.create(userService.demote(-999999)).expectError(InvalidUserIdException.class)
        .verify();
  }


  @Test
  public void demote_UserIsAdmin_Demoted() throws Exception {
    final int mockUserId = 933;
    final int mockAuthId = 46;

    final UserAuth mockAuth = mock(UserAuth.class);
    final UserAuth mockDemotedAuth = mock(UserAuth.class);

    when(authRepository.save(any(UserAuth.class))).thenAnswer(auth -> {
      if (auth.getArgument(0, UserAuth.class) == mockDemotedAuth) {
        // We are saving the admin auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class));
      } else {
        // We are saving the newly created auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class).withId(mockAuthId));
      }
    });

    when(authRepository.findByUserId(mockUserId)).thenReturn(Mono.just(mockAuth));

    when(mockAuth.withAdmin(false)).thenReturn(mockDemotedAuth);
    when(mockDemotedAuth.isAdmin()).thenReturn(false);

    StepVerifier.create(userService.demote(mockUserId)).expectComplete().verify();

    verify(userRepository, never()).findById(any(Integer.class));
    verify(userRepository, never()).save(any(User.class));
    verify(passwordAuthRepository, never()).findByUserId(any(Integer.class));

    verify(authRepository, times(1)).findByUserId(mockUserId);

    verify(mockAuth, times(1)).withAdmin(false);
    verify(authRepository, never()).save(mockAuth);
    verify(authRepository, times(1)).save(mockDemotedAuth);
  }

  @Test
  public void demote_UserIsNotAdmin_StaysNotAdmin() throws Exception {
    final int mockUserId = 933;
    final int mockAuthId = 80;

    final UserAuth mockAuth = mock(UserAuth.class);

    when(authRepository.save(any(UserAuth.class))).thenAnswer(auth -> {
      if (auth.getArgument(0, UserAuth.class) == mockAuth) {
        // We are saving the admin auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class));
      } else {
        // We are saving the newly created auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class).withId(mockAuthId));
      }
    });

    when(authRepository.findByUserId(mockUserId)).thenReturn(Mono.just(mockAuth));

    when(mockAuth.withAdmin(false)).thenReturn(mockAuth);
    when(mockAuth.isAdmin()).thenReturn(false);

    StepVerifier.create(userService.demote(mockUserId)).expectComplete().verify();

    verify(userRepository, never()).findById(any(Integer.class));
    verify(userRepository, never()).save(any(User.class));
    verify(passwordAuthRepository, never()).findByUserId(any(Integer.class));
    verify(passwordAuthRepository, never()).save(any(PasswordAuth.class));

    verify(authRepository, times(1)).save(mockAuth);

    verify(userRepository, never()).findById(any(Integer.class));
    verify(authRepository, times(1)).findByUserId(mockUserId);

    verify(mockAuth, times(1)).withAdmin(false);
  }

  @Test
  public void findById_ExistingUserId_ReturnsUserEntity() {
    final User mockUser = mock(User.class);

    final int mockUserId = 123;

    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(true));

    StepVerifier.create(userService.findById(mockUserId)).expectNext(mockUser).expectComplete()
        .verify();

    verify(userRepository, times(1)).findById(mockUserId);
    verify(authRepository, never()).findByUserId(any(Integer.class));
    verify(passwordAuthRepository, never()).findByUserId(any(Integer.class));
  }

  @Test
  public void findById_InvalidUserId_ThrowsException() {
    StepVerifier.create(Flux.just(-1, -1000, 0, -99999).next().flatMap(userService::findById))
        .expectError(InvalidUserIdException.class).verify();
  }

  @Test
  public void findById_NonExistentUserId_ReturnsEmpty() {
    final int mockUserId = 123;

    when(userRepository.findById(mockUserId)).thenReturn(Mono.empty());

    StepVerifier.create(userService.findById(mockUserId)).expectComplete().verify();

    verify(userRepository, times(1)).findById(mockUserId);
  }

  @Test
  public void findByLoginName_EmptyLoginName_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> userService.findUserIdByLoginName(""));
  }

  @Test
  public void findByLoginName_NullLoginName_ThrowsException() {
    assertThrows(NullPointerException.class, () -> userService.findUserIdByLoginName(null));
  }

  @Test
  public void findByLoginName_UserExists_ReturnsUser() {
    final String loginName = "MockUser";
    final int mockUserId = 117;

    when(userDatabaseClient.findUserIdByLoginName(loginName)).thenReturn(Mono.just(mockUserId));

    StepVerifier.create(userService.findUserIdByLoginName(loginName)).assertNext(userId -> {
      assertThat(userId, is(mockUserId));
    }).expectComplete().verify();

    verify(userDatabaseClient, times(1)).findUserIdByLoginName(loginName);
  }

  @Test
  public void findByLoginName_LoginNameDoesNotExist_ReturnsEmptyMono() {
    final String nonExistentLoginName = "MockUser";

    when(userDatabaseClient.findUserIdByLoginName(nonExistentLoginName)).thenReturn(Mono.empty());

    StepVerifier.create(userService.findUserIdByLoginName(nonExistentLoginName)).expectComplete()
        .verify();

    verify(userDatabaseClient, times(1)).findUserIdByLoginName(nonExistentLoginName);
  }

  @Test
  public void getKeyById_InvalidUserId_ThrowsException() {
    StepVerifier.create(userService.getKeyById(-1)).expectError(InvalidUserIdException.class)
        .verify();
    StepVerifier.create(userService.getKeyById(-1000)).expectError(InvalidUserIdException.class)
        .verify();
    StepVerifier.create(userService.getKeyById(0)).expectError(InvalidUserIdException.class)
        .verify();
  }

  @Test
  public void getKeyById_KeyExists_ReturnsKey() throws Exception {
    // Establish a random key
    final byte[] testRandomBytes =
        {-108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19};
    final int mockId = 17;

    // Mock a test user that has a key
    final User mockUserWithKey = mock(User.class);
    when(mockUserWithKey.getKey()).thenReturn(testRandomBytes);

    when(userRepository.existsById(mockId)).thenReturn(Mono.just(true));
    when(userRepository.findById(mockId)).thenReturn(Mono.just(mockUserWithKey));

    StepVerifier.create(userService.getKeyById(mockId)).expectNext(testRandomBytes).expectComplete()
        .verify();
    final InOrder order = inOrder(mockUserWithKey, userRepository);

    // userService should query the repository
    order.verify(userRepository, times(1)).findById(mockId);

    // and then extract the key
    order.verify(mockUserWithKey, times(1)).getKey();
  }

  @Test
  public void getKeyById_NoKeyExists_GeneratesKey() throws Exception {
    // Establish a random key
    final byte[] testRandomBytes =
        {-108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19};
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

    StepVerifier.create(userService.getKeyById(mockId)).expectNext(testRandomBytes).expectComplete()
        .verify();

    verify(userRepository, times(1)).findById(mockId);
    verify(mockUserWithoutKey, times(1)).getKey();
    verify(keyService, times(1)).generateRandomBytes(16);
    verify(mockUserWithoutKey, times(1)).withKey(testRandomBytes);
    verify(mockUserWithKey, times(1)).getKey();

    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

    verify(userRepository, times(1)).save(argument.capture());

    assertThat(argument.getValue(), is(mockUserWithKey));
    assertThat(argument.getValue().getKey(), is(testRandomBytes));
  }

  @Test
  public void setClassId_ClassIdDoesNotExist_ThrowsException() throws Exception {
    int mockUserId = 12;
    int mockClassId = 84;

    User mockUser = mock(User.class);

    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(classService.existsById(mockClassId)).thenReturn(Mono.just(false));

    StepVerifier.create(userService.setClassId(mockUserId, mockClassId))
        .expectError(ClassIdNotFoundException.class).verify();

    verify(userRepository, times(1)).findById(mockUserId);
    verify(classService, times(1)).existsById(mockClassId);
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
    final int mockUserId = 875;
    final int mockClassId = 213;

    User mockUser = mock(User.class);
    User mockUserWithClass = mock(User.class);

    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(true));
    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(classService.existsById(mockClassId)).thenReturn(Mono.just(true));

    when(mockUser.withClassId(mockClassId)).thenReturn(mockUserWithClass);
    when(userRepository.save(mockUserWithClass)).thenReturn(Mono.just(mockUserWithClass));
    when(mockUserWithClass.getClassId()).thenReturn(mockClassId);

    StepVerifier.create(userService.setClassId(mockUserId, mockClassId)).assertNext(user -> {

      assertThat(user.getClassId(), is(mockClassId));

    }).expectComplete().verify();

  }

  @Test
  public void setDisplayName_EmptyDisplayName_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> userService.setDisplayName(1, ""));
  }

  @Test
  public void setDisplayName_InvalidUserId_ThrowsException() {
    assertThrows(InvalidUserIdException.class, () -> userService.setDisplayName(-1, "displayName"));
    assertThrows(InvalidUserIdException.class, () -> userService.setDisplayName(0, "displayName"));
    assertThrows(InvalidUserIdException.class,
        () -> userService.setDisplayName(-1000, "displayName"));
  }

  @Test
  public void setDisplayName_NullDisplayName_ThrowsException() {
    assertThrows(NullPointerException.class, () -> userService.setDisplayName(1, null));
  }

  @Test
  public void setDisplayName_UserIdDoesNotExist_ReturnsUserIdNotFoundException()
      throws InvalidUserIdException {
    String newDisplayName = "newName";

    int mockUserId = 550;

    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(false));

    StepVerifier.create(userService.setDisplayName(mockUserId, newDisplayName))
        .expectError(UserIdNotFoundException.class).verify();
    verify(userRepository, times(1)).existsById(mockUserId);
  }

  @Test
  public void setDisplayName_ValidName_Succeeds() throws Exception {
    User mockUser = mock(User.class);
    String newDisplayName = "newName";

    int mockUserId = 652;

    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(true));
    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(userRepository.findByDisplayName(newDisplayName)).thenReturn(Mono.empty());

    when(mockUser.withDisplayName(newDisplayName)).thenReturn(mockUser);
    when(userRepository.save(any(User.class))).thenReturn(Mono.just(mockUser));
    when(mockUser.getDisplayName()).thenReturn(newDisplayName);

    StepVerifier.create(userService.setDisplayName(mockUserId, newDisplayName))
        .expectNextMatches(user -> user.getDisplayName().equals(newDisplayName))
        .as("Display name should change to supplied value").expectComplete().verify();

    InOrder order = inOrder(mockUser, userRepository);
    order.verify(mockUser, times(1)).withDisplayName(newDisplayName);
    order.verify(userRepository, times(1)).save(mockUser);
  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong
    Hooks.onOperatorDebug();

    // Set up userService to use our mocked repos and services
    userService = new UserService(userRepository, userDatabaseClient, authRepository,
        passwordAuthRepository, classService, keyService);
  }

}
