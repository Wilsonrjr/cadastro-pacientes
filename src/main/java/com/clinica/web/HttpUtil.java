package com.clinica.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class HttpUtil {

    private HttpUtil() {
    }

    public static void adicionarCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    public static boolean tratarOptions(HttpExchange exchange) throws IOException {
        if (!"OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            return false;
        }
        enviar(exchange, 204, "text/plain; charset=utf-8", "");
        return true;
    }

    public static String lerCorpo(HttpExchange exchange) throws IOException {
        try (InputStream entrada = exchange.getRequestBody()) {
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void enviarJson(HttpExchange exchange, int status, boolean sucesso, String mensagem)
            throws IOException {
        String json = "{\"sucesso\":" + sucesso
            + ",\"mensagem\":\"" + escaparJson(mensagem) + "\"}";
        enviar(exchange, status, "application/json; charset=utf-8", json);
    }

    public static void enviar(HttpExchange exchange, int status, String tipo, String corpo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", tipo);
        if (status == 204) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = exchange.getResponseBody()) {
            saida.write(bytes);
        }
    }

    public static String pegarValorJson(String json, String chave) {
        String busca = "\"" + chave + "\"";
        int indice = json.indexOf(busca);
        if (indice < 0) {
            return "";
        }

        int doisPontos = json.indexOf(':', indice);
        int aspasInicio = json.indexOf('"', doisPontos);
        if (aspasInicio < 0) {
            return "";
        }

        StringBuilder valor = new StringBuilder();
        for (int i = aspasInicio + 1; i < json.length(); i++) {
            char letra = json.charAt(i);
            if (letra == '\\' && i + 1 < json.length()) {
                valor.append(json.charAt(i + 1));
                i++;
                continue;
            }
            if (letra == '"') {
                break;
            }
            valor.append(letra);
        }
        return valor.toString();
    }

    public static int pegarInteiroJson(String json, String chave) {
        String busca = "\"" + chave + "\"";
        int indice = json.indexOf(busca);
        if (indice < 0) {
            return 0;
        }

        int doisPontos = json.indexOf(':', indice);
        if (doisPontos < 0) {
            return 0;
        }

        int inicio = doisPontos + 1;
        while (inicio < json.length() && Character.isWhitespace(json.charAt(inicio))) {
            inicio++;
        }

        if (inicio < json.length() && json.charAt(inicio) == '"') {
            try {
                return Integer.parseInt(pegarValorJson(json, chave).trim());
            } catch (NumberFormatException erro) {
                return 0;
            }
        }

        StringBuilder numero = new StringBuilder();
        for (int i = inicio; i < json.length(); i++) {
            char letra = json.charAt(i);
            if (letra == '-' && numero.length() == 0) {
                numero.append(letra);
                continue;
            }
            if (letra < '0' || letra > '9') {
                break;
            }
            numero.append(letra);
        }

        try {
            return Integer.parseInt(numero.toString());
        } catch (NumberFormatException erro) {
            return 0;
        }
    }

    public static String escaparJson(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static int pegarId(String query) {
        if (query == null || !query.startsWith("id=")) {
            return -1;
        }
        try {
            return Integer.parseInt(query.substring(3));
        } catch (NumberFormatException erro) {
            return -1;
        }
    }

    public static String tipoArquivo(Path arquivo) {
        Map<String, String> tipos = new HashMap<>();
        tipos.put("html", "text/html; charset=utf-8");
        tipos.put("css", "text/css; charset=utf-8");
        tipos.put("js", "application/javascript; charset=utf-8");
        tipos.put("svg", "image/svg+xml");
        tipos.put("png", "image/png");
        tipos.put("ico", "image/x-icon");

        String nome = arquivo.getFileName().toString();
        int ponto = nome.lastIndexOf('.');
        String extensao = ponto >= 0 ? nome.substring(ponto + 1).toLowerCase() : "";
        return tipos.getOrDefault(extensao, "application/octet-stream");
    }
}
