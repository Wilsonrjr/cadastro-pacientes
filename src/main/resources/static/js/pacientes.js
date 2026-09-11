const formPaciente = document.getElementById("form-paciente");
const campoNome = document.getElementById("nome");
const campoCpf = document.getElementById("cpf");
const campoDataNascimento = document.getElementById("dataNascimento");
const campoSexo = document.getElementById("sexo");
const campoTelefone = document.getElementById("telefone");
const campoEmail = document.getElementById("email");
const campoConvenio = document.getElementById("convenio");
const campoTipoSanguineo = document.getElementById("tipoSanguineo");
const campoMedico = document.getElementById("medico");
const dicaMedico = document.getElementById("dica-medico");
const campoObservacoes = document.getElementById("observacoes");
const botaoSalvar = document.getElementById("botao-salvar");
const mensagemApp = document.getElementById("mensagem-app");
const listaPacientes = document.getElementById("lista-pacientes");
const listaVazia = document.getElementById("lista-vazia");
const totalPacientes = document.getElementById("total-pacientes");
const campoBusca = document.getElementById("busca");
const botaoLimpar = document.getElementById("botao-limpar");

let pacientes = [];
let medicos = [];

iniciar();

async function iniciar() {
    await carregarMedicos();
    await carregarPacientes();
}

campoCpf.addEventListener("input", () => {
    campoCpf.value = formatarCpf(campoCpf.value);
});

campoTelefone.addEventListener("input", () => {
    campoTelefone.value = formatarTelefone(campoTelefone.value);
});

campoBusca.addEventListener("input", () => {
    renderizarPacientes(pacientes);
});

botaoLimpar.addEventListener("click", (evento) => {
    evento.preventDefault();
    limparTela();
});

function limparTela() {
    formPaciente.reset();
    campoConvenio.value = "Particular";
    campoBusca.value = "";
    mensagemApp.hidden = true;
    mensagemApp.textContent = "";
    mensagemApp.className = "mensagem";
    preencherSelectMedicos();
    renderizarPacientes(pacientes);
    campoNome.focus();
}

formPaciente.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const nome = campoNome.value.trim();
    if (!nome) {
        mostrarMensagem(mensagemApp, "Informe o nome do paciente.", "erro");
        return;
    }

    botaoSalvar.disabled = true;
    botaoSalvar.textContent = "Salvando...";

    try {
        const resposta = await fetch("/api/pacientes", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nome,
                cpf: campoCpf.value.trim(),
                dataNascimento: campoDataNascimento.value,
                sexo: campoSexo.value,
                telefone: campoTelefone.value.trim(),
                email: campoEmail.value.trim(),
                convenio: campoConvenio.value,
                tipoSanguineo: campoTipoSanguineo.value,
                observacoes: campoObservacoes.value.trim(),
                medicoId: campoMedico.value || "0",
                medicoNome: ""
            })
        });

        const dados = await resposta.json();

        if (dados.sucesso) {
            formPaciente.reset();
            campoConvenio.value = "Particular";
            preencherSelectMedicos();
            campoNome.focus();
            mostrarMensagem(mensagemApp, dados.mensagem, "ok");
            await carregarPacientes();
        } else {
            mostrarMensagem(mensagemApp, dados.mensagem, "erro");
        }
    } catch (erro) {
        mostrarMensagem(mensagemApp, "Não foi possível cadastrar o paciente.", "erro");
    } finally {
        botaoSalvar.disabled = false;
        botaoSalvar.textContent = "Cadastrar paciente";
    }
});

async function carregarMedicos() {
    try {
        const resposta = await fetch("/api/medicos");
        const dados = await resposta.json();
        medicos = dados.medicos || [];
        preencherSelectMedicos();
    } catch (erro) {
        medicos = [];
        preencherSelectMedicos();
        mostrarMensagem(mensagemApp, "Não foi possível carregar os médicos.", "erro");
    }
}

function preencherSelectMedicos() {
    const valorAtual = campoMedico.value;
    campoMedico.innerHTML = "";

    const opcaoVazia = document.createElement("option");
    opcaoVazia.value = "";
    opcaoVazia.textContent = medicos.length === 0
        ? "Nenhum médico cadastrado"
        : "Selecione o médico";
    campoMedico.appendChild(opcaoVazia);

    medicos.forEach((medico) => {
        const opcao = document.createElement("option");
        opcao.value = String(medico.id);
        opcao.textContent = medico.nomeExibicao || medico.nome;
        campoMedico.appendChild(opcao);
    });

    campoMedico.disabled = medicos.length === 0;
    dicaMedico.hidden = medicos.length > 0;

    if (valorAtual && medicos.some((medico) => String(medico.id) === valorAtual)) {
        campoMedico.value = valorAtual;
    }
}

async function carregarPacientes() {
    try {
        const resposta = await fetch("/api/pacientes");
        const dados = await resposta.json();
        pacientes = dados.pacientes || [];
        renderizarPacientes(pacientes);
    } catch (erro) {
        mostrarMensagem(mensagemApp, "Não foi possível carregar os pacientes.", "erro");
    }
}

function renderizarPacientes(lista) {
    const busca = campoBusca.value.trim().toLowerCase();
    const filtrados = lista.filter((paciente) => {
        const nome = (paciente.nome || "").toLowerCase();
        const cpf = (paciente.cpf || "").toLowerCase();
        const medico = (paciente.medicoNome || "").toLowerCase();
        return nome.includes(busca) || cpf.includes(busca) || medico.includes(busca);
    });

    listaPacientes.innerHTML = "";
    listaVazia.hidden = filtrados.length > 0;
    atualizarTotal(lista.length, filtrados.length, busca);

    filtrados.forEach((paciente) => {
        const linha = document.createElement("tr");
        const observacao = paciente.observacoes
            ? "<span class=\"observacao\">" + escaparHtml(paciente.observacoes) + "</span>"
            : "";

        linha.innerHTML =
            "<td>" + escaparHtml(paciente.nome) + observacao + "</td>" +
            "<td>" + escaparHtml(paciente.cpf || "-") + "</td>" +
            "<td>" + escaparHtml(formatarData(paciente.dataNascimento)) + "</td>" +
            "<td>" + escaparHtml(paciente.telefone || "-") + "</td>" +
            "<td>" + escaparHtml(paciente.convenio || "-") + "</td>" +
            "<td>" + escaparHtml(paciente.medicoNome || "-") + "</td>" +
            "<td><button type=\"button\" class=\"botao-remover\">Excluir</button></td>";

        linha.querySelector("button").addEventListener("click", () => excluirPaciente(paciente.id));
        listaPacientes.appendChild(linha);
    });
}

async function excluirPaciente(id) {
    const confirmar = window.confirm("Deseja realmente excluir este paciente?");
    if (!confirmar) {
        return;
    }

    try {
        const resposta = await fetch("/api/pacientes?id=" + id, {
            method: "DELETE"
        });
        const dados = await resposta.json();

        if (dados.sucesso) {
            mostrarMensagem(mensagemApp, dados.mensagem, "ok");
            await carregarPacientes();
        } else {
            mostrarMensagem(mensagemApp, dados.mensagem, "erro");
        }
    } catch (erro) {
        mostrarMensagem(mensagemApp, "Não foi possível excluir o paciente.", "erro");
    }
}

function atualizarTotal(total, filtrados, busca) {
    if (total === 0) {
        totalPacientes.textContent = "Nenhum paciente cadastrado ainda.";
        return;
    }

    if (busca) {
        totalPacientes.textContent = filtrados + " de " + total + " paciente(s) encontrado(s).";
        return;
    }

    totalPacientes.textContent = total + " paciente(s) cadastrado(s).";
}
