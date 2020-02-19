package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.repository.ConfigurationRepository;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class ConfigurationServiceTest {

	@Autowired
	private ConfigurationService configurationService;

	@MockBean
	private ConfigurationRepository configurationRepository;

	@MockBean
	private KeyService keyService;

	@BeforeEach
	void setUp() {
		configurationService = new ConfigurationService(configurationRepository, keyService);
	}

	@Test
	public void getServerKey_NoKeyExists_GeneratesAndReturnsKey() {

		final byte[] serverKey = configurationService.getServerKey();

		assertThat(serverKey, is(notNullValue()));
		assertThat(serverKey.length, is(greaterThan(8)));

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