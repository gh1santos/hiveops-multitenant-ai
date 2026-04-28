import React, { useState } from 'react';
import api from '../services/api';
import { Zap, Building2, ArrowRight } from 'lucide-react';

export default function Login({ onLoginSuccess }) {
  const [companyId, setCompanyId] = useState('');
  const [newCompanyName, setNewCompanyName] = useState('');
  const [isRegistering, setIsRegistering] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await api.post('/auth/login', {
        email: 'ceo@hiveops.com',
        companyId: companyId
      });
      localStorage.setItem('hiveops_token', response.data.token);
      localStorage.setItem('hiveops_company_id', companyId);
      onLoginSuccess();
    } catch (error) {
      alert("ID inválido ou erro de conexão.");
    } finally { setLoading(false); }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await api.post('/companies', { name: newCompanyName });
      setCompanyId(response.data.id);
      setIsRegistering(false);
      alert(`Empresa criada! O seu ID é: ${response.data.id}. Guarde-o para entrar.`);
    } catch (error) {
      alert("Erro ao criar empresa.");
    } finally { setLoading(false); }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-zinc-950 p-4">
      <div className="bg-zinc-900 border border-zinc-800 p-10 rounded-3xl w-full max-w-md shadow-2xl">
        <div className="flex flex-col items-center mb-10">
          <div className="bg-yellow-400 p-3 rounded-2xl mb-4 shadow-[0_0_20px_rgba(250,204,21,0.2)]">
            <Zap className="text-black fill-black" size={32} />
          </div>
          <h1 className="text-3xl font-black italic tracking-tighter">HIVEOPS</h1>
          <p className="text-zinc-500 text-xs font-bold uppercase tracking-widest mt-2">Sistema de Orquestração</p>
        </div>

        {!isRegistering ? (
          <form onSubmit={handleLogin} className="space-y-6">
            <div>
              <label className="block text-[10px] font-black text-zinc-500 uppercase mb-2 ml-1">ID de Acesso da Empresa</label>
              <input
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-4 outline-none focus:border-yellow-400 text-white transition-all"
                placeholder="00000000-0000-0000..."
                value={companyId}
                onChange={(e) => setCompanyId(e.target.value)}
                required
              />
            </div>
            <button className="w-full bg-yellow-400 text-black font-black py-4 rounded-xl hover:bg-yellow-500 transition-all flex items-center justify-center gap-2">
              ACESSAR DASHBOARD <ArrowRight size={18} />
            </button>
            <button
              type="button"
              onClick={() => setIsRegistering(true)}
              className="w-full text-zinc-500 text-xs font-bold hover:text-white transition-colors"
            >
              NÃO TEM UM ID? CRIAR NOVA EMPRESA
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegister} className="space-y-6">
            <div>
              <label className="block text-[10px] font-black text-zinc-500 uppercase mb-2 ml-1">Nome da Organização</label>
              <input
                className="w-full bg-zinc-800 border border-zinc-700 rounded-xl p-4 outline-none focus:border-yellow-400 text-white transition-all"
                placeholder="Ex: Minha Empresa IA"
                value={newCompanyName}
                onChange={(e) => setNewCompanyName(e.target.value)}
                required
              />
            </div>
            <button className="w-full bg-white text-black font-black py-4 rounded-xl hover:bg-zinc-200 transition-all flex items-center justify-center gap-2">
              <Building2 size={18} /> REGISTAR EMPRESA
            </button>
            <button
              type="button"
              onClick={() => setIsRegistering(false)}
              className="w-full text-zinc-500 text-xs font-bold hover:text-white transition-colors"
            >
              VOLTAR PARA O LOGIN
            </button>
          </form>
        )}
      </div>
    </div>
  );
}