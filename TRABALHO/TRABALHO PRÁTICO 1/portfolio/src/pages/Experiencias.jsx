import { useIdioma } from '../i18n/IdiomaContext'
import { experiencias } from '../data/experiencias'
import TituloPagina from '../components/TituloPagina'

export default function Experiencias() {
  const { t, idioma } = useIdioma()

  return (
    <div className="container">
      <TituloPagina
        indice="03"
        titulo={t.experiencias.titulo}
        subtitulo={t.experiencias.subtitulo}
      />

      <ul className="experiencias">
        {experiencias.map((e) => (
          <li key={e.id} className="experiencia">
            <div className="experiencia__meta">
              <span className="experiencia__periodo">{e.periodo[idioma]}</span>
              <span className={`selo selo--${e.tipo}`}>{t.experiencias.tipos[e.tipo]}</span>
            </div>
            <div className="experiencia__corpo">
              <h2>{e.cargo[idioma]}</h2>
              <p className="experiencia__empresa">{e.empresa}</p>
              {e.descricao && <p>{e.descricao[idioma]}</p>}
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
