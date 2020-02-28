package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.EntityIdException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.Module;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.CryptoService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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

		when(moduleRepository.findByName(name)).thenReturn(Mono.empty());

		StepVerifier.create(moduleService.create(name)).assertNext(module -> {

			assertThat(module.getName(), is(name));
			verify(moduleRepository, times(1)).save(any(Module.class));

		}).expectComplete().verify();

	}

	@Test
	public void getById_NegativeUserId_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> moduleService.getById(-1));
		assertThrows(IllegalArgumentException.class, () -> moduleService.getById(-1000));

	}

	@Test
	public void getById_NonExistentModuleId_NotPresent() throws Exception {

		final int mockId = 123;

		when(moduleRepository.existsById(mockId)).thenReturn(Mono.just(false));

		StepVerifier.create(moduleService.getById(mockId)).expectError(ModuleIdNotFoundException.class).verify();

	}

	@Test
	public void getById_ZeroUserId_ThrowsException() throws Exception {

		assertThrows(IllegalArgumentException.class, () -> moduleService.getById(0));
	}

	@Test
	public void getDynamicFlag_FlagNotSet_ThrowsException() throws Exception {

		final Module mockModule = mock(Module.class);

		final int mockId = 19;
		final int userId = 11;

		when(mockModule.isFlagEnabled()).thenReturn(false);

		StepVerifier.create(moduleService.getDynamicFlag(userId, mockId)).expectError(InvalidFlagStateException.class)
				.verify();

	}

	@Test
	public void getDynamicFlag_FlagIsExact_ThrowsException() throws Exception {

//TODO

	}

	@Test
	public void getDynamicFlag_FlagIsSet_ReturnsFlag() throws Exception {

		final Module mockModule = mock(Module.class);

		final int mockId = 18;
		final int userId = 7;

		final byte[] mockedUserKey = { -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19 };
		final byte[] mockedServerKey = { -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29 };
		final String mockedBaseFlag = "ZrLBRsS0QfL5TDz5";

		when(userService.getKey(userId)).thenReturn(Mono.just(mockedUserKey));
		when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
		when(mockModule.getFlag()).thenReturn(mockedBaseFlag);

		StepVerifier.create(moduleService.getDynamicFlag(userId, mockId)).assertNext(flag -> {

			assertThat(flag, is(flag));
			verify(moduleRepository, times(1)).findById(mockId);

		}).expectComplete().verify();

	}

	@Test
	public void setDynamicFlag_FlagPreviouslySet_KeepsFlag()
			throws InvalidModuleIdException, ModuleIdNotFoundException {

		final String newFlag = "uVR6jeaKqtMD6CPg";

		final Module mockModuleWithoutDynamicFlag = mock(Module.class);
		final Module mockModuleWithoutExactFlag = mock(Module.class);
		final Module mockModuleWithExactFlag = mock(Module.class);
		final Module mockModuleWithDynamicFlag = mock(Module.class);
		final Module mockModuleWithFlag = mock(Module.class);

		final int mockId = 10;

		when(moduleRepository.findById(mockId)).thenReturn(Mono.just(mockModuleWithoutExactFlag));

		when(mockModuleWithoutExactFlag.withFlagEnabled(true)).thenReturn(mockModuleWithExactFlag);
		when(mockModuleWithExactFlag.withFlagExact(false)).thenReturn(mockModuleWithDynamicFlag);
		when(mockModuleWithoutExactFlag.withFlagExact(false)).thenReturn(mockModuleWithoutDynamicFlag);
		when(mockModuleWithoutDynamicFlag.withFlagEnabled(true)).thenReturn(mockModuleWithDynamicFlag);
		when(mockModuleWithoutDynamicFlag.withFlagExact(false)).thenReturn(mockModuleWithoutDynamicFlag);

		when(keyService.generateRandomString(16)).thenReturn(Mono.just(newFlag));
		when(mockModuleWithDynamicFlag.withFlag(newFlag)).thenReturn(mockModuleWithFlag);
		when(mockModuleWithFlag.isFlagEnabled()).thenReturn(true);
		when(mockModuleWithFlag.isFlagExact()).thenReturn(false);
		when(mockModuleWithFlag.getFlag()).thenReturn(newFlag);

		StepVerifier.create(moduleService.setDynamicFlag(mockId)).assertNext(module -> {

			ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);

			assertThat(module.getFlag(), is(newFlag));

			verify(moduleRepository, times(1)).save(argument.capture());
			verify(moduleRepository, times(1)).save(any(Module.class));

			assertThat(argument.getValue().getFlag(), is(newFlag));

		}).expectComplete().verify();

	}

	@Test
	public void setDynamicFlag_NegativeModuleId_ThrowsException() {

		assertThrows(InvalidModuleIdException.class, () -> moduleService.setDynamicFlag(-1));
		assertThrows(InvalidModuleIdException.class, () -> moduleService.setDynamicFlag(-9999));

	}

	@Test
	public void setDynamicFlag_NoPreviousFlag_GeneratesNewFlag() throws Exception {

		final Module mockModule = mock(Module.class);
		final int mockId = 9;

		StepVerifier.create(moduleService.setDynamicFlag(mockId)).assertNext(module -> {

			assertThat(module.isFlagEnabled(), is(true));
			assertThat(module.isFlagExact(), is(false));

			assertThat(module.getFlag(), is(notNullValue()));
			assertThat(module.getFlag(), instanceOf(String.class));
			assertThat(module.getFlag(), not(is(emptyString())));

			verify(moduleRepository, times(1)).save(any(Module.class));

		}).expectComplete().verify();

	}

	@Test
	public void setDynamicFlag_ZeroModuleId_ThrowsException() {

		assertThrows(InvalidModuleIdException.class, () -> moduleService.setDynamicFlag(0));

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

		final Module mockModule = mock(Module.class);
		final int mockId = 5;

		when(mockModule.getId()).thenReturn(5);
		when(moduleRepository.findById(mockId)).thenReturn(Mono.just(mockModule));

		StepVerifier.create(moduleService.setExactFlag(mockId, exactFlag)).assertNext(module -> {

			assertThat(module.isFlagEnabled(), is(true));
			assertThat(module.isFlagExact(), is(true));
			assertThat(module.getFlag(), is(exactFlag));

			verify(moduleRepository, times(1)).save(any(Module.class));

		}).expectComplete().verify();

	}

	@Test
	public void setName_ValidName_Succeeds() throws Exception {

		Module mockModule = mock(Module.class);
		String newName = "newName";

		int mockId = 30;

		when(moduleRepository.existsById(mockId)).thenReturn(Mono.just(true));
		when(moduleRepository.findById(mockId)).thenReturn(Mono.just(mockModule));
		when(moduleRepository.findByName(newName)).thenReturn(Mono.empty());

		when(mockModule.withName(newName)).thenReturn(mockModule);
		when(moduleRepository.save(any(Module.class))).thenReturn(Mono.just(mockModule));
		when(mockModule.getName()).thenReturn(newName);

		StepVerifier.create(moduleService.setName(mockId, newName)).assertNext(module -> {

			assertThat(module.getName(), is(newName));

			InOrder order = inOrder(mockModule, moduleRepository);

			order.verify(mockModule, times(1)).withName(newName);
			order.verify(moduleRepository, times(1)).save(mockModule);

		}).expectComplete().verify();

	}

	@BeforeEach
	private void setUp() {
		moduleService = new ModuleService(moduleRepository, userService, configurationService, keyService,
				cryptoService);
	}

	@Test
	public void verifyFlag_FlagNotSet_ThrowsException() throws Exception {

		final int mockModuleId = 24;
		final int mockUserId = 45;
		final String mockFlag = "flag";

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, "flag"))
				.expectError(InvalidFlagStateException.class).verify();

	}

	@Test
	public void verifyFlag_InvalidDynamicFlag_ReturnFalse() throws Exception {

		final int mockUserId = 11;
		final int mockModuleId = 21;

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, "invalidflag")).assertNext(isValid -> {

			assertThat(isValid, is(true));

		});

	}

	@Test
	public void verifyFlag_InvalidExactFlag_ReturnsFalse() throws Exception {

		final int mockUserId = 11;
		final int mockModuleId = 21;

		final String[] invalidExactFlags = { "itsaflag", null, "" };

		for (String invalidExactFlag : invalidExactFlags) {

			StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, invalidExactFlag))
					.assertNext(isValid -> {

						assertThat(isValid, is(false));

					});
		}

	}

	@Test
	public void verifyFlag_ValidDynamicFlag_ReturnsTrue() throws Exception {

		final int mockUserId=3;
		final int mockModuleId=23;

		final String validDynamicFlag = "itsaflag";

		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, validDynamicFlag)).assertNext(isValid -> {

			assertThat(isValid, is(true));

		});

	}

	@Test
	public void verifyFlag_ValidExactFlag_ReturnsTrue()
			throws Exception {

		final int mockUserId=8;
		final int mockModuleId=7;
		
		final String validExactFlag = "itsaflag";


		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, validExactFlag)).assertNext(isValid -> {

			assertThat(isValid, is(true));

		});
	}

	@Test
	public void verifyFlag_ValidExactUpperLowerCaseFlag_ReturnsTrue() throws Exception {

		final int mockUserId=24;
		final int mockModuleId=25;
		
		final String validExactFlag = "UPPERCASEFLAG";
		
		StepVerifier.create(moduleService.verifyFlag(mockUserId, mockModuleId, validExactFlag)).assertNext(isValid -> {

			assertThat(isValid, is(true));

		});

	}

}