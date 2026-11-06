import { useState } from 'react'

const VAZIO = { nome: '', login: '', senha: '', rg: '', cpf: '', endereco: '', profissao: '', rendimentos: [] }

export default function ClienteForm({ inicial, textoBotao, onSalvar, senhaObrigatoria }) {
  const [form, setForm] = useState({ ...VAZIO, ...inicial, senha: '' })
  const [erro, setErro] = useState('')
  const [salvando, setSalvando] = useState(false)

  const mudar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })
  const mudarRendimento = (i, campo, valor) =>
    setForm({ ...form, rendimentos: form.rendimentos.map((r, j) => (j === i ? { ...r, [campo]: valor } : r)) })

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    setSalvando(true)
    try {
      await onSalvar({
        ...form,
        senha: form.senha || null,
        rendimentos: form.rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })),
      })
    } catch (err) {
      setErro(err.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <form className="form" onSubmit={enviar}>
      <div className="grade">
        <label>Nome completo<input required value={form.nome} onChange={mudar('nome')} /></label>
        <label>Profissão<input value={form.profissao ?? ''} onChange={mudar('profissao')} /></label>
        <label>RG<input required value={form.rg} onChange={mudar('rg')} /></label>
        <label>CPF<input required placeholder="000.000.000-00" value={form.cpf} onChange={mudar('cpf')} /></label>
        <label className="inteiro">Endereço<input required value={form.endereco} onChange={mudar('endereco')} /></label>
        <label>Usuário<input required value={form.login} onChange={mudar('login')} /></label>
        <label>
          {senhaObrigatoria ? 'Senha' : 'Nova senha (opcional)'}
          <input type="password" required={senhaObrigatoria} minLength={6} value={form.senha} onChange={mudar('senha')} />
        </label>
      </div>

      <fieldset>
        <legend>Rendimentos (máximo 3)</legend>
        {form.rendimentos.map((r, i) => (
          <div className="linha" key={i}>
            <input required placeholder="Entidade empregadora" value={r.entidadeEmpregadora}
              onChange={(e) => mudarRendimento(i, 'entidadeEmpregadora', e.target.value)} />
            <input required type="number" min="0" step="0.01" placeholder="Valor (R$)" value={r.valor}
              onChange={(e) => mudarRendimento(i, 'valor', e.target.value)} />
            <button type="button" className="btn fantasma"
              onClick={() => setForm({ ...form, rendimentos: form.rendimentos.filter((_, j) => j !== i) })}>Remover</button>
          </div>
        ))}
        {form.rendimentos.length < 3 && (
          <button type="button" className="btn fantasma"
            onClick={() => setForm({ ...form, rendimentos: [...form.rendimentos, { entidadeEmpregadora: '', valor: '' }] })}>
            + Adicionar rendimento
          </button>
        )}
      </fieldset>

      {erro && <p className="erro">{erro}</p>}
      <button className="btn" disabled={salvando}>{salvando ? 'Salvando…' : textoBotao}</button>
    </form>
  )
}
