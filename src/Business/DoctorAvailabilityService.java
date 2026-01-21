package Business;

import java.time.LocalDateTime;

public interface DoctorAvailabilityService {
    boolean isDoctorAvailable(int doctorId, LocalDateTime startTime);
}
