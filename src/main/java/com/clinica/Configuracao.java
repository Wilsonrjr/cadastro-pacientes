package com.clinica;

public final class Configuracao {

    public static final int PORTA = obterPorta();

    private static int obterPorta() {

        String porta = System.getenv("PORT");

        if (porta != null && !porta.isBlank()) {
            return Integer.parseInt(porta);
        }

        return 8082;
    }

    private Configuracao() {
    }
}