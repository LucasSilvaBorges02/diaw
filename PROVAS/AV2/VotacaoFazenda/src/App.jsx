import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Header from './components/Header.jsx'
import Home from './pages/Home.jsx'
import Votacao from './pages/Votacao.jsx'
import Resultado from './pages/Resultado.jsx'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/votacao" element={<Votacao />} />
        <Route path="/resultado" element={<Resultado />} />
      </Routes>
      <footer className="rodape">A Fazenda 2026 · Votação do público</footer>
    </BrowserRouter>
  )
}

export default App
