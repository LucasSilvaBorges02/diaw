import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { STATUS, data, dinheiro } from '../utils/formato'

const hoje = () => new Date().toISOString().slice(0, 10)

function FormPedido({ carros, inicial, onSalvar, onCancelar }) {
  const [form, setForm] = useState(inicial ?? { automovelId: '', dataInicio: hoje(), dataFim: '' })
  const [erro, setErro] = useState('')
  const carro = carros.find((c) => c.id === Number(form.automovelId))
  const dias = form.dataInicio && form.dataFim ? (new Date(form.dataFim) - new Date(form.dataInicio)) / 86400000 : 0

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    try {
      await onSalvar({ ...form, automovelId: Number(form.automovelId) })
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <form className="form cartao" onSubmit={enviar}>
      <div className="grade">
        <label className="inteiro">Automóvel
          <select required value={form.automovelId} onChange={(e) => setForm({ ...form, automovelId: e.target.value })}>
            <option value="">Escolha um carro</option>
            {carros.map((c) => <option key={c.id} value={c.id}>{c.marca} {c.modelo} ({c.ano}) · {dinheiro(c.valorDiaria)}/dia</option>)}
          </select>
        </label>
        <label>Retirada<input type="date" required min={hoje()} value={form.dataInicio} onChange={(e) => setForm({ ...form, dataInicio: e.target.value })} /></label>
        <label>Devolução<input type="date" required min={form.dataInicio} value={form.dataFim} onChange={(e) => setForm({ ...form, dataFim: e.target.value })} /></label>
      </div>
      {carro && dias > 0 && <p className="sub">{dias} diária(s) · total estimado <b>{dinheiro(carro.valorDiaria * dias)}</b></p>}
      {erro && <p className="erro">{erro}</p>}
      <div className="acoes">
        <button className="btn">Salvar pedido</button>
        <button type="button" className="btn fantasma" onClick={onCancelar}>Fechar</button>
      </div>
    </form>
  )
}

function Avaliar({ pedido, bancos, onFeito }) {
  const [parecer, setParecer] = useState('')
  const [tipo, setTipo] = useState('CLIENTE')
  const [bancoId, setBancoId] = useState('')
  const [erro, setErro] = useState('')

  const fazer = (promessa) => promessa.then(onFeito).catch((e) => setErro(e.message))
  const avaliar = (aprovado) =>
    fazer(api(`/api/pedidos/${pedido.id}/avaliacao`, { metodo: 'PATCH', corpo: { aprovado, parecer } }))
  const contratar = () =>
    fazer(api(`/api/pedidos/${pedido.id}/contrato`, {
      metodo: 'POST',
      corpo: { tipoProprietario: tipo, bancoId: bancoId ? Number(bancoId) : null },
    }))

  return (
    <div className="avaliar">
      {pedido.status === 'PENDENTE' && (
        <>
          <input placeholder="Parecer financeiro" value={parecer} onChange={(e) => setParecer(e.target.value)} />
          <button className="btn" onClick={() => avaliar(true)}>Aprovar</button>
          <button className="btn perigo" onClick={() => avaliar(false)}>Reprovar</button>
        </>
      )}
      {pedido.status === 'APROVADO' && (
        <>
          <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
            <option value="CLIENTE">Carro em nome do cliente</option>
            <option value="EMPRESA">Carro em nome da empresa</option>
            <option value="BANCO">Carro em nome do banco</option>
          </select>
          <select value={bancoId} onChange={(e) => setBancoId(e.target.value)}>
            <option value="">Sem contrato de crédito</option>
            {bancos.map((b) => <option key={b.id} value={b.id}>Crédito: {b.nome}</option>)}
          </select>
          <button className="btn" onClick={contratar}>Executar contrato</button>
        </>
      )}
      {erro && <p className="erro">{erro}</p>}
    </div>
  )
}

export default function Pedidos() {
  const { usuario } = useAuth()
  const agente = usuario.papel === 'AGENTE'
  const [pedidos, setPedidos] = useState([])
  const [carros, setCarros] = useState([])
  const [bancos, setBancos] = useState([])
  const [form, setForm] = useState(null) // null | 'novo' | pedido em edição
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(true)

  const carregar = () =>
    api('/api/pedidos').then(setPedidos).catch((e) => setErro(e.message)).finally(() => setCarregando(false))

  useEffect(() => {
    carregar()
    api('/api/automoveis').then(setCarros).catch(() => {})
    if (agente) api('/api/agentes/bancos').then(setBancos).catch(() => {})
  }, [agente])

  async function salvar(dados) {
    if (form === 'novo') await api('/api/pedidos', { metodo: 'POST', corpo: dados })
    else await api(`/api/pedidos/${form.id}`, { metodo: 'PUT', corpo: dados })
    setForm(null)
    carregar()
  }

  async function cancelar(p) {
    if (!confirm('Cancelar este pedido?')) return
    await api(`/api/pedidos/${p.id}/cancelar`, { metodo: 'PATCH' }).catch((e) => setErro(e.message))
    carregar()
  }

  return (
    <>
      <div className="titulo">
        <h1>{agente ? 'Pedidos para análise' : 'Meus pedidos'}</h1>
        {!agente && !form && <button className="btn" onClick={() => setForm('novo')}>+ Novo pedido</button>}
      </div>
      {erro && <p className="erro">{erro}</p>}
      {form && (
        <FormPedido carros={carros} onCancelar={() => setForm(null)} onSalvar={salvar}
          inicial={form === 'novo' ? null : { automovelId: form.automovel.id, dataInicio: form.dataInicio, dataFim: form.dataFim }} />
      )}
      {carregando && <p className="sub">Carregando… (o servidor gratuito pode levar até 1 minuto para acordar)</p>}
      {!carregando && pedidos.length === 0 && <p className="sub">Nenhum pedido ainda.</p>}

      <div className="pedidos">
        {pedidos.map((p) => (
          <article key={p.id} className="cartao pedido">
            <div className="cabeca">
              <h3>#{p.id} · {p.automovel.marca} {p.automovel.modelo}</h3>
              <span className={`status ${p.status.toLowerCase()}`}>{STATUS[p.status]}</span>
            </div>
            <p>{data(p.dataInicio)} até {data(p.dataFim)} · {p.dias} diária(s) · <b>{dinheiro(p.valorTotal)}</b></p>
            {agente && <p className="sub">Cliente: {p.cliente.nome} · {p.cliente.profissao || 'sem profissão'} · renda {dinheiro(p.cliente.rendaTotal)}</p>}
            {p.parecer && <p className="sub">Parecer de {p.agenteAvaliador}: “{p.parecer}”</p>}
            {p.contrato && (
              <p className="sub">
                Contrato #{p.contrato.id} por {p.contrato.agente} · carro em nome de {p.contrato.proprietario}
                {p.contrato.bancoCredito && ` · crédito de ${dinheiro(p.contrato.valorCredito)} pelo ${p.contrato.bancoCredito}`}
              </p>
            )}
            <div className="acoes">
              {p.status === 'PENDENTE' && <button className="btn fantasma" onClick={() => setForm(p)}>Modificar</button>}
              {!agente && ['PENDENTE', 'APROVADO'].includes(p.status) && <button className="btn perigo" onClick={() => cancelar(p)}>Cancelar</button>}
            </div>
            {agente && <Avaliar pedido={p} bancos={bancos} onFeito={carregar} />}
          </article>
        ))}
      </div>
    </>
  )
}
