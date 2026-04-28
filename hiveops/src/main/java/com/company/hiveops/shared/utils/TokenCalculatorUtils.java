package com.company.hiveops.shared.utils;

public class TokenCalculatorUtils {

    private TokenCalculatorUtils() {
        // Construtor privado para evitar instanciação de classe utilitária
    }

    /**
     * Estima a quantidade de tokens baseada no tamanho do texto.
     * Útil como fallback caso a API do LLM não retorne o 'Usage'.
     */
    public static int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // Aproximação padrão da OpenAI: 1 token = 4 caracteres
        return (int) Math.ceil((double) text.length() / 4.0);
    }
}