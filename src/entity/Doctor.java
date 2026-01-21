package entity;

public class Doctor {
    private final long id;
    private final String name;
    private final String specialty;

    public Doctor(long id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }

    @Override
    public String toString() {
        return "Doctor{id=" + id + ", name='" + name + "', specialty='" + specialty + "'}";
    }
}

