package hiber.dao;

import hiber.model.Car;
import hiber.model.User;
import org.hibernate.HibernateError;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserDaoImp implements UserDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDaoImp(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void add(User user) {
        sessionFactory.getCurrentSession().save(user);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> listUsers() {
        TypedQuery<User> query = sessionFactory.getCurrentSession().createQuery("from User");
        return query.getResultList();
    }

    public User getUser(String model, int series) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            List<User> users = session.createQuery("from User where car.model = :modelParam and car.series = :seriesParam")
                    .setParameter("modelParam", model)
                    .setParameter("seriesParam", series)
                    .list();

            if (users.size() != 0) {
                return users.get(0);
            }
        } catch (HibernateError e) {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }

        return null;
    }
}
