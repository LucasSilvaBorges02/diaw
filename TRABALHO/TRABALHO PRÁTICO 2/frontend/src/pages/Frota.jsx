import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { dinheiro } from '../utils/formato'

const NOVO = { matricula: '', ano: new Date().getFullYear(), marca: '', modelo: '', placa: '', valorDiaria: '' }

export default function Frota() {
  const { usuario } = useAuth()
  const agente = usuario.papel === 'AGENTE'
  const [carros, setCarros] = useState([])
  const [form, setForm] = useState(NOVO)
  const [erro, setErro] = useState('')

  const carregar = () => api('/api/automoveis').then(setCarros).catch((e) => setErro(e.message))
  useEffect(() => { carregar() }, [])

  async function cadastrar(e) {
    e.preventDefault()
    setErro('')
    try {
      await api('/api/automoveis', { metodo: 'POST', corpo: { ...form, ano: Number(form.ano), valorDiaria: Number(form.valorDiaria) } })
      setForm(NOVO)
      carregar()
    } catch (err) {
      setErro(err.message)
    }
  }

  async function remover(id) {
    setErro('')
    await api(`/api/automoveis/${id}`, { metodo: 'DELETE' }).catch((e) => setErro(e.message))
    carregar()
  }

  return (
    <>
      <h1>Frota</h1>
      {erro && <p className="erro">{erro}</p>}
      {agente && (
        <form className="form cartao linha-form" onSubmit={cadastrar}>
          {['marca', 'modelo', 'ano', 'placa', 'matricula', 'valorDiaria'].map((campo) => (
            <input key={campo} required placeholder={campo === 'valorDiaria' ? 'Diária (R$)' : campo[0].toUpperCase() + campo.slice(1)}
              type={campo === 'ano' || campo === 'valorDiaria' ? 'number' : 'text'} value={form[campo]}
              onChange={(e) => setForm({ ...form, [campo]: e.target.value })} />
          ))}
          <button className="btn">Adicionar carro</button>
        </form>
      )}
      <div className="carros">
        {carros.map((c) => (
          <article key={c.id} className="cartao carro">
            <span className="ano">{c.ano}</span>
            <h3>{c.marca} <b>{c.modelo}</b></h3>
            <p>Placa {c.placa} · Matrícula {c.matricula}</p>
            <strong>{dinheiro(c.valorDiaria)}<small>/dia</small></strong>
            {agente && <button className="btn fantasma" onClick={() => remover(c.id)}>Remover</button>}
          </article>
        ))}
      </div>
    </>
  )
}
