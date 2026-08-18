package com.luanarabelo.treinodaluana.v11;

public final class WorkoutData {
    private WorkoutData() {}

    public static final String[] LETTERS = {"A", "B", "C", "D"};
    public static final String[] TYPES = {
            "SUPERIOR PUSH",
            "INFERIOR QUADS",
            "SUPERIOR PULL",
            "INFERIOR POSTERIOR"
    };
    public static final String[] FOCUSES = {
            "Peito, ombro e tríceps",
            "Quadríceps e glúteo",
            "Costas, bíceps e posterior de ombro",
            "Posterior de coxa e glúteo"
    };

    public static final String[][] NAMES = {
            {
                    "Supino reto com halter",
                    "Desenvolvimento sentado com halter",
                    "Supino inclinado com halter",
                    "Crucifixo com halter",
                    "Elevação lateral com halter",
                    "Tríceps testa com halter",
                    "Tríceps francês sentado com halter"
            },
            {
                    "Leg press 45°",
                    "Agachamento goblet com halter",
                    "Cadeira extensora",
                    "Búlgaro na Smith assistido",
                    "Cadeira adutora",
                    "Afundo reverso com halteres",
                    "Panturrilha no leg press"
            },
            {
                    "Puxada alta na frente",
                    "Remada sentada na máquina",
                    "Remada unilateral apoiada",
                    "Pullover com halter",
                    "Face pull na polia com corda",
                    "Rosca alternada com halter",
                    "Rosca martelo com halteres"
            },
            {
                    "Stiff com halteres",
                    "Flexora sentada",
                    "Hip thrust na máquina",
                    "Glúteo kickback na máquina",
                    "Cadeira abdutora",
                    "Levantamento terra sumô com halter",
                    "Panturrilha em pé na máquina"
            }
    };

    public static final String[][] REPS = {
            {"8 a 10", "8 a 10", "8 a 10", "10 a 12", "12 a 15", "10 a 12", "10 a 12"},
            {"8 a 12", "10 a 12", "10 a 12", "8 a 10 por perna", "12 a 15", "10 por perna", "12 a 15"},
            {"8 a 10", "8 a 10", "8 a 12 por lado", "10 a 12", "12 a 15", "10 a 12", "10 a 12"},
            {"8 a 10", "10 a 12", "8 a 12", "12 por perna", "15 a 20", "10 a 12", "12 a 15"}
    };

    public static final int[][] SETS = {
            {3, 3, 3, 2, 3, 3, 2},
            {3, 3, 3, 3, 3, 2, 3},
            {3, 3, 3, 2, 3, 3, 2},
            {3, 3, 4, 3, 3, 3, 3}
    };

    public static final String[][] TIPS = {
            {
                    "Pés firmes, escápulas apoiadas e movimento controlado.",
                    "Mantenha o abdômen firme e não arqueie a lombar.",
                    "Desça os halteres com controle até a linha do peito.",
                    "Cotovelos levemente flexionados durante todo o arco.",
                    "Suba até a linha dos ombros sem encolher o pescoço.",
                    "Deixe os cotovelos apontados para cima e estáveis.",
                    "Mantenha o tronco firme e evite abrir os cotovelos."
            },
            {
                    "Joelhos acompanham a direção dos pés; não trave no topo.",
                    "Peito aberto, abdômen firme e joelhos alinhados.",
                    "Estenda com controle e evite chutar o peso.",
                    "Desça no eixo, com o pé da frente totalmente apoiado.",
                    "Controle a volta e mantenha a postura neutra.",
                    "Dê o passo para trás e empurre o chão para subir.",
                    "Faça uma pausa curta no alto e desça devagar."
            },
            {
                    "Leve os cotovelos para baixo sem jogar o tronco para trás.",
                    "Puxe com os cotovelos e mantenha os ombros longe das orelhas.",
                    "Apoie o tronco e evite girar o quadril.",
                    "Costelas encaixadas e amplitude confortável para os ombros.",
                    "Abra a corda na direção do rosto e una as escápulas.",
                    "Cotovelos junto ao corpo e sem embalo.",
                    "Punhos neutros e descida controlada."
            },
            {
                    "Empurre o quadril para trás e mantenha a coluna neutra.",
                    "Flexione os joelhos sem levantar o quadril do banco.",
                    "Queixo levemente recolhido e pausa no alto.",
                    "Evite girar o quadril; aperte o glúteo no final.",
                    "Abra com controle e não bata as placas na volta.",
                    "Pés abertos, joelhos alinhados e halter perto do corpo.",
                    "Suba pelo dedão e faça uma pausa no topo."
            }
    };

    public static String imagePath(int workout, int exercise) {
        return "exercises/" + Character.toLowerCase(LETTERS[workout].charAt(0)) + "_" + exercise + ".jpg";
    }
}
