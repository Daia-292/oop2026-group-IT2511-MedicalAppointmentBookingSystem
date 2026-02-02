package config;

import java.time.LocalTime;

public final class WorkingHoursProvider {

    private static final WorkingHoursProvider INSTANCE = new WorkingHoursProvider();

    private final LocalTime openTime;
    private final LocalTime closeTime;

    private WorkingHoursProvider() {
        this.openTime = LocalTime.of(9, 0);
        this.closeTime = LocalTime.of(18, 0);
    }

    public static WorkingHoursProvider getInstance() {
        return INSTANCE;
    }

    public boolean isWithin(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return false;
        }
        return !start.isBefore(openTime) && !end.isAfter(closeTime);
    }
}
