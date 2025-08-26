package com.transitx.demo.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class FinesDao {
  private final JdbcTemplate jdbc;
  public FinesDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Map<String,Object>> findByPlateSafe(String plate) {
    return jdbc.queryForList("SELECT id, plate, amount FROM FINES WHERE plate = ?", plate);
  }

  // ❌ intentionally vulnerable: SQL built by concatenation
  public List<Map<String,Object>> findByPlateUnsafe(String plate) {
    String sql = "SELECT id, plate, amount FROM FINES WHERE plate = '" + plate + "'";
    return jdbc.queryForList(sql);
  }
}
