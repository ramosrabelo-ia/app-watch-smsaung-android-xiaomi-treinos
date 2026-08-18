# Treino da Luana v11

Aplicativo Android privado, feito para uso no celular.

## O que mudou

- quatro treinos estruturados em dois superiores e dois inferiores
- fotos reais incorporadas ao APK para funcionarem sem internet
- última foto aprovada como capa oficial fixa
- registro de séries, progresso por exercício e check-in semanal
- retomada automática do primeiro exercício ainda incompleto
- visual Premium Obsidian com detalhes em laranja e azul técnico
- assistente guiado para notificações no Withings
- teste de notificação e envio automático do exercício atual
- atalhos para permissões do Android e para o app Withings

## Withings

O relógio recebe o treino por meio das notificações de aplicativos do Android.
Na V11, abra **Configurar Withings**, conclua as quatro etapas e selecione
**Treino da Luana v11** em **Dispositivos > seu relógio > Notificações > Aplicativos**
dentro do app Withings.

## Compilar

```bash
gradle :app:assembleDebug
```

O APK é criado em `app/build/outputs/apk/debug/app-debug.apk`.
