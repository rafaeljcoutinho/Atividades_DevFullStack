package com.example.application.views.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Main")
@Route(value = "")
public class MainView extends VerticalLayout {

    private Edificio edificio;
    private Grid<Apartamento> gridApartamentos = new Grid<>(Apartamento.class);

    public MainView() {

        EntityManager en;
        en = JPAUtil.getEntityManager();
        EdificioDao edificioDao = new EdificioDao(en);

        
        edificio = new Edificio(new ArrayList<>());

        SplitLayout splitLayout = new SplitLayout();
        Button addApartmentButton = new Button("Adicionar apartamento");
        Button listApartmentsButton = new Button("Listar apartamentos");

        addApartmentButton.addClickListener(e -> {
            Dialog addApartmentDialog = new Dialog();
            VerticalLayout dialogLayout = new VerticalLayout();
            VerticalLayout namesLayout = new VerticalLayout();
            TextField unitField = new TextField("Unidade");
            TextField nameField = new TextField("Nome do Morador");
            Button addNameButton = new Button("Adicionar Nome");
            Button confirmButton = new Button("Confirmar");
            List<Morador> moradores = new ArrayList<>();

            addNameButton.addClickListener(event -> {
                moradores.add(new Morador(nameField.getValue()));
                namesLayout.add(new Label(nameField.getValue()));
                nameField.clear();
            });

            confirmButton.addClickListener(event -> {
                String numeroUnidade = unitField.getValue();

                boolean existeUnidade = edificio.getApartamentos().stream()
                        .anyMatch(apartamento -> apartamento.getUnidade().equals(numeroUnidade));

                if (!existeUnidade) {
                    Apartamento novoApartamento = new Apartamento(numeroUnidade, moradores);
                    edificio.getApartamentos().add(novoApartamento);
                    edificioDao.incluir(novoApartamento);
                } else {
                    Notification.show("Já existe um apartamento com o número de unidade " + numeroUnidade);
                }
                
                addApartmentDialog.close();
            });

            dialogLayout.add(unitField, nameField, addNameButton, namesLayout, confirmButton);
            addApartmentDialog.add(dialogLayout);
            addApartmentDialog.open();
        });

        listApartmentsButton.addClickListener(e -> {
            gridApartamentos.setItems(edificio.getApartamentos());
        });

        splitLayout.addToPrimary(addApartmentButton);
        splitLayout.addToSecondary(listApartmentsButton);

        add(splitLayout);

        gridApartamentos.addColumn(apartamento -> {
            return apartamento.getMoradores().stream()
                    .map(Morador::getNome)
                    .collect(Collectors.joining(", "));
        }).setHeader("Moradores");

        gridApartamentos.removeColumnByKey("moradores");

        add(gridApartamentos);
    }
}
