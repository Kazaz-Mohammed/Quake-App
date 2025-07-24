export interface Earthquake {
  id: number
  magnitude: number
  latitude: number
  longitude: number
  location: string
  timestamp: string
}

export const earthquakeData: Earthquake[] = [
  {
    id: 1,
    magnitude: 6.8,
    latitude: 36.1083,
    longitude: 140.0795,
    location: "Near East Coast of Honshu, Japan",
    timestamp: "2023-05-15T08:23:45Z",
  },
  {
    id: 2,
    magnitude: 5.2,
    latitude: -33.8688,
    longitude: 151.2093,
    location: "Off Coast of New South Wales, Australia",
    timestamp: "2023-05-16T12:34:56Z",
  },
  {
    id: 3,
    magnitude: 4.7,
    latitude: 37.7749,
    longitude: -122.4194,
    location: "San Francisco Bay Area, California",
    timestamp: "2023-05-17T15:45:12Z",
  },
  {
    id: 4,
    magnitude: 7.1,
    latitude: -35.2835,
    longitude: -71.3755,
    location: "Maule, Chile",
    timestamp: "2023-05-18T03:12:34Z",
  },
  {
    id: 5,
    magnitude: 3.9,
    latitude: 40.7128,
    longitude: -74.006,
    location: "New York Metropolitan Area, USA",
    timestamp: "2023-05-19T22:10:05Z",
  },
  {
    id: 6,
    magnitude: 5.8,
    latitude: 19.4326,
    longitude: -99.1332,
    location: "Mexico City Region, Mexico",
    timestamp: "2023-05-20T14:23:45Z",
  },
  {
    id: 7,
    magnitude: 4.3,
    latitude: 51.5074,
    longitude: -0.1278,
    location: "Southern England, United Kingdom",
    timestamp: "2023-05-21T09:56:23Z",
  },
  {
    id: 8,
    magnitude: 6.2,
    latitude: 35.6762,
    longitude: 139.6503,
    location: "Tokyo Region, Japan",
    timestamp: "2023-05-22T18:34:12Z",
  },
  {
    id: 9,
    magnitude: 5.5,
    latitude: -36.8485,
    longitude: 174.7633,
    location: "Auckland Region, New Zealand",
    timestamp: "2023-05-23T07:45:32Z",
  },
  {
    id: 10,
    magnitude: 4.8,
    latitude: 34.0522,
    longitude: -118.2437,
    location: "Los Angeles Area, California",
    timestamp: "2023-05-24T11:23:45Z",
  },
  {
    id: 11,
    magnitude: 3.5,
    latitude: 41.9028,
    longitude: 12.4964,
    location: "Rome Region, Italy",
    timestamp: "2023-05-25T16:34:56Z",
  },
  {
    id: 12,
    magnitude: 6.5,
    latitude: -0.7893,
    longitude: 113.9213,
    location: "Borneo, Indonesia",
    timestamp: "2023-05-26T05:45:12Z",
  },
]
