package com.claimly.claimly.validacao;

public final class ValidadorCnpj {

    private ValidadorCnpj() {
    }

    public static boolean valido(String cnpj) {
        if (cnpj == null) {
            return false;
        }
        String n = cnpj.replaceAll("\\D", "");
        if (n.length() != 14 || n.chars().distinct().count() == 1) {
            return false;
        }
        return digito(n, 12) == n.charAt(12) - '0'
                && digito(n, 13) == n.charAt(13) - '0';
    }

    private static int digito(String n, int tamanho) {
        int soma = 0;
        int peso = tamanho - 7;
        for (int i = 0; i < tamanho; i++) {
            soma += (n.charAt(i) - '0') * peso;
            peso = (peso == 2) ? 9 : peso - 1;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
