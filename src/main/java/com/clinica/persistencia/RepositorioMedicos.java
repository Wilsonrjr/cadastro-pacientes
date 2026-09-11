package com.clinica.persistencia;

import com.clinica.dominio.Medico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMedicos {

    private final Path arquivo;
    private final List<Medico> medicos = new ArrayList<>();
    private int proximoId = 1;

    public RepositorioMedicos(Path arquivo) throws IOException {
        this.arquivo = arquivo;
        carregar();
    }

    public synchronized List<Medico> listar() {
        return new ArrayList<>(medicos);
    }

    public synchronized Medico buscarPorId(int id) {
        for (Medico medico : medicos) {
            if (medico.getId() == id) {
                return medico;
            }
        }
        return null;
    }

    public synchronized Medico adicionar(
        String nome,
        String crm,
        String especialidade,
        String telefone,
        String email
    ) throws IOException {
        Medico medico = new Medico(
            proximoId++,
            limpar(nome),
            limpar(crm),
            limpar(especialidade),
            limpar(telefone),
            limpar(email)
        );
        medicos.add(medico);
        salvar();
        return medico;
    }

    public synchronized boolean remover(int id) throws IOException {
        boolean removeu = medicos.removeIf(medico -> medico.getId() == id);
        if (removeu) {
            salvar();
        }
        return removeu;
    }

    private void carregar() throws IOException {
        medicos.clear();
        proximoId = 1;

        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (linha.isBlank()) {
                continue;
            }

            Medico medico = Medico.deLinha(linha);
            if (medico == null) {
                continue;
            }

            medicos.add(medico);
            if (medico.getId() >= proximoId) {
                proximoId = medico.getId() + 1;
            }
        }
    }

    private void salvar() throws IOException {
        StringBuilder texto = new StringBuilder();
        for (Medico medico : medicos) {
            texto.append(medico.paraLinha()).append("\n");
        }
        Files.writeString(arquivo, texto.toString(), StandardCharsets.UTF_8);
    }

    private static String limpar(String texto) {
        return texto.replace("|", " ").replace("\n", " ").replace("\r", " ").trim();
    }
}
