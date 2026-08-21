import path from "node:path";
import { fileURLToPath } from "node:url";
import { createRequire } from "node:module";
import { readFile, writeFile } from "node:fs/promises";

const require = createRequire(import.meta.url);
const runtimeModules = process.env.CODEX_PRIMARY_RUNTIME_NODE_MODULES
  || "/opt/codex/runtimes/codex-primary-runtime/dependencies/node/node_modules";
const sharp = require(path.join(runtimeModules, "sharp"));
const here = path.dirname(fileURLToPath(import.meta.url));
const photoPath = path.resolve(here, "../../wear/src/main/assets/exercises/b_2.jpg");
const output = path.resolve(here, "../screenshots/watch-v14-overview.png");
const svgOutput = path.resolve(here, "../screenshots/watch-v14-overview.svg");

const C = {
  page: "#090909", bg: "#070707", obsidian: "#101010", card: "#181818",
  done: "#2f1d11", line: "#3f3f3f", white: "#f7f7f7", muted: "#a6a6a6",
  orange: "#ff8a3d", green: "#69cf84"
};

const esc = value => String(value).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
const text = (x, y, value, size, color = C.white, weight = 700, anchor = "middle", spacing = 0) =>
  `<text x="${x}" y="${y}" fill="${color}" text-anchor="${anchor}" font-family="DejaVu Sans,Arial,sans-serif" font-size="${size}" font-weight="${weight}" letter-spacing="${spacing}">${esc(value)}</text>`;
const rect = (x, y, w, h, r = 10, fill = C.card, stroke = "none", sw = 0) =>
  `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${r}" fill="${fill}" stroke="${stroke}" stroke-width="${sw}"/>`;

function header(title, back = "‹") {
  return `${rect(7, 5, 25, 22, 11, C.card)}${text(19.5, 21, back, 16, C.orange, 800)}${text(116, 20, title, 9, C.white, 800)}`;
}

function ring(cx, cy, r, value, label, progress = .58, check = false) {
  const circumference = 2 * Math.PI * r;
  return `<circle cx="${cx}" cy="${cy}" r="${r}" fill="none" stroke="#2a2a2a" stroke-width="6"/>
    <circle cx="${cx}" cy="${cy}" r="${r}" fill="none" stroke="${check ? C.green : C.orange}" stroke-width="6" stroke-linecap="round" transform="rotate(-90 ${cx} ${cy})" stroke-dasharray="${circumference}" stroke-dashoffset="${circumference * (1 - progress)}"/>
    ${text(cx, cy + 3, value, check ? 22 : 20, check ? C.green : C.white, 900)}
    ${text(cx, cy + 17, label, 6, C.muted, 800, "middle", .4)}`;
}

function homeScreen() {
  const workouts = [["A", "PUSH", "2/6"], ["B", "QUADS", "0/6"], ["C", "PULL", "0/6"], ["D", "POSTERIOR", "0/6"]];
  let out = header("TREINO DA LUANA", "×");
  out += ring(112.5, 62, 25, "2", "DE 24 BLOCOS", 2 / 24);
  workouts.forEach((w, i) => {
    const col = i % 2;
    const row = Math.floor(i / 2);
    const x = 7 + col * 106;
    const y = 94 + row * 50;
    out += rect(x, y, 100, 44, 11, i === 0 ? C.done : C.card, i === 0 ? C.orange : C.line, 1);
    out += text(x + 16, y + 26, w[0], 17, C.orange, 900);
    out += text(x + 58, y + 17, `TREINO ${w[0]}`, 7, C.white, 800);
    out += text(x + 58, y + 28, w[1], 6, C.muted, 700);
    out += text(x + 88, y + 38, w[2], 6, i === 0 ? C.orange : C.muted, 800);
  });
  out += text(112.5, 205, "CHECKS SINCRONIZADOS", 6.5, C.muted, 800, "middle", .7);
  return out;
}

function workoutScreen() {
  const blocks = [["1", "SUPINO + TRÍCEPS"], ["2", "PECK + LATERAL"], ["3", "OMBRO + COICE"], ["4", "INCLINADO + FRONTAL"], ["5", "MERGULHO + TESTA"], ["F", "ABDOMINAL"]];
  let out = header("TREINO A");
  out += text(112.5, 40, "SUPERIOR PUSH", 8, C.orange, 900);
  out += text(112.5, 51, "Peito, ombro e tríceps", 6.5, C.muted, 600);
  for (let i = 0; i < 6; i++) out += `<circle cx="${82 + i * 12}" cy="64" r="3.5" fill="${i === 0 ? C.orange : "#303030"}"/>`;
  blocks.forEach((b, i) => {
    const col = i % 2;
    const row = Math.floor(i / 2);
    const x = 7 + col * 106;
    const y = 76 + row * 43;
    out += rect(x, y, 100, 37, 10, i === 0 ? C.done : C.card, i === 0 ? C.orange : C.line, 1);
    out += text(x + 16, y + 23, b[0], 13, C.orange, 900);
    out += text(x + 60, y + 16, i === 5 ? "FINAL" : `DUPLA ${b[0]}`, 7, C.white, 800);
    out += text(x + 60, y + 27, b[1], 5.3, C.muted, 700);
  });
  out += text(112.5, 214, "6 BLOCOS · TOQUE PARA ABRIR", 6.2, C.muted, 700, "middle", .45);
  return out;
}

function exerciseScreen(photoBase64) {
  let out = header("TREINO B  •  2A");
  out += text(112.5, 41, "Cadeira extensora", 11.5, C.white, 900);
  out += `<defs><clipPath id="exercise-photo"><rect x="58" y="49" width="109" height="62" rx="13"/></clipPath></defs>
    <image x="58" y="49" width="109" height="62" preserveAspectRatio="xMidYMid slice" clip-path="url(#exercise-photo)" href="data:image/jpeg;base64,${photoBase64}"/>
    <rect x="58" y="49" width="109" height="62" rx="13" fill="none" stroke="${C.line}" stroke-width="1"/>`;
  out += rect(7, 118, 103, 35, 10, C.card, C.line, 1);
  out += rect(115, 118, 103, 35, 10, C.card, C.line, 1);
  out += text(58.5, 130, "REPETIÇÕES", 5.8, C.muted, 800, "middle", .4);
  out += text(58.5, 146, "10–12", 13, C.orange, 900);
  out += text(166.5, 130, "CARGA", 5.8, C.muted, 800, "middle", .4);
  out += text(166.5, 146, "42 kg", 13, C.white, 900);
  out += text(112.5, 169, "●   ○   ○", 12, C.orange, 700);
  out += rect(13, 181, 199, 30, 13, C.orange);
  out += text(112.5, 201, "CONCLUIR EXERCÍCIO", 8, C.bg, 900, "middle", .6);
  return out;
}

function summaryScreen() {
  let out = header("TREINO FINALIZADO");
  out += ring(112.5, 70, 27, "✓", "CONCLUÍDO", 1, true);
  out += text(112.5, 113, "PARABÉNS, LUANA", 11, C.white, 900);
  out += rect(7, 125, 103, 39, 11, C.card, C.line, 1);
  out += rect(115, 125, 103, 39, 11, C.card, C.orange, 1);
  out += text(58.5, 138, "TEMPO", 5.8, C.muted, 800, "middle", .4);
  out += text(58.5, 156, "00:48", 13, C.white, 900);
  out += text(166.5, 138, "EXERCÍCIOS", 5.8, C.muted, 800, "middle", .4);
  out += text(166.5, 156, "11 / 11", 13, C.orange, 900);
  out += text(112.5, 178, "Voltando ao início em 10 s", 7, C.muted, 700);
  out += rect(13, 187, 199, 28, 13, C.orange);
  out += text(112.5, 206, "IR PARA O INÍCIO", 8, C.bg, 900, "middle", .6);
  return out;
}

function watchFrame(x, title, subtitle, content) {
  const y = 112;
  const screenX = x + 28;
  const screenY = y + 18;
  const scale = 1.29;
  const clipId = `screen-${x}`;
  return `<defs><clipPath id="${clipId}"><circle cx="${screenX + 145}" cy="${screenY + 145}" r="145"/></clipPath></defs>
    <rect x="${x}" y="${y}" width="346" height="326" rx="118" fill="url(#metal)" stroke="#70757d" stroke-width="2"/>
    <rect x="${x + 338}" y="${y + 110}" width="13" height="58" rx="6" fill="#666b73"/>
    <rect x="${x + 339}" y="${y + 202}" width="9" height="40" rx="4" fill="#4f545b"/>
    <g clip-path="url(#${clipId})"><circle cx="${screenX + 145}" cy="${screenY + 145}" r="145" fill="${C.bg}"/>
      <g transform="translate(${screenX} ${screenY}) scale(${scale})">${content}</g></g>
    <circle cx="${screenX + 145}" cy="${screenY + 145}" r="145" fill="none" stroke="#050505" stroke-width="6"/>
    ${text(x + 173, 486, title, 18, C.white, 900)}
    ${text(x + 173, 512, subtitle, 12, C.muted, 600)}`;
}

const photoBase64 = (await readFile(photoPath)).toString("base64");
const screens = [
  ["INÍCIO", "Progresso e quatro treinos", homeScreen()],
  ["BLOCOS", "Treino compacto sem rolagem", workoutScreen()],
  ["EXERCÍCIO", "Foto original, repetições e carga", exerciseScreen(photoBase64)],
  ["CONCLUSÃO", "Resumo de 10 segundos", summaryScreen()]
];

let svg = `<svg xmlns="http://www.w3.org/2000/svg" width="1600" height="570" viewBox="0 0 1600 570">
  <defs>
    <linearGradient id="page" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#191919"/><stop offset=".58" stop-color="#0b0b0b"/><stop offset="1" stop-color="#17100c"/></linearGradient>
    <radialGradient id="glow"><stop stop-color="${C.orange}" stop-opacity=".2"/><stop offset="1" stop-color="${C.orange}" stop-opacity="0"/></radialGradient>
    <linearGradient id="metal" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#747a83"/><stop offset=".25" stop-color="#25292f"/><stop offset=".7" stop-color="#111318"/><stop offset="1" stop-color="#555b63"/></linearGradient>
  </defs>
  <rect width="1600" height="570" rx="32" fill="url(#page)"/>
  <circle cx="90" cy="35" r="260" fill="url(#glow)"/>
  ${text(42, 46, "TREINO DA LUANA V14.1", 15, C.orange, 900, "start", 2)}
  ${text(42, 83, "A nova interface do Galaxy Watch8", 30, C.white, 900, "start")}
  ${text(1556, 80, "Obsidian + laranja · Wear OS 6", 13, C.muted, 600, "end")}`;

screens.forEach((screen, index) => {
  svg += watchFrame(32 + index * 390, screen[0], screen[1], screen[2]);
});
svg += `</svg>`;

await writeFile(svgOutput, svg, "utf8");
await sharp(Buffer.from(svg)).png().toFile(output);
