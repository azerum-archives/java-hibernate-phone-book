package phonebook.view;

import phonebook.dao.PhoneBook;
import phonebook.entity.Person;
import phonebook.entity.PhoneNumber;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public class App {
    private final PhoneBook phoneBook;
    private final Console console = new Console();

    public App(PhoneBook phoneBook) {
        this.phoneBook = phoneBook;
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

            handler.run();
        }
    }

    private void listAllPeople() {
        List<Person> people = phoneBook.getAllPeople();
        console.list(people, 1);
    }

    private void selectPersonAndManagePhones() {
        List<Person> people = phoneBook.getAllPeople();

        while (true) {
            Person person = console.select(
                "Cancel",
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
        List<PhoneNumber> numbers = phoneBook.getAllNumbersOf(person);
        console.list(numbers, 1);

        return true;
    }

    private boolean addNumberFor(Person person) {
        System.out.println("Adding new number for " + person);
        System.out.println("[Leave the line empty to cancel adding]");
        System.out.println();

        PhoneNumber number;

        while (true) {
            String value = console.readLine("Enter phone number > ");
            number = new PhoneNumber(value);

            Person owner = phoneBook.findPersonByNumber(number);

            if (owner == null) {
                break;
            }

            if (owner.getId() == person.getId()) {
                System.out.println(person + " already has this phone number");
                System.out.println("Looks like you've added it before");
            }
            else {
                System.out.println("This phone number is already in the book");
                System.out.println("Used by " + owner);
            }

            System.out.println();
        }

        number.setPerson(person);
        phoneBook.add(number);

        return true;
    }

    private boolean removeNumberOf(Person person) {
        List<PhoneNumber> numbers = phoneBook.getAllNumbersOf(person);

        PhoneNumber number = console.select(
            null,
            numbers,
            "Select the number to remote (0 to cancel) > "
        );

        if (number == null) {
            return false;
        }

        person.removePhoneNumber(number);
        phoneBook.update(person);

        return true;
    }

    private void addPerson() {

    }

    private void editPerson() {
    }

    private void removePerson() {
    }
}
