package phonebook.dao;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
class PhoneBookAspect {
    private final SessionFactory sessionFactory;

    public PhoneBookAspect(@Autowired SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Around("execution(public * phonebook.dao.PhoneBook.*(..))")
    public Object setNewSessionBeforeEveryPublicMethodAndCloseSessionAfterwards(
        ProceedingJoinPoint joinPoint
    ) throws Throwable {
        PhoneBook phoneBook = (PhoneBook)joinPoint.getTarget();

        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();

        phoneBook.session = session;

        try {
            return joinPoint.proceed();
        }
        finally {
            tx.commit();
            session.close();

            phoneBook.session = null;
        }
    }
}
