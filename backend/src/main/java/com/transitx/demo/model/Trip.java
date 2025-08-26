package com.transitx.demo.model;

public class Trip {
  public String from;
  public String to;
  public int minutes;
  public String mode;

  public Trip() {}
  public Trip(String from, String to, int minutes, String mode) {
    this.from = from; this.to = to; this.minutes = minutes; this.mode = mode;
  }
}
