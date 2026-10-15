// Endereço da API Spring Boot. Localmente usa a porta 8080; na Vercel vem de VITE_API_URL.
const API_URL = (import.meta.env.VITE_API_URL ?? 'http://localhost:8080').replace(/\/$/, '')

async function get(caminho) {
  const resposta = await fetch(`${API_URL}${caminho}`)
  if (!resposta.ok) throw new Error(`A API respondeu ${resposta.status}`)
  return resposta.json()
}

export const buscarProjetos = () => get('/api/projetos')
export const buscarExperiencias = () => get('/api/experiencias')
