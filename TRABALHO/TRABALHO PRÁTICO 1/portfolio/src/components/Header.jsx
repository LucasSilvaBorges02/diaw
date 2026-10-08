import { useState } from 'react'
import { Link, NavLink } from 'react-router-dom'
import { FiMenu, FiX } from 'react-icons/fi'
import { useIdioma } from '../i18n/IdiomaContext'
import { perfil } from '../data/perfil'

export default function Header() {
  const { t, idioma, alternar } = useIdioma()
  const [aberto, setAberto] = useState(false)
  const fechar = () => setAberto(false)

  const links = [
    { to: '/', label: t.nav.sobre },
    { to: '/projetos', label: t.nav.projetos },
    { to: '/experiencias', label: t.nav.experiencias },
  ]

  return (
    <header className="header">
      <div className="container header__inner">
        <Link to="/" className="logo" aria-label={perfil.nome} onClick={fechar}>
          <span className="logo__marca">{perfil.iniciais}</span>
          <span className="logo__nome">{perfil.nome}</span>
        </Link>

        <nav
          id="menu-principal"
          className={`nav ${aberto ? 'nav--aberto' : ''}`}
          aria-label="Principal"
        >
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} end className="nav__link" onClick={fechar}>
              {link.label}
            </NavLink>
          ))}
        </nav>

        <div className="header__acoes">
          <button
            type="button"
            className="idioma"
            onClick={alternar}
            aria-label={t.nav.trocarIdioma}
          >
            <span className={idioma === 'pt' ? 'ativo' : ''}>PT</span>
            <span aria-hidden="true">/</span>
            <span className={idioma === 'en' ? 'ativo' : ''}>EN</span>
          </button>

          <button
            type="button"
            className="menu-botao"
            onClick={() => setAberto((v) => !v)}
            aria-expanded={aberto}
            aria-controls="menu-principal"
            aria-label={aberto ? t.nav.fecharMenu : t.nav.abrirMenu}
          >
            {aberto ? <FiX /> : <FiMenu />}
          </button>
        </div>
      </div>
    </header>
  )
}
