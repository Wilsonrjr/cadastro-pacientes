package com.clinica.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class ArquivosEstaticos implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        HttpUtil.adicionarCors(exchange);

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.enviar(
                exchange,
                405,
                "text/plain; charset=utf-8",
                "Método não permitido."
            );
            return;
        }

        String caminhoPedido = exchange.getRequestURI().getPath();

        if (caminhoPedido.equals("/")) {
            caminhoPedido = "/index.html";
        }

        String caminhoRelativo = caminhoPedido.substring(1);

        if (caminhoRelativo.contains("..")) {
            HttpUtil.enviar(
                exchange,
                404,
                "text/plain; charset=utf-8",
                "Página não encontrada."
            );
            return;
        }

        String recurso = "static/" + caminhoRelativo;

        try (InputStream entrada =
                 ArquivosEstaticos.class
                     .getClassLoader()
                     .getResourceAsStream(recurso)) {

            if (entrada == null) {
                HttpUtil.enviar(
                    exchange,
                    404,
                    "text/plain; charset=utf-8",
                    "Página não encontrada."
                );
                return;
            }

            byte[] conteudo = entrada.readAllBytes();

            exchange.getResponseHeaders().set(
                "Content-Type",
                HttpUtil.tipoArquivo(Path.of(caminhoRelativo))
            );

            exchange.sendResponseHeaders(200, conteudo.length);

            try (OutputStream saida = exchange.getResponseBody()) {
                saida.write(conteudo);
            }
        }
    }
}