package com.clinica.persistencia;

import com.clinica.dominio.Paciente;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPacientes {

    private final Path arquivo;
    private final List<Paciente> pacientes = new ArrayList<>();
    private int proximoId = 1;

    public RepositorioPacientes(Path arquivo) throws IOException {
        this.arquivo = arquivo;
        carregar();
    }

    public synchronized List<Paciente> listar() {
        return new ArrayList<>(pacientes);
    }

    public synchronized Paciente adicionar(
        String nome,
        String cpf,
        String dataNascimento,
        String sexo,
        String telefone,
        String email,
        String convenio,
        String tipoSanguineo,
        String observacoes,
        int medicoId,
        String medicoNome
    ) throws IOException {
        Paciente paciente = new Paciente(
            proximoId++,
            limpar(nome),
            limpar(cpf),
            limpar(dataNascimento),
            limpar(sexo),
            limpar(telefone),
            limpar(email),
            limpar(convenio),
            limpar(tipoSanguineo),
            limpar(observacoes),
            medicoId,
            limpar(medicoNome)
        );
        pacientes.add(paciente);
        salvar();
        return paciente;
    }

    public synchronized boolean remover(int id) throws IOException {
        boolean removeu = pacientes.removeIf(paciente -> paciente.getId() == id);
        if (removeu) {
            salvar();
        }
        return removeu;
    }

    private void carregar() throws IOException {
        pacientes.clear();
        proximoId = 1;

        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (linha.isBlank()) {
                continue;
            }

            Paciente paciente = Paciente.deLinha(linha);
            if (paciente == null) {
                continue;
            }

            pacientes.add(paciente);
            if (paciente.getId() >= proximoId) {
                proximoId = paciente.getId() + 1;
            }
        }
    }

    private void salvar() throws IOException {
        StringBuilder texto = new StringBuilder();
        for (Paciente paciente : pacientes) {
            texto.append(paciente.paraLinha()).append("\n");
        }
        Files.writeString(arquivo, texto.toString(), StandardCharsets.UTF_8);
    }

    private static String limpar(String texto) {
        return texto.replace("|", " ").replace("\n", " ").replace("\r", " ").trim();
    }
}
