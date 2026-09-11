package com.clinica;

import com.clinica.persistencia.RepositorioMedicos;
import com.clinica.persistencia.RepositorioPacientes;
import com.clinica.web.ServidorWeb;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Path;

public class ClinicaAplicacao {

    public static void main(String[] args) throws Exception {
        Path pastaStatic = PastasDoProjeto.pastaStatic();
        Path arquivoPacientes = PastasDoProjeto.arquivoPacientes();
        Path arquivoMedicos = PastasDoProjeto.arquivoMedicos();

        RepositorioPacientes repositorioPacientes = new RepositorioPacientes(arquivoPacientes);
        RepositorioMedicos repositorioMedicos = new RepositorioMedicos(arquivoMedicos);

        ServidorWeb servidor = new ServidorWeb(pastaStatic, repositorioPacientes, repositorioMedicos);
        servidor.iniciar();

        String endereco = "http://127.0.0.1:" + Configuracao.PORTA;
        System.out.println("Servidor iniciado.");
        System.out.println("Abra no navegador: " + endereco);
        System.out.println("Pacientes salvos em: " + arquivoPacientes);
        System.out.println("Médicos salvos em: " + arquivoMedicos);

        abrirNavegador(endereco);
    }

    private static void abrirNavegador(String endereco) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(endereco));
            }
        } catch (Exception erro) {
            System.out.println("Não foi possível abrir o navegador automaticamente.");
        }
    }
}
