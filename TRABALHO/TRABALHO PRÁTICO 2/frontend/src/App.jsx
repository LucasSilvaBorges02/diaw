import { Navigate, NavLink, Outlet, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Login from './pages/Login'
import Cadastro from './pages/Cadastro'
import Pedidos from './pages/Pedidos'
import MeuCadastro from './pages/MeuCadastro'
import Clientes from './pages/Clientes'
import Frota from './pages/Frota'

function Protegido() {
  const { usuario, sair } = useAuth()
  if (!usuario) return <Navigate to="/login" replace />
  const agente = usuario.papel === 'AGENTE'
  return (
    <>
      <header className="topo">
        <span className="marca">ROTA<b>LIVRE</b></span>
        <nav>
          <NavLink to="/pedidos">Pedidos</NavLink>
          <NavLink to="/frota">Frota</NavLink>
          {agente ? <NavLink to="/clientes">Clientes</NavLink> : <NavLink to="/meu-cadastro">Meu cadastro</NavLink>}
        </nav>
        <div className="quem">
          <span>{usuario.nome} · {agente ? (usuario.tipoAgente === 'BANCO' ? 'Banco' : 'Empresa') : 'Cliente'}</span>
          <button className="btn fantasma" onClick={sair}>Sair</button>
        </div>
      </header>
      <main className="conteudo"><Outlet /></main>
    </>
  )
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />
      <Route element={<Protegido />}>
        <Route path="/pedidos" element={<Pedidos />} />
        <Route path="/frota" element={<Frota />} />
        <Route path="/meu-cadastro" element={<MeuCadastro />} />
        <Route path="/clientes" element={<Clientes />} />
      </Route>
      <Route path="*" element={<Navigate to="/pedidos" replace />} />
    </Routes>
  )
}
