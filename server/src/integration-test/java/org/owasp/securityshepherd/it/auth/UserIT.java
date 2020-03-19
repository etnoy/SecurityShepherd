package org.owasp.securityshepherd.it.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIT {

  @Autowired
  UserService userService;
  
  @LocalServerPort
  private int port;
  
  TestRestTemplate restTemplate = new TestRestTemplate();
  HttpHeaders headers = new HttpHeaders();
  
  private String createURLWithPort(String uri) {
      return "http://localhost:" + port + uri;
  }
  
  @Test
  public void initialTest() throws Exception {
    
    userService.create("testUser").block();
    userService.createPasswordUser("testUser2", "username", "hash").block();

    
    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
    ResponseEntity<String> response = restTemplate.exchange(
      createURLWithPort("/api/v1/user/list"), HttpMethod.GET, entity, String.class);

    System.out.println(response.getBody());

  }

}
