import { createContext, useContext, useEffect, useState } from 'react'
import { textos } from './textos'

const CHAVE = 'portfolio-idioma'
const IdiomaContext = createContext(null)

function idiomaInicial() {
  try {
    const salvo = localStorage.getItem(CHAVE)
    if (salvo === 'pt' || salvo === 'en') return salvo
  } catch {
    // localStorage indisponível (aba anônima, etc.)
  }
  return navigator.language?.startsWith('pt') ? 'pt' : 'en'
}

export function IdiomaProvider({ children }) {
  const [idioma, setIdioma] = useState(idiomaInicial)

  useEffect(() => {
    document.documentElement.lang = idioma === 'pt' ? 'pt-BR' : 'en'
    try {
      localStorage.setItem(CHAVE, idioma)
    } catch {
      // ignora
    }
  }, [idioma])

  const alternar = () => setIdioma((atual) => (atual === 'pt' ? 'en' : 'pt'))

  return (
    <IdiomaContext.Provider value={{ idioma, alternar, t: textos[idioma] }}>
      {children}
    </IdiomaContext.Provider>
  )
}

// oxlint-disable-next-line react/only-export-components
export function useIdioma() {
  return useContext(IdiomaContext)
}
