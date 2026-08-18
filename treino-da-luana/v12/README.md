# Treino da Luana v12

Aplicativo Android privado, feito para uso no celular.

## O que mudou

- quatro treinos estruturados em dois superiores e dois inferiores
- cinco duplas conjugadas e um finalizador abdominal em cada treino
- inferiores combinam uma máquina fixa com um peso livre levado ao lado
- superiores podem combinar duas máquinas da mesma área
- 44 fotos incorporadas ao APK para funcionarem sem internet
- duas fotos no mesmo cartão para visualizar a ordem A e B
- registro individual das séries, descanso após a dupla e check-in semanal
- última foto aprovada mantida como capa oficial fixa
- visual Premium Obsidian preservado
- remoção completa de notificações e comandos destinados ao relógio
- registro opcional do treino concluído no Health Connect
- importação do exercício pelo Withings quando autorizada pela usuária

## Privacidade e Health Connect

A V12 solicita apenas a permissão `WRITE_EXERCISE`. Ela grava uma sessão de treino de força
quando o último abdominal é concluído. O aplicativo não lê nenhum dado de saúde e não envia
informações a servidores próprios.

No Withings, abra **Compartilhar > Health Connect** e permita a importação de **Treinos**.

## Compilar

```bash
gradle :app:assembleDebug
```

O APK é criado em `app/build/outputs/apk/debug/app-debug.apk`.
