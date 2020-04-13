package org.owasp.securityshepherd.application;

import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
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
  
  @Autowired
  SqlInjectionTutorial sqlInjectionTutorial;
  
  @Override
  public void run(ApplicationArguments args) {
    // Create a default admin account
    userService.createPasswordUser("Admin", "admin",
        "$2y$08$WpfUVZLcXNNpmM2VwSWlbe25dae.eEC99AOAVUiU5RaJmfFsE9B5G").block();
    
    sqlInjectionTutorial.initialize().block();
  }
}
