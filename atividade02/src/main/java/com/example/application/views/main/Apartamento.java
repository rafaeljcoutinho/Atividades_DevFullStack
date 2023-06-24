package com.example.application.views.main;

import java.util.List;

public class Apartamento {
    private String unidade;
    private List<Morador> moradores;

    public Apartamento(String unidade, List<Morador> moradores) {
        this.unidade = unidade;
        this.moradores = moradores;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public List<Morador> getMoradores() {
        return moradores;
    }

    public void setMoradores(List<Morador> moradores) {
        this.moradores = moradores;
    }
}
