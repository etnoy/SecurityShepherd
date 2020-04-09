package org.owasp.securityshepherd.application;

import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

  @Autowired
  UserService userService;

  @Autowired
  ModuleService moduleService;
  
  @Override
  public void run(ApplicationArguments args) throws Exception {
    userService.createPasswordUser("Admin", "admin",
        "$2y$08$WpfUVZLcXNNpmM2VwSWlbe25dae.eEC99AOAVUiU5RaJmfFsE9B5G").block();
    
    moduleService.create("Sql Injection").block();
    moduleService.create("XSS").block();
    moduleService.create("Poor Data Validation").block();


  }
}
