package entity;

public class Patient {
    private final long id;
    private final String name;
    private final String email;
    private final String phone;

    public Patient(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name + "', email='" + email + "', phone='" + phone + "'}";
    }
}
