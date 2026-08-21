# Treino da Luana V14.1 — instalador para Galaxy Watch8 no Windows

Este pacote contém tudo o que foi usado no fluxo que funcionou no Galaxy Watch8:

- `Treino-da-Luana-v14.1-Galaxy-Watch8-CORRIGIDO.apk` — aplicativo final corrigido do relógio;
- `INSTALAR-NO-WATCH8.bat` — arquivo para abrir o instalador com dois cliques;
- `instalar-watch8.ps1` — assistente de emparelhamento e instalação;
- `google-platform-tools/platform-tools/` — ADB oficial do Google para Windows.

O pacote não altera o sistema do relógio, não desbloqueia o bootloader e não pede senha da Samsung ou do Google. Ele usa apenas a depuração sem fio oficial do Wear OS para instalar o APK.

## Antes de começar

1. Extraia o ZIP inteiro para uma pasta do Windows. Não execute o `.bat` de dentro do ZIP.
2. Deixe o computador e o Galaxy Watch8 na mesma rede Wi-Fi privada.
3. Mantenha o relógio com pelo menos 30% de bateria e a tela acesa durante o pareamento.
4. Se o Firewall do Windows perguntar, permita o ADB apenas em redes privadas.
5. No relógio, abra `Configurações > Opções do desenvolvedor` e ative temporariamente:
   - `Depuração ADB`;
   - `Desativar Wi-Fi automático`;
   - `Depuração sem fio`.

## Instalação

1. Abra `INSTALAR-NO-WATCH8.bat`.
2. Na tela principal de `Depuração sem fio`, informe o **IP e a porta principal**.
3. Entre em `Emparelhar novo dispositivo` e informe o **IP e a porta de emparelhamento** mostrados abaixo do código.
4. Quando o ADB escrever `Enter pairing code`, digite o código de seis números.
5. Espere a confirmação da instalação e do lançamento do aplicativo.

As portas principal e de emparelhamento são diferentes. O código e a porta de emparelhamento expiram; se uma tentativa falhar, feche e reabra `Emparelhar novo dispositivo` para gerar dados novos.

## Depois de instalar

Desative novamente:

- `Depuração sem fio`;
- `Depuração ADB`;
- `Desativar Wi-Fi automático`.

Se houver uma chave geral das opções do desenvolvedor, ela também pode ser desligada.

## Integridade e origem

SHA-256 do APK final do Watch8:

```text
1C75FFFA496CBE42CEF905E075DFC4DDB555AFA87E62B201AAE99DFD27266657
```

O Platform-Tools incluído foi obtido no endereço oficial do Google:

- <https://developer.android.com/tools/releases/platform-tools>
- <https://dl.google.com/android/repository/platform-tools-latest-windows.zip>

Se a pasta do ADB for removida, o instalador oferece baixar uma nova cópia do mesmo endereço oficial.
