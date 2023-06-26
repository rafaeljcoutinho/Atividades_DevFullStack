package com.example.application.views.main;

import com.example.application.Apartamento;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Route("")
public class MainView extends VerticalLayout  {
    private int apartamentoEmEdicaoIndex = -1;
    private List<Apartamento> apartamentos = new ArrayList<>();
    private Grid<Apartamento> grid = new Grid<>(Apartamento.class);


    private TextField numeroTextField = new TextField("Número");
    private TextField moradorTextField = new TextField("Morador");
    private Button adicionarButton = new Button("Adicionar");
    private Button mostrarButton = new Button("Mostrar Apartamentos");



    public MainView() {
        criarTabela();

        // Cria um HorizontalLayout para os campos de texto
        HorizontalLayout camposLayout = new HorizontalLayout(numeroTextField, moradorTextField);
        camposLayout.setWidth("100%"); 
        camposLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Cria um HorizontalLayout para os botões
        HorizontalLayout botoesLayout = new HorizontalLayout(adicionarButton, mostrarButton);
        botoesLayout.setWidth("100%"); 
        botoesLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        adicionarButton.addClickListener(e -> adicionarApartamento());
        mostrarButton.addClickListener(e -> mostrarApartamentos());

        // Ajuste o tamanho do grid
        grid.setHeight("75vh");  // Configura a altura para 75% da altura da viewport

        // Adiciona os componentes ao layout vertical principal
        add(camposLayout, botoesLayout, grid);

        // Expande o grid para ocupar o espaço disponível
        expand(grid);

        grid.addComponentColumn(this::createDeleteButton).setHeader("Deletar");
        grid.addComponentColumn(this::createEditButton).setHeader("Editar");
    }


    private Button createDeleteButton(Apartamento apartamento) {
        Button deleteButton = new Button("Deletar");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
        // Chama o método para deletar o apartamento do banco de dados
        deletarApartamento(apartamento);
        });
        return deleteButton;
    }

    private void deletarApartamento(Apartamento apartamento) {
        try {
        // Criar uma conexão com o banco de dados SQLite
        Connection connection = DriverManager.getConnection("jdbc:sqlite:src/database/arquivo.db");

        // Cria a instrução SQL para deletar o apartamento com base no número
        String sql = "DELETE FROM apartamentos WHERE numero = ?";

        // Prepara a instrução SQL
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, apartamento.getNumero());

        // Executa a instrução SQL para deletar o apartamento
        int linhasAfetadas = statement.executeUpdate();
        if (linhasAfetadas > 0) {
            mostrarAviso("Apartamento removido com sucesso", NotificationVariant.LUMO_ERROR);

            // Remove o apartamento da lista local
            apartamentos.remove(apartamento);

            // Atualiza o grid com a nova lista de apartamentos
            grid.setItems(apartamentos);
        } else {
            mostrarAviso("Falha ao remover o apartamento", NotificationVariant.LUMO_ERROR);
        }

        // Fechar os recursos
        statement.close();
        connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAviso("Erro ao conectar ao banco de dados", NotificationVariant.LUMO_ERROR);
        }
    }

    private Button createEditButton(Apartamento apartamento) {
        
        Button editButton = new Button("Editar");
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClickListener(e -> {
            numeroTextField.setValue(String.valueOf(apartamento.getNumero()));
            moradorTextField.setValue(apartamento.getMorador());
            apartamentoEmEdicaoIndex = apartamento.getId(); 
            mostrarAviso("ID alterado para "+apartamentoEmEdicaoIndex, NotificationVariant.LUMO_ERROR);
            apartamentos.remove(apartamento);
            adicionarButton.setText("Alterar");
                    

            grid.setItems(apartamentos);

        });
        return editButton;
    }

    private void adicionarApartamento() {
        int numero = Integer.parseInt(numeroTextField.getValue());
        String morador = moradorTextField.getValue();
        int identificador = 0;
        try{
            // Obter o valor atual do contador
            Connection connection = DriverManager.getConnection("jdbc:sqlite:src/database/arquivo.db");

            String sqlSelect = "SELECT id FROM apartamentos ORDER BY id DESC LIMIT 1";
            PreparedStatement selectStatement = connection.prepareStatement(sqlSelect);
            ResultSet resultSet = selectStatement.executeQuery();
            identificador = resultSet.next() ? resultSet.getInt("id") : 0;
            identificador += 1;
            Apartamento apartamento = new Apartamento(numero, morador, identificador);
            apartamentos.add(apartamento);
            numeroTextField.clear();
            moradorTextField.clear();

            // Fecha a conexão e recursos relacionados
            selectStatement.close();
            connection.close();
        }catch (SQLException e){
            mostrarAviso("Falha ao buscar itens na tabela", NotificationVariant.LUMO_ERROR);
        }

        if(adicionarButton.getText().equals("Adicionar")){
            try {
                
            // Criar uma conexão com o banco de dados SQLite
            Connection connection = DriverManager.getConnection("jdbc:sqlite:src/database/arquivo.db");
            
            // Cria a instrução SQL para a inserção dos dados
            String sql = "INSERT INTO apartamentos (numero, morador, id) VALUES (?, ?, ?)";

            // Prepara a instrução SQL
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, numero);
            statement.setString(2, morador);
            statement.setInt(3, identificador);

            // Executa a instrução SQL para inserir os dados
            int linhasAfetadas = statement.executeUpdate();

            if (linhasAfetadas > 0) {
                mostrarAviso("Apartamento adicionado com sucesso!", NotificationVariant.LUMO_SUCCESS);
            } else {
                mostrarAviso("Falha ao adicionar o apartamento", NotificationVariant.LUMO_ERROR);
            }

            // Fecha a conexão e recursos relacionados
            statement.close();
            connection.close();
        }
            catch (SQLException e) {
                e.printStackTrace();
                mostrarAviso("Erro ao conectar ao banco de dados - ADD", NotificationVariant.LUMO_ERROR);
            }
        }
        else{
            try {
                Connection connection = DriverManager.getConnection("jdbc:sqlite:src/database/arquivo.db");

                // Instrução SQL para atualizar os dados do apartamento
                String sql = "UPDATE apartamentos SET id = ?, numero = ?, morador = ? WHERE id = ?";
                
                identificador -= 1;
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, identificador);
                statement.setInt(2, numero);
                statement.setString(3, morador);
                statement.setInt(4, identificador); // Supondo que o objeto Apartamento possua um campo "id"

                // Executa a instrução SQL de atualização
                int linhasAfetadas = statement.executeUpdate();
                if (linhasAfetadas > 0) {
                    mostrarAviso("Apartamento alterado com sucesso!", NotificationVariant.LUMO_SUCCESS);
                } else {
                    mostrarAviso("Falha ao alterar o apartamento com identificador: "+identificador, NotificationVariant.LUMO_ERROR);
                }

            statement.close();
            connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAviso("Erro ao conectar ao banco de dados", NotificationVariant.LUMO_ERROR);
            }
        }
        adicionarButton.setText("Adicionar");
        mostrarApartamentos();
    }

    private void mostrarApartamentos() {
        try {
            // Criar uma conexão com o banco de dados SQLite
            Connection connection = DriverManager.getConnection("jdbc:sqlite:src/database/arquivo.db");

            // Cria a instrução SQL para selecionar todos os apartamentos
            String sql = "SELECT numero, morador, id FROM apartamentos";

            // Prepara a instrução SQL
            PreparedStatement statement = connection.prepareStatement(sql);

            // Executa a instrução SQL para recuperar os dados do banco de dados
            ResultSet resultSet = statement.executeQuery();

            // Limpar a lista de apartamentos
            apartamentos.clear();

            // Iterar pelos resultados do ResultSet
            while (resultSet.next()) {
                int numero = resultSet.getInt("numero");
                String morador = resultSet.getString("morador");
                int id = resultSet.getInt("id");
                // Criar um objeto Apartamento com os dados recuperados
                Apartamento apartamento = new Apartamento(numero, morador, id);

                // Adicionar o apartamento à lista
                apartamentos.add(apartamento);
            }

            // Fechar os recursos
            resultSet.close();
            statement.close();
            connection.close();

            // Atualizar o grid com os novos dados
            grid.setItems(apartamentos);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAviso("Erro ao conectar ao banco de dados", NotificationVariant.LUMO_ERROR);
        }
    }

    private void criarTabela(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:src/database/arquivo.db");
    
            // Instrução SQL para criar a tabela "apartamentos"
            String sql = "CREATE TABLE IF NOT EXISTS apartamentos ("
                + "id INTEGER NOT NULL,"
                + "numero INTEGER NOT NULL,"
                + "morador TEXT NOT NULL"
                + ")";
    
            PreparedStatement statement = connection.prepareStatement(sql);
    
            // Executa a instrução SQL para criar a tabela
            statement.execute();
    
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAviso("Erro ao conectar ao banco de dados", NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void mostrarAviso(String mensagem, NotificationVariant variant) {
        Notification notification = new Notification(mensagem, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
