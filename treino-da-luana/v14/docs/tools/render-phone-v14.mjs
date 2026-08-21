import path from "node:path";
import { fileURLToPath } from "node:url";
import { createRequire } from "node:module";
import { readFile, writeFile } from "node:fs/promises";

const require = createRequire(import.meta.url);
const sharp = require("/opt/codex/runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp");
const here = path.dirname(fileURLToPath(import.meta.url));
const heroPath = path.resolve(here, "../../app/src/main/assets/heroes/hero_official.jpg");
const output = path.resolve(here, "../screenshots/phone-v14-home.png");
const svgOutput = path.resolve(here, "../screenshots/phone-v14-home.svg");

const C = {
  bg: "#070707", card: "#181818", card2: "#1f1f1f", white: "#f4efe9",
  muted: "#a69d95", orange: "#ff8a3d", cyan: "#5cc8d7", green: "#75cd8b", line: "#3a302a"
};

const esc = value => String(value).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
const txt = (x, y, value, size, color = C.white, weight = 700, anchor = "start", spacing = 0) =>
  `<text x="${x}" y="${y}" fill="${color}" text-anchor="${anchor}" font-family="DejaVu Sans,Arial,sans-serif" font-size="${size}" font-weight="${weight}" letter-spacing="${spacing}">${esc(value)}</text>`;
const card = (x, y, w, h, stroke = C.line, radius = 28) =>
  `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${radius}" fill="${C.card}" stroke="${stroke}" stroke-width="2"/>`;

const workouts = [
  ["A", "SUPERIOR PUSH", "Peito, ombro e tríceps"],
  ["B", "INFERIOR QUADS", "Quadríceps e glúteos"],
  ["C", "SUPERIOR PULL", "Costas, bíceps e ombro"],
  ["D", "INFERIOR POSTERIOR", "Posterior e glúteos"]
];

const heroBase64 = (await readFile(heroPath)).toString("base64");

let svg = `<svg xmlns="http://www.w3.org/2000/svg" width="720" height="1600">
  <defs>
    <linearGradient id="heroShade" x1="0" x2="1"><stop stop-color="#070707" stop-opacity=".94"/><stop offset=".58" stop-color="#070707" stop-opacity=".32"/><stop offset="1" stop-color="#070707" stop-opacity=".08"/></linearGradient>
    <clipPath id="heroClip"><rect x="30" y="30" width="660" height="285" rx="30"/></clipPath>
  </defs>
  <rect width="720" height="1600" fill="${C.bg}"/>
  <image x="30" y="30" width="660" height="285" preserveAspectRatio="xMidYMin slice" clip-path="url(#heroClip)" href="data:image/jpeg;base64,${heroBase64}"/>
  <rect x="30" y="30" width="660" height="285" rx="30" fill="url(#heroShade)"/>
  <rect x="53" y="55" width="145" height="38" rx="19" fill="#101010" fill-opacity=".86" stroke="${C.orange}" stroke-width="2"/>
  ${txt(125, 81, "CAPA OFICIAL", 15, C.white, 800, "middle", 1)}
  ${txt(55, 228, "TREINO DA", 42, C.white, 900)}
  ${txt(55, 274, "LUANA", 42, C.white, 900)}
  ${txt(55, 300, "V14.1  •  XIAOMI + WATCH8", 16, C.cyan, 800, "start", 1)}
  ${card(30, 335, 660, 145)}
  ${txt(58, 372, "SEMANA ATUAL", 15, C.orange, 800, "start", 1)}
  ${txt(58, 407, "0 de 4 treinos", 24, C.white, 800)}
  <rect x="58" y="426" width="604" height="8" rx="4" fill="#222"/>
  ${txt(58, 462, "0 de 24 blocos concluídos", 16, C.muted, 500)}
  ${txt(30, 520, "PLANO CONJUGADO", 15, C.orange, 800, "start", 1)}
  ${txt(30, 559, "Sua semana em 4 treinos", 30, C.white, 900)}
`;

let y = 584;
for (const [letter, type, focus] of workouts) {
  svg += card(30, y, 660, 132);
  svg += `<rect x="54" y="${y + 23}" width="78" height="78" rx="24" fill="${C.orange}"/>`;
  svg += txt(93, y + 73, letter, 31, C.bg, 900, "middle");
  svg += txt(154, y + 42, `TREINO ${letter}`, 24, C.white, 900);
  svg += txt(154, y + 67, type, 14, C.cyan, 800, "start", 1);
  svg += txt(154, y + 94, focus, 17, C.white, 500);
  svg += txt(648, y + 44, "COMEÇAR", 14, C.orange, 800, "end", 1);
  svg += `<rect x="154" y="${y + 110}" width="494" height="6" rx="3" fill="#252525"/>`;
  y += 146;
}

svg += `${card(30, 1174, 660, 352, C.orange)}
  ${txt(56, 1216, "GALAXY WATCH8 + SAMSUNG HEALTH", 15, C.cyan, 800, "start", 1)}
  ${txt(56, 1253, "Checks e cargas em sintonia", 24, C.white, 900)}
  <rect x="551" y="1198" width="112" height="38" rx="19" fill="${C.card2}" stroke="${C.green}" stroke-width="2"/>
  ${txt(607, 1223, "ATIVO", 14, C.green, 800, "middle", 1)}
  ${txt(56, 1295, "Checks são atualizados nos dois aparelhos. A carga", 16, C.muted, 500)}
  ${txt(56, 1320, "definida no Xiaomi aparece no relógio ao abrir o treino.", 16, C.muted, 500)}
  ${txt(56, 1345, "A sessão concluída segue pelo Health Connect.", 16, C.muted, 500)}
  <rect x="56" y="1375" width="608" height="66" rx="22" fill="${C.card2}" stroke="${C.orange}" stroke-width="2"/>
  ${txt(360, 1417, "REVISAR CONEXÕES", 17, C.orange, 900, "middle", 1)}
  ${txt(360, 1480, "V14.1  •  XIAOMI + GALAXY WATCH8", 14, C.muted, 700, "middle", 1)}
</svg>`;

await writeFile(svgOutput, svg, "utf8");

const roundedHero = await sharp(heroPath)
  .resize(660, 285, { fit: "cover", position: "north" })
  .composite([{ input: Buffer.from('<svg width="660" height="285"><rect width="660" height="285" rx="30" fill="white"/></svg>'), blend: "dest-in" }])
  .png()
  .toBuffer();

await sharp({ create: { width: 720, height: 1600, channels: 4, background: C.bg } })
  .composite([
    { input: roundedHero, left: 30, top: 30 },
    { input: Buffer.from(svg), left: 0, top: 0 }
  ])
  .png()
  .toFile(output);
