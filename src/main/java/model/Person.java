package model;

public class Person {

    private int id;
    private String name;
    private int age;
    private String email;

    private Person(PersonBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
    }

    /**
    * I am using a PersonBuilder to implement the Builder Pattern. This is a static inner class so that it can both
    * be used prior to instantiation of a Person AND so that it can access the private Constructor. The Client
    * (PersonService) builds a person by creating a PersonBuilder with id and name, age() and email() can be used to set
    * these, and calling build()
     */
    public static class PersonBuilder {
        private final int id;
        private final String name;

        private String email;
        private int age;

        public PersonBuilder(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public PersonBuilder email(String email) {
            this.email = email;
            return this;
        }

        public PersonBuilder age(int age) {
            this.age = age;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
