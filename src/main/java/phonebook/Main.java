package phonebook;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.logging.Logger;

import phonebook.entity.Person;
import phonebook.entity.PhoneNumber;

public class Main {
    public static void main(String[] args) {
        //Отключаем все логирование от Hibernate
        Logger.getLogger("org.hibernate")
            .setLevel(java.util.logging.Level.OFF);

        SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Person.class)
            .addAnnotatedClass(PhoneNumber.class)
            .buildSessionFactory();

        App app = new App(sessionFactory);
        app.start();

        sessionFactory.close();
    }
}
