const formMedico = document.getElementById("form-medico");
const campoNome = document.getElementById("nome");
const campoCrm = document.getElementById("crm");
const campoEspecialidade = document.getElementById("especialidade");
const campoTelefone = document.getElementById("telefone");
const campoEmail = document.getElementById("email");
const botaoSalvar = document.getElementById("botao-salvar");
const mensagemApp = document.getElementById("mensagem-app");
const listaMedicos = document.getElementById("lista-medicos");
const listaVazia = document.getElementById("lista-vazia");
const totalMedicos = document.getElementById("total-medicos");
const campoBusca = document.getElementById("busca");
const botaoLimpar = document.getElementById("botao-limpar");

let medicos = [];

carregarMedicos();

campoTelefone.addEventListener("input", () => {
    campoTelefone.value = formatarTelefone(campoTelefone.value);
});

campoBusca.addEventListener("input", () => {
    renderizarMedicos(medicos);
});

botaoLimpar.addEventListener("click", (evento) => {
    evento.preventDefault();
    limparTela();
});

function limparTela() {
    formMedico.reset();
    campoEspecialidade.value = "Clínica Geral";
    campoBusca.value = "";
    mensagemApp.hidden = true;
    mensagemApp.textContent = "";
    mensagemApp.className = "mensagem";
    renderizarMedicos(medicos);
    campoNome.focus();
}

formMedico.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const nome = campoNome.value.trim();
    const crm = campoCrm.value.trim();

    if (!nome) {
        mostrarMensagem(mensagemApp, "Informe o nome do médico.", "erro");
        return;
    }

    if (!crm) {
        mostrarMensagem(mensagemApp, "Informe o CRM do médico.", "erro");
        return;
    }

    botaoSalvar.disabled = true;
    botaoSalvar.textContent = "Salvando...";

    try {
        const resposta = await fetch("/api/medicos", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nome,
                crm,
                especialidade: campoEspecialidade.value,
                telefone: campoTelefone.value.trim(),
                email: campoEmail.value.trim()
            })
        });

        const dados = await resposta.json();

        if (dados.sucesso) {
            formMedico.reset();
            campoEspecialidade.value = "Clínica Geral";
            campoNome.focus();
            mostrarMensagem(mensagemApp, dados.mensagem, "ok");
            await carregarMedicos();
        } else {
            mostrarMensagem(mensagemApp, dados.mensagem, "erro");
        }
    } catch (erro) {
        mostrarMensagem(mensagemApp, "Não foi possível cadastrar o médico.", "erro");
    } finally {
        botaoSalvar.disabled = false;
        botaoSalvar.textContent = "Cadastrar médico";
    }
});

async function carregarMedicos() {
    try {
        const resposta = await fetch("/api/medicos");
        const dados = await resposta.json();
        medicos = dados.medicos || [];
        renderizarMedicos(medicos);
    } catch (erro) {
        mostrarMensagem(mensagemApp, "Não foi possível carregar os médicos.", "erro");
    }
}

function renderizarMedicos(lista) {
    const busca = campoBusca.value.trim().toLowerCase();
    const filtrados = lista.filter((medico) => {
        const nome = (medico.nome || "").toLowerCase();
        const crm = (medico.crm || "").toLowerCase();
        const especialidade = (medico.especialidade || "").toLowerCase();
        return nome.includes(busca) || crm.includes(busca) || especialidade.includes(busca);
    });

    listaMedicos.innerHTML = "";
    listaVazia.hidden = filtrados.length > 0;
    atualizarTotal(lista.length, filtrados.length, busca);

    filtrados.forEach((medico) => {
        const linha = document.createElement("tr");
        linha.innerHTML =
            "<td>" + escaparHtml(medico.nome) + "</td>" +
            "<td>" + escaparHtml(medico.crm || "-") + "</td>" +
            "<td>" + escaparHtml(medico.especialidade || "-") + "</td>" +
            "<td>" + escaparHtml(medico.telefone || "-") + "</td>" +
            "<td>" + escaparHtml(medico.email || "-") + "</td>" +
            "<td><button type=\"button\" class=\"botao-remover\">Excluir</button></td>";

        linha.querySelector("button").addEventListener("click", () => excluirMedico(medico.id));
        listaMedicos.appendChild(linha);
    });
}

async function excluirMedico(id) {
    const confirmar = window.confirm("Deseja realmente excluir este médico?");
    if (!confirmar) {
        return;
    }

    try {
        const resposta = await fetch("/api/medicos?id=" + id, {
            method: "DELETE"
        });
        const dados = await resposta.json();

        if (dados.sucesso) {
            mostrarMensagem(mensagemApp, dados.mensagem, "ok");
            await carregarMedicos();
        } else {
            mostrarMensagem(mensagemApp, dados.mensagem, "erro");
        }
    } catch (erro) {
        mostrarMensagem(mensagemApp, "Não foi possível excluir o médico.", "erro");
    }
}

function atualizarTotal(total, filtrados, busca) {
    if (total === 0) {
        totalMedicos.textContent = "Nenhum médico cadastrado ainda.";
        return;
    }

    if (busca) {
        totalMedicos.textContent = filtrados + " de " + total + " médico(s) encontrado(s).";
        return;
    }

    totalMedicos.textContent = total + " médico(s) cadastrado(s).";
}
