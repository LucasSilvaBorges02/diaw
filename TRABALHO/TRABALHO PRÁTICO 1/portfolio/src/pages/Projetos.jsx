import { FiGithub } from 'react-icons/fi'
import { useIdioma } from '../i18n/IdiomaContext'
import { projetos } from '../data/projetos'
import TituloPagina from '../components/TituloPagina'

function formatarData(data, idioma) {
  const [ano, mes] = data.split('-').map(Number)
  const nomeMes = new Intl.DateTimeFormat(idioma === 'pt' ? 'pt-BR' : 'en-US', { month: 'short' })
    .format(new Date(ano, mes - 1))
    .replace('.', '')
  return `${nomeMes[0].toUpperCase()}${nomeMes.slice(1)} ${ano}` // ex.: "Ago 2026"
}

export default function Projetos() {
  const { t, idioma } = useIdioma()
  const ordenados = [...projetos].sort((a, b) => a.data.localeCompare(b.data))

  return (
    <div className="container">
      <TituloPagina indice="02" titulo={t.projetos.titulo} subtitulo={t.projetos.subtitulo} />

      <ol className="timeline">
        {ordenados.map((p) => (
          <li key={p.id} className="timeline__item">
            <time className="timeline__data" dateTime={p.data}>
              {formatarData(p.data, idioma)}
            </time>

            <article className="projeto">
              <div className="projeto__midia">
                {p.imagem ? (
                  <img src={p.imagem} alt={p.nome} loading="lazy" />
                ) : (
                  <span>{t.projetos.semImagem}</span>
                )}
              </div>

              <div className="projeto__corpo">
                <h2>{p.nome}</h2>
                <p>{p.descricao[idioma]}</p>

                <h3 className="visualmente-oculto">{t.projetos.tecnologias}</h3>
                <ul className="tags">
                  {p.tecnologias.map((tec) => (
                    <li key={tec} className="tag">
                      {tec}
                    </li>
                  ))}
                </ul>

                <a href={p.github} target="_blank" rel="noreferrer" className="link-seta">
                  <FiGithub aria-hidden="true" /> {t.projetos.repositorio}
                </a>
              </div>
            </article>
          </li>
        ))}
      </ol>
    </div>
  )
}
