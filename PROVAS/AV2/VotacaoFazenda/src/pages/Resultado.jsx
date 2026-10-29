import { useState } from 'react'
import { Link } from 'react-router-dom'
import { obterVotos, calcularTotalVotos, calcularResultado, zerarVotos } from '../services/votacaoService.js'

function Resultado() {
  const [votos, setVotos] = useState(() => obterVotos())
  const totalVotos = calcularTotalVotos(votos)
  const resultado = calcularResultado(votos)

  function handleZerarVotos() {
    const confirmar = window.confirm('Tem certeza de que deseja zerar todos os votos?')
    if (confirmar) {
      zerarVotos()
      setVotos({})
    }
  }

  return (
    <main className="pagina pagina-resultado">
      <section className="secao-titulo">
        <p className="sobretitulo">A Fazenda 2026</p>
        <h1>Parcial da votação</h1>
        <p>Veja como está a disputa para continuar ou deixar o programa.</p>
      </section>

      <section className="painel-resultado">
        <div className="total-votos">
          <span>Total de votos</span>
          <strong>{totalVotos.toLocaleString('pt-BR')}</strong>
        </div>

        {totalVotos === 0 ? (
          <div className="resultado-vazio">
            <h2>A votação ainda não começou</h2>
            <p>Seja o primeiro a votar no participante que deve deixar a Fazenda.</p>
            <Link to="/votacao" className="botao botao-principal">Votar agora</Link>
          </div>
        ) : (
          <div className="placar">
            {resultado.map((participante, indice) => (
              <article key={participante.id} className={`placar-item ${indice === 0 ? 'placar-lider' : ''}`}>
                <img src={participante.foto} alt={participante.nome} />
                <div className="placar-dados">
                  <div className="placar-nome">
                    <h2>{participante.nome}</h2>
                    {indice === 0 && <span className="selo-lider">Sairia hoje</span>}
                  </div>
                  <div className="barra" aria-label={`${participante.percentual.toLocaleString('pt-BR', { maximumFractionDigits: 1 })}% dos votos`}>
                    <div className="barra-preenchida" style={{ width: `${participante.percentual}%` }} />
                  </div>
                  <div className="placar-numeros">
                    <span>{participante.votos.toLocaleString('pt-BR')} {participante.votos === 1 ? 'voto' : 'votos'}</span>
                    <strong>{participante.percentual.toLocaleString('pt-BR', { maximumFractionDigits: 1 })}%</strong>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}

        <div className="resultado-acoes">
          <Link to="/votacao" className="botao botao-secundario">Voltar a votar</Link>
          <button type="button" className="botao botao-perigo" onClick={handleZerarVotos}>Zerar votação</button>
        </div>
      </section>
    </main>
  )
}

export default Resultado
