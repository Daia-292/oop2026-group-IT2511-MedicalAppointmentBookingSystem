package entity;

public class Patient {
    private long id;
    private String name;
    private String email;
    private int phone;

    public Patient(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getPhone() { return phone; }

    public void setId(int id) { this.id = id; }
    public void setName(String Name) { this.name = name; }
    public void setPhone(int phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }


    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name + "', email='" + email + "', phone='" + phone + "'}";
    }
}
