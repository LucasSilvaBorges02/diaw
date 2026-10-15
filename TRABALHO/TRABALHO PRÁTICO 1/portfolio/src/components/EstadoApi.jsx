import { useIdioma } from '../i18n/IdiomaContext'

// Mensagem de carregando / erro / lista vazia para as páginas que usam a API.
export default function EstadoApi({ carregando, erro, vazio, onTentarNovamente }) {
  const { t } = useIdioma()

  if (carregando) {
    return (
      <p className="estado-api" role="status">
        {t.api.carregando}
      </p>
    )
  }

  if (erro) {
    return (
      <div className="estado-api estado-api--erro" role="alert">
        <p>{t.api.erro}</p>
        <button type="button" className="botao botao--secundario" onClick={onTentarNovamente}>
          {t.api.tentarNovamente}
        </button>
      </div>
    )
  }

  if (vazio) {
    return <p className="estado-api">{t.api.vazio}</p>
  }

  return null
}
