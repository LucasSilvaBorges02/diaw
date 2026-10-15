import { FiGithub } from 'react-icons/fi'
import { useIdioma } from '../i18n/IdiomaContext'
import { buscarProjetos } from '../services/api'
import { useApi } from '../hooks/useApi'
import TituloPagina from '../components/TituloPagina'
import EstadoApi from '../components/EstadoApi'

function formatarData(data, idioma) {
  const [ano, mes] = data.split('-').map(Number)
  const nomeMes = new Intl.DateTimeFormat(idioma === 'pt' ? 'pt-BR' : 'en-US', { month: 'short' })
    .format(new Date(ano, mes - 1))
    .replace('.', '')
  return `${nomeMes[0].toUpperCase()}${nomeMes.slice(1)} ${ano}` // ex.: "Ago 2026"
}

export default function Projetos() {
  const { t, idioma } = useIdioma()
  // a API já devolve os projetos do mais antigo ao mais recente
  const { dados: projetos, erro, carregando, recarregar } = useApi(buscarProjetos)

  return (
    <div className="container">
      <TituloPagina indice="02" titulo={t.projetos.titulo} subtitulo={t.projetos.subtitulo} />

      <EstadoApi
        carregando={carregando}
        erro={erro}
        vazio={projetos?.length === 0}
        onTentarNovamente={recarregar}
      />

      <ol className="timeline">
        {projetos?.map((p) => (
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
