import { redirect } from "next/navigation"

export default function Home() {
  // Redirect to the chat page by default
  redirect("/chat")
}
