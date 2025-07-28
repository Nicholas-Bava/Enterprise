package service;

import dto.PersonDTO;
import model.Person;
import repository.PersonRepository;

import java.util.List;

/**
 * Handles the business logic, which in this case is the CRUD operations
 */
public class PersonService {

    PersonRepository personRepository;
    private int personId;

    /**
     * I implement the Singleton pattern here. The single instance of a PersonRepository is passed in to the service
     * constructor (dependency injection). This instance is then used for query operations.
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        personId = 0;
    }

    public Person createPerson(PersonDTO personDTO){
        if (personDTO.getName() == null || personDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        personId++;
        return savePerson(personId, personDTO);
    }

    public boolean deletePerson(int id){
        return personRepository.delete(id);
    }

    public Person findPersonById(int id){
        return personRepository.findById(id);
    }

    public List<Person> findAllPersons(){
        return personRepository.findAll();
    }

    public Person updatePerson(int id, PersonDTO personDTO){
        if (personDTO.getName() == null || personDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        return savePerson(id, personDTO);
    }

    // Helper method for both create and update to use
    private Person savePerson(int id, PersonDTO personDTO){
        Person person = new Person.PersonBuilder(id, personDTO.getName())
                .age(personDTO.getAge())
                .email(personDTO.getEmail())
                .build();
        personRepository.save(person.getId(), person);
        return person;

    }
}
