package com.example.application.views.main;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "apartamento")
public class Apartamento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @Column(name = "unidade")
    private String unidade;

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
