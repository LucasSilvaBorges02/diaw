import { Link } from 'react-router-dom'
import { FiArrowRight } from 'react-icons/fi'
import { useIdioma } from '../i18n/IdiomaContext'
import { perfil, habilidades } from '../data/perfil'

export default function Sobre() {
  const { t } = useIdioma()
  const s = t.sobre

  const blocos = [
    { titulo: s.tituloFormacao, texto: s.formacao },
    { titulo: s.tituloAtuacao, texto: s.atuacao },
    { titulo: s.tituloInteresses, texto: s.interesses },
    { titulo: s.tituloObjetivos, texto: s.objetivos },
  ]

  return (
    <div className="container">
      <section className="hero">
        <div className="hero__texto">
          <p className="hero__saudacao">{s.saudacao}</p>
          <h1 className="hero__nome">{perfil.nome}</h1>
          <p className="hero__papel">{s.papel}</p>
          <p className="hero__resumo">{s.resumo}</p>
          <div className="hero__acoes">
            <Link to="/projetos" className="botao botao--primario">
              {s.verProjetos} <FiArrowRight aria-hidden="true" />
            </Link>
          </div>
        </div>

        <div className="hero__foto" aria-hidden={!perfil.foto}>
          {perfil.foto ? (
            <img src={perfil.foto} alt={perfil.nome} />
          ) : (
            <span>{perfil.iniciais}</span>
          )}
        </div>
      </section>

      <section className="sobre-grade" aria-label={t.nav.sobre}>
        {blocos.map((b, i) => (
          <article key={b.titulo} className="cartao">
            <span className="cartao__indice">0{i + 1}</span>
            <h2>{b.titulo}</h2>
            <p>{b.texto}</p>
          </article>
        ))}
      </section>

      <section className="habilidades">
        <h2>{s.tituloHabilidades}</h2>
        <ul className="tags">
          {habilidades.map((h) => (
            <li key={h} className="tag">
              {h}
            </li>
          ))}
        </ul>
      </section>
    </div>
  )
}
