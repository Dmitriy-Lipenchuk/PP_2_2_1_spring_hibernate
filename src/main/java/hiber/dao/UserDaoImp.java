package hiber.dao;

import hiber.model.Car;
import hiber.model.User;
import org.hibernate.HibernateError;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserDaoImp implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

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

            List<Car> cars = session.createQuery("from Car where series = :seriesParam and model = :modelParam")
                    .setParameter("seriesParam", series)
                    .setParameter("modelParam", model)
                    .list();

            return session.get(User.class, cars.get(0).getUser().getId());
        } catch (HibernateError e) {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }

        return null;
    }

}
