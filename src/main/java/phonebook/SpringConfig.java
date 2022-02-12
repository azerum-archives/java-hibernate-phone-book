package phonebook;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import phonebook.entity.Person;
import phonebook.entity.PhoneNumber;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("phonebook.dao")
public class SpringConfig {
    @Bean
    public SessionFactory sessionFactory() {
        return new org.hibernate.cfg.Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Person.class)
            .addAnnotatedClass(PhoneNumber.class)
            .buildSessionFactory();
    }
}
