package repository;


import model.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * My "repository" stores Person objects in the Application's scope (no persistent storage to a database) Note that
 * this also SOMEWHAT acts as an Identity Map. In the real Identity Map pattern, this would connect to a database and the
 * identity map class would contain a Map (like below) to store and reuse any data retrieved from the database.
 * The Identity map would first check its own map for the requested item and provide it if it has it. Otherwise, it
 * retrieves the requested data from the database. However, this class is already using a Map for storage, so there is
 * no real need to create an additional identity map on top of it.
 */
public class PersonRepository {

    private Map<Integer, Person> personRepository;

    public PersonRepository() {
        personRepository = new HashMap<>();
    }

    public void save(int id, Person person) {
        personRepository.put(id, person);
    }

    public Person findById(int id) {
        return personRepository.get(id);
    }

    public boolean delete(int id){
        return personRepository.remove(id) != null;
    }

    public void update(int id, Person person){
        personRepository.put(id, person);
    }
    public List<Person> findAll(){
        return personRepository.values().stream().toList();
    }
}
