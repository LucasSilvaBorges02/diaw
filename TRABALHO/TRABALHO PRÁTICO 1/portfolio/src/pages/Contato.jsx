import { useState } from 'react'
import { useIdioma } from '../i18n/IdiomaContext'
import { enviarMensagem, EmailNaoConfiguradoError } from '../services/email'
import TituloPagina from '../components/TituloPagina'
import SocialLinks from '../components/SocialLinks'

const VAZIO = { nome: '', email: '', mensagem: '' }
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/

function validar(campos, erros) {
  const resultado = {}
  if (!campos.nome.trim()) resultado.nome = erros.nome
  if (!EMAIL_REGEX.test(campos.email.trim())) resultado.email = erros.email
  if (campos.mensagem.trim().length < 10) resultado.mensagem = erros.mensagem
  return resultado
}

function Campo({ nome, rotulo, valor, erro, onChange, multilinha, ...props }) {
  const Tag = multilinha ? 'textarea' : 'input'
  return (
    <div className={`campo ${erro ? 'campo--erro' : ''}`}>
      <label htmlFor={nome}>{rotulo}</label>
      <Tag
        id={nome}
        name={nome}
        value={valor}
        onChange={onChange}
        aria-invalid={Boolean(erro)}
        aria-describedby={erro ? `${nome}-erro` : undefined}
        {...props}
      />
      {erro && (
        <span id={`${nome}-erro`} className="campo__erro">
          {erro}
        </span>
      )}
    </div>
  )
}

export default function Contato() {
  const { t } = useIdioma()
  const c = t.contato

  const [campos, setCampos] = useState(VAZIO)
  const [erros, setErros] = useState({})
  const [status, setStatus] = useState('parado') // parado | enviando | sucesso | falha | naoConfigurado

  function alterar(e) {
    const { name, value } = e.target
    setCampos((atual) => ({ ...atual, [name]: value }))
    if (erros[name]) setErros((atual) => ({ ...atual, [name]: undefined }))
  }

  async function enviar(e) {
    e.preventDefault()
    const encontrados = validar(campos, c.erros)
    setErros(encontrados)
    if (Object.keys(encontrados).length > 0) return

    setStatus('enviando')
    try {
      await enviarMensagem(campos)
      setStatus('sucesso')
      setCampos(VAZIO)
    } catch (erro) {
      setStatus(erro instanceof EmailNaoConfiguradoError ? 'naoConfigurado' : 'falha')
    }
  }

  const propsCampo = (nome) => ({
    nome,
    valor: campos[nome],
    erro: erros[nome],
    onChange: alterar,
  })

  return (
    <div className="container">
      <TituloPagina indice="04" titulo={c.titulo} subtitulo={c.subtitulo} />

      <div className="contato">
        <SocialLinks variante="cartoes" />

        <form className="formulario" onSubmit={enviar} noValidate>
          <Campo {...propsCampo('nome')} rotulo={c.nome} type="text" autoComplete="name" />
          <Campo {...propsCampo('email')} rotulo={c.email} type="email" autoComplete="email" />
          <Campo {...propsCampo('mensagem')} rotulo={c.mensagem} multilinha rows={6} />

          <button type="submit" className="botao botao--primario" disabled={status === 'enviando'}>
            {status === 'enviando' ? c.enviando : c.enviar}
          </button>

          <p className={`formulario__status formulario__status--${status}`} role="status">
            {status === 'sucesso' && c.sucesso}
            {status === 'falha' && c.falha}
            {status === 'naoConfigurado' && c.naoConfigurado}
          </p>
        </form>
      </div>
    </div>
  )
}
