package entity;

public class Doctor {
    private long id;
    private String name;
    private String specialty;
    private boolean active;

    public Doctor(long id, String name, String specialty, boolean active) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.active = active;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public boolean isActive() { return active; }

    public void setId(int id) { this.id = id; }
    public void setName(String Name) { this.name = Name; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Doctor{id=" + id + ", name='" + name + "', specialty='" + specialty + "'}";
    }
}

