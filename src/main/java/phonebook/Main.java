package phonebook;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.logging.Logger;

import phonebook.dao.PhoneBook;
import phonebook.view.App;

public class Main {
    public static void main(String[] args) {
        //Отключаем все логирование от Hibernate
        Logger.getLogger("org.hibernate")
            .setLevel(java.util.logging.Level.OFF);

        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(SpringConfig.class);

        PhoneBook phoneBook = context.getBean(PhoneBook.class);

        App app = new App(phoneBook);
        app.start();

        context.close();
    }
}
