package com.example.application.views.main;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Service;

@Service
public class ApartamentoService {

    private SessionFactory sessionFactory;

    public ApartamentoService() {
        Configuration configuration = new Configuration().configure();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public void salvarApartamento(Apartamento apartamento) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(apartamento);

        session.getTransaction().commit();
        session.close();
    }
}
