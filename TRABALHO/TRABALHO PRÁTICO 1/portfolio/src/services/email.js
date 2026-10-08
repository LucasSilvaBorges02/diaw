import emailjs from '@emailjs/browser'

// Chaves do EmailJS lidas do arquivo .env.local (veja .env.example).
const SERVICE_ID = import.meta.env.VITE_EMAILJS_SERVICE_ID
const TEMPLATE_ID = import.meta.env.VITE_EMAILJS_TEMPLATE_ID
const PUBLIC_KEY = import.meta.env.VITE_EMAILJS_PUBLIC_KEY

export class EmailNaoConfiguradoError extends Error {}

export async function enviarMensagem({ nome, email, mensagem }) {
  if (!SERVICE_ID || !TEMPLATE_ID || !PUBLIC_KEY) {
    throw new EmailNaoConfiguradoError('EmailJS não configurado')
  }

  // os nomes das variáveis precisam bater com os usados no template do EmailJS
  return emailjs.send(
    SERVICE_ID,
    TEMPLATE_ID,
    { from_name: nome, from_email: email, message: mensagem },
    { publicKey: PUBLIC_KEY },
  )
}
