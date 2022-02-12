package phonebook.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import java.util.List;

import phonebook.entity.Person;
import phonebook.entity.PhoneNumber;

import javax.persistence.NoResultException;

@Component
public class PhoneBook {
    Session session;

    public List<Person> getAllPeople() {
        return session.createQuery("from Person", Person.class)
            .setCacheable(true)
            .getResultList();
    }

    public List<PhoneNumber> getAllNumbersOf(Person person) {
        return session.createQuery(
                "from PhoneNumber where owner = :person",
                PhoneNumber.class
            )
            .setCacheable(true)
            .setParameter("person", person)
            .getResultList();
    }

    @Nullable
    public PhoneNumber findNumberByValue(String value) {
        Query<PhoneNumber> query = session.createQuery(
                "from PhoneNumber where value = :value",
                PhoneNumber.class
            )
            .setCacheable(true)
            .setParameter("value", value);

        try {
            return query.getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public void update(Person person) {
        session.update(person);
    }
}
