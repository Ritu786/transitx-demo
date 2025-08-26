package com.transitx.demo.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

public class WalletDao {
  private final JdbcTemplate jdbc;
  public WalletDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public Map<String,Object> getBalance(String userId) {
    try {
      return jdbc.queryForMap("SELECT id, balance FROM WALLET WHERE id = ?", userId);
    } catch (EmptyResultDataAccessException e) {
      jdbc.update("INSERT INTO WALLET(id, balance) VALUES (?, ?)", userId, 0);
      return Map.of("id", userId, "balance", 0);
    }
  }

  public void topup(String userId, int amount) {
    jdbc.update("UPDATE WALLET SET balance = balance + ? WHERE id = ?", amount, userId);
  }
}
