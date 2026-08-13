package com.luanarabelo.treinodaluana.v7;

public final class WorkoutData {
    private WorkoutData() {}

    public static final String[] LETTERS = {"A", "B", "C", "D"};
    public static final String[] TITLES = {
            "Peito e tríceps",
            "Quadríceps e glúteo",
            "Costas e bíceps",
            "Posterior, glúteo e ombro"
    };

    public static final String[][] NAMES = {
            {
                    "Supino reto com halter",
                    "Tríceps testa com halter",
                    "Crucifixo com halter",
                    "Supino inclinado com halter",
                    "Tríceps francês sentado com halter",
                    "Pullover com halter",
                    "Elevação de joelhos na barra"
            },
            {
                    "Leg press 45°",
                    "Agachamento goblet com halter",
                    "Cadeira extensora",
                    "Búlgaro na Smith assistido",
                    "Cadeira adutora",
                    "Cable core press"
            },
            {
                    "Puxada alta na frente",
                    "Remada sentada na máquina",
                    "Remada curvada com halter",
                    "Rosca alternada com halter",
                    "Rosca martelo simultânea",
                    "Barra deitada",
                    "Flexão de braço de joelhos",
                    "Crunch na polia com corda"
            },
            {
                    "Stiff com halter",
                    "Flexora sentada",
                    "Hip thrust na máquina",
                    "Glúteo kickback na máquina",
                    "Desenvolvimento sentado com halter",
                    "Elevação lateral com halter",
                    "Elevação de pernas suspenso"
            }
    };

    public static final String[][] REPS = {
            {
                    "3 séries de 8 a 10", "3 séries de 10 a 12", "2 séries de 10 a 12",
                    "3 séries de 8 a 10", "2 séries de 10 a 12", "2 séries de 10 a 12",
                    "3 séries de 8 a 12"
            },
            {
                    "3 séries de 8 a 12", "3 séries de 10 a 12", "3 séries de 10 a 12",
                    "2 séries de 8 a 10 em cada perna", "3 séries de 12 a 15",
                    "3 séries de 10 a 12 em cada lado"
            },
            {
                    "3 séries de 8 a 10", "3 séries de 8 a 10", "3 séries de 8 a 12 em cada lado",
                    "3 séries de 10 a 12", "2 séries de 10 a 12", "2 séries de 8 a 12",
                    "2 séries de 8 a 12", "3 séries de 10 a 15"
            },
            {
                    "3 séries de 8 a 10", "3 séries de 10 a 12", "3 séries de 8 a 12",
                    "3 séries de 10 a 15 em cada perna", "3 séries de 8 a 10",
                    "2 séries de 12 a 15", "3 séries de 6 a 10"
            }
    };
}
