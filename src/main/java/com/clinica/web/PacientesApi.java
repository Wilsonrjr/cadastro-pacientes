package com.clinica.web;

import com.clinica.dominio.Medico;
import com.clinica.dominio.Paciente;
import com.clinica.persistencia.RepositorioMedicos;
import com.clinica.persistencia.RepositorioPacientes;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class PacientesApi implements HttpHandler {

    private final RepositorioPacientes repositorioPacientes;
    private final RepositorioMedicos repositorioMedicos;

    public PacientesApi(RepositorioPacientes repositorioPacientes, RepositorioMedicos repositorioMedicos) {
        this.repositorioPacientes = repositorioPacientes;
        this.repositorioMedicos = repositorioMedicos;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.adicionarCors(exchange);

        if (HttpUtil.tratarOptions(exchange)) {
            return;
        }

        String metodo = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(metodo)) {
            HttpUtil.enviar(exchange, 200, "application/json; charset=utf-8", listarJson());
            return;
        }

        if ("POST".equalsIgnoreCase(metodo)) {
            String corpo = HttpUtil.lerCorpo(exchange);
            String nome = HttpUtil.pegarValorJson(corpo, "nome").trim();
            String cpf = HttpUtil.pegarValorJson(corpo, "cpf").trim();
            String dataNascimento = HttpUtil.pegarValorJson(corpo, "dataNascimento").trim();
            String sexo = HttpUtil.pegarValorJson(corpo, "sexo").trim();
            String telefone = HttpUtil.pegarValorJson(corpo, "telefone").trim();
            String email = HttpUtil.pegarValorJson(corpo, "email").trim();
            String convenio = HttpUtil.pegarValorJson(corpo, "convenio").trim();
            String tipoSanguineo = HttpUtil.pegarValorJson(corpo, "tipoSanguineo").trim();
            String observacoes = HttpUtil.pegarValorJson(corpo, "observacoes").trim();
            int medicoId = HttpUtil.pegarInteiroJson(corpo, "medicoId");
            String medicoNome = HttpUtil.pegarValorJson(corpo, "medicoNome").trim();

            if (nome.isEmpty()) {
                HttpUtil.enviarJson(exchange, 400, false, "Informe o nome do paciente.");
                return;
            }

            if (medicoId > 0) {
                Medico medico = repositorioMedicos.buscarPorId(medicoId);
                if (medico == null) {
                    HttpUtil.enviarJson(exchange, 400, false, "Médico selecionado não encontrado. Cadastre o médico antes.");
                    return;
                }
                medicoNome = medico.getNomeExibicao();
            } else {
                medicoId = 0;
                medicoNome = "";
            }

            repositorioPacientes.adicionar(
                nome,
                cpf,
                dataNascimento,
                sexo,
                telefone,
                email,
                convenio,
                tipoSanguineo,
                observacoes,
                medicoId,
                medicoNome
            );
            HttpUtil.enviarJson(exchange, 201, true, "Paciente cadastrado com sucesso!");
            return;
        }

        if ("DELETE".equalsIgnoreCase(metodo)) {
            int id = HttpUtil.pegarId(exchange.getRequestURI().getQuery());
            if (id <= 0) {
                HttpUtil.enviarJson(exchange, 400, false, "Informe o id do paciente.");
                return;
            }

            if (repositorioPacientes.remover(id)) {
                HttpUtil.enviarJson(exchange, 200, true, "Paciente removido.");
            } else {
                HttpUtil.enviarJson(exchange, 404, false, "Paciente não encontrado.");
            }
            return;
        }

        HttpUtil.enviarJson(exchange, 405, false, "Método não permitido.");
    }

    private String listarJson() {
        List<Paciente> pacientes = repositorioPacientes.listar();
        StringBuilder json = new StringBuilder("{\"sucesso\":true,\"pacientes\":[");

        for (int i = 0; i < pacientes.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            Paciente paciente = pacientes.get(i);
            json.append("{\"id\":").append(paciente.getId())
                .append(",\"nome\":\"").append(HttpUtil.escaparJson(paciente.getNome())).append("\"")
                .append(",\"cpf\":\"").append(HttpUtil.escaparJson(paciente.getCpf())).append("\"")
                .append(",\"dataNascimento\":\"").append(HttpUtil.escaparJson(paciente.getDataNascimento())).append("\"")
                .append(",\"sexo\":\"").append(HttpUtil.escaparJson(paciente.getSexo())).append("\"")
                .append(",\"telefone\":\"").append(HttpUtil.escaparJson(paciente.getTelefone())).append("\"")
                .append(",\"email\":\"").append(HttpUtil.escaparJson(paciente.getEmail())).append("\"")
                .append(",\"convenio\":\"").append(HttpUtil.escaparJson(paciente.getConvenio())).append("\"")
                .append(",\"tipoSanguineo\":\"").append(HttpUtil.escaparJson(paciente.getTipoSanguineo())).append("\"")
                .append(",\"observacoes\":\"").append(HttpUtil.escaparJson(paciente.getObservacoes())).append("\"")
                .append(",\"medicoId\":").append(paciente.getMedicoId())
                .append(",\"medicoNome\":\"").append(HttpUtil.escaparJson(paciente.getMedicoNome())).append("\"}");
        }

        json.append("]}");
        return json.toString();
    }
}
