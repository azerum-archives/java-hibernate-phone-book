package phonebook.dal;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.lang.Nullable;
import java.util.List;
import javax.persistence.NoResultException;

import phonebook.entity.Person;
import phonebook.entity.PhoneNumber;

public class Queries {
    private final Session session;

    private Queries(Session session) {
        this.session = session;
    }

    public static Queries query(Session session) {
        return new Queries(session);
    }

    public List<Person> getAllPeople() {
        return session.createQuery("from Person", Person.class)
            .setCacheable(true)
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

    @Nullable
    public Person findPersonByFullName(String firstName, String lastName) {
        Query<Person> query = session.createQuery(
                "from Person where firstName = :firstName and lastName = :lastName",
                Person.class
        )
            .setCacheable(true)
            .setParameter("firstName", firstName)
            .setParameter("lastName", lastName);

        try {
            return query.getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }
}
