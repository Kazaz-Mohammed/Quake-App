# 🌍 Quake-App

> A Real-Time Seismic Activity Monitoring System

![Architecture Diagram](archQuakeAppV.PNG)

Quake-App is a comprehensive earthquake monitoring application that provides real-time seismic data visualization through a mobile application and web dashboard. Built with modern technologies, it offers an intuitive interface for tracking earthquake events worldwide.

## ✨ Features

### Mobile Application (Android)
- 📱 **Real-time earthquake monitoring** with live updates
- 🗺️ **Interactive map visualization** using Google Maps
- 📊 **Data visualization charts** using MPAndroidChart
- 🔔 **Push notifications** for significant earthquake events
- 📍 **Location-based alerts** for nearby seismic activity
- 💾 **Offline data caching** with Room database
- 🎨 **Modern Material Design** UI

### Web Dashboard
- 🌐 **Real-time seismic dashboard** with live data updates
- 🗺️ **Interactive Leaflet map** with satellite/street view toggle
- 📊 **Comprehensive data table** with search and pagination
- 🔐 **Secure authentication** system
- 📈 **Visual magnitude indicators** with color-coded severity
- ⚡ **Auto-refresh capability** (10-second polling)
- 📱 **Responsive design** for all devices

### Backend API
- 🔄 **WebSocket support** for real-time bidirectional communication
- 📡 **Integration with USGS Earthquake API** for official seismic data
- 🗄️ **PostgreSQL database** with Sequelize ORM
- 🔒 **RESTful API endpoints** for data operations
- 📦 **Automatic data synchronization** from external sources

## 🏗️ Architecture

The application follows a three-tier architecture:

### Mobile Frontend (Android)
```
┌─────────────────────────────────────┐
│         View Layer                  │
│    (Activities & Screens)           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Business Layer                 │
│  (Sensor Processing, Models)        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      API Layer                      │
│    (Retrofit REST Client)           │
└─────────────────────────────────────┘
```

### Web Dashboard (Next.js)
```
┌─────────────────────────────────────┐
│         View Layer                  │
│   (Pages & UI Components)           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Business Layer                 │
│  (State Management, API)            │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      API Layer                      │
│    (Fetch HTTP Client)              │
└─────────────────────────────────────┘
```

### Central Server (Express.js + PostgreSQL)
```
┌─────────────────────────────────────┐
│    REST API Endpoints               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Service Layer                  │
│      (Business Logic)               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Database Layer                 │
│    (Sequelize ORM)                   │
└─────────────────────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      PostgreSQL                     │
└─────────────────────────────────────┘
```

### External Services
- **USGS Earthquake API** - Official seismic data source
- **Google Maps API** - Location services and mapping

## 🛠️ Tech Stack

### Mobile Application
- **Language**: Java
- **Framework**: Android SDK
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite with ORM)
- **Networking**: Retrofit, OkHttp, Volley
- **Maps**: Google Maps SDK
- **Charts**: MPAndroidChart
- **Dependency Injection**: Manual with ViewModelFactory

### Web Dashboard
- **Framework**: Next.js 14 (React 18)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: shadcn/ui (Radix UI)
- **Maps**: Leaflet (react-leaflet)
- **State Management**: React Hooks
- **HTTP Client**: Fetch API

### Backend API
- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: PostgreSQL
- **ORM**: Sequelize
- **Real-time**: WebSocket (ws)
- **Validation**: body-parser
- **Environment**: dotenv

## 📦 Project Structure

```
Quake-App/
├── mobile/
│   ├── app/                    # Android app source
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/       # Java source files
│   │   │   │   └── res/        # Resources (layouts, drawables)
│   │   │   └── test/           # Unit tests
│   │   └── build.gradle        # Gradle build config
│   └── backend/                # Node.js backend
│       ├── server.js           # Express server
│       ├── package.json         # NPM dependencies
│       └── README.md           # Backend documentation
├── web/                        # Next.js web dashboard
│   ├── app/                    # Next.js app directory
│   │   ├── dashboard/          # Dashboard page
│   │   ├── layout.tsx          # Root layout
│   │   ├── page.tsx            # Home/login page
│   │   └── globals.css         # Global styles
│   ├── components/             # React components
│   │   ├── dashboard-header.tsx
│   │   ├── earthquake-map.tsx
│   │   ├── earthquake-table.tsx
│   │   └── ui/                 # shadcn/ui components
│   ├── lib/                    # Utilities
│   │   ├── sample-data.ts
│   │   └── utils.ts
│   ├── hooks/                  # Custom React hooks
│   ├── next.config.mjs         # Next.js config
│   └── package.json            # Web dependencies
├── archQuakeAppV.PNG           # Architecture diagram
└── README.md                   # This file
```

## 🚀 Getting Started

### Prerequisites
- **Node.js** (v16 or higher)
- **PostgreSQL** (v12 or higher)
- **Android Studio** (for mobile development)
- **Java JDK** (v8 or higher)

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/Kazaz-Mohammed/Quake-App.git
cd Quake-App
```

#### 2. Backend Setup

```bash
cd mobile/backend

# Install dependencies
npm install

# Create .env file
cat > .env << EOF
PORT=3001
DATABASE_URL=postgres://postgres:your_password@localhost:5432/quakeapp
EOF

# Create PostgreSQL database
psql -U postgres -c "CREATE DATABASE quakeapp;"

# Start the server (development)
npm run dev

# Or start in production
npm start
```

The backend server will run on `http://localhost:3001`

#### 3. Web Dashboard Setup

```bash
cd web

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
npm start
```

The web dashboard will run on `http://localhost:3000`

**Default Login:**
- Username: (any)
- Password: `password123`

#### 4. Mobile App Setup

```bash
cd mobile

# Open in Android Studio
# File > Open > Select the 'mobile' folder

# Sync Gradle dependencies
./gradlew build

# Run on connected device/emulator
./gradlew installDebug
```

## 📡 API Documentation

### Base URL
```
http://localhost:3001/api
```

### Endpoints

#### GET `/api/earthquakes`
Retrieve all earthquake events (limited to 100 most recent)

**Response:**
```json
[
  {
    "id": 1,
    "magnitude": 6.8,
    "latitude": 36.1083,
    "longitude": 140.0795,
    "depth": 15.0,
    "location": "Near East Coast of Honshu, Japan",
    "timestamp": "2023-05-15T08:23:45.000Z",
    "confidence": 0.95,
    "createdAt": "2023-05-15T08:23:45.000Z",
    "updatedAt": "2023-05-15T08:23:45.000Z"
  }
]
```

#### GET `/api/earthquakes/:id`
Get a specific earthquake event by ID

#### POST `/api/earthquakes`
Create a new earthquake event

**Request Body:**
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

### WebSocket Connection

The server also supports WebSocket connections for real-time updates:

**Connect to:** `ws://localhost:3001`

**Message Format:**
```json
{
  "type": "location",
  "latitude": 37.7749,
  "longitude": -122.4194
}
```

## 🗄️ Database Schema

### Earthquakes Table
```sql
CREATE TABLE earthquakes (
  id SERIAL PRIMARY KEY,
  magnitude FLOAT NOT NULL,
  latitude FLOAT NOT NULL,
  longitude FLOAT NOT NULL,
  depth FLOAT,
  location VARCHAR(255),
  timestamp TIMESTAMP NOT NULL,
  confidence FLOAT,
  "createdAt" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updatedAt" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🎨 Features in Detail

### Real-Time Updates
The application fetches earthquake data every 10 seconds from the backend API, ensuring users always have the latest seismic information.

### Interactive Map
- **Street View**: Standard OpenStreetMap tiles
- **Satellite View**: High-resolution satellite imagery
- **Marker Click**: View detailed earthquake information
- **Global Coverage**: Zoom from world view to street level

### Data Visualization
- **Magnitude Color Coding**:
  - 🔴 Red: Magnitude ≥ 6.0 (Major)
  - 🟡 Yellow: Magnitude 4.0-6.0 (Moderate)
  - 🟢 Green: Magnitude < 4.0 (Minor)

### Search & Filter
The web dashboard includes powerful search functionality across:
- Location names
- Earthquake IDs
- Magnitude values

### Responsive Design
The web dashboard is fully responsive and optimized for:
- 📱 Mobile devices
- 💻 Tablets
- 🖥️ Desktop computers

## 🧪 Running Experiments

This section provides instructions for running experiments, generating synthetic data, and testing the Quake-App system.

### Prerequisites for Experiments

Before running experiments, ensure:
1. **Backend server is running**: The API server must be running on `http://localhost:3001`
2. **Database is set up**: PostgreSQL database should be created and accessible
3. **Dependencies installed**: All Node.js dependencies should be installed in `mobile/backend`

```bash
cd mobile/backend
npm install
```

### Steps to Run Experiments

#### Step 1: Start the Backend Server

```bash
cd mobile/backend

# Create .env file if it doesn't exist
cat > .env << EOF
PORT=3001
DATABASE_URL=postgres://postgres:your_password@localhost:5432/quakeapp
EOF

# Start the server
npm run dev
```

The server should be running on `http://localhost:3001`

#### Step 2: Generate Synthetic Data (Optional)

Generate synthetic earthquake data for testing:

```bash
# Generate 100 synthetic earthquake events
node scripts/generate_synthetic_data.js 100

# Or specify custom output file
node scripts/generate_synthetic_data.js 50 data/my_earthquakes.json
```

#### Step 3: Load Data into Database (Optional)

Load synthetic or sample data into the database:

```bash
# Load sample data (10 earthquakes)
node scripts/load_synthetic_data.js data/sample_earthquakes.json

# Load generated synthetic data
node scripts/load_synthetic_data.js data/synthetic_earthquakes.json
```

#### Step 4: Run Experiments

Run the experiment suite to test the system:

```bash
# Run all experiments
node scripts/run_experiments.js all

# Run specific experiments
node scripts/run_experiments.js api          # Test API endpoints only
node scripts/run_experiments.js websocket   # Test WebSocket connections only
node scripts/run_experiments.js performance # Performance benchmarking only
```

### Script Descriptions

#### 1. `scripts/generate_synthetic_data.js`

**Purpose**: Generates synthetic earthquake data for testing and experimentation.

**Description**: 
This script creates realistic earthquake events with random magnitudes, locations, and timestamps. It uses known earthquake-prone regions (Pacific Ring of Fire, San Andreas Fault, etc.) to generate geographically accurate test data. Each earthquake includes:
- Realistic magnitude values (3.0-8.5)
- Coordinates from actual seismic regions
- Random timestamps within the last year
- Calculated depth and confidence scores

**Usage**:
```bash
node scripts/generate_synthetic_data.js [count] [output_file]
```

**Parameters**:
- `count` (optional): Number of earthquakes to generate (default: 50)
- `output_file` (optional): Output file path (default: `data/synthetic_earthquakes.json`)

**Examples**:
```bash
# Generate 100 earthquakes
node scripts/generate_synthetic_data.js 100

# Generate 50 earthquakes to custom file
node scripts/generate_synthetic_data.js 50 data/test_data.json
```

**Output**: JSON file with array of earthquake objects

---

#### 2. `scripts/load_synthetic_data.js`

**Purpose**: Loads earthquake data from JSON files into the PostgreSQL database.

**Description**:
This script reads earthquake data from a JSON file and inserts it into the database. It:
- Validates data format
- Checks for duplicate records (by timestamp and coordinates)
- Handles database connection errors gracefully
- Provides progress feedback during insertion

**Usage**:
```bash
node scripts/load_synthetic_data.js [data_file] [database_url]
```

**Parameters**:
- `data_file` (optional): Path to JSON file (default: `data/synthetic_earthquakes.json`)
- `database_url` (optional): PostgreSQL connection string (default: from `.env` file)

**Examples**:
```bash
# Load sample data
node scripts/load_synthetic_data.js data/sample_earthquakes.json

# Load with custom database URL
node scripts/load_synthetic_data.js data/synthetic_earthquakes.json postgres://user:pass@localhost:5432/quakeapp
```

**Output**: Console logs showing insertion progress and statistics

---

#### 3. `scripts/run_experiments.js`

**Purpose**: Runs comprehensive tests and experiments on the Quake-App system.

**Description**:
This script performs three types of experiments:

1. **API Endpoint Testing**: Tests all REST API endpoints
   - GET `/api/earthquakes` - Retrieve all earthquakes
   - POST `/api/earthquakes` - Create new earthquake
   - GET `/api/earthquakes/:id` - Get specific earthquake

2. **WebSocket Connection Testing**: Tests real-time WebSocket functionality
   - Connection establishment
   - Location message sending
   - Message reception

3. **Performance Benchmarking**: Measures API response times
   - Multiple request iterations
   - Average, min, and max response times

**Usage**:
```bash
node scripts/run_experiments.js [experiment_name]
```

**Parameters**:
- `experiment_name` (optional): Specific experiment to run (default: `all`)
  - `all` - Run all experiments
  - `api` - API endpoint testing only
  - `websocket` - WebSocket testing only
  - `performance` - Performance benchmarking only

**Examples**:
```bash
# Run all experiments
node scripts/run_experiments.js all

# Test only API endpoints
node scripts/run_experiments.js api

# Test only WebSocket
node scripts/run_experiments.js websocket
```

**Output**: Detailed test results and statistics printed to console

---

### Sample Dataset

A sample dataset with 10 earthquake events is included in the repository:

**Location**: `data/sample_earthquakes.json`

**Dataset Description**:
- **Size**: 10 earthquake records
- **Coverage**: Global (Japan, Australia, USA, Chile, Mexico, UK, New Zealand)
- **Magnitude Range**: 3.9 - 7.1
- **Time Period**: January 2024
- **Format**: JSON array with complete earthquake objects

**Sample Record**:
```json
{
  "id": 1,
  "magnitude": 6.8,
  "latitude": 36.1083,
  "longitude": 140.0795,
  "depth": 15.0,
  "location": "Near East Coast of Honshu, Japan",
  "timestamp": "2024-01-15T08:23:45.000Z",
  "confidence": 0.95
}
```

**Using the Sample Dataset**:

1. **Load into database**:
   ```bash
   node scripts/load_synthetic_data.js data/sample_earthquakes.json
   ```

2. **View in web dashboard**:
   - Start the web dashboard: `cd web && npm run dev`
   - Navigate to `http://localhost:3000`
   - Login and view the dashboard

3. **Access via API**:
   ```bash
   curl http://localhost:3001/api/earthquakes
   ```

### Generating Your Own Synthetic Data

To generate custom synthetic data:

1. **Basic generation**:
   ```bash
   node scripts/generate_synthetic_data.js 100
   ```

2. **Custom output location**:
   ```bash
   node scripts/generate_synthetic_data.js 200 data/my_custom_data.json
   ```

3. **Load generated data**:
   ```bash
   node scripts/load_synthetic_data.js data/my_custom_data.json
   ```

The generator creates realistic data based on:
- 10 known earthquake-prone regions worldwide
- Realistic magnitude distributions per region
- Geographically accurate coordinates
- Random timestamps within the last year
- Calculated depth and confidence values

### Experiment Workflow Example

Complete workflow for running experiments:

```bash
# 1. Start backend server (in one terminal)
cd mobile/backend
npm run dev

# 2. Generate and load test data (in another terminal)
node scripts/generate_synthetic_data.js 50
node scripts/load_synthetic_data.js data/synthetic_earthquakes.json

# 3. Run experiments
node scripts/run_experiments.js all

# 4. Verify results in web dashboard
cd web
npm run dev
# Open http://localhost:3000 and login
```

## 🔧 Configuration

### Environment Variables

**Backend (.env)**
```env
PORT=3001
DATABASE_URL=postgres://postgres:password@localhost:5432/quakeapp
```

**Web Dashboard**
Update the API endpoint in `web/app/dashboard/page.tsx`:
```typescript
fetch("http://YOUR_BACKEND_IP:3001/api/earthquakes")
```

### Mobile App Configuration
Update `mobile/app/src/main/java/...` with your backend server IP address in the API service configuration.

## 🐛 Troubleshooting

### Backend Issues
- **Database connection error**: Ensure PostgreSQL is running and credentials are correct
- **Port already in use**: Change the PORT in .env file
- **CORS errors**: Backend includes CORS middleware, but verify if accessing from different origin

### Web Dashboard Issues
- **API connection failed**: Check if backend server is running and IP address is correct
- **Map not displaying**: Ensure Leaflet CSS is loaded in layout.tsx
- **Authentication not persisting**: Check localStorage implementation

### Mobile App Issues
- **Build errors**: Sync Gradle project and clean build cache
- **Runtime crashes**: Check logcat for detailed error messages
- **Network errors**: Verify internet permissions in AndroidManifest.xml

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow existing code style and conventions
- Write meaningful commit messages
- Add comments for complex logic
- Test your changes thoroughly
- Update documentation as needed

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Mohammed Kazaz** - *Initial work* - [@Kazaz-Mohammed](https://github.com/Kazaz-Mohammed)

## 🙏 Acknowledgments

- [USGS Earthquake API](https://earthquake.usgs.gov/earthquakes/feed/v1.0/) for providing seismic data
- [Google Maps Platform](https://mapsplatform.google.com/) for mapping services
- [Leaflet](https://leafletjs.com/) for open-source mapping
- [Next.js](https://nextjs.org/) for the amazing web framework
- [shadcn/ui](https://ui.shadcn.com/) for beautiful UI components

## 📊 Statistics

- 🏗️ **Architecture**: Three-tier (Client-Server-Database)
- 📱 **Platforms**: Android, Web
- 🔄 **Updates**: Real-time (10-second polling)
- 🗺️ **Map Support**: Street & Satellite views
- 📈 **Data Source**: USGS Earthquake API
- 💾 **Database**: PostgreSQL with Sequelize ORM
- 🔔 **Notifications**: WebSocket support
- 🌐 **API Type**: RESTful + WebSocket

---

**Built with ❤️ for seismic awareness and safety**

