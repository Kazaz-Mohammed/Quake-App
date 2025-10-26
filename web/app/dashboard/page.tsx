"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import DashboardHeader from "@/components/dashboard-header"
import EarthquakeTable from "@/components/earthquake-table"
import EarthquakeMap from "@/components/earthquake-map"
// import { earthquakeData } from "@/lib/sample-data" // Remove static data

export default function Dashboard() {
  const router = useRouter()
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [earthquakes, setEarthquakes] = useState([])
  const [dataLoading, setDataLoading] = useState(true)

  useEffect(() => {
    // Check if user is authenticated
    const authStatus = localStorage.getItem("isAuthenticated")
    if (authStatus !== "true") {
      router.push("/")
    } else {
      setIsAuthenticated(true)
    }
    setIsLoading(false)
  }, [router])

  useEffect(() => {
    if (!isAuthenticated) return
    const fetchData = () => {
      fetch("http://192.168.1.7:3001/api/earthquakes")
        .then(res => res.json())
        .then(data => {
          setEarthquakes(data)
          setDataLoading(false)
        })
        .catch(() => setDataLoading(false))
    }
    fetchData()
    const interval = setInterval(fetchData, 10000) // Poll every 10 seconds
    return () => clearInterval(interval)
  }, [isAuthenticated])

  if (isLoading || dataLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p>Loading...</p>
      </div>
    )
  }

  if (!isAuthenticated) {
    return null
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <DashboardHeader />
      <main className="container mx-auto p-4">
        <h1 className="mb-6 text-2xl font-bold">Seismic Activity Dashboard</h1>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="order-2 lg:order-1">
            <EarthquakeTable data={earthquakes} />
          </div>
          <div className="order-1 h-[500px] lg:order-2">
            <EarthquakeMap data={earthquakes} />
          </div>
        </div>
      </main>
    </div>
  )
}
