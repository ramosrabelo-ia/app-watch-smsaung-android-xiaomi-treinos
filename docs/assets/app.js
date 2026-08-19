const workouts=[
  {
    letter:'A',type:'Superior Push',focus:'Peito, ombro e tríceps',
    names:['Supino na máquina','Tríceps francês com halter','Voador peck deck','Elevação lateral com halteres','Desenvolvimento na máquina','Tríceps coice com halteres','Supino inclinado na máquina','Elevação frontal com halteres','Mergulho assistido','Tríceps testa com halteres','Abdominal na máquina'],
    reps:['10 a 12','10 a 12','10 a 12','12 a 15','8 a 10','10 a 12','8 a 10','12 a 15','8 a 10','10 a 12','12 a 15'],
    sets:[3,3,3,3,3,3,3,3,2,2,3]
  },
  {
    letter:'B',type:'Inferior Quads',focus:'Quadríceps e glúteos',
    names:['Leg press 45°','Agachamento goblet com halter','Cadeira extensora','Afundo reverso com halteres','Cadeira adutora','Agachamento sumô com halter','Búlgaro na Smith assistido','Stiff com halteres','Panturrilha no leg press','Agachamento isométrico com anilha','Prancha'],
    reps:['10 a 12','10 a 12','10 a 12','10 por perna','12 a 15','10 a 12','8 a 10 por perna','8 a 10','12 a 15','30 a 40 segundos','30 a 45 segundos'],
    sets:[3,3,3,3,3,3,3,3,2,2,3]
  },
  {
    letter:'C',type:'Superior Pull',focus:'Costas, bíceps e posterior de ombro',
    names:['Puxada alta na frente','Remada baixa sentada','Remada articulada','Rosca bíceps na máquina','Remada unilateral na máquina','Rosca martelo com halteres','Face pull na polia com corda','Pulldown com braços estendidos','Voador inverso','Rosca alternada com halteres','Elevação de joelhos'],
    reps:['8 a 10','8 a 10','8 a 10','10 a 12','8 a 12 por lado','10 a 12','12 a 15','10 a 12','12 a 15','10 a 12','10 a 15'],
    sets:[3,3,3,3,3,3,3,3,2,2,3]
  },
  {
    letter:'D',type:'Inferior Posterior',focus:'Posterior de coxa e glúteos',
    names:['Flexora sentada','Stiff com halteres','Hip thrust na máquina','Stiff unilateral com halter','Glúteo kickback na máquina','Afundo reverso com halteres','Cadeira abdutora','Afundo lateral com halter','Panturrilha em pé na máquina','Terra sumô com halter','Abdominal infra reverso'],
    reps:['10 a 12','8 a 10','8 a 12','8 a 10 por lado','12 por perna','10 por perna','15 a 20','10 por lado','12 a 15','10 a 12','12 a 15'],
    sets:[3,3,3,3,3,3,3,3,2,2,3]
  }
];

const blocks=[[0,1],[2,3],[4,5],[6,7],[8,9],[10]];
const $=s=>document.querySelector(s);
const $$=s=>Array.from(document.querySelectorAll(s));

function showScreen(name){
  $$('.screen').forEach(s=>s.classList.toggle('active',s.id===`screen-${name}`));
  $$('#nav button').forEach(b=>b.classList.toggle('active',b.dataset.screen===name));
  $('.workspace').scrollTo({top:0,behavior:'smooth'});
}

$$('#nav button').forEach(b=>b.addEventListener('click',()=>showScreen(b.dataset.screen)));

function buildWorkoutSelector(){
  const selector=$('#workout-selector');
  selector.innerHTML='';
  workouts.forEach((w,i)=>{
    const b=document.createElement('button');
    b.type='button';
    b.dataset.workout=i;
    b.innerHTML=`<span>TREINO ${w.letter}</span><strong>${w.type}</strong><small>${w.focus}</small>`;
    b.addEventListener('click',()=>renderWorkout(i));
    selector.appendChild(b);
  });
  renderWorkout(0);
}

function renderWorkout(index){
  const w=workouts[index];
  $$('#workout-selector button').forEach(b=>b.classList.toggle('active',Number(b.dataset.workout)===index));
  const rows=blocks.map((idxs,block)=>{
    const items=idxs.map((exercise,offset)=>{
      const label=block===5?'ABS':`${block+1}${offset===0?'A':'B'}`;
      return `<article class="exercise-item"><span>${label}</span><div><strong>${w.names[exercise]}</strong><small>${w.sets[exercise]} séries</small></div><em>${w.reps[exercise]}</em></article>`;
    }).join('');
    return `<div class="block-row"><div class="block-label">${block===5?'Finalizador':`Dupla ${block+1}`}</div><div class="exercise-pair ${idxs.length===1?'single':''}">${items}</div></div>`;
  }).join('');
  $('#workout-detail').innerHTML=`<div class="workout-title-row"><div><span>TREINO ${w.letter}</span><h3>${w.type}</h3></div><p>${w.focus}</p></div><div class="blocks-grid">${rows}</div>`;
}

$$('[data-open-workout]').forEach(b=>b.addEventListener('click',()=>{
  showScreen('workouts');
  renderWorkout(Number(b.dataset.openWorkout));
}));

let restTimer=null;
$('#demo-complete').addEventListener('click',()=>{
  if(restTimer)clearInterval(restTimer);
  const state=$('#demo-rest');
  state.hidden=false;
  let seconds=60;
  $('#rest-count').textContent=seconds;
  $('#demo-complete').textContent='Dupla concluída';
  $('#demo-complete').disabled=true;
  restTimer=setInterval(()=>{
    seconds-=1;
    $('#rest-count').textContent=seconds;
    if(seconds<=0){
      clearInterval(restTimer);restTimer=null;
      $('#demo-complete').textContent='Concluir dupla';
      $('#demo-complete').disabled=false;
      state.hidden=true;
    }
  },1000);
});

const answers={
  funciona:'A V12 organiza a semana em quatro treinos. Cada treino tem cinco duplas conjugadas e um finalizador abdominal. Você faz A, faz B, descansa depois da dupla e marca o bloco. O aplicativo mantém o progresso semanal e permite continuar de onde parou.',
  offline:'As 44 fotos dos exercícios ficam incorporadas ao APK. Por isso a consulta da execução não depende de internet durante o treino.',
  health:'Quando o treino termina, o aplicativo pode gravar uma sessão de treino de força no Health Connect. A V12 solicita apenas permissão de escrita de exercício. Ela não lê dados de saúde.',
  watch:'O Galaxy Watch8 recebe um aplicativo Wear OS 6 próprio. Ele mostra os quatro treinos, as cinco duplas e o finalizador e permite marcar os blocos diretamente no pulso, sem depender de notificações do celular.',
  privacy:'O aplicativo não envia dados para servidor próprio. A integração de saúde é opcional e limitada ao registro do exercício concluído no Health Connect.',
  treino:'Existem quatro divisões: A Superior Push, B Inferior Quads, C Superior Pull e D Inferior Posterior. Todas usam a mesma estrutura de cinco duplas mais um finalizador.',
  versao:'A versão atual é a V12. Ela adiciona treinos conjugados, 44 fotos offline, Health Connect e um aplicativo Wear OS independente para o Galaxy Watch8, além de remover notificações.'
};

function inferAnswer(text){
  const q=text.toLowerCase();
  if(q.includes('watch')||q.includes('relógio')||q.includes('relogio'))return answers.watch;
  if(q.includes('health')||q.includes('withings'))return answers.health;
  if(q.includes('offline')||q.includes('internet')||q.includes('foto'))return answers.offline;
  if(q.includes('privacidade')||q.includes('dados'))return answers.privacy;
  if(q.includes('treino')||q.includes('a b c d'))return answers.treino;
  if(q.includes('v12')||q.includes('versão')||q.includes('versao'))return answers.versao;
  return answers.funciona;
}

function showAnswer(text){
  const box=$('#ask-answer');
  box.hidden=false;
  box.textContent=text;
}

$$('[data-question]').forEach(b=>b.addEventListener('click',()=>showAnswer(answers[b.dataset.question]||answers.funciona)));
$('#ask-send').addEventListener('click',()=>{
  const input=$('#ask-input');
  const q=input.value.trim();
  if(!q)return;
  showAnswer(inferAnswer(q));
});
$('#ask-input').addEventListener('keydown',e=>{if(e.key==='Enter')$('#ask-send').click()});

buildWorkoutSelector();
