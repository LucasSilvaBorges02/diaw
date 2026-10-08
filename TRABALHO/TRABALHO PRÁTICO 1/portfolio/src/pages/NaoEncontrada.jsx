import { Link } from 'react-router-dom'
import { useIdioma } from '../i18n/IdiomaContext'

export default function NaoEncontrada() {
  const { t } = useIdioma()

  return (
    <div className="container nao-encontrada">
      <span className="nao-encontrada__codigo">404</span>
      <h1>{t.naoEncontrada.titulo}</h1>
      <Link to="/" className="botao botao--secundario">
        {t.naoEncontrada.voltar}
      </Link>
    </div>
  )
}
