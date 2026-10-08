import { useIdioma } from '../i18n/IdiomaContext'
import { perfil } from '../data/perfil'

const ano = new Date().getFullYear()

export default function Footer() {
  const { t } = useIdioma()

  return (
    <footer className="footer">
      <div className="container footer__inner">
        <p>
          © {ano} {perfil.nome}. {t.rodape.direitos}
          <br />
          <small>{t.rodape.feito}</small>
        </p>
      </div>
    </footer>
  )
}
