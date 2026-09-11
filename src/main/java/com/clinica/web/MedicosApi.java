package com.clinica.web;

import com.clinica.dominio.Medico;
import com.clinica.persistencia.RepositorioMedicos;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class MedicosApi implements HttpHandler {

    private final RepositorioMedicos repositorio;

    public MedicosApi(RepositorioMedicos repositorio) {
        this.repositorio = repositorio;
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
            String crm = HttpUtil.pegarValorJson(corpo, "crm").trim();
            String especialidade = HttpUtil.pegarValorJson(corpo, "especialidade").trim();
            String telefone = HttpUtil.pegarValorJson(corpo, "telefone").trim();
            String email = HttpUtil.pegarValorJson(corpo, "email").trim();

            if (nome.isEmpty()) {
                HttpUtil.enviarJson(exchange, 400, false, "Informe o nome do médico.");
                return;
            }

            if (crm.isEmpty()) {
                HttpUtil.enviarJson(exchange, 400, false, "Informe o CRM do médico.");
                return;
            }

            repositorio.adicionar(nome, crm, especialidade, telefone, email);
            HttpUtil.enviarJson(exchange, 201, true, "Médico cadastrado com sucesso!");
            return;
        }

        if ("DELETE".equalsIgnoreCase(metodo)) {
            int id = HttpUtil.pegarId(exchange.getRequestURI().getQuery());
            if (id <= 0) {
                HttpUtil.enviarJson(exchange, 400, false, "Informe o id do médico.");
                return;
            }

            if (repositorio.remover(id)) {
                HttpUtil.enviarJson(exchange, 200, true, "Médico removido.");
            } else {
                HttpUtil.enviarJson(exchange, 404, false, "Médico não encontrado.");
            }
            return;
        }

        HttpUtil.enviarJson(exchange, 405, false, "Método não permitido.");
    }

    private String listarJson() {
        List<Medico> medicos = repositorio.listar();
        StringBuilder json = new StringBuilder("{\"sucesso\":true,\"medicos\":[");

        for (int i = 0; i < medicos.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            Medico medico = medicos.get(i);
            json.append("{\"id\":").append(medico.getId())
                .append(",\"nome\":\"").append(HttpUtil.escaparJson(medico.getNome())).append("\"")
                .append(",\"crm\":\"").append(HttpUtil.escaparJson(medico.getCrm())).append("\"")
                .append(",\"especialidade\":\"").append(HttpUtil.escaparJson(medico.getEspecialidade())).append("\"")
                .append(",\"telefone\":\"").append(HttpUtil.escaparJson(medico.getTelefone())).append("\"")
                .append(",\"email\":\"").append(HttpUtil.escaparJson(medico.getEmail())).append("\"")
                .append(",\"nomeExibicao\":\"").append(HttpUtil.escaparJson(medico.getNomeExibicao())).append("\"}");
        }

        json.append("]}");
        return json.toString();
    }
}
