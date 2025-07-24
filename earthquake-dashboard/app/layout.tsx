import type React from "react"
import type { Metadata } from "next"
import { Inter } from "next/font/google"
import "./globals.css"
import 'leaflet/dist/leaflet.css'

const inter = Inter({ subsets: ["latin"] })

export const metadata: Metadata = {
  title: "Seismic Data Dashboard",
  description: "Monitor earthquake data in real-time",
    generator: 'v0.dev'
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>{children}</body>
    </html>
  )
}
