package dto;

/**
 * I am using the DTO pattern to transfer Person objects without needing to use the full object. In this case
 * they are largely the same. In general, this would help keep private information hidden and only transfer the details
 * that are needed.
 */
public class PersonDTO {
    private String name;
    private String email;
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
