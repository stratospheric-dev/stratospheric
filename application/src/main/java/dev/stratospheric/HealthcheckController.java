package dev.stratospheric;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HealthcheckController {

  @GetMapping("/health")
  ResponseEntity<String> health(){
    return ResponseEntity.ok("healthy");
  }

}
