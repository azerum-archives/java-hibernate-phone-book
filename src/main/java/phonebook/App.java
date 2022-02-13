package phonebook;

import java.util.*;
import java.util.function.Consumer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
        options.put("Edit person", this::selectAndEditPerson);
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

        Person person = console.select(
            null,
            people,
            "Select the person whose numbers to manage (0 to cancel) > "
        );

        if (person == null) {
            return;
        }

        managePhonesOf(person);
    }

    private void managePhonesOf(Person person) {
        LinkedHashMap<String, Consumer<Person>> options =
            new LinkedHashMap<>();

        options.put("List all numbers", this::listAllNumbersOf);
        options.put("Add new number", this::addNumberFor);
        options.put("Remove number", this::removeNumberOf);

        while (true) {
            System.out.println("Selected " + person + "\n");

            Consumer<Person> handler = console.select(
                "Go back",
                options,
                "> "
            );

            if (handler == null) {
                return;
            }

            handler.accept(person);
        }
    }

    private void listAllNumbersOf(Person person) {
        List<PhoneNumber> numbers = person.getPhoneNumbers();

        if (numbers.isEmpty()) {
            System.out.println("<No numbers>\n");
        }
        else {
            console.list(person.getPhoneNumbers(), 1);
        }

        console.pressEnterToContinue();
    }

    private void addNumberFor(Person person) {
        System.out.println("Adding new number for " + person + "\n");

        String value;

        while (true) {
            value = console.readLine("Enter phone number > ");
            System.out.println();

            if (value.isEmpty()) {
                return;
            }

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

            System.out.println();
            System.out.println("Leave the line empty to cancel adding");
        }

        PhoneNumber number = new PhoneNumber(value);
        person.addPhoneNumber(number);

        session.save(number);
    }

    private void removeNumberOf(Person person) {
        List<PhoneNumber> numbers = person.getPhoneNumbers();

        PhoneNumber number = console.select(
            null,
            numbers,
            "Select the number to remove (0 to cancel) > "
        );

        if (number != null) {
            person.removePhoneNumber(number);
            session.remove(number);
        }
    }

    private void addPerson() {
        final String howToCancelMessage =
            "Leave any field empty to cancel adding";

        System.out.println("Adding new person");
        System.out.println(howToCancelMessage + "\n");

        String firstName;
        String lastName;

        while (true) {
            firstName = console.readLine("Enter first name > ");

            if (firstName.isEmpty()) {
                System.out.println("\nCancelled\n");
                return;
            }

            lastName = console.readLine("Enter last name > ");

            if (lastName.isEmpty()) {
                System.out.println("\nCancelled\n");
                return;
            }

            System.out.println();

            Person p = Queries.query(session)
                .findPersonByFullName(firstName, lastName);

            if (p == null) {
                break;
            }

            System.out.println(
                "'" + p + "' is already added to the phone book\n"
            );

            System.out.println(howToCancelMessage);
        }

        Person person = new Person(firstName, lastName);
        session.save(person);
    }

    private void selectAndEditPerson() {
        List<Person> people = Queries.query(session).getAllPeople();

        Person person = console.select(
            null,
            people,
            "Select the person to edit (0 to cancel) > "
        );

        if (person == null) {
            return;
        }

        edit(person);
    }

    private void edit(Person person) {
        final String howToCancelMessage =
            "Leave the field empty to keep it unchanged";

        System.out.println("Editing " + person);
        System.out.println(howToCancelMessage + "\n");

        String firstName;
        String lastName;

        while (true) {
            firstName = console.readLine("Enter new first name > ");

            if (firstName.isEmpty()) {
                firstName = person.getFirstName();
            }

            lastName = console.readLine("Enter new last name > ");

            if (lastName.isEmpty()) {
                lastName = person.getLastName();
            }

            System.out.println();

            Person p = Queries.query(session)
                .findPersonByFullName(firstName, lastName);

            //Если пользователь ввел точно такие же имя и фамилию,
            //не редактируем запись
            if (p == person) {
                return;
            }

            if (p == null) {
                break;
            }

            System.out.println(
                "'" + p + "' is already added to the phone book\n"
            );

            System.out.println(howToCancelMessage);
        }

        person.setFirstName(firstName);
        person.setLastName(lastName);

        session.update(person);
    }

    private void removePerson() {
        List<Person> people = Queries.query(session).getAllPeople();

        Person person = console.select(
            null,
            people,
            "Select the person to remove (0 to cancel) > "
        );

        if (person == null) {
            return;
        }

        boolean userIsSure = console.yesNo(
            "This operation will remove" + person + " and ALL their NUMBERS\n" +
            "from the phone book PERMANENTLY.\n" +
            "Are you sure? (y/N) ",
            false
        );

        if (userIsSure) {
            session.remove(person);
        }
    }
}
