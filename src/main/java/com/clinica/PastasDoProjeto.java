package com.clinica;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PastasDoProjeto {

    private PastasDoProjeto() {
    }

    public static Path pastaStatic() {
        Path pasta = Path.of("src", "main", "resources", "static").toAbsolutePath().normalize();
        if (Files.isDirectory(pasta) && Files.exists(pasta.resolve("index.html"))) {
            return pasta;
        }

        throw new IllegalStateException(
            "Pasta static não encontrada. Abra o terminal na pasta cadastro-pacientes e rode iniciar.bat"
        );
    }

    public static Path arquivoPacientes() throws IOException {
        return arquivoDados("pacientes.txt");
    }

    public static Path arquivoMedicos() throws IOException {
        return arquivoDados("medicos.txt");
    }

    private static Path arquivoDados(String nomeArquivo) throws IOException {
        Path pasta = Path.of("data").toAbsolutePath().normalize();
        Files.createDirectories(pasta);

        Path arquivo = pasta.resolve(nomeArquivo);
        if (!Files.exists(arquivo)) {
            Files.writeString(arquivo, "", StandardCharsets.UTF_8);
        }
        return arquivo;
    }
}
