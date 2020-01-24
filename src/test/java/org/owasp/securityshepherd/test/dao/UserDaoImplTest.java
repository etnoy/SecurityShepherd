package org.owasp.securityshepherd.test.dao;

import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.securityshepherd.dao.mapper.UserDaoImpl;
import org.owasp.securityshepherd.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class UserDaoImplTest {

	@Autowired
	private UserDaoImpl userDao;

	@Test
	public void testGetAllUsers() {

		UserEntity testUser = new UserEntity("userid123", "classid456", "mockuser", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);
		
		userDao.create(testUser);

		userDao.listUsers().get(0);

	}

}