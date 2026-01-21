package entity;

public class Patient {
    private Long id;
    private String fullName;
    private String phone;

    public Patient() {}

    public Patient(Long id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    public Patient(String fullName, String phone) {
        this(null, fullName, phone);
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", fullName='" + fullName + "', phone='" + phone + "'}";
    }
}
