package org.owasp.securityshepherd.controller;

import java.util.Map;
import org.owasp.securityshepherd.module.SqlModule;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class SqlModuleController {
  private final SqlModule sqlModule;

  @PostMapping(path = "challenge/sql/submit")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Map<String, Object>> submit(@RequestBody final String sql) {
    return sqlModule.submitSql(1L, 2L, sql);
  }
}
