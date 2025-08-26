package com.transitx.demo.controller;

import com.transitx.demo.dao.FinesDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fines")
public class FinesController {
  private final FinesDao dao;

  public FinesController(JdbcTemplate jdbc) { this.dao = new FinesDao(jdbc); }

  @GetMapping("/lookup")
  public List<Map<String, Object>> lookup(@RequestParam String plate) {
    boolean vuln = "on".equalsIgnoreCase(System.getenv().getOrDefault("VULN_MODE", "off"));
    return vuln ? dao.findByPlateUnsafe(plate) : dao.findByPlateSafe(plate);
  }
}
