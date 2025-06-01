package dev.biddan.nubblev2;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FixableClock extends Clock {

    private Clock currentClock;

    public FixableClock() {
        this.currentClock = Clock.systemDefaultZone();
    }

    @Override
    public ZoneId getZone() {
        return currentClock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new FixableClock().setDelegate(currentClock.withZone(zone));
    }

    @Override
    public Instant instant() {
        return currentClock.instant();
    }

    private FixableClock setDelegate(Clock clock) {
        this.currentClock = clock;
        return this;
    }

    public void setFixedTime(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = dateTime.atZone(getZone());
        this.currentClock = Clock.fixed(zonedDateTime.toInstant(), getZone());
    }

    public void setFixedTime(int year, int month, int day, int hour, int minute) {
        setFixedTime(LocalDateTime.of(year, month, day, hour, minute));
    }

    public void advanceTime(java.time.Duration duration) {
        Instant newInstant = instant().plus(duration);
        this.currentClock = Clock.fixed(newInstant, getZone());
    }

    public void reset() {
        this.currentClock = Clock.systemDefaultZone();
    }

    public LocalDateTime now() {
        return LocalDateTime.now(this);
    }
}
