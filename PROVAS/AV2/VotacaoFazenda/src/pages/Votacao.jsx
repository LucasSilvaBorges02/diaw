import { useState } from 'react'
import { Link } from 'react-router-dom'
import ParticipanteCard from '../components/ParticipanteCard.jsx'
import { listarNaRoca, listarForaDaRoca, registrarVoto } from '../services/votacaoService.js'

function Votacao() {
  const naRoca = listarNaRoca()
  const foraDaRoca = listarForaDaRoca()
  const [ultimoVoto, setUltimoVoto] = useState(null)
  const [quantidadeVotos, setQuantidadeVotos] = useState(0)

  function handleVotar(participante) {
    registrarVoto(participante.id)
    setUltimoVoto(participante)
    setQuantidadeVotos((quantidade) => quantidade + 1)
  }

  return (
    <main className="pagina">
      <section className="secao-titulo">
        <p className="sobretitulo">A decisão está nas suas mãos</p>
        <h1>Quem deve sair da Fazenda?</h1>
        <p>Escolha um dos participantes que estão na roça para votar pela eliminação.</p>
      </section>

      {ultimoVoto && (
        <div className="confirmacao" role="status">
          <strong>Voto registrado!</strong>
          <p>Você votou para eliminar {ultimoVoto.nome}. Você já votou {quantidadeVotos} {quantidadeVotos === 1 ? 'vez' : 'vezes'} nesta visita.</p>
          <Link to="/resultado">Confira a parcial da votação</Link>
        </div>
      )}

      <section className="secao-conteudo">
        <h2>Na roça</h2>
        <div className="grade-roca">
          {naRoca.map((participante) => (
            <ParticipanteCard key={participante.id} participante={participante} onVotar={handleVotar} />
          ))}
        </div>
      </section>

      <section className="secao-conteudo">
        <h2>Os demais peões</h2>
        <div className="grade-demais">
          {foraDaRoca.map((participante) => (
            <ParticipanteCard key={participante.id} participante={participante} />
          ))}
        </div>
      </section>
    </main>
  )
}

export default Votacao
