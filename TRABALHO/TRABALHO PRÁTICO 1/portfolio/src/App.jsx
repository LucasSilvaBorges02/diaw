import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { IdiomaProvider } from './i18n/IdiomaContext'
import Layout from './components/Layout'
import Sobre from './pages/Sobre'
import Projetos from './pages/Projetos'
import Experiencias from './pages/Experiencias'
import NaoEncontrada from './pages/NaoEncontrada'

export default function App() {
  return (
    <IdiomaProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<Sobre />} />
            <Route path="projetos" element={<Projetos />} />
            <Route path="experiencias" element={<Experiencias />} />
            <Route path="*" element={<NaoEncontrada />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </IdiomaProvider>
  )
}
