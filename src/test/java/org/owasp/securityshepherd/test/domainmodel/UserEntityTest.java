package org.owasp.securityshepherd.test.domainmodel;

import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.securityshepherd.model.UserEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class UserEntityTest {

	@Test
	public void testUsersEqual() {

		UserEntity user1 = new UserEntity("userid123", "classid456", "user1", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		UserEntity user2 = new UserEntity("userid456", "classid456", "user2", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		UserEntity user3 = new UserEntity("userid123", "classid456", "user3", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		Assert.isTrue(user1 != user2, "Users with different userId should not be equal");
		Assert.isTrue(user1 == user3, "Users with identical userid should be equal");
		Assert.isTrue(user3 != user2, "Users with different userId should not be equal");

	}

}