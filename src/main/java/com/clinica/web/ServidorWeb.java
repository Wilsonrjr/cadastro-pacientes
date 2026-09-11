package com.clinica.web;

import com.clinica.Configuracao;
import com.clinica.persistencia.RepositorioMedicos;
import com.clinica.persistencia.RepositorioPacientes;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class ServidorWeb {

    private final RepositorioPacientes repositorioPacientes;
    private final RepositorioMedicos repositorioMedicos;

    public ServidorWeb(
        RepositorioPacientes repositorioPacientes,
        RepositorioMedicos repositorioMedicos
    ) {
        this.repositorioPacientes = repositorioPacientes;
        this.repositorioMedicos = repositorioMedicos;
    }

    public void iniciar() throws Exception {

        HttpServer servidor = HttpServer.create(
            new InetSocketAddress("0.0.0.0", Configuracao.PORTA),
            0
        );

        servidor.createContext(
            "/api/pacientes",
            new PacientesApi(repositorioPacientes, repositorioMedicos)
        );

        servidor.createContext(
            "/api/medicos",
            new MedicosApi(repositorioMedicos)
        );

        servidor.createContext(
            "/",
            new ArquivosEstaticos()
        );

        servidor.start();
    }
}