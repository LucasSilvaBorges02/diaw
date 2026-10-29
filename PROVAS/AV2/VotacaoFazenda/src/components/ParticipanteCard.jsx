function ParticipanteCard({ participante, onVotar }) {
  return (
    <article className={`participante-card ${participante.naRoca ? 'card-roca' : ''}`}>
      {participante.naRoca && <span className="selo-roca">Na roça</span>}
      <img src={participante.foto} alt={participante.nome} className="participante-foto" />
      <div className="participante-info">
        <h3>{participante.nome}</h3>
        {onVotar && (
          <button className="botao botao-principal" onClick={() => onVotar(participante)}>
            Votar para eliminar
          </button>
        )}
      </div>
    </article>
  )
}

export default ParticipanteCard
