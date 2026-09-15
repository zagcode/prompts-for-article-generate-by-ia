# 3. Imagem de capa

## Como a capa foi feita

A capa (`capa.png`) não saiu de um gerador de imagens. Ela foi **escrita como código** (HTML + CSS + SVG em `capa.html`) pelo Claude e exportada em PNG com o Microsoft Edge em modo headless:

```powershell
msedge --headless=new --hide-scrollbars --window-size=1200,630 --screenshot="capa.png" "file:///.../capa.html"
```

Vantagens: o texto sai nítido e sem erros de ortografia (geradores de imagem costumam errar letras), o tamanho é exato (1200×630) e dá para editar a headline em segundos.

## Prompt usado

> Crie uma capa 1200×630 em HTML/CSS com SVG inline para o artigo "Do zero ao agente: atendimento com IA em Spring Boot + OpenRouter".
> Estilo tech, fundo escuro com gradiente roxo/verde, headline grande à esquerda e, à direita, uma simulação de conversa
> entre cliente e um robô atendente mostrando as chamadas de ferramenta (consultarPedido, abrirChamado). Sem fontes ou imagens externas.

**IA usada:** Claude (Anthropic), via Claude Code, modelo Opus 5

## Prompt alternativo para gerador de imagens (opcional)

Se quiser uma versão ilustrada (Lexica, DALL·E, Midjourney, Ideogram), use a imagem só como **fundo** e aplique o título por cima no editor:

> Friendly cute robot customer support agent wearing a headset, sitting at a futuristic desk with floating chat bubbles and a shipping box icon,
> dark purple and teal neon gradient background, subtle Java coffee cup, clean flat 3D illustration, soft glow, lots of empty space on the left for a title,
> 16:9, no text

Se usar essa opção, registre a ferramenta e o modelo nos créditos do `README.MD`.
