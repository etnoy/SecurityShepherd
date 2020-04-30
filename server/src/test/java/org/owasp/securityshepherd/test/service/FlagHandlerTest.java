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

import static org.mockito.Mockito.atLeast;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.crypto.CryptoService;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.user.UserService;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("FlagHandler unit test")
public class FlagHandlerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private FlagHandler flagHandler;

  @Mock
  private ModuleService moduleService;

  @Mock
  private UserService userService;

  @Mock
  private ConfigurationService configurationService;

  @Mock
  private KeyService keyService;

  @Mock
  private CryptoService cryptoService;

  @Test
  public void getDynamicFlag_FlagIsExact_ReturnsInvalidFlagStateException() {
    final Module mockModule = mock(Module.class);

    final long mockModuleId = 18;
    final long mockUserId = 7;

    final byte[] mockedUserKey =
        {-108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19};
    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.isFlagEnabled()).thenReturn(true);

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    StepVerifier.create(flagHandler.getDynamicFlag(mockUserId, mockModuleId))
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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
    when(mockModule.getFlag()).thenReturn(mockedBaseFlag);
    when(mockModule.isFlagEnabled()).thenReturn(true);

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, mockedBaseFlag.getBytes()))
        .thenReturn(mockedHmacOutput);
    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(correctFlag);

    StepVerifier.create(flagHandler.getDynamicFlag(mockUserId, mockModuleId))
        .expectNext(correctFlag).expectComplete().verify();

    verify(moduleService, times(1)).findById(mockModuleId);

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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(mockModule.isFlagEnabled()).thenReturn(false);

    StepVerifier.create(flagHandler.getDynamicFlag(mockUserId, mockModuleId))
        .expectError(InvalidFlagStateException.class).verify();
  }

  @Test
  public void getDynamicFlag_NegativeModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(flagHandler.getDynamicFlag(768, -1))
        .expectError(InvalidModuleIdException.class).verify();
    StepVerifier.create(flagHandler.getDynamicFlag(768, -1000))
        .expectError(InvalidModuleIdException.class).verify();
  }

  @Test
  public void getDynamicFlag_NegativeUserId_ReturnsInvalidUserIdException() {
    StepVerifier.create(flagHandler.getDynamicFlag(-1, 302))
        .expectError(InvalidUserIdException.class).verify();
    StepVerifier.create(flagHandler.getDynamicFlag(-1000, 302))
        .expectError(InvalidUserIdException.class).verify();
  }

  @Test
  public void getDynamicFlag_ZeroModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(flagHandler.getDynamicFlag(267, 0))
        .expectError(InvalidModuleIdException.class).verify();
  }

  @Test
  public void getDynamicFlag_ZeroUserId_ReturnsInvalidUserIdException() {
    StepVerifier.create(flagHandler.getDynamicFlag(0, 186))
        .expectError(InvalidUserIdException.class).verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    flagHandler = new FlagHandler(moduleService, userService, configurationService, cryptoService,
        keyService);
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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, baseFlag.getBytes())).thenReturn(mockedHmacOutput);

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(validFlag);

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, validFlag))
        .expectNext(true).expectComplete().verify();

    verify(moduleService, atLeast(1)).findById(mockModuleId);
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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, validExactFlag))
        .expectNext(true).expectComplete().verify();

    verify(moduleService, times(1)).findById(mockModuleId);

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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    StepVerifier
        .create(flagHandler.verifyFlag(mockUserId, mockModuleId, validExactFlag.toLowerCase()))
        .expectNext(true).expectComplete().verify();

    verify(moduleService, times(1)).findById(mockModuleId);

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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    StepVerifier
        .create(flagHandler.verifyFlag(mockUserId, mockModuleId, validExactFlag.toUpperCase()))
        .expectNext(true).expectComplete().verify();

    verify(moduleService, times(1)).findById(mockModuleId);

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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, validFlag.getBytes())).thenReturn(mockedHmacOutput);

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(validFlag);

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, ""))
        // We expect this to return false
        .expectNext(false)
        //
        .expectComplete().verify();

    verify(moduleService, atLeast(1)).findById(mockModuleId);

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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, "")).expectNext(false)
        .expectComplete().verify();

    verify(moduleService, times(1)).findById(mockModuleId);

    verify(mockModule, times(2)).isFlagEnabled();
    verify(mockModule, times(2)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
  }

  @Test
  public void verifyFlag_FlagNotEnabled_ReturnsInvalidFlagStateException() {
    final long mockUserId = 515;
    final long mockModuleId = 161;

    final Module mockModule = mock(Module.class);

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagEnabled()).thenReturn(false);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, "flag"))
        .expectError(InvalidFlagStateException.class).verify();
  }

  @Test
  public void verifyFlag_NullDynamicFlag_ReturnsFalse() {
    final long mockUserId = 756;
    final long mockModuleId = 543;

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, null))
        .expectError(NullPointerException.class).verify();
  }

  @Test
  public void verifyFlag_NullExactFlag_ReturnsFalse() {
    final long mockUserId = 487;
    final long mockModuleId = 941;

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, null))
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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedTotalKey, validFlag.getBytes())).thenReturn(mockedHmacOutput);

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

    when(keyService.byteFlagToString(mockedHmacOutput)).thenReturn(validFlag);

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, "invalidFlag"))
        //
        .expectNext(false)
        //
        .expectComplete().verify();

    verify(moduleService, atLeast(1)).findById(mockModuleId);
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

    when(moduleService.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagEnabled()).thenReturn(true);
    when(mockModule.isFlagExact()).thenReturn(true);
    when(mockModule.getFlag()).thenReturn(validExactFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));
    when(mockModule.getName()).thenReturn("TestModule");

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleId, "invalidFlag"))
        .expectNext(false).expectComplete().verify();

    verify(moduleService, times(1)).findById(mockModuleId);

    verify(mockModule, times(2)).isFlagEnabled();
    verify(mockModule, times(2)).isFlagExact();
    verify(mockModule, times(2)).getFlag();
  }
}
