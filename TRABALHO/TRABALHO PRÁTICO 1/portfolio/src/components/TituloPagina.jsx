export default function TituloPagina({ indice, titulo, subtitulo }) {
  return (
    <header className="titulo-pagina">
      <span className="titulo-pagina__indice">{indice}</span>
      <h1>{titulo}</h1>
      {subtitulo && <p>{subtitulo}</p>}
    </header>
  )
}
