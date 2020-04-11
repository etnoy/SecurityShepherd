package org.owasp.securityshepherd.controller;

import java.util.Map;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/")
public class SqlModuleController {
  private final SqlInjectionTutorial sqlInjectionTutorial;

  @PostMapping(path = "module/sql-injection-tutorial/query")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Map<String, Object>> submit(@RequestBody String request)
      throws JsonMappingException, JsonProcessingException {
    return sqlInjectionTutorial.submitQuery(1L, 1L, readQueryFromRequestBody(request));
  }

  private String readQueryFromRequestBody(final String body)
      throws JsonMappingException, JsonProcessingException {

    ObjectMapper JSON = new ObjectMapper();
    return JSON.readTree(body).get("query").asText();
  }
}

