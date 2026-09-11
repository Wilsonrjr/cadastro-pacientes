package com.clinica.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArquivosEstaticos implements HttpHandler {

    private final Path pastaStatic;

    public ArquivosEstaticos(Path pastaStatic) {
        this.pastaStatic = pastaStatic;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.adicionarCors(exchange);

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.enviar(exchange, 405, "text/plain; charset=utf-8", "Método não permitido.");
            return;
        }

        String caminhoPedido = exchange.getRequestURI().getPath();
        if (caminhoPedido.equals("/")) {
            caminhoPedido = "/index.html";
        }

        Path arquivo = pastaStatic.resolve(caminhoPedido.substring(1)).normalize();
        if (!arquivo.startsWith(pastaStatic) || !Files.isRegularFile(arquivo)) {
            HttpUtil.enviar(exchange, 404, "text/plain; charset=utf-8", "Página não encontrada.");
            return;
        }

        byte[] conteudo = Files.readAllBytes(arquivo);
        exchange.getResponseHeaders().set("Content-Type", HttpUtil.tipoArquivo(arquivo));
        exchange.sendResponseHeaders(200, conteudo.length);
        try (OutputStream saida = exchange.getResponseBody()) {
            saida.write(conteudo);
        }
    }
}
