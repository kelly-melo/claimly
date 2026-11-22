package com.claimly.claimly.validacao;

public final class ValidadorCpf {

    private ValidadorCpf() {
    }

    public static boolean valido(String cpf) {
        if (cpf == null) {
            return false;
        }
        String n = cpf.replaceAll("\\D", "");
        if (n.length() != 11 || n.chars().distinct().count() == 1) {
            return false;
        }
        return digito(n, 9) == n.charAt(9) - '0'
                && digito(n, 10) == n.charAt(10) - '0';
    }

    private static int digito(String n, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (n.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
