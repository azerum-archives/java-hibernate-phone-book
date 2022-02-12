package phonebook.dao;

import org.hibernate.Session;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import phonebook.entity.Person;
import phonebook.entity.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhoneBook {
    Session session;

    public List<Person> getFirstNPeople(int n) {
        return new ArrayList<>();
    }

    public List<Person> getAllPeople() {
        return new ArrayList<>();
    }

    public List<PhoneNumber> getAllNumbersOf(Person person) {
        return new ArrayList<>();
    }

    @Nullable
    public Person findPersonByNumber(PhoneNumber number) {
        return null;
    }

    public void add(PhoneNumber number) {

    }

    public void update(Person person) {

    }
}
