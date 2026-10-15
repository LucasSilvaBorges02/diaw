import { useEffect, useState } from 'react'

const CARREGANDO = { dados: null, erro: null, carregando: true }

// Executa `buscar` (uma função de services/api.js) e guarda o resultado.
// `buscar` precisa ser estável (definida fora do componente).
export function useApi(buscar) {
  const [estado, setEstado] = useState(CARREGANDO)
  const [tentativa, setTentativa] = useState(0)

  useEffect(() => {
    let ativo = true
    buscar()
      .then((dados) => {
        if (ativo) setEstado({ dados, erro: null, carregando: false })
      })
      .catch((erro) => {
        if (ativo) setEstado({ dados: null, erro, carregando: false })
      })
    return () => {
      ativo = false
    }
  }, [buscar, tentativa])

  function recarregar() {
    setEstado(CARREGANDO)
    setTentativa((n) => n + 1)
  }

  return { ...estado, recarregar }
}
