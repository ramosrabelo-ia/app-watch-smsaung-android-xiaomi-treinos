# Da primeira tela ao Galaxy Watch8

O Treino da Luana começou como uma interface simples no celular. As primeiras versões ajudaram a validar a divisão dos treinos, mas ainda faltavam fotos, navegação completa e uma forma prática de acompanhar o progresso.

Nas versões seguintes, o projeto ganhou o visual Premium Obsidian, quatro treinos estruturados — A e C superiores; B e D inferiores — e duplas pensadas para a academia: peso livre junto de máquina fixa nos inferiores e máquinas próximas nos superiores. Cada treino passou a terminar com abdômen.

A V12 consolidou a experiência do celular: fotos locais, séries, cargas persistentes, botão de voltar corrigido, progresso semanal e o aplicativo Wear OS instalado diretamente no Galaxy Watch8. A etapa mais difícil foi o pareamento ADB sem fio, com portas diferentes para conexão e pareamento, até chegar ao fluxo que funcionou no relógio real.

A V13 fecha o ciclo. O aplicativo do relógio deixa de ter um check isolado: celular e Watch compartilham os 24 blocos pelo Wear OS Data Layer. A integração antiga com Withings foi removida. A sessão concluída agora é gravada no Health Connect para o Samsung Health, enquanto o progresso do treino circula diretamente entre os dois aparelhos.

## Linguagens e componentes

- Java para toda a interface e lógica principal do celular e do relógio
- Kotlin para a integração segura com Health Connect
- Gradle para os dois APKs
- Wear OS Data Layer para o check bidirecional
- GitHub Actions para compilar e validar o projeto sem publicar a chave privada

![Todas as telas do Galaxy Watch8](screenshots/watch-todas-as-telas.png)

