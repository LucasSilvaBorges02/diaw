export const dinheiro = (v) => Number(v ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
export const data = (d) => (d ? new Date(`${d}T00:00:00`).toLocaleDateString('pt-BR') : '-')
export const STATUS = {
  PENDENTE: 'Pendente',
  APROVADO: 'Aprovado',
  REPROVADO: 'Reprovado',
  CANCELADO: 'Cancelado',
  CONTRATADO: 'Contratado',
}
