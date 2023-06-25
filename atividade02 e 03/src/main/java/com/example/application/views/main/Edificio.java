package com.example.application.views.main;

import java.util.List;

public class Edificio {
    private List<Apartamento> apartamentos;

    public Edificio(List<Apartamento> apartamentos) {
        this.apartamentos = apartamentos;
    }

    public List<Apartamento> getApartamentos() {
        return apartamentos;
    }

    public void setApartamentos(List<Apartamento> apartamentos) {
        this.apartamentos = apartamentos;
    }
}
