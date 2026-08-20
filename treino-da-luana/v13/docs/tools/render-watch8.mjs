import path from "node:path";
import { fileURLToPath } from "node:url";
import { createRequire } from "node:module";
import { writeFile } from "node:fs/promises";

const require = createRequire(import.meta.url);
const sharp = require("/opt/codex/runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp");
const here = path.dirname(fileURLToPath(import.meta.url));

const C = {
  bg: "#07090e", surface: "#13171f", surfaceDone: "#261d14",
  white: "#f4f5f7", muted: "#9ca4b2", orange: "#ff7a1a",
  stroke: "#353d4b", page: "#0b0e14"
};

const workouts = [
  {
    letter: "A", type: "SUPERIOR PUSH", focus: "Peito, ombro e tríceps",
    blocks: [
      [["Supino máquina", "3 x 10–12"], ["Tríceps francês halter", "3 x 10–12"]],
      [["Peck deck", "3 x 10–12"], ["Elevação lateral", "3 x 12–15"]],
      [["Desenvolvimento máquina", "3 x 8–10"], ["Tríceps coice", "3 x 10–12"]],
      [["Supino inclinado máquina", "3 x 8–10"], ["Elevação frontal", "3 x 12–15"]],
      [["Mergulho assistido", "2 x 8–10"], ["Tríceps testa", "2 x 10–12"]],
      [["Abdominal na máquina", "3 x 12–15"]]
    ]
  },
  {
    letter: "B", type: "INFERIOR QUADS", focus: "Quadríceps e glúteos",
    blocks: [
      [["Leg press 45°", "3 x 10–12"], ["Agachamento goblet", "3 x 10–12"]],
      [["Cadeira extensora", "3 x 10–12"], ["Afundo reverso", "3 x 10 cada"]],
      [["Cadeira adutora", "3 x 12–15"], ["Agachamento sumô", "3 x 10–12"]],
      [["Búlgaro na Smith", "3 x 8–10 cada"], ["Stiff com halteres", "3 x 8–10"]],
      [["Panturrilha no leg", "2 x 12–15"], ["Isométrico com anilha", "2 x 30–40s"]],
      [["Prancha", "3 x 30–45s"]]
    ]
  },
  {
    letter: "C", type: "SUPERIOR PULL", focus: "Costas, bíceps e ombro",
    blocks: [
      [["Puxada alta", "3 x 8–10"], ["Remada baixa", "3 x 8–10"]],
      [["Remada articulada", "3 x 8–10"], ["Rosca máquina", "3 x 10–12"]],
      [["Remada unilateral", "3 x 8–12 cada"], ["Rosca martelo", "3 x 10–12"]],
      [["Face pull", "3 x 12–15"], ["Pulldown braços estendidos", "3 x 10–12"]],
      [["Voador inverso", "2 x 12–15"], ["Rosca alternada", "2 x 10–12"]],
      [["Elevação de joelhos", "3 x 10–15"]]
    ]
  },
  {
    letter: "D", type: "INFERIOR POSTERIOR", focus: "Posterior e glúteos",
    blocks: [
      [["Flexora sentada", "3 x 10–12"], ["Stiff com halteres", "3 x 8–10"]],
      [["Hip thrust máquina", "3 x 8–12"], ["Stiff unilateral", "3 x 8–10 cada"]],
      [["Glúteo kickback", "3 x 12 cada"], ["Afundo reverso", "3 x 10 cada"]],
      [["Cadeira abdutora", "3 x 15–20"], ["Afundo lateral", "3 x 10 cada"]],
      [["Panturrilha máquina", "2 x 12–15"], ["Terra sumô", "2 x 10–12"]],
      [["Abdominal infra reverso", "3 x 12–15"]]
    ]
  }
];

const esc = (value) => String(value)
  .replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

function centered(lines, y, size, color, weight = 800, lineHeight = 1.16) {
  const values = Array.isArray(lines) ? lines : [lines];
  const spans = values.map((line, i) =>
    `<tspan x="219" dy="${i === 0 ? 0 : Math.round(size * lineHeight)}">${esc(line)}</tspan>`
  ).join("");
  return `<text x="219" y="${y}" text-anchor="middle" fill="${color}" font-family="DejaVu Sans, Arial, sans-serif" font-size="${size}" font-weight="${weight}">${spans}</text>`;
}

function roundedCard(y, lines, { done = false, primary = false, fontSize = 24, height = 96 } = {}) {
  const fill = primary ? C.orange : done ? C.surfaceDone : C.surface;
  const stroke = primary || done ? C.orange : C.stroke;
  const color = primary ? C.bg : done ? C.orange : C.white;
  const values = Array.isArray(lines) ? lines : [lines];
  const lineH = Math.round(fontSize * 1.22);
  const total = (values.length - 1) * lineH;
  const baseline = y + height / 2 - total / 2 + fontSize * .34;
  return `
    <rect x="44" y="${y}" width="350" height="${height}" rx="30" fill="${fill}" stroke="${stroke}" stroke-width="2"/>
    ${centered(values, baseline, fontSize, color, 800, 1.22)}`;
}

function progress(y, done, total) {
  const width = Math.max(4, Math.round(350 * done / total));
  return `
    <rect x="44" y="${y}" width="350" height="10" rx="5" fill="#1f242e"/>
    <rect x="44" y="${y}" width="${width}" height="10" rx="5" fill="${C.orange}"/>`;
}

function homeContent(done = 0) {
  let y = 58;
  let out = centered("V13 // GALAXY WATCH8", y, 20, C.orange); y += 38;
  out += centered(["TREINO", "DA LUANA"], y, 42, C.white, 900, 1.02); y += 102;
  out += centered(`${done} / 24 BLOCOS`, y, 21, done === 24 ? C.orange : C.muted); y += 15;
  out += progress(y, done, 24); y += 28;
  workouts.forEach((w, i) => {
    const count = done === 24 ? 6 : (done >= (i + 1) * 6 ? 6 : Math.max(0, done - i * 6));
    const complete = count === 6;
    out += roundedCard(y, [
      `${complete ? "✓  " : ""}TREINO ${w.letter}  ·  ${count}/6`, w.type, w.focus
    ], { done: complete, fontSize: 21, height: 105 });
    y += 116;
  });
  out += centered("SYNC COM O CELULAR", y + 20, 18, C.muted);
  return out;
}

function workoutContent(index, done = 0) {
  const w = workouts[index];
  let y = 44;
  let out = roundedCard(y, "‹  TODOS OS TREINOS", { fontSize: 20, height: 54 }); y += 75;
  out += centered(`TREINO ${w.letter}`, y, 40, C.white, 900); y += 36;
  out += centered(w.type, y, 20, C.orange); y += 31;
  out += centered(`${done} / 6 CONCLUÍDOS`, y, 18, C.muted); y += 13;
  out += progress(y, done, 6); y += 26;
  w.blocks.forEach((block, i) => {
    const complete = i < done;
    const label = i === 5 ? "ABS FINAL" : `DUPLA ${i + 1}`;
    const names = block.map((e, ei) => i === 5 ? e[0] : `${i + 1}${ei === 0 ? "A" : "B"}  ${e[0]}`);
    out += roundedCard(y, [`${complete ? "✓  " : ""}${label}`, ...names], {
      done: complete, fontSize: 20, height: block.length === 1 ? 78 : 104
    });
    y += block.length === 1 ? 90 : 116;
  });
  return out;
}

function blockContent(workoutIndex, blockIndex, done = false) {
  const w = workouts[workoutIndex];
  const block = w.blocks[blockIndex];
  const abs = blockIndex === 5;
  let y = 44;
  let out = roundedCard(y, `‹  TREINO ${w.letter}`, { fontSize: 20, height: 54 }); y += 76;
  out += centered(abs ? "ABS FINAL" : `DUPLA ${blockIndex + 1}`, y, 39, C.white, 900); y += 36;
  out += centered(abs ? "FINALIZADOR" : "A → B → DESCANSO", y, 19, C.orange); y += 25;
  block.forEach((exercise, e) => {
    const prefix = abs ? "ABS" : `${blockIndex + 1}${e === 0 ? "A" : "B"}`;
    out += roundedCard(y, [prefix, exercise[0], exercise[1]], { fontSize: 23, height: 107 });
    y += 118;
  });
  out += roundedCard(y, done ? "✓  CONCLUÍDO" : "MARCAR CONCLUÍDO", { primary: true, fontSize: 21, height: 60 });
  y += 70;
  if (blockIndex < 5) out += roundedCard(y, "PRÓXIMA  ›", { fontSize: 21, height: 54 });
  return out;
}

function wrapText(text, maxChars = 40) {
  const words = text.split(/\s+/);
  const lines = [];
  let line = "";
  for (const word of words) {
    if ((line + " " + word).trim().length > maxChars && line) {
      lines.push(line);
      line = word;
    } else line = (line + " " + word).trim();
  }
  if (line) lines.push(line);
  return lines.slice(0, 2);
}

function centeredAt(lines, x, y, size, color, weight = 700, lineHeight = 1.2) {
  const values = Array.isArray(lines) ? lines : [lines];
  const spans = values.map((line, i) =>
    `<tspan x="${x}" dy="${i === 0 ? 0 : Math.round(size * lineHeight)}">${esc(line)}</tspan>`
  ).join("");
  return `<text x="${x}" y="${y}" text-anchor="middle" fill="${color}" font-family="DejaVu Sans, Arial, sans-serif" font-size="${size}" font-weight="${weight}">${spans}</text>`;
}

let clipCounter = 0;
function watchFrame(x, y, title, subtitle, content, scroll = 0) {
  const clip = `clip-${clipCounter++}`;
  const screenX = x + 8;
  const screenY = y + 8;
  const scale = 2 / 3;
  const titleLines = wrapText(title, 34);
  const subtitleLines = wrapText(subtitle, 48);
  return `
    <defs><clipPath id="${clip}"><circle cx="${screenX + 146}" cy="${screenY + 146}" r="146"/></clipPath></defs>
    <rect x="${x}" y="${y + 4}" width="308" height="308" rx="112" fill="url(#metal)" stroke="#6b7078" stroke-width="2"/>
    <rect x="${x + 304}" y="${y + 112}" width="10" height="54" rx="5" fill="#5e646d"/>
    <rect x="${x + 305}" y="${y + 204}" width="8" height="38" rx="4" fill="#4a5058"/>
    <g clip-path="url(#${clip})">
      <circle cx="${screenX + 146}" cy="${screenY + 146}" r="146" fill="${C.bg}"/>
      <g transform="translate(${screenX} ${screenY - scroll}) scale(${scale})">${content}</g>
      <rect x="${screenX + 258}" y="${screenY + 213}" width="24" height="42" rx="12" fill="#07090e" fill-opacity=".82" stroke="#ffffff" stroke-opacity=".14"/>
      <text x="${screenX + 270}" y="${screenY + 240}" text-anchor="middle" fill="${C.muted}" font-family="Arial" font-size="16">↕</text>
    </g>
    <circle cx="${screenX + 146}" cy="${screenY + 146}" r="146" fill="none" stroke="#050608" stroke-width="5"/>
    ${centeredAt(titleLines, x + 154, y + 345, 17, C.white, 800, 1.2)}
    ${centeredAt(subtitleLines, x + 154, y + 390, 13, C.muted, 500, 1.22)}`;
}

function sheetSvg(title, eyebrow, frames) {
  const width = 1450;
  const height = frames.length > 4 ? 930 : 530;
  const positions = frames.map((_, i) => {
    const row = Math.floor(i / 4);
    const col = i % 4;
    return { x: 45 + col * 350, y: 120 + row * 405 };
  });
  const watches = frames.map((f, i) => watchFrame(
    positions[i].x, positions[i].y, f.title, f.subtitle, f.content, f.scroll
  )).join("");
  return `
    <svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">
      <defs>
        <linearGradient id="page" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" stop-color="#171b24"/><stop offset=".7" stop-color="#090b10"/>
        </linearGradient>
        <radialGradient id="glow"><stop stop-color="#ff7a1a" stop-opacity=".18"/><stop offset="1" stop-color="#ff7a1a" stop-opacity="0"/></radialGradient>
        <linearGradient id="metal" x1="0" y1="0" x2="1" y2="1">
          <stop stop-color="#4f555e"/><stop offset=".38" stop-color="#15181d"/><stop offset="1" stop-color="#3b4047"/>
        </linearGradient>
      </defs>
      <rect width="${width}" height="${height}" rx="32" fill="url(#page)"/>
      <circle cx="80" cy="20" r="240" fill="url(#glow)"/>
      <text x="42" y="48" fill="${C.orange}" font-family="DejaVu Sans, Arial" font-size="15" font-weight="800" letter-spacing="2">${esc(eyebrow.toUpperCase())}</text>
      <text x="42" y="84" fill="${C.white}" font-family="DejaVu Sans, Arial" font-size="29" font-weight="900">${esc(title)}</text>
      <text x="1406" y="82" text-anchor="end" fill="${C.muted}" font-family="DejaVu Sans, Arial" font-size="14">Galaxy Watch8 40 mm · 438 × 438 px</text>
      ${watches}
    </svg>`;
}

const overview = [
  { title: "Início", subtitle: "Progresso geral e quatro treinos", content: homeContent(0), scroll: 0 },
  { title: "Início com progresso", subtitle: "Treino A concluído", content: homeContent(6), scroll: 72 },
  { title: "Treino A", subtitle: "Peito, ombro e tríceps", content: workoutContent(0, 1), scroll: 70 },
  { title: "Treino B", subtitle: "Quadríceps e glúteos", content: workoutContent(1, 0), scroll: 70 },
  { title: "Treino C", subtitle: "Costas, bíceps e ombro", content: workoutContent(2, 0), scroll: 70 },
  { title: "Treino D", subtitle: "Posterior e glúteos", content: workoutContent(3, 0), scroll: 70 },
  { title: "Semana concluída", subtitle: "Todos os 24 blocos marcados", content: homeContent(24), scroll: 72 }
];

const sheets = [
  { id: "overview", title: "Painel e seleção de treinos", eyebrow: "Fluxo principal", frames: overview }
];

workouts.forEach((w, wi) => {
  const frames = [{
    title: `Treino ${w.letter}`,
    subtitle: `${w.focus} · visão dos blocos`,
    content: workoutContent(wi, 0), scroll: 72
  }];
  w.blocks.forEach((block, bi) => frames.push({
    title: bi === 5 ? "Abdominal final" : `Dupla ${bi + 1}`,
    subtitle: block.map(e => e[0]).join(" + "),
    content: blockContent(wi, bi, bi === 0),
    scroll: bi === 5 ? 42 : 70
  }));
  sheets.push({
    id: `workout-${w.letter.toLowerCase()}`,
    title: `Todas as telas do Treino ${w.letter}`,
    eyebrow: `Treino ${w.letter} · ${w.type}`,
    frames
  });
});

const rendered = [];
for (const item of sheets) {
  const svg = sheetSvg(item.title, item.eyebrow, item.frames);
  const output = path.join(here, "..", "screenshots", `watch-${item.id}.png`);
  await writeFile(path.join(here, "..", "screenshots", `watch-${item.id}.svg`), svg, "utf8");
  await sharp(Buffer.from(svg)).png().toFile(output);
  rendered.push(output);
}

const metadata = await Promise.all(rendered.map(file => sharp(file).metadata()));
const maxWidth = Math.max(...metadata.map(m => m.width));
const totalHeight = metadata.reduce((sum, m) => sum + m.height, 0) + (rendered.length - 1) * 28;
const composite = [];
let top = 0;
for (let i = 0; i < rendered.length; i++) {
  composite.push({ input: rendered[i], left: 0, top });
  top += metadata[i].height + 28;
}
await sharp({ create: { width: maxWidth, height: totalHeight, channels: 4, background: C.page } })
  .composite(composite)
  .png()
  .toFile(path.join(here, "..", "screenshots", "watch-todas-as-telas.png"));
