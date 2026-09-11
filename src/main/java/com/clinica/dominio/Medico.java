package com.clinica.dominio;

public class Medico {

    private final int id;
    private final String nome;
    private final String crm;
    private final String especialidade;
    private final String telefone;
    private final String email;

    public Medico(int id, String nome, String crm, String especialidade, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.crm = crm;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCrm() {
        return crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getNomeExibicao() {
        if (especialidade == null || especialidade.isBlank()) {
            return nome;
        }
        return nome + " - " + especialidade;
    }

    public String paraLinha() {
        return id + "|" + nome + "|" + crm + "|" + especialidade + "|" + telefone + "|" + email;
    }

    public static Medico deLinha(String linha) {
        String[] partes = linha.split("\\|", 6);
        if (partes.length < 6) {
            return null;
        }

        try {
            return new Medico(
                Integer.parseInt(partes[0]),
                partes[1],
                partes[2],
                partes[3],
                partes[4],
                partes[5]
            );
        } catch (NumberFormatException erro) {
            return null;
        }
    }
}
