package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.service.CryptoService;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@DisplayName("CryptoService unit test")
public class CryptoServiceTest {

  CryptoService cryptoService;

  @Test
  public void hmac_NullKey_ThrowsException() throws Exception {

    final byte[] message = {120, 56, 111, -98, -118, 44, -65, -127, 39, 35};

    StepVerifier.create(cryptoService.hmac(null, message)).expectError(NullPointerException.class)
        .verify();

  }

  @Test
  public void hmac_NullMessage_ThrowsException() throws Exception {

    final byte[] key = {-91, -79, 67, -107, 9, 91, 62, -95, 80, 78};

    StepVerifier.create(cryptoService.hmac(key, null)).expectError(NullPointerException.class)
        .verify();

  }

  @Test
  public void hmac_ValidData_ReturnsHash() throws Exception {

    final byte[] key = {-91, -79, 67, -107, 9, 91, 62, -95, 80, 78};

    final byte[] message = {120, 56, 111, -98, -118, 44, -65, -127, 39, 35};

    final byte[] expectedHash = {46, 102, -1, 90, 100, 13, 14, -96, 57, 8, 67, 116, 104, -45, 12,
        -122, -80, -110, 110, 19, 12, 77, 66, -39, 95, 26, -17, 107, 58, -106, 48, -6, 108, 22,
        -113, -49, 5, -21, -52, 119, 46, 102, 39, -9, 45, 124, -103, -100, 43, -1, 84, 105, -35,
        -81, 65, -97, -49, 23, 2, 111, 20, 58, 56, -2};

    StepVerifier.create(cryptoService.hmac(key, message)).assertNext(hash -> {

      assertThat(hash, is(expectedHash));

      assertThat(hash.length, greaterThan(1));

    }).expectComplete().verify();

  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong
    Hooks.onOperatorDebug();
    cryptoService = new CryptoService();
  }

}
