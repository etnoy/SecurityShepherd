/*
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
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
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
class FlagHandlerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private FlagHandler flagHandler;

  @Mock private ModuleService moduleService;

  @Mock private UserService userService;

  @Mock private ConfigurationService configurationService;

  @Mock private CryptoService cryptoService;

  @Test
  void getDynamicFlag_FlagIsStatic_ReturnsInvalidFlagStateException() {
    final Module mockModule = mock(Module.class);

    final String mockModuleName = "module-id";
    final long mockUserId = 7;

    final byte[] mockedUserKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19
    };
    final byte[] mockedServerKey = {
      -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29
    };

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
    when(mockModule.isFlagStatic()).thenReturn(true);

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    StepVerifier.create(flagHandler.getDynamicFlag(mockUserId, mockModuleName))
        .expectError(InvalidFlagStateException.class)
        .verify();
  }

  @Test
  void getDynamicFlag_DynamicFlag_ReturnsFlag() {
    final Module mockModule = mock(Module.class);

    final String mockModuleName = "module-id";
    final long mockUserId = 785;

    final byte[] mockedServerKey = {
      -118, 17, 4, -35, 17, -3, -94, 0, -72, -17, 65, -127, 12, 82, 9, 29
    };

    final byte[] mockedUserKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19
    };
    final byte[] mockedModuleKey = {
      -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19
    };

    final byte[] mockedHmacOutput = {
      -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19
    };

    final byte[] mockedTotalKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118, 9, -7, -35, 15,
      -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19, 102, 108, 97, 103
    };
    final String correctFlag = "flag{qaa7txiprsrabyelooaqyutbcm}";

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));
    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));
    when(mockModule.getKey()).thenReturn(mockedModuleKey);

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedServerKey, mockedTotalKey)).thenReturn(mockedHmacOutput);

    StepVerifier.create(flagHandler.getDynamicFlag(mockUserId, mockModuleName))
        .expectNext(correctFlag)
        .expectComplete()
        .verify();

    verify(moduleService, times(1)).findByName(mockModuleName);

    verify(userService, times(1)).findKeyById(mockUserId);
    verify(configurationService, times(1)).getServerKey();
    verify(mockModule, times(1)).getKey();

    verify(configurationService, times(1)).getServerKey();

    verify(cryptoService, times(1)).hmac(mockedServerKey, mockedTotalKey);
  }

  @Test
  void getDynamicFlag_NegativeUserId_ReturnsInvalidUserIdException() {
    StepVerifier.create(flagHandler.getDynamicFlag(-1, "id"))
        .expectError(InvalidUserIdException.class)
        .verify();
    StepVerifier.create(flagHandler.getDynamicFlag(-1000, "id"))
        .expectError(InvalidUserIdException.class)
        .verify();
  }

  @Test
  void getDynamicFlag_ZeroUserId_ReturnsInvalidUserIdException() {
    StepVerifier.create(flagHandler.getDynamicFlag(0, "id"))
        .expectError(InvalidUserIdException.class)
        .verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    flagHandler = new FlagHandler(moduleService, userService, configurationService, cryptoService);
  }

  @Test
  void verifyFlag_CorrectDynamicFlag_ReturnsTrue() {
    final Module mockModule = mock(Module.class);

    final long mockUserId = 158;
    final String mockModuleName = "module-id";
    final byte[] mockedServerKey = {
      -118, 17, 4, -35, 17, -3, -94, 0, -72, -17, 65, -127, 12, 82, 9, 29
    };

    final byte[] mockedUserKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19
    };
    final byte[] mockedModuleKey = {
      -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19
    };

    final byte[] mockedHmacOutput = {
      -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19
    };

    final byte[] mockedTotalKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118, 9, -7, -35, 15,
      -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19, 102, 108, 97, 103
    };

    final String correctFlag = "flag{qaa7txiprsrabyelooaqyutbcm}";

    when(mockModule.isFlagStatic()).thenReturn(false);
    when(mockModule.getKey()).thenReturn(mockedModuleKey);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedServerKey, mockedTotalKey)).thenReturn(mockedHmacOutput);

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, correctFlag))
        .expectNext(true)
        .expectComplete()
        .verify();

    verify(moduleService, atLeast(1)).findByName(mockModuleName);
    verify(mockModule, atLeast(1)).isFlagStatic();
    verify(mockModule, times(2)).getKey();
    verify(configurationService, atLeast(1)).getServerKey();
    verify(cryptoService, atLeast(1)).hmac(mockedServerKey, mockedTotalKey);
    verify(userService, atLeast(1)).findKeyById(mockUserId);
  }

  @Test
  void verifyFlag_CorrectStaticFlag_ReturnsTrue() {
    final long mockUserId = 225;
    final String mockModuleName = "module-id";
    final String validStaticFlag = "validFlag";

    final Module mockModule = mock(Module.class);

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    when(mockModule.isFlagStatic()).thenReturn(true);
    when(mockModule.getStaticFlag()).thenReturn(validStaticFlag);

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, validStaticFlag))
        .expectNext(true)
        .expectComplete()
        .verify();

    verify(moduleService, times(1)).findByName(mockModuleName);

    verify(mockModule, times(2)).isFlagStatic();
    verify(mockModule, times(2)).getStaticFlag();
  }

  @Test
  void verifyFlag_CorrectLowerCaseStaticFlag_ReturnsTrue() {
    final long mockUserId = 594;
    final String mockModuleName = "module-id";
    final String validStaticFlag = "validFlagWithUPPERCASEandlowercase";

    final Module mockModule = mock(Module.class);

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    when(mockModule.isFlagStatic()).thenReturn(true);
    when(mockModule.getStaticFlag()).thenReturn(validStaticFlag);

    StepVerifier.create(
            flagHandler.verifyFlag(mockUserId, mockModuleName, validStaticFlag.toLowerCase()))
        .expectNext(true)
        .expectComplete()
        .verify();

    verify(moduleService, times(1)).findByName(mockModuleName);

    verify(mockModule, times(2)).isFlagStatic();
    verify(mockModule, times(2)).getStaticFlag();
  }

  @Test
  void verifyFlag_CorrectUpperCaseStaticFlag_ReturnsTrue() {
    final long mockUserId = 594;
    final String mockModuleName = "module-id";
    final String validStaticFlag = "validFlagWithUPPERCASEandlowercase";

    final Module mockModule = mock(Module.class);

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    when(mockModule.isFlagStatic()).thenReturn(true);
    when(mockModule.getStaticFlag()).thenReturn(validStaticFlag);

    StepVerifier.create(
            flagHandler.verifyFlag(mockUserId, mockModuleName, validStaticFlag.toUpperCase()))
        .expectNext(true)
        .expectComplete()
        .verify();

    verify(moduleService, times(1)).findByName(mockModuleName);

    verify(mockModule, times(2)).isFlagStatic();
    verify(mockModule, times(2)).getStaticFlag();
  }

  @Test
  void verifyFlag_EmptyDynamicFlag_ReturnsFalse() {
    final Module mockModule = mock(Module.class);

    final long mockUserId = 193;
    final String mockModuleName = "module-id";
    final byte[] mockedServerKey = {
      -118, 17, 4, -35, 17, -3, -94, 0, -72, -17, 65, -127, 12, 82, 9, 29
    };

    final byte[] mockedUserKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19
    };
    final byte[] mockedModuleKey = {
      -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19
    };

    final byte[] mockedHmacOutput = {
      -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19
    };

    final byte[] mockedTotalKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118, 9, -7, -35, 15,
      -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19, 102, 108, 97, 103
    };

    when(mockModule.isFlagStatic()).thenReturn(false);
    when(mockModule.getKey()).thenReturn(mockedModuleKey);
    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedServerKey, mockedTotalKey)).thenReturn(mockedHmacOutput);

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, ""))
        // We expect this to return false
        .expectNext(false)
        .expectComplete()
        .verify();

    verify(moduleService, atLeast(1)).findByName(mockModuleName);

    // TODO: this is too many interactions, why 4?
    verify(mockModule, times(4)).isFlagStatic();
    verify(mockModule, times(2)).getKey();
    verify(configurationService, atLeast(1)).getServerKey();
    verify(cryptoService, times(2)).hmac(mockedServerKey, mockedTotalKey);
    verify(userService, times(2)).findKeyById(mockUserId);
  }

  @Test
  void verifyFlag_EmptyStaticFlag_ReturnsFalse() {
    final long mockUserId = 709;
    final String mockModuleName = "module-id";
    final String validStaticFlag = "validFlag";

    final Module mockModule = mock(Module.class);

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagStatic()).thenReturn(true);
    when(mockModule.getStaticFlag()).thenReturn(validStaticFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, ""))
        .expectNext(false)
        .expectComplete()
        .verify();

    verify(moduleService, times(1)).findByName(mockModuleName);

    verify(mockModule, times(2)).isFlagStatic();
    verify(mockModule, times(2)).getStaticFlag();
  }

  @Test
  void verifyFlag_NullDynamicFlag_ReturnsFalse() {
    final long mockUserId = 756;
    final String mockModuleName = "module-id";

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, null))
        .expectError(NullPointerException.class)
        .verify();
  }

  @Test
  void verifyFlag_NullStaticFlag_ReturnsFalse() {
    final long mockUserId = 487;
    final String mockModuleName = "module-id";

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, null))
        .expectError(NullPointerException.class)
        .verify();
  }

  @Test
  void verifyFlag_WrongDynamicFlag_ReturnsFalse() {
    final Module mockModule = mock(Module.class);

    final long mockUserId = 193;
    final String mockModuleName = "module-id";
    final byte[] mockedServerKey = {
      -118, 17, 4, -35, 17, -3, -94, 0, -72, -17, 65, -127, 12, 82, 9, 29
    };

    final byte[] mockedUserKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19
    };
    final byte[] mockedModuleKey = {
      -118, 9, -7, -35, 15, -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19
    };

    final byte[] mockedHmacOutput = {
      -128, 1, -7, -35, 15, -116, -94, 0, -32, -117, 115, -127, 12, 82, 97, 19
    };

    final byte[] mockedTotalKey = {
      -108, 101, -7, -35, 17, -16, -94, 0, -32, -117, 65, -127, 22, 62, 9, 19, -118, 9, -7, -35, 15,
      -116, -94, 0, -32, -117, 65, -127, 12, 82, 97, 19, 102, 108, 97, 103
    };

    when(mockModule.isFlagStatic()).thenReturn(false);
    when(mockModule.getKey()).thenReturn(mockedModuleKey);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(configurationService.getServerKey()).thenReturn(Mono.just(mockedServerKey));

    when(cryptoService.hmac(mockedServerKey, mockedTotalKey)).thenReturn(mockedHmacOutput);

    when(userService.findKeyById(mockUserId)).thenReturn(Mono.just(mockedUserKey));

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, "invalidFlag"))
        //
        .expectNext(false)
        //
        .expectComplete()
        .verify();

    verify(moduleService, atLeast(1)).findByName(mockModuleName);
    verify(mockModule, atLeast(1)).isFlagStatic();
    verify(mockModule, times(2)).getKey();
    verify(configurationService, atLeast(1)).getServerKey();
    verify(cryptoService, atLeast(1)).hmac(mockedServerKey, mockedTotalKey);
    verify(userService, atLeast(1)).findKeyById(mockUserId);
  }

  @Test
  void verifyFlag_WrongStaticFlag_ReturnsFalse() {
    final long mockUserId = 709;
    final String mockModuleName = "module-id";
    final String validStaticFlag = "validFlag";

    final Module mockModule = mock(Module.class);

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(mockModule.isFlagStatic()).thenReturn(true);
    when(mockModule.getStaticFlag()).thenReturn(validStaticFlag);

    when(userService.findDisplayNameById(mockUserId)).thenReturn(Mono.just("MockUser"));

    StepVerifier.create(flagHandler.verifyFlag(mockUserId, mockModuleName, "invalidFlag"))
        .expectNext(false)
        .expectComplete()
        .verify();

    verify(moduleService, times(1)).findByName(mockModuleName);

    verify(mockModule, times(2)).isFlagStatic();
    verify(mockModule, times(2)).getStaticFlag();
  }
}
