// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<Event> relevantEvents = new ArrayList<Event>();
    Map<Integer, Integer> times = new HashMap<Integer, Integer>();
    Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();

    initializeMap(times);
    reduceToRelevantEvents(relevantEvents, events, request);
    markBusyTimes(relevantEvents, times);
    getAvailableTimes(request.getDuration(), availableTimes, times);

    return availableTimes;
  }

  private void initializeMap(Map<Integer, Integer> times) {
    for (int i = 0; i < 1440; i++) {
      times.put(i, 0);
    }
  }

  private void reduceToRelevantEvents(Collection<Event> relevantEvents, Collection<Event> events, MeetingRequest request) {
    for (Event event: events) {
      for (String attendee: request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          relevantEvents.add(event);
          break;
        }
      }
    }
  }

  private void markBusyTimes(Collection<Event> relevantEvents, Map<Integer, Integer> times) {
    for (Event event: relevantEvents) {
      for(int i = event.getWhen().start(); i < event.getWhen().end(); i++) {
        times.put(i, 1);
      }
    }
  }

  private void getAvailableTimes(long duration, Collection<TimeRange> availableTimes, Map<Integer, Integer> times) {
    for(int i = 0; i < 1440; i++) {
      if(times.get(i) == 0) {
        int start = i;
        while(i < 1440 && times.get(i) == 0) {
          i++;
        }
        int end = i;
        if(durationLongEnough(start, end, duration)) {
          availableTimes.add(TimeRange.fromStartEnd(start, end, false));
        }
      }
    }
  }

  private boolean durationLongEnough(int start, int end, long duration) {
    if (end - start >= (int) duration) {
      return true;
    }
    return false;
  }
}
