package com.transitx.demo.dao;

import com.transitx.demo.model.Trip;

import java.util.Arrays;
import java.util.List;

public class TripDao {
  public List<Trip> search(String from, String to) {
    return Arrays.asList(
      new Trip(from, to, 18, "Metro + Tram"),
      new Trip(from, to, 25, "Bus Express"),
      new Trip(from, to, 32, "Taxi")
    );
  }
}
