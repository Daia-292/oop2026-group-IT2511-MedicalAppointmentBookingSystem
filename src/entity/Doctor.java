package entity;

public class Doctor {
    private Long id;
    private String fullName;
    private String specialty;
    private boolean active;

    public Doctor() {}

    public Doctor(Long id, String fullName, String specialty, boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.specialty = specialty;
        this.active = active;
    }

    public Doctor(String fullName, String specialty, boolean active) {
        this(null, fullName, specialty, active);
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getSpecialty() { return specialty; }
    public boolean isActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Doctor{id=" + id + ", fullName='" + fullName + "', specialty='" + specialty + "', active=" + active + "}";
    }
}
