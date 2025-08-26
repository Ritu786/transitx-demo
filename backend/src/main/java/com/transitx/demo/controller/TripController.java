package com.transitx.demo.controller;

import com.transitx.demo.dao.TripDao;
import com.transitx.demo.model.Trip;
import com.transitx.demo.util.Sanitizer;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip")
public class TripController {
  private final TripDao dao = new TripDao();

  @GetMapping("/search")
  public Map<String, Object> search(@RequestParam String from,
                                    @RequestParam String to,
                                    @RequestParam(required = false, defaultValue = "") String q) {
    boolean vuln = "on".equalsIgnoreCase(System.getenv().getOrDefault("VULN_MODE", "off"));
    String echoed = vuln ? q : Sanitizer.escape(q);
    List<Trip> trips = dao.search(from, to);
    Map<String, Object> res = new HashMap<>();
    res.put("message", "Results for: " + echoed);
    res.put("routes", trips);
    return res;
  }
}
