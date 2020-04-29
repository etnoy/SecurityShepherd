/**
 * This file is part of Security Shepherd.
 *
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.PasswordAuth;
import org.owasp.securityshepherd.user.User;
import org.owasp.securityshepherd.user.UserAuth;
import org.owasp.securityshepherd.user.UserAuthRepository;
import org.owasp.securityshepherd.user.UserRepository;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService unit test")
public class UserServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserAuthRepository userAuthRepository;

  @Mock
  private PasswordAuthRepository passwordAuthRepository;

  @Mock
  private ClassService classService;

  @Mock
  private KeyService keyService;

  @Test
  public void authenticate_EmptyPassword_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.authenticate("username", ""))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void authenticate_EmptyUsername_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.authenticate("", "password"))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void authenticate_InvalidUsername_ReturnsFalse() {
    final String mockedLoginName = "MockUser";
    final String mockedPassword = "MockPassword";

    when(passwordAuthRepository.findByLoginName(mockedLoginName)).thenReturn(Mono.empty());
    StepVerifier.create(userService.authenticate(mockedLoginName, mockedPassword)).expectNext(false)
        .expectComplete().verify();
    verify(passwordAuthRepository, times(1)).findByLoginName(mockedLoginName);
  }

  @Test
  public void authenticate_NullPassword_ReturnsNullPointerException() {
    StepVerifier.create(userService.authenticate("username", null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void authenticate_NullUsername_ReturnsNullPointerException() {
    StepVerifier.create(userService.authenticate(null, "password"))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void authenticate_ValidUsernameAndPassword_ReturnsTrue() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
    final String mockedLoginName = "MockUser";
    final String mockedPassword = "MockPassword";
    final String mockedPasswordHash = encoder.encode(mockedPassword);
    final PasswordAuth mockedPasswordAuth = mock(PasswordAuth.class);

    when(passwordAuthRepository.findByLoginName(mockedLoginName))
        .thenReturn(Mono.just(mockedPasswordAuth));
    when(mockedPasswordAuth.getHashedPassword()).thenReturn(mockedPasswordHash);
    StepVerifier.create(userService.authenticate(mockedLoginName, mockedPassword)).expectNext(true)
        .expectComplete().verify();
    verify(passwordAuthRepository, times(1)).findByLoginName(mockedLoginName);
  }

  @Test
  public void authenticate_ValidUsernameButInvalidPassword_ReturnsTrue() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
    final String mockedLoginName = "MockUser";
    final String wrongPassword = "WrongPassword";
    final String mockedPassword = "MockPassword";
    final String mockedPasswordHash = encoder.encode(mockedPassword);
    final PasswordAuth mockedPasswordAuth = mock(PasswordAuth.class);

    when(passwordAuthRepository.findByLoginName(mockedLoginName))
        .thenReturn(Mono.just(mockedPasswordAuth));
    when(mockedPasswordAuth.getHashedPassword()).thenReturn(mockedPasswordHash);
    StepVerifier.create(userService.authenticate(mockedLoginName, wrongPassword)).expectNext(false)
        .expectComplete().verify();
    verify(passwordAuthRepository, times(1)).findByLoginName(mockedLoginName);
  }

  @Test
  @DisplayName("count() should call repository and return user count")
  public void count_NoArgument_ReturnsCount() {
    final long mockedUserCount = 11L;
    when(userRepository.count()).thenReturn(Mono.just(mockedUserCount));
    StepVerifier.create(userService.count()).expectNext(mockedUserCount).expectComplete().verify();
    verify(userRepository, times(1)).count();
  }

  @Test
  @DisplayName("create() must return exception if display name already exists")
  public void create_DisplayNameAlreadyExists_ReturnsDuplicateUserDisplayNameException() {
    final String displayName = "createPasswordUser_DuplicateDisplayName";

    final User mockUser = mock(User.class);

    when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.just(mockUser));

    StepVerifier.create(userService.create(displayName))
        .expectError(DuplicateUserDisplayNameException.class).verify();

    verify(userRepository, times(1)).findByDisplayName(displayName);
  }

  @Test
  @DisplayName("create() must return exception if display name already exists")
  public void create_EmptyArgument_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.create("")).expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  public void create_NullArgument_ReturnsNullPointerException() {
    StepVerifier.create(userService.create(null)).expectError(NullPointerException.class).verify();
  }

  @Test
  public void create_ValidDisplayName_CreatesUser() {
    final String displayName = "TestUser";
    final Long mockUserId = 651L;

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
  public void createPasswordUser_DuplicateDisplayName_ReturnsDuplicateUserDisplayNameException() {
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
  public void createPasswordUser_DuplicateLoginName_ReturnsDuplicateClassNameException() {
    final String displayName = "createPasswordUser_DuplicateLoginName";
    final String loginName = "_createPasswordUser_DuplicateLoginName_";

    final String password = "a_valid_password";

    final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);

    when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.just(mockPasswordAuth));
    when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

    StepVerifier.create(userService.createPasswordUser(displayName, loginName, password))
        .expectError(DuplicateUserLoginNameException.class).verify();

    verify(passwordAuthRepository, times(1)).findByLoginName(loginName);
    verify(userRepository, times(1)).findByDisplayName(displayName);
  }

  @Test
  public void createPasswordUser_EmptyDisplayName_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.createPasswordUser("", "loginName", "passwordHash"))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void createPasswordUser_EmptyLoginName_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.createPasswordUser("displayName", "", "passwordHash"))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void createPasswordUser_EmptyPasswordHash_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.createPasswordUser("displayName", "loginName", ""))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void createPasswordUser_NullDisplayName_ReturnsNullPointerException() {
    StepVerifier.create(userService.createPasswordUser(null, "loginName", "passwordHash"))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void createPasswordUser_NullLoginName_ReturnsNullPointerException() {
    StepVerifier.create(userService.createPasswordUser("displayName", null, "passwordHash"))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void createPasswordUser_NullPasswordHash_ReturnsNullPointerException() {
    StepVerifier.create(userService.createPasswordUser("displayName", "loginName", null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void createPasswordUser_ValidData_Succeeds() {
    final String displayName = "createPasswordUser_ValidData";
    final String loginName = "_createPasswordUser_ValidData_";

    final String hashedPassword = "a_valid_password";

    final Long mockUserId = 199L;

    when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.empty());
    when(userRepository.findByDisplayName(displayName)).thenReturn(Mono.empty());

    when(userRepository.save(any(User.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, User.class).withId(mockUserId)));

    when(userAuthRepository.save(any(UserAuth.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, UserAuth.class).withId(mockUserId)));

    when(passwordAuthRepository.save(any(PasswordAuth.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, PasswordAuth.class).withId(mockUserId)));

    StepVerifier.create(userService.createPasswordUser(displayName, loginName, hashedPassword))
        .expectNext(mockUserId).expectComplete().verify();

    verify(passwordAuthRepository, times(1)).findByLoginName(loginName);
    verify(userRepository, times(1)).findByDisplayName(displayName);

    verify(userRepository, times(1)).save(any(User.class));
    verify(userAuthRepository, times(1)).save(any(UserAuth.class));
    verify(passwordAuthRepository, times(1)).save(any(PasswordAuth.class));

    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getId(), is(nullValue()));
    assertThat(userArgumentCaptor.getValue().getDisplayName(), is(displayName));

    ArgumentCaptor<UserAuth> userAuthArgumentCaptor = ArgumentCaptor.forClass(UserAuth.class);
    verify(userAuthRepository, times(1)).save(userAuthArgumentCaptor.capture());
    assertThat(userAuthArgumentCaptor.getValue().getUserId(), is(mockUserId));

    ArgumentCaptor<PasswordAuth> passwordAuthArgumentCaptor =
        ArgumentCaptor.forClass(PasswordAuth.class);
    verify(passwordAuthRepository, times(1)).save(passwordAuthArgumentCaptor.capture());
    assertThat(passwordAuthArgumentCaptor.getValue().getUserId(), is(mockUserId));
    assertThat(passwordAuthArgumentCaptor.getValue().getLoginName(), is(loginName));
    assertThat(passwordAuthArgumentCaptor.getValue().getHashedPassword(), is(hashedPassword));
  }

  @Test
  public void findPasswordAuthByLoginName_NullLoginName_ReturnsNullPointerException() {
    StepVerifier.create(userService.findPasswordAuthByLoginName(null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void deleteById_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final Long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.deleteById(userId)).expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  public void deleteById_ValidUserId_CallsRepository() {
    final long mockUserId = 358L;
    when(passwordAuthRepository.deleteByUserId(mockUserId)).thenReturn(Mono.empty());
    when(userAuthRepository.deleteByUserId(mockUserId)).thenReturn(Mono.empty());
    when(userRepository.deleteById(mockUserId)).thenReturn(Mono.empty());

    StepVerifier.create(userService.deleteById(mockUserId)).expectComplete().verify();

    // Order of deletion is important due to RDBMS constraints
    final InOrder deletionOrder =
        inOrder(passwordAuthRepository, userAuthRepository, userRepository);
    deletionOrder.verify(passwordAuthRepository, times(1)).deleteByUserId(mockUserId);
    deletionOrder.verify(userAuthRepository, times(1)).deleteByUserId(mockUserId);
    deletionOrder.verify(userRepository, times(1)).deleteById(mockUserId);
  }

  @Test
  public void demote_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final Long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.demote(userId)).expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  public void demote_UserIsAdmin_Demoted() {
    final long mockUserId = 933;
    final long mockAuthId = 46;

    final UserAuth mockAuth = mock(UserAuth.class);
    final UserAuth mockDemotedAuth = mock(UserAuth.class);

    when(userAuthRepository.save(any(UserAuth.class))).thenAnswer(auth -> {
      if (auth.getArgument(0, UserAuth.class) == mockDemotedAuth) {
        // We are saving the admin auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class));
      } else {
        // We are saving the newly created auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class).withId(mockAuthId));
      }
    });

    when(userAuthRepository.findByUserId(mockUserId)).thenReturn(Mono.just(mockAuth));

    when(mockAuth.withAdmin(false)).thenReturn(mockDemotedAuth);

    StepVerifier.create(userService.demote(mockUserId)).expectComplete().verify();

    verify(userRepository, never()).findById(any(Long.class));
    verify(userRepository, never()).save(any(User.class));
    verify(passwordAuthRepository, never()).findByUserId(any(Long.class));

    verify(userAuthRepository, times(1)).findByUserId(mockUserId);

    verify(mockAuth, times(1)).withAdmin(false);
    verify(userAuthRepository, never()).save(mockAuth);
    verify(userAuthRepository, times(1)).save(mockDemotedAuth);
  }

  @Test
  public void demote_UserIsNotAdmin_StaysNotAdmin() {
    final long mockUserId = 933;
    final long mockAuthId = 80;

    final UserAuth mockAuth = mock(UserAuth.class);

    when(userAuthRepository.save(any(UserAuth.class))).thenAnswer(auth -> {
      if (auth.getArgument(0, UserAuth.class) == mockAuth) {
        // We are saving the admin auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class));
      } else {
        // We are saving the newly created auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class).withId(mockAuthId));
      }
    });

    when(userAuthRepository.findByUserId(mockUserId)).thenReturn(Mono.just(mockAuth));

    when(mockAuth.withAdmin(false)).thenReturn(mockAuth);

    StepVerifier.create(userService.demote(mockUserId)).expectComplete().verify();

    verify(userRepository, never()).findById(any(Long.class));
    verify(userRepository, never()).save(any(User.class));
    verify(passwordAuthRepository, never()).findByUserId(any(Integer.class));
    verify(passwordAuthRepository, never()).save(any(PasswordAuth.class));

    verify(userAuthRepository, times(1)).save(mockAuth);

    verify(userRepository, never()).findById(any(Long.class));
    verify(userAuthRepository, times(1)).findByUserId(mockUserId);

    verify(mockAuth, times(1)).withAdmin(false);
  }

  @Test
  public void findAll_NoUsersExist_ReturnsEmpty() {
    when(userRepository.findAll()).thenReturn(Flux.empty());
    StepVerifier.create(userService.findAll()).expectComplete().verify();
    verify(userRepository, times(1)).findAll();
  }

  @Test
  public void findAll_UsersExist_ReturnsUsers() {
    final User mockUser1 = mock(User.class);
    final User mockUser2 = mock(User.class);
    final User mockUser3 = mock(User.class);

    when(userRepository.findAll()).thenReturn(Flux.just(mockUser1, mockUser2, mockUser3));

    StepVerifier.create(userService.findAll()).expectNext(mockUser1).expectNext(mockUser2)
        .expectNext(mockUser3).expectComplete().verify();

    verify(userRepository, times(1)).findAll();
  }

  @Test
  public void findById_ExistingUserId_ReturnsUserEntity() {
    final User mockUser = mock(User.class);

    final long mockUserId = 910;

    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));

    StepVerifier.create(userService.findById(mockUserId)).expectNext(mockUser).expectComplete()
        .verify();

    verify(userRepository, times(1)).findById(mockUserId);
  }

  @Test
  public void getAuthoritiesByUserId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.getAuthoritiesByUserId(userId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void findById_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.findById(userId)).expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  public void findById_NonExistentUserId_ReturnsEmpty() {
    final long nonExistentUserId = 248;
    when(userRepository.findById(nonExistentUserId)).thenReturn(Mono.empty());
    StepVerifier.create(userService.findById(nonExistentUserId)).expectComplete().verify();
    verify(userRepository, times(1)).findById(nonExistentUserId);
  }

  @Test
  public void findByLoginName_EmptyLoginName_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.findUserIdByLoginName(""))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void findByLoginName_LoginNameDoesNotExist_ReturnsEmptyMono() {
    final String nonExistentLoginName = "NonExistentUser";
    when(passwordAuthRepository.findByLoginName(nonExistentLoginName)).thenReturn(Mono.empty());
    StepVerifier.create(userService.findUserIdByLoginName(nonExistentLoginName)).expectComplete()
        .verify();
    verify(passwordAuthRepository, times(1)).findByLoginName(nonExistentLoginName);
  }

  @Test
  public void findByLoginName_NullLoginName_ReturnsNullPointerException() {
    StepVerifier.create(userService.findUserIdByLoginName(null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void findByLoginName_UserExists_ReturnsUser() {
    final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);
    final String loginName = "MockUser";
    final long mockUserId = 117;

    when(passwordAuthRepository.findByLoginName(loginName)).thenReturn(Mono.just(mockPasswordAuth));
    when(mockPasswordAuth.getUserId()).thenReturn(mockUserId);

    StepVerifier.create(userService.findUserIdByLoginName(loginName)).expectNext(mockUserId)
        .expectComplete().verify();
    verify(passwordAuthRepository, times(1)).findByLoginName(loginName);
  }

  @Test
  public void findDisplayNameById_InvalidUserId_ReturnsInvalidUserIdExceptio() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.findDisplayNameById(userId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void findDisplayNameById_NoUserExists_ReturnsEmpty() {
    final long mockUserId = 294;
    when(userRepository.findById(mockUserId)).thenReturn(Mono.empty());
    StepVerifier.create(userService.findDisplayNameById(mockUserId)).expectComplete().verify();
    verify(userRepository, times(1)).findById(mockUserId);
  }

  @Test
  public void findDisplayNameById_UserExists_ReturnsDisplayName() {
    final User mockUser = mock(User.class);
    final long mockUserId = 490;
    final String mockDisplayName = "mockDisplayName";

    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(mockUser.getDisplayName()).thenReturn(mockDisplayName);

    StepVerifier.create(userService.findDisplayNameById(mockUserId)).expectNext(mockDisplayName)
        .expectComplete().verify();

    verify(userRepository, times(1)).findById(mockUserId);
    verify(mockUser, times(1)).getDisplayName();
  }

  @Test
  public void findPasswordAuthByLoginName_EmptyLoginName_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.findPasswordAuthByLoginName(""))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void findPasswordAuthByLoginName_ExistingLoginName_ReturnsPasswordAuth() {
    final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);

    final String mockLoginName = "loginName";
    when(passwordAuthRepository.findByLoginName(mockLoginName))
        .thenReturn(Mono.just(mockPasswordAuth));

    StepVerifier.create(userService.findPasswordAuthByLoginName(mockLoginName))
        .expectNext(mockPasswordAuth).expectComplete().verify();

    verify(passwordAuthRepository, times(1)).findByLoginName(mockLoginName);
  }

  @Test
  public void findPasswordAuthByLoginName_NonExistentLoginName_ReturnsEmpty() {
    final String mockLoginName = "loginName";
    when(passwordAuthRepository.findByLoginName(mockLoginName)).thenReturn(Mono.empty());
    StepVerifier.create(userService.findPasswordAuthByLoginName(mockLoginName)).expectComplete()
        .verify();
    verify(passwordAuthRepository, times(1)).findByLoginName(mockLoginName);
  }

  @Test
  public void findPasswordAuthByUserId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.findPasswordAuthByUserId(userId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void findPasswordAuthByUserId_NoPasswordAuthExists_ReturnsEmpty() {
    final long mockUserId = 999;

    when(passwordAuthRepository.findByUserId(mockUserId)).thenReturn(Mono.empty());

    StepVerifier.create(userService.findPasswordAuthByUserId(mockUserId)).expectComplete().verify();

    verify(passwordAuthRepository, times(1)).findByUserId(mockUserId);
  }

  @Test
  public void findPasswordAuthByUserId_PasswordAuthExists_ReturnsPasswordAuth() {
    final PasswordAuth mockPasswordAuth = mock(PasswordAuth.class);
    final long mockUserId = 974;

    when(passwordAuthRepository.findByUserId(mockUserId)).thenReturn(Mono.just(mockPasswordAuth));

    StepVerifier.create(userService.findPasswordAuthByUserId(mockUserId))
        .expectNext(mockPasswordAuth).expectComplete().verify();

    verify(passwordAuthRepository, times(1)).findByUserId(mockUserId);
  }

  @Test
  public void findUserAuthByUserId_ExistingUserId_ReturnsUserAuth() {
    final UserAuth mockUserAuth = mock(UserAuth.class);

    final long mockUserId = 841;

    when(userAuthRepository.findByUserId(mockUserId)).thenReturn(Mono.just(mockUserAuth));

    StepVerifier.create(userService.findUserAuthByUserId(mockUserId)).expectNext(mockUserAuth)
        .expectComplete().verify();

    verify(userAuthRepository, times(1)).findByUserId(mockUserId);
    verify(userRepository, never()).findById(any(Long.class));
    verify(passwordAuthRepository, never()).findByUserId(any(Integer.class));
  }

  @Test
  public void findUserAuthByUserId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.findUserAuthByUserId(userId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void findUserAuthByUserId_NonExistentUserId_ReturnsEmpty() {
    final long nonExistentUserId = 547;
    when(userAuthRepository.findByUserId(nonExistentUserId)).thenReturn(Mono.empty());
    StepVerifier.create(userService.findUserAuthByUserId(nonExistentUserId)).expectComplete()
        .verify();
    verify(userAuthRepository, times(1)).findByUserId(nonExistentUserId);
  }

  @Test
  public void getAuthoritiesByUserId_UserIsAdmin_ReturnsAdminAuthority() {
    final long mockedUserId = 158L;
    final UserAuth mockedUserAuth = mock(UserAuth.class);
    when(userAuthRepository.findByUserId(mockedUserId)).thenReturn(Mono.just(mockedUserAuth));
    when(mockedUserAuth.isAdmin()).thenReturn(true);
    StepVerifier.create(userService.getAuthoritiesByUserId(mockedUserId))
        .expectNext(new SimpleGrantedAuthority("ROLE_ADMIN"))
        .expectNext(new SimpleGrantedAuthority("ROLE_USER")).expectComplete().verify();
    verify(userAuthRepository, times(1)).findByUserId(mockedUserId);
  }

  @Test
  public void getAuthoritiesByUserId_UserIsNotAdmin_ReturnsUserAuthority() {
    final long mockedUserId = 158L;
    final UserAuth mockedUserAuth = mock(UserAuth.class);
    when(userAuthRepository.findByUserId(mockedUserId)).thenReturn(Mono.just(mockedUserAuth));
    when(mockedUserAuth.isAdmin()).thenReturn(false);
    StepVerifier.create(userService.getAuthoritiesByUserId(mockedUserId))
        .expectNext(new SimpleGrantedAuthority("ROLE_USER")).expectComplete().verify();
    verify(userAuthRepository, times(1)).findByUserId(mockedUserId);
  }

  @Test
  public void getKeyById_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.findKeyById(userId)).expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  public void getKeyById_KeyExists_ReturnsKey() {
    // Establish a random key
    final byte[] testRandomBytes =
        {-108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19};
    final long mockUserId = 17;

    // Mock a test user that has a key
    final User mockUserWithKey = mock(User.class);
    when(mockUserWithKey.getKey()).thenReturn(testRandomBytes);

    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(true));
    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUserWithKey));

    StepVerifier.create(userService.findKeyById(mockUserId)).expectNext(testRandomBytes)
        .expectComplete().verify();

    final InOrder order = inOrder(mockUserWithKey, userRepository);
    // userService should query the repository
    order.verify(userRepository, times(1)).findById(mockUserId);
    // and then extract the key
    order.verify(mockUserWithKey, times(1)).getKey();
  }

  @Test
  public void getKeyById_NoKeyExists_GeneratesKey() {
    // Establish a random key
    final byte[] testRandomBytes =
        {-108, 101, -7, -36, 17, -26, -24, 0, -32, -117, 75, -127, 22, 62, 9, 19};
    final long mockUserId = 19;

    // This user does not have a key
    final User mockUserWithoutKey = mock(User.class);
    when(mockUserWithoutKey.getKey()).thenReturn(null);

    // When that user is given a key, return an entity that has the key
    final User mockUserWithKey = mock(User.class);
    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUserWithoutKey));
    when(mockUserWithoutKey.getKey()).thenReturn(null);

    when(mockUserWithoutKey.withKey(testRandomBytes)).thenReturn(mockUserWithKey);

    // Set up the mock repository
    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(true));
    when(userRepository.save(mockUserWithKey)).thenReturn(Mono.just(mockUserWithKey));

    // Set up the mock key service
    when(keyService.generateRandomBytes(16)).thenReturn(Mono.just(testRandomBytes));

    when(mockUserWithKey.getKey()).thenReturn(testRandomBytes);

    StepVerifier.create(userService.findKeyById(mockUserId)).expectNext(testRandomBytes)
        .expectComplete().verify();

    verify(userRepository, times(1)).findById(mockUserId);
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
  public void promote_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.promote(userId)).expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  public void promote_UserIsAdmin_StaysAdmin() {
    final long mockUserId = 899;
    final long mockAuthId = 551;

    final UserAuth mockAuth = mock(UserAuth.class);

    when(userAuthRepository.save(any(UserAuth.class))).thenAnswer(auth -> {
      if (auth.getArgument(0, UserAuth.class).equals(mockAuth)) {
        // We are saving the admin auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class));
      } else {
        // We are saving the newly created auth to db
        return Mono.just(auth.getArgument(0, UserAuth.class).withId(mockAuthId));
      }
    });

    when(userAuthRepository.findByUserId(mockUserId)).thenReturn(Mono.just(mockAuth));

    when(mockAuth.withAdmin(true)).thenReturn(mockAuth);

    StepVerifier.create(userService.promote(mockUserId)).expectComplete().verify();

    verify(userRepository, never()).findById(any(Long.class));
    verify(userRepository, never()).save(any(User.class));
    verify(passwordAuthRepository, never()).findByUserId(any(Integer.class));

    verify(userAuthRepository, times(1)).findByUserId(mockUserId);

    verify(mockAuth, times(1)).withAdmin(true);
    verify(userAuthRepository, times(1)).save(mockAuth);
  }

  @Test
  public void setClassId_InvalidClassId_ReturnsInvalidClassIdException() {
    for (final long classId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.setClassId(10L, classId))
          .expectError(InvalidClassIdException.class).verify();
    }
  }

  @Test
  public void setClassId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.setClassId(userId, 61))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void setClassId_NonExistentClassId_ReturnsClassIdNotFoundException() {
    final long mockUserId = 16;
    final long mockClassId = 638;

    User mockUser = mock(User.class);

    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(classService.existsById(mockClassId)).thenReturn(Mono.just(false));

    StepVerifier.create(userService.setClassId(mockUserId, mockClassId))
        .expectError(ClassIdNotFoundException.class).verify();

    verify(userRepository, times(1)).findById(mockUserId);
    verify(classService, times(1)).existsById(mockClassId);
  }

  @Test
  public void setClassId_ValidClassId_Succeeds() {
    final long mockUserId = 875;
    final long mockClassId = 213;

    final User mockUser = mock(User.class);
    final User mockUserWithClass = mock(User.class);

    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(classService.existsById(mockClassId)).thenReturn(Mono.just(true));

    when(mockUser.withClassId(mockClassId)).thenReturn(mockUserWithClass);
    when(userRepository.save(mockUserWithClass)).thenReturn(Mono.just(mockUserWithClass));
    when(mockUserWithClass.getClassId()).thenReturn(mockClassId);

    StepVerifier.create(userService.setClassId(mockUserId, mockClassId))
        .expectNextMatches(user -> user.getClassId() == mockClassId).expectComplete().verify();

    verify(userRepository, times(1)).findById(mockUserId);
    verify(classService, times(1)).existsById(mockClassId);

    verify(mockUser, times(1)).withClassId(mockClassId);
    verify(userRepository, times(1)).save(mockUserWithClass);
    verify(mockUserWithClass, times(1)).getClassId();
  }

  @Test
  public void setDisplayName_EmptyDisplayName_ReturnsIllegalArgumentException() {
    StepVerifier.create(userService.setDisplayName(725, ""))
        .expectError(IllegalArgumentException.class).verify();
  }

  @Test
  public void setDisplayName_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(userService.setDisplayName(userId, "displayName"))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void setDisplayName_NullDisplayName_ReturnsNullPointerException() {
    StepVerifier.create(userService.setDisplayName(480, null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void setDisplayName_UserIdDoesNotExist_ReturnsUserIdNotFoundException() {
    final String newDisplayName = "newName";

    final long mockUserId = 550;

    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(false));

    StepVerifier.create(userService.setDisplayName(mockUserId, newDisplayName))
        .expectError(UserIdNotFoundException.class).verify();
    verify(userRepository, times(1)).existsById(mockUserId);
  }

  @Test
  public void setDisplayName_ValidArguments_DisplayNameIsSet() {
    User mockUser = mock(User.class);
    String newDisplayName = "newDisplayName";

    final long mockUserId = 652;

    when(userRepository.existsById(mockUserId)).thenReturn(Mono.just(true));
    when(userRepository.findById(mockUserId)).thenReturn(Mono.just(mockUser));
    when(userRepository.findByDisplayName(newDisplayName)).thenReturn(Mono.empty());

    when(mockUser.withDisplayName(newDisplayName)).thenReturn(mockUser);
    when(userRepository.save(any(User.class))).thenReturn(Mono.just(mockUser));
    when(mockUser.getDisplayName()).thenReturn(newDisplayName);

    StepVerifier.create(userService.setDisplayName(mockUserId, newDisplayName))
        .expectNextMatches(user -> user.getDisplayName().equals(newDisplayName))
        .as("Display name should change to supplied value").expectComplete().verify();

    verify(userRepository, times(1)).existsById(mockUserId);
    verify(userRepository, times(1)).findById(mockUserId);
    verify(userRepository, times(1)).findByDisplayName(newDisplayName);

    verify(mockUser, times(1)).withDisplayName(newDisplayName);
    verify(userRepository, times(1)).save(any(User.class));
    verify(mockUser, times(1)).getDisplayName();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    userService = new UserService(userRepository, userAuthRepository, passwordAuthRepository,
        classService, keyService);
  }
}
