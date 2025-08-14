import type React from "react"
import type { Metadata } from "next"
import { Inter } from "next/font/google"
import "./globals.css"
import { ThemeProvider } from "@/components/theme-provider"
import Navbar from "@/frontend/components/Navbar"
import { Toaster } from "@/frontend/components/ui/toaster"

const inter = Inter({ subsets: ["latin"] })

export const metadata: Metadata = {
  title: "AI Multi-Agent Platform",
  description: "A platform for managing and interacting with AI agents",
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className={`${inter.className} min-h-screen`}>
        <ThemeProvider attribute="class" defaultTheme="light" enableSystem>
          <Navbar />
          <main className="container mx-auto px-4 py-4">{children}</main>
          <Toaster />
        </ThemeProvider>
      </body>
    </html>
  )
}
