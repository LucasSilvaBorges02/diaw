import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { dinheiro } from '../utils/formato'
import ClienteForm from '../components/ClienteForm'

export default function Clientes() {
  const [clientes, setClientes] = useState([])
  const [editando, setEditando] = useState(null)
  const [erro, setErro] = useState('')

  const carregar = () => api('/api/clientes').then(setClientes).catch((e) => setErro(e.message))
  useEffect(() => { carregar() }, [])

  async function salvar(dados) {
    await api(`/api/clientes/${editando.id}`, { metodo: 'PUT', corpo: dados })
    setEditando(null)
    carregar()
  }

  async function excluir(c) {
    if (!confirm(`Excluir ${c.nome} e os pedidos dele?`)) return
    await api(`/api/clientes/${c.id}`, { metodo: 'DELETE' }).catch((e) => setErro(e.message))
    carregar()
  }

  return (
    <>
      <h1>Clientes</h1>
      {erro && <p className="erro">{erro}</p>}
      {editando && (
        <div className="cartao">
          <h2>Editando {editando.nome}</h2>
          <ClienteForm inicial={editando} textoBotao="Salvar" onSalvar={salvar} />
          <button className="btn fantasma" onClick={() => setEditando(null)}>Fechar</button>
        </div>
      )}
      <table className="tabela">
        <thead><tr><th>Nome</th><th>CPF</th><th>Profissão</th><th>Renda</th><th></th></tr></thead>
        <tbody>
          {clientes.map((c) => (
            <tr key={c.id}>
              <td>{c.nome}<small>{c.endereco}</small></td>
              <td>{c.cpf}</td>
              <td>{c.profissao || '-'}</td>
              <td>{dinheiro(c.rendaTotal)}</td>
              <td className="acoes">
                <button className="btn fantasma" onClick={() => setEditando(c)}>Editar</button>
                <button className="btn perigo" onClick={() => excluir(c)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  )
}
