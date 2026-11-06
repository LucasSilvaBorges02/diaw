const API_URL = (import.meta.env.VITE_API_URL ?? 'http://localhost:8080').replace(/\/$/, '')

export async function api(caminho, { metodo = 'GET', corpo } = {}) {
  const token = localStorage.getItem('token')
  const resposta = await fetch(`${API_URL}${caminho}`, {
    method: metodo,
    headers: {
      ...(corpo ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: corpo ? JSON.stringify(corpo) : undefined,
  })
  if (resposta.status === 401 && token) {
    localStorage.clear()
    window.location.href = '/login'
  }
  if (resposta.status === 204) return null
  const dados = await resposta.json().catch(() => null)
  if (!resposta.ok) {
    const campos = dados?.campos ? Object.values(dados.campos).join(', ') : ''
    throw new Error([dados?.erro ?? `Erro ${resposta.status}`, campos].filter(Boolean).join(': '))
  }
  return dados
}
