# QuakeApp Backend Server

This is the backend server for the QuakeApp earthquake detection system. It provides APIs for storing and retrieving earthquake data using PostgreSQL.

## Setup Instructions

1. Install dependencies:
```bash
npm install
```

2. Create a `.env` file in the root directory with the following content:
```
PORT=3000
DATABASE_URL=postgres://postgres:your_password@localhost:5432/quakeapp
```

3. Make sure PostgreSQL is installed and running on your system.

4. Create the database:
```sql
CREATE DATABASE quakeapp;
```

5. Start the server:
```bash
# Development mode with auto-reload
npm run dev

# Production mode
npm start
```

## API Endpoints

### POST /api/earthquakes
Create a new earthquake event.

Request body:
```json
{
    "magnitude": 4.5,
    "latitude": 37.7749,
    "longitude": -122.4194,
    "depth": 10.0,
    "location": "San Francisco, CA",
    "timestamp": "2024-03-24T12:00:00.000Z",
    "confidence": 0.85
}
```

### GET /api/earthquakes
Get all earthquake events (limited to 100 most recent).

### GET /api/earthquakes/:id
Get a specific earthquake event by ID.

## Environment Variables

- `PORT`: The port number for the server (default: 3000)
- `DATABASE_URL`: PostgreSQL connection string (format: postgres://username:password@host:port/database)

## Database Schema

The `Earthquake` table has the following columns:
- `id`: INTEGER (Primary Key, Auto-increment)
- `magnitude`: FLOAT
- `latitude`: FLOAT
- `longitude`: FLOAT
- `depth`: FLOAT
- `location`: STRING
- `timestamp`: TIMESTAMP
- `confidence`: FLOAT
- `createdAt`: TIMESTAMP
- `updatedAt`: TIMESTAMP 