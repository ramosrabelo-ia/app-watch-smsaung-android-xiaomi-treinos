# Simbora — treino no Android e no Galaxy Watch

O Simbora é uma base completa de aplicativo de treino para **celular Android/Xiaomi** e **Samsung Galaxy Watch com Wear OS**. A V13 organiza quatro treinos conjugados, guarda fotos e cargas no celular, permite marcar os blocos no relógio e sincroniza o progresso entre os dois aparelhos.

O objetivo deste repositório é permitir que outra pessoa adapte a experiência ao próprio treino, aparelho e ecossistema sem precisar começar do zero.

> Esta branch mantém somente a versão final V13. As versões anteriores continuam recuperáveis pelo histórico do Git.

## Baixar as versões finais

| Plataforma | Arquivo | O que entrega |
|---|---|---|
| Android / Xiaomi | [`Treino-da-Luana-v13-Xiaomi.apk`](downloads/Treino-da-Luana-v13-Xiaomi.apk) | Interface completa, fotos offline, cargas, séries, descanso, progresso e Health Connect |
| Galaxy Watch8 / Wear OS | [`Treino-da-Luana-v13-Galaxy-Watch8.apk`](downloads/Treino-da-Luana-v13-Galaxy-Watch8.apk) | Seleção dos quatro treinos, blocos A–D e checks sincronizados com o celular |

SHA-256 dos APKs publicados:

```text
e92818d92671df192763c108bfa3110c2ed9606aa57b735456a253201802b702  Treino-da-Luana-v13-Xiaomi.apk
0d24fb064da13b2f78385904dd7e2ec46834abe71eedc65934ba6609d40e3503  Treino-da-Luana-v13-Galaxy-Watch8.apk
```

Para instalar o relógio pelo Windows, consulte [`installer-watch8/LEIA-ME.md`](installer-watch8/LEIA-ME.md). O pacote distribuído separadamente inclui também o Google Platform-Tools oficial; no repositório, o script baixa o ADB diretamente do Google quando necessário.

## O que foi feito na V13

### Aplicativo do celular

- visual Premium Obsidian em preto, laranja e ciano;
- quatro treinos: dois de superiores e dois de inferiores;
- cinco duplas conjugadas e um abdominal final por treino;
- 44 fotos de exercícios e uma capa, todas incorporadas ao APK;
- registro de séries, descanso e carga por exercício;
- carga persistente, editável para acompanhar a progressão;
- botão e gesto de voltar corrigidos, com margem segura para a barra do sistema;
- painel semanal com 24 blocos e check-in de cada treino;
- remoção completa da interface da Withings;
- registro opcional de treino de força no Samsung Health por meio do Health Connect;
- sincronização bidirecional dos checks com o Galaxy Watch8.

### Aplicativo do relógio

- interface circular própria para Wear OS;
- seleção rápida dos treinos A, B, C e D;
- visão das seis etapas de cada treino;
- nome dos dois exercícios de cada dupla e do abdominal final;
- marcar e desmarcar blocos diretamente no pulso;
- progresso local mesmo quando o celular está temporariamente distante;
- sincronização posterior pelo Wear OS Data Layer quando os aparelhos voltam a se comunicar;
- sem fotos e sem cargas no relógio, para manter a navegação leve e legível.

## Como a dupla funciona

```text
Celular Android / Xiaomi                 Galaxy Watch8
┌──────────────────────────┐             ┌────────────────────────┐
│ fotos, cargas e séries   │             │ treino A, B, C ou D    │
│ 24 blocos semanais       │◄───────────►│ check dos seis blocos  │
│ Health Connect opcional  │ Wear Data   │ navegação no pulso     │
└──────────────────────────┘   Layer     └────────────────────────┘
```

Cada bloco usa um caminho estável no Data Layer e um horário de alteração. Quando celular e relógio discordam, prevalece a mudança mais recente. As cargas continuam apenas no celular; somente os checks dos 24 blocos são sincronizados.

O Samsung Health não é acessado por uma API proprietária. O celular grava uma sessão de treino de força no **Health Connect**, se a pessoa autorizar, e o Samsung Health pode importar esse registro conforme sua configuração.

## Telas

| Celular Android | Painel do Galaxy Watch8 |
|---|---|
| <img src="treino-da-luana/v13/docs/screenshots/phone-v13-home.svg" width="340" alt="Tela inicial da V13 no Android"> | <img src="treino-da-luana/v13/docs/screenshots/watch-overview.svg" width="340" alt="Painel de seleção no Galaxy Watch8"> |

Treinos no relógio: [A](treino-da-luana/v13/docs/screenshots/watch-workout-a.svg) · [B](treino-da-luana/v13/docs/screenshots/watch-workout-b.svg) · [C](treino-da-luana/v13/docs/screenshots/watch-workout-c.svg) · [D](treino-da-luana/v13/docs/screenshots/watch-workout-d.svg)

## Estrutura final

```text
.
├── downloads/
│   ├── Treino-da-Luana-v13-Xiaomi.apk
│   └── Treino-da-Luana-v13-Galaxy-Watch8.apk
├── installer-watch8/
│   ├── INSTALAR-NO-WATCH8.bat
│   ├── instalar-watch8.ps1
│   └── LEIA-ME.md
├── treino-da-luana/v13/
│   ├── app/                  aplicativo Android
│   │   └── src/main/assets/ fotos offline
│   ├── wear/                 aplicativo Wear OS
│   ├── docs/                 instalação, privacidade e telas
│   └── settings.gradle       projeto com os dois módulos
└── .github/workflows/
    └── build-treino-da-luana-v13.yml
```

## Tecnologia

- Java 17 para as interfaces Android e Wear OS e para a sincronização;
- Kotlin para a ponte do Health Connect;
- Android SDK 36, target SDK 35 e Gradle 8.11.1;
- Google Play services Wearable `20.0.1`;
- AndroidX Health Connect `1.1.0`;
- `SharedPreferences` para progresso e cargas locais;
- Android Views construídas diretamente em código;
- GitHub Actions para compilar e validar os dois módulos.

Não há backend, login obrigatório ou banco de dados externo.

## Use como base para outro treino ou aparelho

Os principais pontos de adaptação são:

| Necessidade | Onde alterar |
|---|---|
| nomes, séries, repetições, dicas e divisão dos treinos | `app/src/main/java/.../WorkoutData.java` e as tabelas do módulo `wear` |
| fotos dos exercícios e capa | `app/src/main/assets/` |
| cores, cartões, tipografia e navegação do celular | `app/src/main/java/.../MainActivity.java` |
| layout circular e conteúdo do relógio | `wear/src/main/java/.../WatchMainActivity.java` |
| ícone | `app/src/main/icon/` e `wear/src/main/res/` |
| sincronização | `PhoneProgressSync.java` e `WatchProgressSync.java` |
| integração de saúde | `HealthConnectBridge.kt` |
| identificador, SDK e assinatura | `app/build.gradle`, `wear/build.gradle` e `signing.properties.example` |

Para outro celular Android, normalmente basta ajustar identidade visual, dados do treino, permissões e testes de tela. Para outro relógio **Wear OS**, o módulo `wear` é a base. Relógios que não usam Wear OS exigem um módulo nativo para o sistema do fabricante, mas ainda podem reaproveitar a estrutura de treinos, estados, telas e protocolo de sincronização como referência.

## Compilar

Requisitos: JDK 17 e Gradle 8.11.1.

```bash
cd treino-da-luana/v13
gradle :app:assembleDebug :wear:assembleDebug
```

Saídas:

```text
app/build/outputs/apk/debug/app-debug.apk
wear/build/outputs/apk/debug/wear-debug.apk
```

Para a sincronização funcionar, os dois APKs precisam usar o mesmo `applicationId` e ser assinados com a mesma chave. Nunca publique sua chave privada, `.jks` ou `signing.properties`. O workflow do GitHub gera um par de teste para instalação nova; uma atualização de um app já instalado precisa usar a assinatura original.

## Documentação

- [Visão técnica da V13](treino-da-luana/v13/README.md)
- [Instalação no celular e no relógio](treino-da-luana/v13/docs/INSTALACAO.md)
- [Jornada de desenvolvimento](treino-da-luana/v13/docs/JORNADA.md)
- [Privacidade](treino-da-luana/v13/docs/PRIVACIDADE.md)

Este projeto organiza treinos; ele não substitui orientação de profissional de educação física ou atendimento médico.
