package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.CryptoService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("ModuleService unit test")
public class ModuleServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ModuleService moduleService;

  private UserService userService = Mockito.mock(UserService.class);

  private ModuleRepository moduleRepository = Mockito.mock(ModuleRepository.class);

  private ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

  private KeyService keyService = Mockito.mock(KeyService.class);

  private CryptoService cryptoService = Mockito.mock(CryptoService.class);
  
  private SubmissionRepository submissionRepository = Mockito.mock(SubmissionRepository.class);

  @Test
  public void count_NoArgument_ReturnsCount() {
    final long mockedModuleCount = 75L;

    when(moduleRepository.count()).thenReturn(Mono.just(mockedModuleCount));

    StepVerifier.create(moduleService.count()).expectNext(mockedModuleCount).expectComplete()
        .verify();
    verify(moduleRepository, times(1)).count();
  }

  @Test
  public void create_DuplicateName_ReturnsDuplicateModuleNameException() {
    final String name = "TestModule";
    final String url = "test-module";

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findByName(name)).thenReturn(Mono.just(mockModule));

    StepVerifier.create(moduleService.create(name, url))
        .expectError(DuplicateModuleNameException.class).verify();

    verify(moduleRepository, times(1)).findByName(name);
  }

  @Test
  public void create_EmptyName_ReturnsIllegalArgumentException() {
    StepVerifier.create(moduleService.create("", "url")).expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  public void create_NullName_ReturnsNullPointerException() {
    StepVerifier.create(moduleService.create(null, "url")).expectError(NullPointerException.class)
        .verify();
  }

  @Test
  public void create_ValidData_Succeeds() {
    final String name = "TestModule";
    final String url = "test-module";

    final long mockModuleId = 390;

    when(moduleRepository.save(any(Module.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, Module.class).withId(mockModuleId)));

    when(moduleRepository.findByName(name)).thenReturn(Mono.empty());

    StepVerifier.create(moduleService.create(name, url)).assertNext(module -> {
      assertThat(module.getName(), is(name));
    }).expectComplete().verify();

    ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);

    verify(moduleRepository, times(1)).findByName(name);
    verify(moduleRepository, times(1)).save(argument.capture());
    verify(moduleRepository, times(1)).save(any(Module.class));
    assertThat(argument.getValue().getName(), is(name));
  }

  @Test
  public void findAll_ModulesExist_ReturnsModules() {
    final Module mockModule1 = mock(Module.class);
    final Module mockModule2 = mock(Module.class);
    final Module mockModule3 = mock(Module.class);

    when(moduleRepository.findAll()).thenReturn(Flux.just(mockModule1, mockModule2, mockModule3));

    StepVerifier.create(moduleService.findAll()).expectNext(mockModule1).expectNext(mockModule2)
        .expectNext(mockModule3).expectComplete().verify();

    verify(moduleRepository, times(1)).findAll();
  }

  @Test
  public void findAll_NoModulesExist_ReturnsEmpty() {
    when(moduleRepository.findAll()).thenReturn(Flux.empty());
    StepVerifier.create(moduleService.findAll()).expectComplete().verify();
    verify(moduleRepository, times(1)).findAll();
  }

  @Test
  public void findById_InvalidModuleId_ReturnsInvalidModuleIdException() {
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(moduleService.findById(moduleId))
          .expectError(InvalidModuleIdException.class).verify();
    }
  }

  @Test
  public void findById_NonExistentModuleId_ReturnsEmpty() {
    final long mockModuleId = 286;
    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.empty());
    StepVerifier.create(moduleService.findById(mockModuleId)).expectComplete().verify();
    verify(moduleRepository, times(1)).findById(mockModuleId);
  }

  @Test
  public void findNameById_ExistingModuleId_ReturnsUserEntity() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "MockName";
    final long mockModuleId = 21;

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(mockModule.getName()).thenReturn(mockModuleName);

    StepVerifier.create(moduleService.findNameById(mockModuleId)).expectNext(mockModuleName)
        .expectComplete().verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);
    verify(mockModule, times(1)).getName();
  }

  @Test
  public void findNameById_InvalidModuleId_ReturnsInvalidModuleIdException() {
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(moduleService.findNameById(moduleId))
          .expectError(InvalidModuleIdException.class).verify();
    }
  }

  @Test
  public void findNameById_NonExistentModuleId_ReturnsEmpty() {
    final long nonExistentModuleId = 248;
    when(moduleRepository.findById(nonExistentModuleId)).thenReturn(Mono.empty());
    StepVerifier.create(moduleService.findNameById(nonExistentModuleId)).expectComplete().verify();
    verify(moduleRepository, times(1)).findById(nonExistentModuleId);
  }

  @Test
  public void getDynamicFlag_FlagIsExact_ReturnsInvalidFlagStateException() {
    final Module mockModule = mock(Module.class);

    final long mockModuleId = 18;
    final long mockUserId = 7;

    final byte[] mockedUserKey =
        {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19};
    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};
    final byte[] mockedTotalKey = {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62,
        9, 19, -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};
    final byte[] mockedHmacOutput =
        {-128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19};

    final String mockedBaseFlag = "ZrLBRsS0QfL5TDz5";

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
    when(mockModule.getFlag()).thenReturn(mockedBaseFlag);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.isFlagEnabled()).thenReturn(true);

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, mockedBaseFlag.getBytes()))
        .thenReturn(Mono.just(mockedHmacOutput));
    when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn("thisistheoutputtedflag");

    StepVerifier.create(moduleService.getDynamicFlag(mockUserId, mockModuleId))
        .expectError(InvalidFlagStateException.class).verify();
  }

  @Test
  public void getDynamicFlag_FlagIsSet_ReturnsFlag() {
    final Module mockModule = mock(Module.class);

    final long mockModuleId = 76;
    final long mockUserId = 785;

    final byte[] mockedUserKey =
        {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19};
    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};
    final byte[] mockedTotalKey = {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62,
        9, 19, -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};
    final byte[] mockedHmacOutput =
        {-128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19};

    final String mockedBaseFlag = "ZrLBRsS0QfL5TDz5";
    final String correctFlag = "thisistheoutputtedflag";

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
    when(mockModule.getFlag()).thenReturn(mockedBaseFlag);
    when(mockModule.isFlagEnabled()).thenReturn(true);

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, mockedBaseFlag.getBytes()))
        .thenReturn(Mono.just(mockedHmacOutput));
    when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn(correctFlag);
    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(correctFlag);

    StepVerifier.create(moduleService.getDynamicFlag(mockUserId, mockModuleId))
        .expectNext(correctFlag).expectComplete().verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);

    verify(userService, times(1)).findKeyById(mockUserId);
    verify(configurationService, times(1)).getServerKey();
    verify(mockModule, times(1)).getFlag();
    verify(mockModule, times(1)).isFlagEnabled();

    verify(configurationService, times(1)).getServerKey();

    verify(cryptoService, times(1)).hmac(mockedTotalKey, mockedBaseFlag.getBytes());
  }

  @Test
  public void getDynamicFlag_FlagNotEnabled_ReturnsInvalidFlagStateException() {
    final long mockModuleId = 440;
    final long mockUserId = 332;

    final Module mockModule = mock(Module.class);

    final byte[] mockedUserKey =
        {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19};
    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};

    when(mockModule.isFlagEnabled()).thenReturn(true);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(moduleRepository.findById(mockUserId)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(mockModule.isFlagEnabled()).thenReturn(false);

    StepVerifier.create(moduleService.getDynamicFlag(mockUserId, mockModuleId))
        .expectError(InvalidFlagStateException.class).verify();
  }

  @Test
  public void getDynamicFlag_NegativeModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.getDynamicFlag(768, -1))
        .expectError(InvalidModuleIdException.class).verify();
    StepVerifier.create(moduleService.getDynamicFlag(768, -1000))
        .expectError(InvalidModuleIdException.class).verify();
  }

  @Test
  public void getDynamicFlag_NegativeUserId_ReturnsInvalidUserIdException() {
    StepVerifier.create(moduleService.getDynamicFlag(-1, 302))
        .expectError(InvalidUserIdException.class).verify();
    StepVerifier.create(moduleService.getDynamicFlag(-1000, 302))
        .expectError(InvalidUserIdException.class).verify();
  }

  @Test
  public void getDynamicFlag_ZeroModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.getDynamicFlag(267, 0))
        .expectError(InvalidModuleIdException.class).verify();
  }

  @Test
  public void getDynamicFlag_ZeroUserId_ReturnsInvalidUserIdException() {
    StepVerifier.create(moduleService.getDynamicFlag(0, 186))
        .expectError(InvalidUserIdException.class).verify();
  }

  @Test
  public void setDynamicFlag_FlagPreviouslySet_ReturnPreviousFlag() {
    final String newFlag = "uVR6jeaKqtMD6CPg";

    final Module mockModuleWithoutDynamicFlag = mock(Module.class);
    final Module mockModuleWithoutExactFlag = mock(Module.class);
    final Module mockModuleWithExactFlag = mock(Module.class);
    final Module mockModuleWithDynamicFlag = mock(Module.class);

    final long mockModuleId = 517;

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModuleWithoutExactFlag));

    when(mockModuleWithoutExactFlag.withFlagEnabled(true)).thenReturn(mockModuleWithExactFlag);
    when(mockModuleWithExactFlag.withFlagExact(false)).thenReturn(mockModuleWithDynamicFlag);
    when(mockModuleWithoutExactFlag.withFlagExact(false)).thenReturn(mockModuleWithoutDynamicFlag);
    when(mockModuleWithoutDynamicFlag.withFlagEnabled(true)).thenReturn(mockModuleWithDynamicFlag);

    when(mockModuleWithDynamicFlag.isFlagEnabled()).thenReturn(true);
    when(mockModuleWithDynamicFlag.isFlagExact()).thenReturn(false);
    when(mockModuleWithDynamicFlag.getFlag()).thenReturn(newFlag);

    when(moduleRepository.save(mockModuleWithDynamicFlag))
        .thenReturn(Mono.just(mockModuleWithDynamicFlag));

    StepVerifier.create(moduleService.setDynamicFlag(mockModuleId)).assertNext(module -> {
      assertThat(module.getFlag(), is(newFlag));
    }).expectComplete().verify();

    verify(moduleRepository, times(1)).save(any(Module.class));
    verify(keyService, never()).generateRandomString(any(Integer.class));

    ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);
    verify(moduleRepository, times(1)).save(argument.capture());
    assertThat(argument.getValue().getFlag(), is(newFlag));
  }

  @Test
  public void setDynamicFlag_NegativeModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.setDynamicFlag(-1))
        .expectError(InvalidModuleIdException.class).verify();
    StepVerifier.create(moduleService.setDynamicFlag(-1000))
        .expectError(InvalidModuleIdException.class).verify();
  }

  @Test
  public void setDynamicFlag_NoPreviousFlag_GeneratesNewFlag() {
    final String newFlag = "uVR6jeaKqtMD6CPg";

    final Module mockModuleWithoutDynamicFlag = mock(Module.class);
    final Module mockModuleWithoutExactFlag = mock(Module.class);
    final Module mockModuleWithExactFlag = mock(Module.class);
    final Module mockModuleWithDynamicFlag = mock(Module.class);
    final Module mockModuleWithFlag = mock(Module.class);

    final long mockModuleId = 134;

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModuleWithoutExactFlag));

    when(mockModuleWithoutExactFlag.withFlagEnabled(true)).thenReturn(mockModuleWithExactFlag);
    when(mockModuleWithExactFlag.withFlagExact(false)).thenReturn(mockModuleWithDynamicFlag);
    when(mockModuleWithoutExactFlag.withFlagExact(false)).thenReturn(mockModuleWithoutDynamicFlag);
    when(mockModuleWithoutDynamicFlag.withFlagEnabled(true)).thenReturn(mockModuleWithDynamicFlag);

    when(keyService.generateRandomString(16)).thenReturn(Mono.just(newFlag));
    when(mockModuleWithDynamicFlag.withFlag(newFlag)).thenReturn(mockModuleWithFlag);
    when(mockModuleWithFlag.isFlagEnabled()).thenReturn(true);
    when(mockModuleWithFlag.isFlagExact()).thenReturn(false);
    when(mockModuleWithFlag.getFlag()).thenReturn(newFlag);

    when(moduleRepository.save(mockModuleWithFlag)).thenReturn(Mono.just(mockModuleWithFlag));

    StepVerifier.create(moduleService.setDynamicFlag(mockModuleId)).assertNext(module -> {
      assertThat(module.isFlagEnabled(), is(true));
      assertThat(module.isFlagExact(), is(false));
      assertThat(module.getFlag(), is(newFlag));
    }).expectComplete().verify();

    verify(mockModuleWithoutExactFlag, atMost(1)).withFlagEnabled(true);
    verify(mockModuleWithExactFlag, atMost(1)).withFlagExact(false);
    verify(mockModuleWithoutExactFlag, atMost(1)).withFlagExact(false);
    verify(mockModuleWithoutDynamicFlag, atMost(1)).withFlagEnabled(true);

    verify(keyService, times(1)).generateRandomString(16);
    verify(mockModuleWithDynamicFlag, times(1)).withFlag(newFlag);

    verify(moduleRepository, times(1)).save(any(Module.class));
  }

  @Test
  public void setDynamicFlag_ZeroModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.setDynamicFlag(0)).expectError(InvalidModuleIdException.class)
        .verify();
  }

  @Test
  public void setExactFlag_EmptyExactFlag_ReturnsInvalidFlagException() {
    StepVerifier.create(moduleService.setExactFlag(1, "")).expectError(InvalidFlagException.class)
        .verify();
  }

  @Test
  public void setExactFlag_InvalidModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.setExactFlag(0, "flag"))
        .expectError(InvalidModuleIdException.class).verify();
    StepVerifier.create(moduleService.setExactFlag(-1, "flag"))
        .expectError(InvalidModuleIdException.class).verify();
    StepVerifier.create(moduleService.setExactFlag(-9999, "flag"))
        .expectError(InvalidModuleIdException.class).verify();
  }

  @Test
  public void setExactFlag_NullExactFlag_ReturnsInvalidFlagException() {
    StepVerifier.create(moduleService.setExactFlag(1, null)).expectError(InvalidFlagException.class)
        .verify();
  }

  @Test
  public void setExactFlag_ValidFlag_SetsFlagToExact() {
    final String exactFlag = "setExactFlag_ValidFlag_flag";

    final Module mockModuleWithoutFlag = mock(Module.class);
    final Module mockModuleWithFlagEnabled = mock(Module.class);
    final Module mockModuleWithExactFlagEnabled = mock(Module.class);
    final Module mockModuleWithExactFlagEnabledAndSet = mock(Module.class);

    final long mockModuleId = 239;

    when(mockModuleWithoutFlag.getId()).thenReturn(mockModuleId);

    when(mockModuleWithoutFlag.withFlagEnabled(true)).thenReturn(mockModuleWithFlagEnabled);
    when(mockModuleWithFlagEnabled.withFlagExact(true)).thenReturn(mockModuleWithExactFlagEnabled);
    when(mockModuleWithExactFlagEnabled.withFlag(exactFlag))
        .thenReturn(mockModuleWithExactFlagEnabledAndSet);

    when(mockModuleWithExactFlagEnabledAndSet.isFlagEnabled()).thenReturn(true);
    when(mockModuleWithExactFlagEnabledAndSet.isFlagExact()).thenReturn(true);
    when(mockModuleWithExactFlagEnabledAndSet.getFlag()).thenReturn(exactFlag);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModuleWithoutFlag));
    when(moduleRepository.save(mockModuleWithExactFlagEnabledAndSet))
        .thenReturn(Mono.just(mockModuleWithExactFlagEnabledAndSet));

    StepVerifier.create(moduleService.setExactFlag(mockModuleId, exactFlag)).assertNext(module -> {
      assertThat(module.isFlagEnabled(), is(true));
      assertThat(module.isFlagExact(), is(true));
      assertThat(module.getFlag(), is(exactFlag));
    }).expectComplete().verify();

    ArgumentCaptor<Long> findArgument = ArgumentCaptor.forClass(Long.class);
    verify(moduleRepository, times(1)).findById(findArgument.capture());
    assertThat(findArgument.getValue(), is(mockModuleId));

    ArgumentCaptor<Module> saveArgument = ArgumentCaptor.forClass(Module.class);
    verify(moduleRepository, times(1)).save(saveArgument.capture());
    assertThat(saveArgument.getValue().getFlag(), is(exactFlag));
  }

  @Test
  public void setName_EmptyName_ReturnsIllegalArgumentException() {
    StepVerifier.create(moduleService.setName(847L, "")).expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  public void setName_InvalidModuleId_ReturnsInvalidModuleIdException() {

    // TODO: make this a list
    StepVerifier.create(moduleService.setName(-1L, "name"))
        .expectError(InvalidModuleIdException.class).verify();

    StepVerifier.create(moduleService.setName(-1000L, "name"))
        .expectError(InvalidModuleIdException.class).verify();

    StepVerifier.create(moduleService.setName(0L, "name"))
        .expectError(InvalidModuleIdException.class).verify();
  }

  @Test
  public void setName_NullName_ReturnsNullPointerException() {
    StepVerifier.create(moduleService.setName(204L, null)).expectError(NullPointerException.class)
        .verify();
  }

  @Test
  public void setName_ValidName_Succeeds() {
    Module mockModule = mock(Module.class);
    String newName = "newName";

    final long mockModuleId = 30;

    when(moduleRepository.existsById(mockModuleId)).thenReturn(Mono.just(true));
    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(moduleRepository.findByName(newName)).thenReturn(Mono.empty());

    when(mockModule.withName(newName)).thenReturn(mockModule);
    when(moduleRepository.save(any(Module.class))).thenReturn(Mono.just(mockModule));
    when(mockModule.getName()).thenReturn(newName);

    StepVerifier.create(moduleService.setName(mockModuleId, newName))
        .assertNext(module -> assertThat(module.getName(), is(newName))).expectComplete().verify();

    InOrder order = inOrder(mockModule, moduleRepository);

    order.verify(mockModule, times(1)).withName(newName);
    order.verify(moduleRepository, times(1)).save(mockModule);
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    moduleService = new ModuleService(moduleRepository, userService, configurationService,
        keyService, cryptoService, submissionRepository);
  }

  @Test
  public void verifyFlag_CorrectDynamicFlag_ReturnsTrue() {
    final Module mockModule = mock(Module.class);

    final long mockUserId = 158;
    final long mockModuleId = 184;
    final String baseFlag = "baseFlag";
    final String validFlag = "thisisavalidflag";

    final byte[] mockedUserKey =
        {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19};
    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19};
    final byte[] mockedHmacOutput =
        {-128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19};

    final byte[] mockedTotalKey = {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62,
        9, 19, -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19};

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(false);
    when(mockModule.getFlag()).thenReturn(baseFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, baseFlag.getBytes()))
        .thenReturn(Mono.just(mockedHmacOutput));
    when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn(validFlag);

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(validFlag);

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, validFlag))
        .expectNext(true).expectComplete().verify();

    verify(moduleRepository, atLeast(1)).findById(mockModuleId);
    verify(mockModule, atLeast(1)).isFlagEnabled();
    verify(mockModule, atLeast(1)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
    verify(mockModule, never()).getId();
    verify(configurationService, atLeast(1)).getServerKey();
    verify(cryptoService, atLeast(1)).hmac(mockedTotalKey, baseFlag.getBytes());
    verify(userService, atLeast(1)).findKeyById(mockUserId);
  }

  @Test
  public void verifyFlag_CorrectExactFlag_ReturnsTrue() {
    final long mockUserId = 225;
    final long mockModuleId = 201;
    final String validExactFlag = "validFlag";

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, validExactFlag))
        .expectNext(true).expectComplete().verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);

    verify(mockModule, times(2)).isFlagEnabled();
    verify(mockModule, times(2)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
  }

  @Test
  public void verifyFlag_CorrectLowerCaseFlag_ReturnsTrue() {
    final long mockUserId = 594;
    final long mockModuleId = 769;
    final String validExactFlag = "validFlagWithUPPERCASEandlowercase";

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    StepVerifier
        .create(moduleService.verifyFlag(mockUserId, mockModuleId, validExactFlag.toLowerCase()))
        .expectNext(true).expectComplete().verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);

    verify(mockModule, times(2)).isFlagEnabled();
    verify(mockModule, times(2)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
  }


  @Test
  public void verifyFlag_CorrectUpperCaseFlag_ReturnsTrue() {
    final long mockUserId = 594;
    final long mockModuleId = 769;
    final String validExactFlag = "validFlagWithUPPERCASEandlowercase";

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    StepVerifier
        .create(moduleService.verifyFlag(mockUserId, mockModuleId, validExactFlag.toUpperCase()))
        .expectNext(true).expectComplete().verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);

    verify(mockModule, times(2)).isFlagEnabled();
    verify(mockModule, times(2)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
  }

  @Test
  public void verifyFlag_EmptyDynamicFlag_ReturnsFalse() {
    final Module mockModule = mock(Module.class);

    final long mockUserId = 193;
    final long mockModuleId = 34;
    final String validFlag = "validFlag";

    final byte[] mockedUserKey =
        {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19};
    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19};
    final byte[] mockedHmacOutput =
        {-128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19};

    final byte[] mockedTotalKey = {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62,
        9, 19, -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19};

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(false);
    when(mockModule.getFlag()).thenReturn(validFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, validFlag.getBytes()))
        .thenReturn(Mono.just(mockedHmacOutput));
    when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn("thisistheoutputtedflag");

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(validFlag);

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, ""))
        // We expect this to return false
        .expectNext(false)
        //
        .expectComplete().verify();

    verify(moduleRepository, atLeast(1)).findById(mockModuleId);

    // TODO: this is too many interactions, why 4?
    verify(mockModule, times(4)).isFlagEnabled();
    verify(mockModule, times(4)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
    verify(mockModule, never()).getId();
    verify(configurationService, atLeast(1)).getServerKey();
    verify(cryptoService, times(2)).hmac(mockedTotalKey, validFlag.getBytes());
    verify(userService, times(2)).findKeyById(mockUserId);
    verify(keyService, times(2)).byteFlagToString(mockedHmacOutput);
  }

  @Test
  public void verifyFlag_EmptyExactFlag_ReturnsFalse() {
    final long mockUserId = 709;
    final long mockModuleId = 677;
    final String validExactFlag = "validFlag";

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, "")).expectNext(false)
        .expectComplete().verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);

    verify(mockModule, times(2)).isFlagEnabled();
    verify(mockModule, times(2)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
  }

  @Test
  public void verifyFlag_FlagNotEnabled_ReturnsInvalidFlagStateException() {
    final long mockUserId = 515;
    final long mockModuleId = 161;

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagEnabled()).thenReturn(false);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, "flag"))
        .expectError(InvalidFlagStateException.class).verify();
  }

  @Test
  public void verifyFlag_NullDynamicFlag_ReturnsFalse() {
    final long mockUserId = 756;
    final long mockModuleId = 543;

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void verifyFlag_NullExactFlag_ReturnsFalse() {
    final long mockUserId = 487;
    final long mockModuleId = 941;

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void verifyFlag_WrongDynamicFlag_ReturnsFalse() {
    final Module mockModule = mock(Module.class);

    final long mockUserId = 193;
    final long mockModuleId = 34;
    final String validFlag = "validFlag";

    final byte[] mockedUserKey =
        {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19};
    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19};
    final byte[] mockedHmacOutput =
        {-128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19};

    final byte[] mockedTotalKey = {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62,
        9, 19, -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19};

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(false);
    when(mockModule.getFlag()).thenReturn(validFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, validFlag.getBytes()))
        .thenReturn(Mono.just(mockedHmacOutput));
    when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn("thisistheoutputtedflag");

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(validFlag);

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, "invalidFlag"))
        //
        .expectNext(false)
        //
        .expectComplete().verify();

    verify(moduleRepository, atLeast(1)).findById(mockModuleId);
    verify(mockModule, atLeast(1)).isFlagEnabled();
    verify(mockModule, atLeast(1)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
    verify(mockModule, never()).getId();
    verify(configurationService, atLeast(1)).getServerKey();
    verify(cryptoService, atLeast(1)).hmac(mockedTotalKey, validFlag.getBytes());
    verify(userService, atLeast(1)).findKeyById(mockUserId);
  }

  @Test
  public void verifyFlag_WrongExactFlag_ReturnsFalse() {
    final long mockUserId = 709;
    final long mockModuleId = 677;
    final String validExactFlag = "validFlag";

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, "invalidFlag"))
        .expectNext(false).expectComplete().verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);

    verify(mockModule, times(2)).isFlagEnabled();
    verify(mockModule, times(2)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
  }
}
