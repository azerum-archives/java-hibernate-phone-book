package phonebook;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import phonebook.dao.PhoneBook;
import phonebook.view.App;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(SpringConfig.class);

        PhoneBook phoneBook = context.getBean(PhoneBook.class);

        App app = new App(phoneBook);
        app.start();

        context.close();
    }
}
