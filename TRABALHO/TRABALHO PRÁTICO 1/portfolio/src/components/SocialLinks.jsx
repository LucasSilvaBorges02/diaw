import { FiGithub, FiLinkedin, FiMail } from 'react-icons/fi'
import { FaWhatsapp } from 'react-icons/fa'
import { perfil } from '../data/perfil'

const redes = [
  { nome: 'E-mail', href: `mailto:${perfil.email}`, Icone: FiMail, valor: perfil.email },
  {
    nome: 'WhatsApp',
    href: `https://wa.me/${perfil.whatsapp}`,
    Icone: FaWhatsapp,
    valor: `+${perfil.whatsapp}`,
  },
  {
    nome: 'LinkedIn',
    href: perfil.linkedin,
    Icone: FiLinkedin,
    valor: perfil.linkedin.replace(/^https?:\/\/(www\.)?/, ''),
  },
  {
    nome: 'GitHub',
    href: perfil.github,
    Icone: FiGithub,
    valor: perfil.github.replace(/^https?:\/\//, ''),
  },
]

// variante "icones": só os ícones (rodapé); "cartoes": ícone + nome + valor (contato)
export default function SocialLinks({ variante = 'icones' }) {
  return (
    <ul className={`sociais sociais--${variante}`}>
      {redes.map(({ nome, href, Icone, valor }) => (
        <li key={nome}>
          <a
            href={href}
            target={href.startsWith('mailto:') ? undefined : '_blank'}
            rel="noreferrer"
            aria-label={nome}
            title={nome}
          >
            <Icone aria-hidden="true" />
            {variante === 'cartoes' && (
              <span>
                <strong>{nome}</strong>
                <small>{valor}</small>
              </span>
            )}
          </a>
        </li>
      ))}
    </ul>
  )
}
