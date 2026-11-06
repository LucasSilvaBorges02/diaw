import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import ClienteForm from '../components/ClienteForm'

export default function MeuCadastro() {
  const { usuario, sair } = useAuth()
  const [cliente, setCliente] = useState(null)
  const [aviso, setAviso] = useState('')

  useEffect(() => {
    api(`/api/clientes/${usuario.id}`).then(setCliente).catch((e) => setAviso(e.message))
  }, [usuario.id])

  async function salvar(dados) {
    setCliente(await api(`/api/clientes/${usuario.id}`, { metodo: 'PUT', corpo: dados }))
    setAviso('Dados atualizados!')
  }

  async function excluir() {
    if (!confirm('Excluir sua conta e todos os seus pedidos?')) return
    await api(`/api/clientes/${usuario.id}`, { metodo: 'DELETE' })
    sair()
  }

  return (
    <>
      <h1>Meu cadastro</h1>
      {aviso && <p className="aviso">{aviso}</p>}
      {cliente && <ClienteForm key={JSON.stringify(cliente)} inicial={cliente} textoBotao="Salvar alterações" onSalvar={salvar} />}
      <button className="btn perigo" onClick={excluir}>Excluir minha conta</button>
    </>
  )
}
