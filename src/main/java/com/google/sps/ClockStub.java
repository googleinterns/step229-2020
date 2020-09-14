// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

public final class ClockStub implements MyClock {
  private String time;

  public void setTime(String time) {
    this.time = time;  
  }
  
  public String getCurrentTime() {
    return time;
  }
}