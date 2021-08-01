package main.domain;

import main.infra.exception.DifferentDatesException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Guest {

    private final String email;
    private final Map<LocalDate, List<TimeInterval>> availability;

    public Guest(String email) {
        this.email = email;
        this.availability = new HashMap<>();
    }

    public void addAvailability(LocalDateTime start, LocalDateTime end) {
        if (!start.toLocalDate().isEqual(end.toLocalDate()))
            throw new DifferentDatesException(start.toLocalDate(), end.toLocalDate());

        final LocalDate date = start.toLocalDate();

        if (!availability.containsKey(date)) availability.put(date, TimeInterval.buildTimeIntervalList(date));

        List<TimeInterval> timeIntervals = availability.get(date);
        TimeInterval.addAvailability(start, end, timeIntervals);
    }

    public String getEmail() {
        return email;
    }

    public Map<LocalDate, List<TimeInterval>> getAvailability() {
        return availability;
    }
}
