import React, { useState, useEffect } from 'react';
import Login from './pages/Login';
import api from './services/api';
import { Users, ClipboardList, Activity, Zap, Plus, Send, UserPlus, CheckCircle2, Terminal } from 'lucide-react';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('hiveops_token'));
  const [summary, setSummary] = useState(null);
  const [agents, setAgents] = useState([]);
  const [executions, setExecutions] = useState([]);
  const [logs, setLogs] = useState([{ id: 1, msg: '> Sistema HiveOps inicializado.', type: 'sys' }]);

  // Controles de Visibilidade dos Modais
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [showAgentModal, setShowAgentModal] = useState(false);

  // Estados dos Formulários
  const [newTask, setNewTask] = useState({ title: '', description: '', agentId: '', priority: 'MEDIUM' });
  const [newAgent, setNewAgent] = useState({ name: '', role: '', description: '', adapterType: 'BASH', budgetMonthly: 1000 });

  // Ciclo de Vida: Busca dados e inicia atualização automática (Polling)
  useEffect(() => {
    if (isAuthenticated) {
      fetchDashboardData();
      const interval = setInterval(fetchDashboardData, 5000); // Atualiza a cada 5 segundos
      return () => clearInterval(interval);
    }
  }, [isAuthenticated]);

  const fetchDashboardData = async () => {
    try {
      const [sumRes, agentsRes, execRes] = await Promise.all([
        api.get('/dashboard/summary'),
        api.get('/agents'),
        api.get('/executions')
      ]);
      setSummary(sumRes.data);
      setAgents(agentsRes.data);

      // Se houver novas execuções, avisa no log
      if (execRes.data?.length > executions.length) {
        addLog(`Nova tarefa concluída por ${execRes.data[0].agent.name}`, 'success');
      }
      setExecutions(execRes.data || []);
    } catch (e) {
      console.error("Erro na pulsação do sistema.");
    }
  };

  const addLog = (msg, type = 'sys') => {
    setLogs(prev => [{ id: Date.now(), msg: `> [${new Date().toLocaleTimeString()}] ${msg}`, type }, ...prev].slice(0, 15));
  };

  const handleCreateTask = async (e) => {
    e.preventDefault();
    try {
      await api.post('/tasks', newTask);
      setShowTaskModal(false);
      addLog(`Tarefa "${newTask.title}" enviada para a fila de processamento.`, 'info');
      setNewTask({ title: '', description: '', agentId: '', priority: 'MEDIUM' });
      fetchDashboardData();
    } catch (e) {
      alert("Erro ao criar tarefa. Verifique se preencheu todos os campos.");
    }
  };

  const handleCreateAgent = async (e) => {
    e.preventDefault();
    try {
      await api.post('/agents', newAgent);
      setShowAgentModal(false);
      addLog(`Novo agente contratado: ${newAgent.name}`, 'sys');
      setNewAgent({ name: '', role: '', description: '', adapterType: 'BASH', budgetMonthly: 1000 });
      fetchDashboardData();
    } catch (e) {
      alert("Erro ao criar agente.");
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    setIsAuthenticated(false);
  };

  if (!isAuthenticated) return <Login onLoginSuccess={() => setIsAuthenticated(true)} />;

  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-100 font-sans selection:bg-yellow-400 selection:text-black">

      {/* HEADER BAR */}
      <header className="h-16 border-b border-zinc-800 flex items-center px-8 justify-between bg-zinc-900/80 backdrop-blur-xl sticky top-0 z-50">
        <div className="flex items-center gap-3">
          <div className="bg-yellow-400 p-1.5 rounded shadow-[0_0_15px_rgba(250,204,21,0.3)]">
            <Zap size={18} className="text-black fill-black" />
          </div>
          <span className="font-black tracking-tighter text-2xl italic">HIVEOPS</span>
        </div>

        <div className="flex items-center gap-3">
            <button
                onClick={() => setShowAgentModal(true)}
                className="text-zinc-400 hover:text-white px-4 py-1.5 rounded-lg text-sm font-bold flex items-center gap-2 transition-all"
            >
                <UserPlus size={16} /> Contratar Agente
            </button>
            <button
                onClick={() => setShowTaskModal(true)}
                className="bg-white text-black px-6 py-2 rounded-full font-black text-xs uppercase tracking-widest hover:scale-105 active:scale-95 transition-all shadow-lg"
            >
                Delegar Missão
            </button>
            <button onClick={handleLogout} className="ml-4 p-2 text-zinc-600 hover:text-red-500 transition-colors">Sair</button>
        </div>
      </header>

      {/* MAIN CONTENT */}
      <main className="p-8 max-w-[1600px] mx-auto w-full grid grid-cols-1 lg:grid-cols-12 gap-8">

        {/* COLUNA ESQUERDA (DASHBOARD) */}
        <div className="lg:col-span-8 space-y-8">

          {/* STATS GRID */}
          <section className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="bg-zinc-900/50 p-6 rounded-2xl border border-zinc-800">
                <p className="text-[10px] text-zinc-500 uppercase font-black mb-1 tracking-widest">Agentes Ativos</p>
                <h3 className="text-4xl font-black text-white">{summary?.totalAgents || 0}</h3>
            </div>
            <div className="bg-zinc-900/50 p-6 rounded-2xl border border-zinc-800">
                <p className="text-[10px] text-zinc-500 uppercase font-black mb-1 tracking-widest">Missões Concluídas</p>
                <h3 className="text-4xl font-black text-green-500">{summary?.completedTasks || 0}</h3>
            </div>
            <div className="bg-zinc-900/50 p-6 rounded-2xl border border-zinc-800">
                <p className="text-[10px] text-zinc-500 uppercase font-black mb-1 tracking-widest">Fila de Espera</p>
                <h3 className="text-4xl font-black text-yellow-500">{summary?.pendingTasks || 0}</h3>
            </div>
          </section>

          {/* AGENTS LIST */}
          <section className="bg-zinc-900/30 rounded-3xl border border-zinc-800 p-8">
            <h2 className="text-sm font-black uppercase tracking-[0.2em] text-zinc-500 mb-8 flex items-center gap-3 font-mono">
               <div className="h-1 w-8 bg-yellow-400 rounded-full" /> OPERAÇÃO_DA_COLMEIA
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {agents.map(agent => (
                <div key={agent.id} className="p-5 bg-zinc-900 border border-zinc-800 rounded-2xl flex items-center justify-between hover:border-zinc-600 transition-all">
                  <div className="flex items-center gap-4">
                    <div className="h-10 w-10 rounded-xl bg-zinc-800 flex items-center justify-center font-black text-zinc-500 border border-zinc-700">
                      {agent.name.charAt(0)}
                    </div>
                    <div>
                        <h4 className="font-bold text-white leading-none mb-1">{agent.name}</h4>
                        <p className="text-[10px] text-zinc-500 uppercase font-bold tracking-tighter">{agent.role}</p>
                    </div>
                  </div>
                  <div className="text-right">
                     <span className={`text-[9px] px-2 py-0.5 rounded font-black border ${agent.adapterType === 'BASH' ? 'border-blue-500/30 text-blue-500' : 'border-orange-500/30 text-orange-400'}`}>
                        {agent.adapterType}
                     </span>
                     <div className="mt-2 flex justify-end">
                        <div className="h-1.5 w-1.5 bg-green-500 rounded-full animate-pulse shadow-[0_0_8px_#22c55e]" />
                     </div>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* OUTPUT / EXECUÇÕES (Quem vê o resultado!) */}
          <section className="space-y-4">
            <h2 className="text-sm font-black uppercase tracking-[0.2em] text-zinc-500 flex items-center gap-3 font-mono">
               <div className="h-1 w-8 bg-green-500 rounded-full" /> SAÍDA_DOS_AGENTES
            </h2>
            {executions.length === 0 && <p className="text-zinc-600 italic text-sm p-8 bg-zinc-900/20 border border-dashed border-zinc-800 rounded-2xl text-center">Aguardando a primeira execução ser concluída...</p>}
            {executions.slice(0, 5).map(ex => (
              <div key={ex.id} className="bg-zinc-900 border-l-4 border-l-green-500 border border-zinc-800 rounded-r-2xl p-6 shadow-xl">
                 <div className="flex justify-between items-start mb-4">
                    <div className="flex items-center gap-2">
                        <CheckCircle2 size={14} className="text-green-500" />
                        <span className="text-xs font-bold text-zinc-300">Tarefa: {ex.task?.title}</span>
                    </div>
                    <span className="text-[10px] font-mono text-zinc-600">{new Date(ex.createdAt).toLocaleString()}</span>
                 </div>
                 <div className="bg-black/50 p-4 rounded-lg border border-zinc-800/50">
                    <p className="text-sm text-zinc-400 font-mono leading-relaxed whitespace-pre-wrap">
                        {ex.resultOutput}
                    </p>
                 </div>
                 <div className="mt-4 flex gap-4 text-[9px] font-black text-zinc-500 uppercase tracking-widest">
                    <span>Consumo: <span className="text-zinc-300">{ex.tokensConsumed} tokens</span></span>
                    <span>Latência: <span className="text-zinc-300">{ex.executionTimeMs}ms</span></span>
                    <span>Agente: <span className="text-zinc-300">{ex.agent?.name}</span></span>
                 </div>
              </div>
            ))}
          </section>
        </div>

        {/* COLUNA DIREITA (TERMINAL LOG) */}
        <aside className="lg:col-span-4">
            <div className="bg-black rounded-2xl border border-zinc-800 overflow-hidden shadow-2xl sticky top-24">
                <div className="bg-zinc-900/80 px-4 py-2 border-b border-zinc-800 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <Terminal size={14} className="text-zinc-500" />
                        <span className="text-[10px] font-mono text-zinc-500 uppercase tracking-widest">Terminal_Consola</span>
                    </div>
                    <div className="flex gap-1.5">
                        <div className="h-2 w-2 rounded-full bg-zinc-800" />
                        <div className="h-2 w-2 rounded-full bg-zinc-800" />
                    </div>
                </div>
                <div className="p-6 font-mono text-[11px] space-y-3 h-[600px] overflow-y-auto">
                    {logs.map(log => (
                        <p key={log.id} className={`${log.type === 'success' ? 'text-green-400' : log.type === 'info' ? 'text-blue-400' : 'text-zinc-500'}`}>
                            {log.msg}
                        </p>
                    ))}
                    <p className="text-yellow-400 animate-pulse">{`> _`}</p>
                </div>
            </div>
        </aside>
      </main>

      {/* MODAL: NOVO AGENTE */}
      {showAgentModal && (
        <div className="fixed inset-0 bg-black/90 backdrop-blur-md flex items-center justify-center p-4 z-[100]">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-3xl w-full max-w-md shadow-2xl">
            <h2 className="text-2xl font-black mb-2 text-white italic">CONTRATAR AGENTE</h2>
            <p className="text-zinc-500 text-xs mb-8 uppercase tracking-widest font-bold">Defina as competências do novo membro</p>
            <form onSubmit={handleCreateAgent} className="space-y-4">
              <input
                placeholder="Nome do Agente"
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-3 outline-none focus:border-yellow-400 transition-all text-sm"
                onChange={e => setNewAgent({...newAgent, name: e.target.value})}
                required
              />
              <input
                placeholder="Cargo (ex: Analista de Dados)"
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-3 outline-none focus:border-yellow-400 transition-all text-sm"
                onChange={e => setNewAgent({...newAgent, role: e.target.value})}
                required
              />
              <select
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-3 outline-none focus:border-yellow-400 text-sm"
                onChange={e => setNewAgent({...newAgent, adapterType: e.target.value})}
                value={newAgent.adapterType}
              >
                <option value="BASH">TIPO: MOCK (Simulador Local)</option>
                <option value="OPENAI">TIPO: OPENAI (Requer API Key)</option>
              </select>
              <div className="flex gap-3 pt-6">
                <button type="button" onClick={() => setShowAgentModal(false)} className="flex-1 px-4 py-3 text-zinc-500 font-bold hover:text-white transition-all">CANCELAR</button>
                <button type="submit" className="flex-1 bg-yellow-400 text-black font-black py-3 rounded-xl hover:bg-yellow-500 shadow-lg transition-all">CONTRATAR</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: NOVA TAREFA */}
      {showTaskModal && (
        <div className="fixed inset-0 bg-black/90 backdrop-blur-md flex items-center justify-center p-4 z-[100]">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-3xl w-full max-w-lg shadow-2xl">
            <h2 className="text-2xl font-black mb-2 text-white italic tracking-tighter">DELEGAR MISSÃO</h2>
            <p className="text-zinc-500 text-xs mb-8 uppercase tracking-widest font-bold">Envie ordens para a rede de agentes</p>
            <form onSubmit={handleCreateTask} className="space-y-4">
              <input
                placeholder="Título da Tarefa"
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-3 outline-none focus:border-yellow-400 text-sm transition-all"
                onChange={e => setNewTask({...newTask, title: e.target.value})}
                required
              />
              <textarea
                placeholder="Descreva as instruções detalhadas aqui..."
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-3 h-32 outline-none focus:border-yellow-400 text-sm transition-all resize-none"
                onChange={e => setNewTask({...newTask, description: e.target.value})}
                required
              />
              <select
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-3 outline-none focus:border-yellow-400 text-sm"
                onChange={e => setNewTask({...newTask, agentId: e.target.value})}
                required
              >
                <option value="">Selecione o Agente Executor...</option>
                {agents.map(a => <option key={a.id} value={a.id}>{a.name} ({a.adapterType})</option>)}
              </select>
              <div className="flex gap-3 pt-6">
                <button type="button" onClick={() => setShowTaskModal(false)} className="flex-1 px-4 py-3 text-zinc-500 font-bold hover:text-white transition-all">DESCARTAR</button>
                <button type="submit" className="flex-1 bg-white text-black font-black py-3 rounded-xl hover:bg-zinc-200 shadow-lg flex items-center justify-center gap-2 transition-all">
                   <Send size={18}/> EXECUTAR_ORDEM
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;