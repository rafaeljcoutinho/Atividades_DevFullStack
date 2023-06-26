package com.example.application;

public class Apartamento {
    private int numero;
    private int id;
    private String morador;
    public Apartamento(int numero, String morador, int id) {
        this.numero = numero;
        this.morador = morador;
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public String getMorador() {
        return morador;
    }

    public int getId() {
        return id;
    }
}
