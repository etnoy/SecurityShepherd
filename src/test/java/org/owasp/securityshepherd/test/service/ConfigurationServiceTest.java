package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.owasp.securityshepherd.persistence.model.Configuration;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.proxy.ConfigurationRepositoryProxy;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.KeyService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class ConfigurationServiceTest {

	private ConfigurationService configurationService;

	@Mock
	private ConfigurationRepositoryProxy configurationRepositoryProxy;

	@Mock
	private KeyService keyService;

	@BeforeEach
	private void setUp() {
		configurationService = new ConfigurationService(configurationRepositoryProxy, keyService);
	}

	@Test
	public void getServerKey_NoKeyExists_GeneratesAndReturnsKey() {

		// Establish a random key
		final byte[] testRandomBytes = { -118, 101, -7, -36, 17, -26, -24, 0, -31, -117, 75, -127, 22, 92, 9, 19 };
		final int userId = 17;

		User testUser = mock(User.class);
		when(testUser.getId()).thenReturn(userId);
		when(configurationRepositoryProxy.findByKey("serverKey")).thenReturn(Optional.empty());
		when(configurationRepositoryProxy.existsByKey("serverKey")).thenReturn(false);
		when(configurationRepositoryProxy.save(any(Configuration.class)))
				.thenAnswer(configuration -> (configuration.getArgument(0)));

		when(keyService.generateRandomBytes(16)).thenReturn(testRandomBytes);

		final byte[] serverKey = configurationService.getServerKey();

		assertThat(serverKey, is(testRandomBytes));
		
		//TODO: verify method calls

	}

	@Test
	public void getServerKey_KeyExists_ReturnsExistingKey() {

		final byte[] serverKey = configurationService.getServerKey();

		assertThat(configurationService.getServerKey(), is(serverKey));

	}

	@Test
	public void refreshServerKey_KeyExists_RefreshesKey() {

		final byte[] serverKey = configurationService.getServerKey();

		configurationService.refreshServerKey();

		assertThat(configurationService.getServerKey(), is(not(serverKey)));

	}

	@Test
	public void refreshServerKey_NoKeyExists_GeneratesKey() {

		configurationService.refreshServerKey();

		final byte[] serverKey = configurationService.getServerKey();

		assertThat(serverKey, is(notNullValue()));
		assertThat(serverKey.length, is(greaterThan(8)));

	}

}