# Simbora · Treino da Luana V13

Aplicativo pessoal de treino conjugado que agora funciona como um único produto no **Xiaomi** e no **Samsung Galaxy Watch8**. A V13 remove a integração Withings, conecta o treino concluído ao Samsung Health por meio do Health Connect e sincroniza os 24 checks semanais nos dois sentidos.

> Abriu, escolheu o treino, registrou carga e séries, marcou no celular ou no relógio — os dois aparelhos ficam com o mesmo progresso.

## A experiência

| Celular Android | Dupla conjugada |
|---|---|
| <img src="treino-da-luana/v13/docs/screenshots/phone-v13-home.svg" width="330" alt="Tela inicial Premium Obsidian da V13"> | <img src="treino-da-luana/v12/app/src/main/assets/exercises/a_0.jpg" width="330" alt="Foto offline de um exercício do Treino A"> |

### No Galaxy Watch8

O relógio mostra os quatro treinos e os seis blocos de cada sessão em uma interface própria para a tela redonda. Um toque marca ou desmarca o bloco e envia a alteração ao celular.

![Painel e seleção de treinos no Galaxy Watch8](treino-da-luana/v13/docs/screenshots/watch-overview.svg)

[Treino A](treino-da-luana/v13/docs/screenshots/watch-workout-a.svg) · [Treino B](treino-da-luana/v13/docs/screenshots/watch-workout-b.svg) · [Treino C](treino-da-luana/v13/docs/screenshots/watch-workout-c.svg) · [Treino D](treino-da-luana/v13/docs/screenshots/watch-workout-d.svg)

## Os quatro treinos

| Treino | Tipo | Foco |
|---|---|---|
| A | Superior push | peito, ombro e tríceps |
| B | Inferior quads | quadríceps e glúteos |
| C | Superior pull | costas, bíceps e posterior de ombro |
| D | Inferior posterior | posterior de coxa e glúteos |

Cada treino tem **cinco duplas conjugadas e um abdominal final**. São 44 movimentos com fotos locais, séries, repetições, descanso, registro de carga e evolução semanal.

## Como a sincronização funciona

```text
Xiaomi                         Galaxy Watch8
┌──────────────────────┐       ┌──────────────────────┐
│ fotos, séries, carga │       │ seleção A · B · C · D│
│ 24 blocos da semana  │◄─────►│ check dos 24 blocos  │
│ progresso e histórico│ Wear  │ progresso no pulso   │
└──────────┬───────────┘ Data  └──────────────────────┘
           │               Layer
           ▼
     Health Connect
           │
           ▼
     Samsung Health
```

- O **Wear OS Data Layer** leva os checks diretamente entre celular e relógio.
- A alteração mais recente vence; se um aparelho estiver offline, o sistema entrega quando a conexão voltar.
- As cargas continuam privadas e armazenadas somente no Xiaomi.
- Ao finalizar o treino, o app pode escrever uma sessão de força no **Health Connect** para aparecer no Samsung Health.
- O app não lê frequência cardíaca, sono, peso, localização ou outros dados de saúde.

## Tecnologia

- **Java 17** para as interfaces Android/Wear OS e a sincronização
- **Kotlin** para a ponte com o Health Connect
- Android SDK 36, target 35
- Google Play services Wearable 20.0.1
- Health Connect 1.1.0
- Gradle 8.11.1 e GitHub Actions
- sem backend, conta, anúncio ou telemetria

## A jornada

`V5–V7` estrutura funcional e estabilidade  
`V8–V9` imagens dos exercícios  
`V10` identidade Premium Obsidian e progresso semanal  
`V11` testes de conectividade  
`V12` treinos conjugados, carga persistente e primeira edição Wear OS  
`V13` Samsung Health e check bidirecional entre Xiaomi e Galaxy Watch8

O desafio mais trabalhoso foi colocar a primeira edição no relógio real: ADB sem fio, portas diferentes de conexão e pareamento e várias tentativas até validar o fluxo. A V13 aproveita essa base e transforma os dois aplicativos em um par sincronizado.

## Código

- [`treino-da-luana/v13/app`](treino-da-luana/v13/app): aplicativo do Xiaomi
- [`treino-da-luana/v13/wear`](treino-da-luana/v13/wear): aplicativo do Galaxy Watch8
- [`treino-da-luana/v13/docs`](treino-da-luana/v13/docs): telas, instalação, privacidade e jornada
- [`build-treino-da-luana-v13.yml`](.github/workflows/build-treino-da-luana-v13.yml): compilação automática dos dois APKs

[Abrir a documentação completa da V13](treino-da-luana/v13/README.md)

> Projeto pessoal de organização de treino. Não substitui orientação profissional.
