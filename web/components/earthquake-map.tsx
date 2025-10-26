"use client"

import { useEffect, useState } from "react"
import dynamic from "next/dynamic"
import { Card, CardContent } from "@/components/ui/card"
import type { Earthquake } from "@/lib/sample-data"
import type { LatLngExpression } from "leaflet"
import "leaflet/dist/leaflet.css"

// Dynamically import all Leaflet components with no SSR
const MapContainer = dynamic(
  () => import("react-leaflet").then((mod) => mod.MapContainer),
  { ssr: false }
)
const TileLayer = dynamic(
  () => import("react-leaflet").then((mod) => mod.TileLayer),
  { ssr: false }
)
const Marker = dynamic(
  () => import("react-leaflet").then((mod) => mod.Marker),
  { ssr: false }
)
const Popup = dynamic(
  () => import("react-leaflet").then((mod) => mod.Popup),
  { ssr: false }
)

interface EarthquakeMapProps {
  data: Earthquake[]
}

export default function EarthquakeMap({ data }: EarthquakeMapProps) {
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [isMounted, setIsMounted] = useState(false)
  const [mapStyle, setMapStyle] = useState<'street' | 'satellite'>('street')

  useEffect(() => {
    setIsMounted(true)
    // Import and set up Leaflet only on client side
    if (typeof window !== 'undefined') {
      const L = require('leaflet')
      const defaultIcon = L.icon({
        iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
        iconRetinaUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
        shadowUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41]
      })
      L.Marker.prototype.options.icon = defaultIcon
    }
  }, [])

  if (!isMounted) {
    return <div className="h-full w-full bg-gray-100" />
  }

  // Sort data by timestamp ascending (oldest first)
  const sortedData = [...data].sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime())

  // Center of the map (approximate global center)
  const center: LatLngExpression = [10, 0]

  return (
    <Card className="h-full">
      <CardContent className="p-0 relative h-full overflow-hidden">
        <MapContainer
          center={center}
          zoom={2}
          minZoom={2}
          maxZoom={6}
          style={{ height: "100%", width: "100%", zIndex: 1 }}
          scrollWheelZoom={true}
          worldCopyJump={true}
        >
          {mapStyle === 'street' ? (
            <TileLayer
              attribution='&copy; <a href="https://osm.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
          ) : (
            <TileLayer
              attribution='Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'
              url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
            />
          )}

          {sortedData.map((earthquake) => (
            <Marker
              key={earthquake.id}
              position={[earthquake.latitude, earthquake.longitude] as LatLngExpression}
              eventHandlers={{
                click: () => setSelectedId(earthquake.id),
              }}
            >
              {selectedId === earthquake.id && (
                <Popup
                  eventHandlers={{
                    remove: () => setSelectedId(null),
                  }}
                >
                  <div className="max-w-xs">
                    <h3 className="font-bold text-sm mb-1">{earthquake.location}</h3>
                    <p className="text-xs">Magnitude: {earthquake.magnitude.toFixed(1)}</p>
                    <p className="text-xs">Coordinates: {earthquake.latitude.toFixed(2)}, {earthquake.longitude.toFixed(2)}</p>
                    <p className="text-xs">Time: {new Date(earthquake.timestamp).toLocaleString()}</p>
                  </div>
                </Popup>
              )}
            </Marker>
          ))}
        </MapContainer>

        {/* Map style toggle */}
        <div className="absolute top-2 right-2 bg-white/80 p-2 rounded-md text-xs z-[1000]">
          <button
            onClick={() => setMapStyle(mapStyle === 'street' ? 'satellite' : 'street')}
            className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
          >
            {mapStyle === 'street' ? 'Satellite View' : 'Street View'}
          </button>
        </div>

        {/* Instructions */}
        <div className="absolute top-2 left-2 bg-white/80 p-2 rounded-md text-xs max-w-xs z-[1000]">
          <p>Click on a marker to see earthquake details</p>
        </div>
      </CardContent>
    </Card>
  )
}
// Note: Import 'leaflet/dist/leaflet.css' in your main layout or _app file for correct map display.
