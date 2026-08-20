# Instalação da V13

## Antes de instalar

- mantenha o Xiaomi e o Galaxy Watch8 pareados pelo aplicativo Galaxy Wearable
- deixe Bluetooth e Wi-Fi ativos nos dois aparelhos
- use APKs gerados na mesma compilação; eles possuem o mesmo identificador e a mesma assinatura

## Xiaomi

1. Use o APK oficial da V13, assinado com a mesma chave privada da V12.
2. Abra `Treino-da-Luana-v13-Xiaomi.apk` no Xiaomi.
3. Confirme **Atualizar**. O identificador da edição de teste foi preservado, portanto as cargas e o progresso da V12 permanecem.
4. Abra o app e entre em **Revisar conexões**.

Os APKs gerados automaticamente pelo GitHub Actions usam uma chave temporária e são apenas para uma instalação nova de teste. Eles não substituem o app já instalado.

## Galaxy Watch8

A primeira V13 sincronizada usa o mesmo identificador do aplicativo do celular. Por isso, remova a edição V12 independente do relógio antes de instalar a V13.

1. Instale `Treino-da-Luana-v13-Galaxy-Watch8.apk` pelo ADB sem fio, pelo Android Studio ou pelo instalador local já usado na V12.
2. Abra **Treino da Luana** no relógio.
3. Abra também o app no Xiaomi uma vez. O celular envia o estado atual dos 24 blocos para o relógio.
4. Faça um check de teste no Watch e confirme que ele aparece no celular.

Depois da instalação, desligue **Depuração sem fio** e **Depuração ADB** nas opções do desenvolvedor do relógio.

## Samsung Health

1. No app, toque em **Revisar conexões**.
2. Autorize a gravação de exercícios no Health Connect.
3. Abra o Samsung Health pelo botão do app.
4. Nas configurações do Samsung Health, abra a integração com Health Connect e permita a leitura de **Exercícios**.

O check dos blocos não depende do Samsung Health; ele usa o Wear OS Data Layer. O Samsung Health é usado somente para receber a sessão concluída.

## Se um check demorar

- aproxime relógio e celular
- confirme Bluetooth/Wi-Fi
- abra os dois aplicativos uma vez
- no celular, toque em **Sincronizar checks agora**

O Data Layer guarda alterações feitas offline e tenta entregá-las quando a conexão volta.
