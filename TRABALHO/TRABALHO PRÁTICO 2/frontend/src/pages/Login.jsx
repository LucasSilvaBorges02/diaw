import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import TelaComFoto from '../components/TelaComFoto'

export default function Login() {
  const { usuario, entrar } = useAuth()
  const navegar = useNavigate()
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  if (usuario) return <Navigate to="/pedidos" replace />

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      await entrar(login, senha)
      navegar('/pedidos')
    } catch (err) {
      setErro(err.message === 'Failed to fetch' ? 'Não consegui falar com o servidor. Ele pode estar acordando, tente de novo em alguns segundos.' : err.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <TelaComFoto>
      <h1>Entrar</h1>
      <p className="sub">Use seu usuário e senha para acessar o sistema.</p>
      <form className="form" onSubmit={enviar}>
        <label>Usuário<input required autoFocus value={login} onChange={(e) => setLogin(e.target.value)} /></label>
        <label>Senha<input required type="password" value={senha} onChange={(e) => setSenha(e.target.value)} /></label>
        {erro && <p className="erro">{erro}</p>}
        <button className="btn" disabled={carregando}>{carregando ? 'Entrando…' : 'Entrar'}</button>
      </form>
      <p className="sub">Ainda não tem conta? <Link to="/cadastro">Cadastre-se</Link></p>
      <div className="dica">
        <b>Contas de teste</b> (senha <code>123456</code>): <code>cliente</code>, <code>empresa</code> e <code>banco</code>
      </div>
    </TelaComFoto>
  )
}
