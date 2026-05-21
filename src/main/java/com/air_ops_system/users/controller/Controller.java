package com.air_ops_system.users.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/asd")
public class Controller {

  @GetMapping("/test")
  public String test() {
    return "API funcionando";
  }
}
