# Treino da Luana v14

Aplicativos privados para o celular Xiaomi e o Samsung Galaxy Watch8. A V14 mantém o layout
Premium Obsidian do celular e redesenha somente a experiência do relógio.

## Principais mudanças

- identidade preto/obsidiana e laranja preservada
- 44 fotos originais dos exercícios incluídas também no APK do relógio
- contorno do movimento usado automaticamente se uma foto não puder ser aberta
- textos menores e centralizados no relógio, com as telas principais sem rolagem
- progresso circular, cartões compactos, métricas grandes e botões largos
- botão voltar em todas as telas; durante o treino há confirmação antes de sair
- uma carga por exercício, cadastrada no celular e sincronizada com o relógio
- sincronização de cargas solicitada automaticamente ao abrir um treino no relógio
- conclusão com tempo e 11 exercícios concluídos, seguida de retorno ao início em 10 segundos
- botão da tela inicial do relógio fecha o aplicativo
- checks dos blocos continuam sincronizados nos dois sentidos
- registro opcional do treino concluído no Health Connect e encaminhamento ao Samsung Health

## Correção V14.1

- corrige a incompatibilidade binária de `DataItemBuffer` que fechava o app no Xiaomi ao abrir
- usa a interface `Iterable` implementada pelos buffers reais do Google Play Services
- aplica a mesma proteção aos eventos de sincronização do celular e do Galaxy Watch8
- mantém pacote, assinatura, cargas e progresso da V13/V14 ao instalar como atualização

## Compatibilidade

Os dois módulos usam o mesmo identificador da V13 de teste:
`com.luanarabelo.treinodaluana.v12.xiaomitest`. Para atualizar sem perder dados, os APKs devem
ser assinados com a mesma chave privada usada na V13.

## Compilar

```bash
gradle :app:assembleDebug :wear:assembleDebug
```

Saídas:

- celular: `app/build/outputs/apk/debug/app-debug.apk`
- relógio: `wear/build/outputs/apk/debug/wear-debug.apk`

Para usar a chave compartilhada, copie `signing.properties.example` para
`signing.properties`, ajuste os valores e coloque o arquivo de chave na raiz da V14. Esses
arquivos são ignorados pelo Git.
