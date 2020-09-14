// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

public final class ClockServer implements MyClock {
  public String getCurrentTime() {
    return java.time.Clock.systemUTC().instant().toString();
  }
}