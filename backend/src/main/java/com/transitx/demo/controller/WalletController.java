package com.transitx.demo.controller;

import com.transitx.demo.dao.WalletDao;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
  private final WalletDao dao;

  public WalletController(JdbcTemplate jdbc) { this.dao = new WalletDao(jdbc); }

  @GetMapping("/{userId}")
  public Map<String, Object> balance(@PathVariable String userId,
                                     @RequestHeader(value = "X-User", required = false) String current) {
    boolean vuln = "on".equalsIgnoreCase(System.getenv().getOrDefault("VULN_MODE", "off"));
    if (!vuln) {
      if (current == null || !current.equals(userId)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your wallet");
      }
    }
    return dao.getBalance(userId);
  }

  @PostMapping("/{userId}/topup")
  public Map<String, Object> topup(@PathVariable String userId, @RequestParam int amount) {
    dao.topup(userId, amount);
    return Map.of("status", "OK");
  }
}
