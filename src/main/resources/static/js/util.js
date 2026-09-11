function mostrarMensagem(elemento, texto, tipo) {
    elemento.hidden = false;
    elemento.textContent = texto;
    elemento.className = "mensagem " + tipo;
}

function formatarCpf(valor) {
    const numeros = valor.replace(/\D/g, "").slice(0, 11);
    return numeros
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

function formatarTelefone(valor) {
    const numeros = valor.replace(/\D/g, "").slice(0, 11);
    if (numeros.length <= 10) {
        return numeros
            .replace(/(\d{2})(\d)/, "($1) $2")
            .replace(/(\d{4})(\d)/, "$1-$2");
    }
    return numeros
        .replace(/(\d{2})(\d)/, "($1) $2")
        .replace(/(\d{5})(\d)/, "$1-$2");
}

function formatarData(valor) {
    if (!valor) {
        return "-";
    }
    const partes = valor.split("-");
    if (partes.length !== 3) {
        return valor;
    }
    return partes[2] + "/" + partes[1] + "/" + partes[0];
}

function escaparHtml(texto) {
    return String(texto)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;");
}
