package com.example.application.views.main;

import java.util.List;

import javax.persistence.EntityManager;


public class EdificioDao {
	private EntityManager entityManager;

    public EdificioDao(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public void incluir(Apartamento apartamento){
        entityManager.getTransaction().begin();
        entityManager.persist(apartamento);
        entityManager.getTransaction().commit();
        //entityManager.close();
    }



}
