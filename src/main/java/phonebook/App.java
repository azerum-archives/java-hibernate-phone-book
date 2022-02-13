package phonebook;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import org.hibernate.Transaction;
import phonebook.dal.Queries;
import phonebook.entity.Person;
import phonebook.entity.PhoneNumber;
import phonebook.view.Console;

public class App {
    private final SessionFactory sessionFactory;
    private Session session = null;

    private final Console console = new Console();

    public App(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void start() {
        LinkedHashMap<String, Runnable> options = new LinkedHashMap<>();

        options.put("List all people", this::listAllPeople);
        options.put("Add new person", this::addPerson);
        options.put("Edit person", this::editPerson);
        options.put("Remove person", this::removePerson);
        options.put("Manage person's phone numbers", this::selectPersonAndManagePhones);

        while (true) {
            Runnable handler = console.select("Exit", options, "> ");

            if (handler == null) {
                return;
            }

            session = sessionFactory.getCurrentSession();
            Transaction tx = session.beginTransaction();

            try {
                handler.run();
            }
            finally {
                tx.commit();
                session.close();

                session = null;
            }
        }
    }

    private void listAllPeople() {
        List<Person> people = Queries.query(session).getAllPeople();
        console.list(people, 1);

        console.pressEnterToContinue();
    }

    private void selectPersonAndManagePhones() {
        List<Person> people = Queries.query(session).getAllPeople();

        while (true) {
            Person person = console.select(
                null,
                people,
                "Select the person whose numbers to manage (0 to cancel) > "
            );

            if (person == null) {
                return;
            }

            if (managePhonesOf(person)) {
                return;
            }
        }
    }

    private boolean managePhonesOf(Person person) {
        LinkedHashMap<String, Function<Person, Boolean>> options =
            new LinkedHashMap<>();

        options.put("List all numbers", this::listAllNumbersOf);
        options.put("Add new number", this::addNumberFor);
        options.put("Remove number", this::removeNumberOf);

        while (true) {
            System.out.println("Selected " + person);
            System.out.println();

            Function<Person, Boolean> handler = console.select(
                "Go back",
                options,
                "> "
            );

            if (handler == null) {
                return false;
            }

            if (handler.apply(person)) {
                return true;
            }
        }
    }

    private boolean listAllNumbersOf(Person person) {
        console.list(person.getPhoneNumbers(), 1);
        console.pressEnterToContinue();

        return true;
    }

    private boolean addNumberFor(Person person) {
        System.out.println("Adding new number for " + person);
        System.out.println("[Leave the line empty to cancel adding]");
        System.out.println();

        String value;

        while (true) {
            value = console.readLine("Enter phone number > ");
            System.out.println();

            PhoneNumber number =
                Queries.query(session).findNumberByValue(value);

            if (number == null) {
                break;
            }

            Person owner = number.getOwner();

            if (owner.getId() == person.getId()) {
                System.out.println(person + " already has this phone number");
                System.out.println("Looks like you've added it before");
            }
            else {
                System.out.println("This phone number is already in the book");
                System.out.println("Used by " + owner);
            }
        }

        person.addPhoneNumber(new PhoneNumber(value));
        session.update(person);

        return true;
    }

    private boolean removeNumberOf(Person person) {
        List<PhoneNumber> numbers = person.getPhoneNumbers();

        PhoneNumber number = console.select(
            null,
            numbers,
            "Select the number to remove (0 to cancel) > "
        );

        if (number == null) {
            return false;
        }

        person.removePhoneNumber(number);
        session.update(person);

        return true;
    }

    private void addPerson() {

    }

    private void editPerson() {
    }

    private void removePerson() {
    }
}
