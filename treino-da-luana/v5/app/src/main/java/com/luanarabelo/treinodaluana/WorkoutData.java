package com.luanarabelo.treinodaluana;

public final class WorkoutData {
    private WorkoutData() {}

    public static final String[] LETTERS = {"A", "B", "C", "D"};
    public static final String[] TITLES = {
            "PEITO + TRÍCEPS",
            "PERNA 1: QUADRÍCEPS + GLÚTEO",
            "COSTAS + BÍCEPS",
            "PERNA 2: POSTERIOR + GLÚTEO + OMBRO"
    };

    public static final String[][] NAMES = {
            {
                    "SUPINO RETO COM HALTER",
                    "TRÍCEPS TESTA COM HALTER",
                    "CRUCIFIXO COM HALTER",
                    "SUPINO INCLINADO COM HALTER",
                    "TRÍCEPS FRANCÊS SENTADO COM HALTER",
                    "PULLOVER COM HALTER",
                    "ELEVAÇÃO DE JOELHOS NA BARRA"
            },
            {
                    "LEG PRESS 45°",
                    "AGACHAMENTO GOBLET COM HALTER",
                    "CADEIRA EXTENSORA",
                    "BÚLGARO NA SMITH (ASSISTIDO)",
                    "CADEIRA ADUTORA",
                    "CABLE CORE PRESS"
            },
            {
                    "PUXADA ALTA NA FRENTE",
                    "REMADA SENTADA NA MÁQUINA",
                    "REMADA CURVADA COM HALTER",
                    "ROSCA ALTERNADA COM HALTER",
                    "ROSCA MARTELO SIMULTÂNEA",
                    "BARRA DEITADA (INVERTED ROW)",
                    "FLEXÃO DE BRAÇO DE JOELHOS",
                    "CRUNCH NA POLIA COM CORDA"
            },
            {
                    "STIFF COM HALTER",
                    "FLEXORA SENTADA",
                    "HIP THRUST NA MÁQUINA",
                    "GLÚTEO KICKBACK NA MÁQUINA",
                    "DESENVOLVIMENTO SENTADO COM HALTER",
                    "ELEVAÇÃO LATERAL COM HALTER",
                    "ELEVAÇÃO DE PERNAS SUSPENSO"
            }
    };

    public static final String[][] REPS = {
            {
                    "3x 8–10", "3x 10–12", "2x 10–12", "3x 8–10",
                    "2x 10–12", "2x 10–12", "3x 8–12"
            },
            {
                    "3x 8–12", "3x 10–12", "3x 10–12", "2x 8–10 cada perna",
                    "3x 12–15", "3x 10–12 cada lado"
            },
            {
                    "3x 8–10", "3x 8–10", "3x 8–12 cada lado", "3x 10–12",
                    "2x 10–12", "2x 8–12", "2x 8–12", "3x 10–15"
            },
            {
                    "3x 8–10", "3x 10–12", "3x 8–12", "3x 10–15 cada perna",
                    "3x 8–10", "2x 12–15", "3x 6–10"
            }
    };
}
