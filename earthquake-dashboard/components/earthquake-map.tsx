"use client"

import { useState, useMemo } from "react"
import { Card, CardContent } from "@/components/ui/card"
import type { Earthquake } from "@/lib/sample-data"
import { MapContainer, TileLayer, CircleMarker, Popup, LayersControl } from "react-leaflet"
import type { LatLngExpression } from "leaflet"
import "leaflet/dist/leaflet.css"

interface EarthquakeMapProps {
  data: Earthquake[]
}

// Helper to get color by recency (yellow = oldest, red = newest)
function getRecencyColor(index: number, total: number) {
  // Linear gradient from yellow (oldest) to red (newest)
  // index: 0 (oldest) -> yellow, index: total-1 (newest) -> red
  const percent = total <= 1 ? 1 : index / (total - 1)
  // Interpolate from yellow (255, 221, 51) to red (239, 68, 68)
  const r = Math.round(255 + (239 - 255) * percent)
  const g = Math.round(221 + (68 - 221) * percent)
  const b = Math.round(51 + (68 - 51) * percent)
  return `rgb(${r},${g},${b})`
}

export default function EarthquakeMap({ data }: EarthquakeMapProps) {
  const [selectedId, setSelectedId] = useState<number | null>(null)

  // Sort data by timestamp ascending (oldest first)
  const sortedData = useMemo(() =>
    [...data].sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()),
    [data]
  )

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
          <LayersControl position="topright">
            <LayersControl.BaseLayer checked name="OpenStreetMap">
              <TileLayer
                attribution='&copy; <a href="https://osm.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
            </LayersControl.BaseLayer>
            <LayersControl.BaseLayer name="ESRI World Imagery">
              <TileLayer
                attribution='Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'
                url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
              />
            </LayersControl.BaseLayer>
            {/* Add more layers as needed */}
          </LayersControl>

          {sortedData.map((earthquake, idx) => (
            <CircleMarker
              key={earthquake.id}
              center={[earthquake.latitude, earthquake.longitude] as LatLngExpression}
              radius={6 + earthquake.magnitude}
              pathOptions={{
                color: getRecencyColor(idx, sortedData.length),
                fillColor: getRecencyColor(idx, sortedData.length),
                fillOpacity: 0.8,
                weight: selectedId === earthquake.id ? 3 : 1,
              }}
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
            </CircleMarker>
          ))}
        </MapContainer>

        {/* Map legend */}
        <div className="absolute bottom-2 right-2 bg-white/80 p-2 rounded-md text-xs z-[1000]">
          <div className="flex items-center mb-1">
            <div className="w-3 h-3 rounded-full mr-2" style={{ background: 'rgb(239,68,68)' }}></div>
            <span>Plus récents (rouge)</span>
          </div>
          <div className="flex items-center">
            <div className="w-3 h-3 rounded-full mr-2" style={{ background: 'rgb(255,221,51)' }}></div>
            <span>Plus anciens (jaune)</span>
          </div>
        </div>

        {/* Instructions */}
        <div className="absolute top-2 left-2 bg-white/80 p-2 rounded-md text-xs max-w-xs z-[1000]">
          <p>Cliquez sur un marqueur pour voir les détails du séisme</p>
        </div>
      </CardContent>
    </Card>
  )
}
// Note: Import 'leaflet/dist/leaflet.css' in your main layout or _app file for correct map display.
