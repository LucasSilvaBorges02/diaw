import { Link, useNavigate } from 'react-router-dom'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import ClienteForm from '../components/ClienteForm'
import TelaComFoto from '../components/TelaComFoto'

export default function Cadastro() {
  const { entrar } = useAuth()
  const navegar = useNavigate()

  async function salvar(dados) {
    await api('/api/clientes', { metodo: 'POST', corpo: dados })
    await entrar(dados.login, dados.senha)
    navegar('/pedidos')
  }

  return (
    <TelaComFoto>
      <h1>Criar conta</h1>
      <p className="sub">Cadastro de cliente. Já tem conta? <Link to="/login">Entrar</Link></p>
      <ClienteForm senhaObrigatoria textoBotao="Cadastrar" onSalvar={salvar} />
    </TelaComFoto>
  )
}
