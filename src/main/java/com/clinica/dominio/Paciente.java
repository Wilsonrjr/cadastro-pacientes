package com.clinica.dominio;

public class Paciente {

    private final int id;
    private final String nome;
    private final String cpf;
    private final String dataNascimento;
    private final String sexo;
    private final String telefone;
    private final String email;
    private final String convenio;
    private final String tipoSanguineo;
    private final String observacoes;
    private final int medicoId;
    private final String medicoNome;

    public Paciente(
        int id,
        String nome,
        String cpf,
        String dataNascimento,
        String sexo,
        String telefone,
        String email,
        String convenio,
        String tipoSanguineo,
        String observacoes,
        int medicoId,
        String medicoNome
    ) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.telefone = telefone;
        this.email = email;
        this.convenio = convenio;
        this.tipoSanguineo = tipoSanguineo;
        this.observacoes = observacoes;
        this.medicoId = medicoId;
        this.medicoNome = medicoNome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getConvenio() {
        return convenio;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public int getMedicoId() {
        return medicoId;
    }

    public String getMedicoNome() {
        return medicoNome;
    }

    public String paraLinha() {
        return id + "|" + nome + "|" + cpf + "|" + dataNascimento + "|" + sexo
            + "|" + telefone + "|" + email + "|" + convenio + "|" + tipoSanguineo
            + "|" + observacoes + "|" + medicoId + "|" + medicoNome;
    }

    public static Paciente deLinha(String linha) {
        String[] partes = linha.split("\\|", 12);
        if (partes.length < 10) {
            return null;
        }

        try {
            int medicoId = 0;
            String medicoNome = "";
            if (partes.length >= 12) {
                medicoId = Integer.parseInt(partes[10]);
                medicoNome = partes[11];
            }

            return new Paciente(
                Integer.parseInt(partes[0]),
                partes[1],
                partes[2],
                partes[3],
                partes[4],
                partes[5],
                partes[6],
                partes[7],
                partes[8],
                partes[9],
                medicoId,
                medicoNome
            );
        } catch (NumberFormatException erro) {
            return null;
        }
    }
}
