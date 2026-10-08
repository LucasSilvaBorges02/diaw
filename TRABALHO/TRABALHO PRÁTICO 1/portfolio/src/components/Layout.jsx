import { useEffect } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Header from './Header'
import Footer from './Footer'

export default function Layout() {
  const { pathname } = useLocation()

  // volta ao topo ao trocar de página
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])

  return (
    <div className="app">
      <a href="#conteudo" className="pular-link">
        Pular para o conteúdo
      </a>
      <Header />
      <main id="conteudo" className="conteudo">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
