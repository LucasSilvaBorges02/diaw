import { createContext, useContext, useState } from 'react'
import { api } from '../services/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => JSON.parse(localStorage.getItem('usuario') ?? 'null'))

  async function entrar(login, senha) {
    const { token, usuario } = await api('/api/auth/login', { metodo: 'POST', corpo: { login, senha } })
    localStorage.setItem('token', token)
    localStorage.setItem('usuario', JSON.stringify(usuario))
    setUsuario(usuario)
  }

  function sair() {
    localStorage.clear()
    setUsuario(null)
  }

  return <AuthContext.Provider value={{ usuario, entrar, sair }}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext)
