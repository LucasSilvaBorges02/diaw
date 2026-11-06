export default function TelaComFoto({ children }) {
  return (
    <div className="tela-foto">
      <aside className="foto">
        <div>
          <span className="marca grande">ROTA<b>LIVRE</b></span>
          <p>Alugue, acompanhe e feche contratos de carros pela internet.</p>
        </div>
      </aside>
      <section className="painel">{children}</section>
    </div>
  )
}
