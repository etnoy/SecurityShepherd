package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.Module;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.CryptoService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ModuleServiceTest {

	private ModuleService moduleService;

	@Mock
	private UserService userService;

	@Mock
	private ModuleRepository moduleRepository;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private KeyService keyService;

	@Mock
	private CryptoService cryptoService;

	@Test
	public void count_FiniteNumberOfModules_ReturnsCount() throws Exception {

		final long mockedModuleCount = 75L;

		when(moduleRepository.count()).thenReturn(Mono.just(mockedModuleCount));

		StepVerifier.create(moduleService.count()).assertNext(count -> {

			assertThat(count, is(mockedModuleCount));
			verify(moduleRepository, times(1)).count();

		}).expectComplete().verify();

	}

	@Test
	public void create_DuplicateName_ThrowsException() {

		final String name = "TestModule";
		final Module mockModule = mock(Module.class);

		when(moduleRepository.findByName(name)).thenReturn(Mono.just(mockModule));

		StepVerifier.create(moduleService.create(name)).expectError(DuplicateModuleNameException.class).verify();

	}

	@Test
	public void create_EmptyArgument_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> moduleService.create(""));

	}

	@Test
	public void create_NullArgument_ThrowsException() {

		assertThrows(NullPointerException.class, () -> moduleService.create(null));

	}

	@Test
	public void create_ValidData_Succeeds() {

		final String name = "TestModule";
		final int mockModuleId = 390;

		when(moduleRepository.save(any(Module.class)))
				.thenAnswer(user -> Mono.just(user.getArgument(0, Module.class).withId(mockModuleId)));

		when(moduleRepository.findByName(name)).thenReturn(Mono.empty());

		StepVerifier.create(moduleService.create(name)).assertNext(module -> {

			assertThat(module.getName(), is(name));

			ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);

			verify(moduleRepository, times(1)).findByName(name);
			verify(moduleRepository, times(1)).save(argument.capture());
			verify(moduleRepository, times(1)).save(any(Module.class));
			assertThat(argument.getValue().getName(), is(name));

		}).expectComplete().verify();

	}

	@Test
	public void getById_NegativeModuleId_ThrowsException() {

		StepVerifier.create(Flux.just(-1, -1000, 0, -99).next().flatMap(moduleService::getById))
				.expectError(InvalidModuleIdException.class).verify();

	}

	@Test
	public void getById_NonExistentModuleId_ThrowsException() throws Exception {

		final int mockModuleId = 286;

		when(moduleRepository.existsById(mockModuleId)).thenReturn(Mono.just(false));
		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.empty());

		StepVerifier.create(moduleService.getById(mockModuleId)).expectError(ModuleIdNotFoundException.class).verify();

	}

	@Test
	public void getDynamicFlag_FlagIsExact_ThrowsException() throws Exception {

		final Module mockModule = mock(Module.class);

		final int mockModuleId = 18;
		final int mockUserId = 7;

		final byte[] mockedUserKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19 };
		final byte[] mockedServerKey = { -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29 };
		final byte[] mockedTotalKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118,
				9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29 };
		final byte[] mockedHmacOutput = { -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19 };

		final String mockedBaseFlag = "ZrLBRsS0QfL5TDz5";

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(userService.getKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
		when(mockModule.getFlag()).thenReturn(mockedBaseFlag);
		when(mockModule.isFlagExact()).thenReturn(true);
		when(mockModule.isFlagEnabled()).thenReturn(true);

		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

		when(cryptoService.hmac(mockedTotalKey, mockedBaseFlag.getBytes())).thenReturn(Mono.just(mockedHmacOutput));
		when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn("thisistheoutputtedflag");

		StepVerifier.create(moduleService.getDynamicFlag(mockUserId, mockModuleId))
				.expectError(InvalidFlagStateException.class).verify();

	}

	@Test
	public void getDynamicFlag_FlagIsSet_ReturnsFlag() throws Exception {

		final Module mockModule = mock(Module.class);

		final int mockModuleId = 76;
		final int mockUserId = 785;

		final byte[] mockedUserKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19 };
		final byte[] mockedServerKey = { -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29 };
		final byte[] mockedTotalKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118,
				9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29 };
		final byte[] mockedHmacOutput = { -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19 };

		final String mockedBaseFlag = "ZrLBRsS0QfL5TDz5";

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(userService.getKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
		when(mockModule.getFlag()).thenReturn(mockedBaseFlag);
		when(mockModule.isFlagEnabled()).thenReturn(true);

		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

		when(cryptoService.hmac(mockedTotalKey, mockedBaseFlag.getBytes())).thenReturn(Mono.just(mockedHmacOutput));
		when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn("thisistheoutputtedflag");

		StepVerifier.create(moduleService.getDynamicFlag(mockUserId, mockModuleId)).assertNext(flag -> {

			assertThat(flag, is(flag));
			verify(moduleRepository, times(1)).findById(mockModuleId);

			verify(userService, times(1)).getKeyById(mockUserId);
			verify(configurationService, times(1)).getServerKey();
			verify(mockModule, times(1)).getFlag();
			verify(mockModule, times(1)).isFlagEnabled();

			verify(configurationService, times(1)).getServerKey();

			verify(cryptoService, times(1)).hmac(mockedTotalKey, mockedBaseFlag.getBytes());
			verify(keyService, times(1)).convertByteKeyToString(mockedHmacOutput);

		}).expectComplete().verify();

	}

	@Test
	public void getDynamicFlag_FlagNotEnabled_ThrowsException() throws Exception {

		final int mockModuleId = 440;
		final int mockUserId = 332;

		final Module mockModule = mock(Module.class);

		final byte[] mockedUserKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19 };
		final byte[] mockedServerKey = { -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29 };

		when(mockModule.isFlagEnabled()).thenReturn(true);

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(moduleRepository.findById(mockUserId)).thenReturn(Mono.just(mockModule));

		when(userService.getKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

		when(mockModule.isFlagEnabled()).thenReturn(false);

		StepVerifier.create(moduleService.getDynamicFlag(mockUserId, mockModuleId))
				.expectError(InvalidFlagStateException.class).verify();

	}

	@Test
	public void getDynamicFlag_NegativeModuleId_ThrowsException() {

		StepVerifier
				.create(Flux.just(-1, -1000, -99999).next()
						.flatMap(moduleId -> moduleService.getDynamicFlag(768, moduleId)))
				.expectError(InvalidModuleIdException.class).verify();

	}

	@Test
	public void getDynamicFlag_NegativeUserId_ThrowsException() {

		StepVerifier
				.create(Flux.just(-1, -1000, -99999).next()
						.flatMap(userId -> moduleService.getDynamicFlag(userId, 302)))
				.expectError(InvalidUserIdException.class).verify();

	}

	@Test
	public void getDynamicFlag_ZeroModuleId_ThrowsException() {

		StepVerifier.create(moduleService.getDynamicFlag(267, 0)).expectError(InvalidModuleIdException.class).verify();

	}

	@Test
	public void getDynamicFlag_ZeroUserId_ThrowsException() {

		StepVerifier.create(moduleService.getDynamicFlag(0, 186)).expectError(InvalidUserIdException.class).verify();

	}

	@Test
	public void setDynamicFlag_FlagPreviouslySet_ReturnPreviousFlag() throws Exception {

		final String newFlag = "uVR6jeaKqtMD6CPg";

		final Module mockModuleWithoutDynamicFlag = mock(Module.class);
		final Module mockModuleWithoutExactFlag = mock(Module.class);
		final Module mockModuleWithExactFlag = mock(Module.class);
		final Module mockModuleWithDynamicFlag = mock(Module.class);

		final int mockModuleId = 517;

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModuleWithoutExactFlag));

		when(mockModuleWithoutExactFlag.withFlagEnabled(true)).thenReturn(mockModuleWithExactFlag);
		when(mockModuleWithExactFlag.withFlagExact(false)).thenReturn(mockModuleWithDynamicFlag);
		when(mockModuleWithoutExactFlag.withFlagExact(false)).thenReturn(mockModuleWithoutDynamicFlag);
		when(mockModuleWithoutDynamicFlag.withFlagEnabled(true)).thenReturn(mockModuleWithDynamicFlag);

		when(mockModuleWithDynamicFlag.isFlagEnabled()).thenReturn(true);
		when(mockModuleWithDynamicFlag.isFlagExact()).thenReturn(false);
		when(mockModuleWithDynamicFlag.getFlag()).thenReturn(newFlag);

		when(moduleRepository.save(mockModuleWithDynamicFlag)).thenReturn(Mono.just(mockModuleWithDynamicFlag));

		StepVerifier.create(moduleService.setDynamicFlag(mockModuleId)).assertNext(module -> {

			assertThat(module.getFlag(), is(newFlag));

			verify(moduleRepository, times(1)).save(any(Module.class));
			verify(keyService, never()).generateRandomString(any(Integer.class));

			ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);
			verify(moduleRepository, times(1)).save(argument.capture());
			assertThat(argument.getValue().getFlag(), is(newFlag));

		}).expectComplete().verify();

	}

	@Test
	public void setDynamicFlag_NegativeModuleId_ThrowsException() {

		StepVerifier.create(Flux.just(-1, -1000, -99999).next().flatMap(moduleService::setDynamicFlag))
				.expectError(InvalidModuleIdException.class).verify();

	}

	@Test
	public void setDynamicFlag_NoPreviousFlag_GeneratesNewFlag() throws Exception {

		final String newFlag = "uVR6jeaKqtMD6CPg";

		final Module mockModuleWithoutDynamicFlag = mock(Module.class);
		final Module mockModuleWithoutExactFlag = mock(Module.class);
		final Module mockModuleWithExactFlag = mock(Module.class);
		final Module mockModuleWithDynamicFlag = mock(Module.class);
		final Module mockModuleWithFlag = mock(Module.class);

		final int mockModuleId = 134;

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

			verify(mockModuleWithoutExactFlag, atMost(1)).withFlagEnabled(true);
			verify(mockModuleWithExactFlag, atMost(1)).withFlagExact(false);
			verify(mockModuleWithoutExactFlag, atMost(1)).withFlagExact(false);
			verify(mockModuleWithoutDynamicFlag, atMost(1)).withFlagEnabled(true);

			verify(keyService, times(1)).generateRandomString(16);
			verify(mockModuleWithDynamicFlag, times(1)).withFlag(newFlag);

			verify(moduleRepository, times(1)).save(any(Module.class));

		}).expectComplete().verify();

	}

	@Test
	public void setDynamicFlag_ZeroModuleId_ThrowsException() {

		StepVerifier.create(moduleService.setDynamicFlag(0)).expectError(InvalidModuleIdException.class).verify();

	}

	@Test
	public void setExactFlag_EmptyExactFlag_ThrowsException() throws Exception {

		assertThrows(InvalidFlagException.class, () -> moduleService.setExactFlag(1, ""));

	}

	@Test
	public void setExactFlag_InvalidModuleId_ThrowsException() throws Exception {

		assertThrows(InvalidModuleIdException.class, () -> moduleService.setExactFlag(0, "flag"));
		assertThrows(InvalidModuleIdException.class, () -> moduleService.setExactFlag(-1, "flag"));
		assertThrows(InvalidModuleIdException.class, () -> moduleService.setExactFlag(-9999, "flag"));

	}

	@Test
	public void setExactFlag_NullExactFlag_ThrowsException() throws Exception {

		assertThrows(InvalidFlagException.class, () -> moduleService.setExactFlag(1, null));

	}

	@Test
	public void setExactFlag_ValidFlag_SetsFlagToExact() throws Exception {

		final String exactFlag = "setExactFlag_ValidFlag_flag";

		final Module mockModuleWithoutFlag = mock(Module.class);
		final Module mockModuleWithFlagEnabled = mock(Module.class);
		final Module mockModuleWithExactFlagEnabled = mock(Module.class);
		final Module mockModuleWithExactFlagEnabledAndSet = mock(Module.class);

		final int mockModuleId = 239;

		when(mockModuleWithoutFlag.getId()).thenReturn(mockModuleId);

		when(mockModuleWithoutFlag.withFlagEnabled(true)).thenReturn(mockModuleWithFlagEnabled);
		when(mockModuleWithFlagEnabled.withFlagExact(true)).thenReturn(mockModuleWithExactFlagEnabled);
		when(mockModuleWithExactFlagEnabled.withFlag(exactFlag)).thenReturn(mockModuleWithExactFlagEnabledAndSet);

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

			ArgumentCaptor<Integer> findArgument = ArgumentCaptor.forClass(Integer.class);
			verify(moduleRepository, times(1)).findById(findArgument.capture());
			assertThat(findArgument.getValue(), is(mockModuleId));

			ArgumentCaptor<Module> saveArgument = ArgumentCaptor.forClass(Module.class);
			verify(moduleRepository, times(1)).save(saveArgument.capture());
			assertThat(saveArgument.getValue().getFlag(), is(exactFlag));

		}).expectComplete().verify();

	}

	@Test
	public void setName_EmptyName_ThrowsException() throws Exception {

		StepVerifier.create(moduleService.setName(847, "")).expectError(IllegalArgumentException.class).verify();
	}

	@Test
	public void setName_InvalidModuleId_ThrowsException() throws Exception {

		StepVerifier.create(moduleService.setName(-1, "name")).expectError(InvalidModuleIdException.class).verify();

		StepVerifier.create(moduleService.setName(-1000, "name")).expectError(InvalidModuleIdException.class).verify();

		StepVerifier.create(moduleService.setName(0, "name")).expectError(InvalidModuleIdException.class).verify();

	}

	@Test
	public void setName_NullName_ThrowsException() throws Exception {

		StepVerifier.create(moduleService.setName(204, null)).expectError(NullPointerException.class).verify();
	}

	@Test
	public void setName_ValidName_Succeeds() throws Exception {

		Module mockModule = mock(Module.class);
		String newName = "newName";

		int mockModuleId = 30;

		when(moduleRepository.existsById(mockModuleId)).thenReturn(Mono.just(true));
		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));
		when(moduleRepository.findByName(newName)).thenReturn(Mono.empty());

		when(mockModule.withName(newName)).thenReturn(mockModule);
		when(moduleRepository.save(any(Module.class))).thenReturn(Mono.just(mockModule));
		when(mockModule.getName()).thenReturn(newName);

		StepVerifier.create(moduleService.setName(mockModuleId, newName)).assertNext(module -> {

			assertThat(module.getName(), is(newName));

			InOrder order = inOrder(mockModule, moduleRepository);

			order.verify(mockModule, times(1)).withName(newName);
			order.verify(moduleRepository, times(1)).save(mockModule);

		}).expectComplete().verify();

	}

	@BeforeEach
	private void setUp() {
		// Print more verbose errors if something goes wrong
		Hooks.onOperatorDebug();

		moduleService = new ModuleService(moduleRepository, userService, configurationService, keyService,
				cryptoService);
	}

	@Test
	public void verifyFlag_FlagNotEnabled_ThrowsException() throws Exception {

		final int mockUserId = 515;
		final int mockModuleId = 161;

		final Module mockModule = mock(Module.class);

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(mockModule.isFlagEnabled()).thenReturn(false);

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, "flag"))
				.expectError(InvalidFlagStateException.class).verify();

	}

	@Test
	public void verifyFlag_InvalidDynamicFlag_ReturnFalse() throws Exception {

		final Module mockModule = mock(Module.class);

		final int mockUserId = 193;
		final int mockModuleId = 34;
		final String validFlag = "validFlag";

		final byte[] mockedUserKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19 };
		final byte[] mockedServerKey = { -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19 };
		final byte[] mockedHmacOutput = { -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19 };

		final byte[] mockedTotalKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118,
				9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19 };

		when(mockModule.isFlagEnabled()).thenReturn(true);
		when(mockModule.isFlagExact()).thenReturn(false);
		when(mockModule.getFlag()).thenReturn(validFlag);

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

		when(cryptoService.hmac(mockedTotalKey, validFlag.getBytes())).thenReturn(Mono.just(mockedHmacOutput));
		when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn("thisistheoutputtedflag");

		when(userService.getKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

		StepVerifier
				.create(Flux.just("invalidFlag", "", "123", "+-/#@").next()
						.flatMap(invalidFlag -> moduleService.verifyFlag(mockUserId, mockModuleId, invalidFlag)))
				.assertNext(isValid -> {

					assertThat(isValid, is(false));
					verify(moduleRepository, atLeast(1)).findById(mockModuleId);
					verify(mockModule, atLeast(1)).isFlagEnabled();
					verify(mockModule, atLeast(1)).isFlagExact();
					verify(mockModule, times(1)).getFlag();
					verify(mockModule, never()).getId();
					verify(configurationService, atLeast(1)).getServerKey();
					verify(cryptoService, atLeast(1)).hmac(mockedTotalKey, validFlag.getBytes());
					verify(keyService, atLeast(1)).convertByteKeyToString(mockedHmacOutput);
					verify(userService, atLeast(1)).getKeyById(mockUserId);

				}).expectComplete().verify();

	}

	@Test
	public void verifyFlag_InvalidExactFlag_ReturnsFalse() throws Exception {

		final int mockUserId = 709;
		final int mockModuleId = 677;
		final String validExactFlag = "validFlag";

		final Module mockModule = mock(Module.class);

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(mockModule.isFlagEnabled()).thenReturn(true);
		when(mockModule.isFlagExact()).thenReturn(true);
		when(mockModule.getFlag()).thenReturn(validExactFlag);

		StepVerifier
				.create(Flux.just("invalidFlag", "", "123", "+-/#@").next()
						.flatMap(invalidFlag -> moduleService.verifyFlag(mockUserId, mockModuleId, invalidFlag)))
				.assertNext(isValid -> {

					assertThat(isValid, is(false));

					verify(moduleRepository, times(1)).findById(mockModuleId);

					verify(mockModule, times(1)).isFlagEnabled();
					verify(mockModule, times(1)).isFlagExact();
					verify(mockModule, times(1)).getFlag();

				}).expectComplete().verify();

	}

	@Test
	public void verifyFlag_NullDynamicFlag_ReturnFalse() throws Exception {

		final int mockUserId = 756;
		final int mockModuleId = 543;

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, null)).assertNext(isValid -> {

			assertThat(isValid, is(false));

			verify(moduleRepository, never()).findById(any(Integer.class));

			verify(configurationService, never()).getServerKey();
			verify(cryptoService, never()).hmac(any(byte[].class), any(byte[].class));
			verify(keyService, never()).convertByteKeyToString(any(byte[].class));
			verify(userService, never()).getKeyById(mockUserId);

		}).expectComplete().verify();

	}

	@Test
	public void verifyFlag_NullExactFlag_ReturnsFalse() throws Exception {

		final int mockUserId = 487;
		final int mockModuleId = 941;

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, null)).assertNext(isValid -> {

			assertThat(isValid, is(false));

			verify(moduleRepository, never()).findById(any(Integer.class));

		}).expectComplete().verify();

	}

	@Test
	public void verifyFlag_ValidDynamicFlag_ReturnsTrue() throws Exception {

		final Module mockModule = mock(Module.class);

		final int mockUserId = 158;
		final int mockModuleId = 184;
		final String baseFlag = "baseFlag";
		final String validFlag = "thisisavalidflag";

		final byte[] mockedUserKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19 };
		final byte[] mockedServerKey = { -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19 };
		final byte[] mockedHmacOutput = { -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19 };

		final byte[] mockedTotalKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118,
				9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19 };

		when(mockModule.isFlagEnabled()).thenReturn(true);
		when(mockModule.isFlagExact()).thenReturn(false);
		when(mockModule.getFlag()).thenReturn(baseFlag);

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

		when(cryptoService.hmac(mockedTotalKey, baseFlag.getBytes())).thenReturn(Mono.just(mockedHmacOutput));
		when(keyService.convertByteKeyToString(mockedHmacOutput)).thenReturn(validFlag);

		when(userService.getKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, validFlag)).assertNext(isValid -> {

			assertThat(isValid, is(true));

			verify(moduleRepository, atLeast(1)).findById(mockModuleId);
			verify(mockModule, atLeast(1)).isFlagEnabled();
			verify(mockModule, atLeast(1)).isFlagExact();
			verify(mockModule, times(1)).getFlag();
			verify(mockModule, never()).getId();
			verify(configurationService, atLeast(1)).getServerKey();
			verify(cryptoService, atLeast(1)).hmac(mockedTotalKey, baseFlag.getBytes());
			verify(keyService, atLeast(1)).convertByteKeyToString(mockedHmacOutput);
			verify(userService, atLeast(1)).getKeyById(mockUserId);

		}).expectComplete().verify();

	}

	@Test
	public void verifyFlag_ValidExactFlag_ReturnsTrue() throws Exception {

		final int mockUserId = 225;
		final int mockModuleId = 201;
		final String validExactFlag = "validFlag";

		final Module mockModule = mock(Module.class);

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(mockModule.isFlagEnabled()).thenReturn(true);
		when(mockModule.isFlagExact()).thenReturn(true);
		when(mockModule.getFlag()).thenReturn(validExactFlag);

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, validExactFlag)).assertNext(isValid -> {

			assertThat(isValid, is(true));

			verify(moduleRepository, times(1)).findById(mockModuleId);

			verify(mockModule, times(1)).isFlagEnabled();
			verify(mockModule, times(1)).isFlagExact();
			verify(mockModule, times(1)).getFlag();

		}).expectComplete().verify();
	}

	@Test
	public void verifyFlag_ValidExactUpperLowerCaseFlag_ReturnsTrue() throws Exception {

		final int mockUserId = 594;
		final int mockModuleId = 769;
		final String validExactFlag = "validFlagWithUPPERCASEandlowercase";

		final Module mockModule = mock(Module.class);

		when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

		when(mockModule.isFlagEnabled()).thenReturn(true);
		when(mockModule.isFlagExact()).thenReturn(true);
		when(mockModule.getFlag()).thenReturn(validExactFlag);

		StepVerifier
				.create(Flux.just(validExactFlag.toUpperCase(), validExactFlag.toLowerCase()).next().flatMap(flag -> {

					return moduleService.verifyFlag(mockUserId, mockModuleId, flag);

				})).assertNext(isValid -> {

					assertThat(isValid, is(true));

					verify(moduleRepository, times(1)).findById(mockModuleId);

					verify(mockModule, times(1)).isFlagEnabled();
					verify(mockModule, times(1)).isFlagExact();
					verify(mockModule, times(1)).getFlag();

				}).expectComplete().verify();

	}

}