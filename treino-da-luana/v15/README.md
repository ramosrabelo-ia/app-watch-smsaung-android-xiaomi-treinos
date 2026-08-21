# Treino da Luana V15

Código dos aplicativos Android e Wear OS usados no pacote de teste V15.

## Módulos

| Pasta | Conteúdo |
| --- | --- |
| `app` | Aplicativo Android do Xiaomi, cargas, progresso e Health Connect |
| `wear` | Aplicativo do Galaxy Watch8, duplas, séries e resumo final |
| `installer-watch8` | Instalador guiado para Windows |
| `docs/mockups` | Interfaces aprovadas em PNG e SVG |

## Comportamento do relógio

* lista rolável de duplas com duas fotografias por cartão
* entrada no primeiro exercício incompleto ao reabrir uma dupla
* séries tocáveis e reversíveis
* progresso parcial preservado
* botões Anterior e Próximo dentro da área circular
* carga somente para consulta
* finalização permitida com exercícios pendentes
* resumo com tempo e exercícios concluídos
* retorno automático ao início em 10 segundos

## Build

```bash
gradle :app:assembleDebug :wear:assembleDebug --no-daemon
```

Para assinar como atualização da V14.1, use a mesma chave privada e configure localmente um `signing.properties`. Nunca envie a chave ou as senhas ao Git.
