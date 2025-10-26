const express = require('express');
const { Sequelize, DataTypes } = require('sequelize');
const cors = require('cors');
const bodyParser = require('body-parser');
const WebSocket = require('ws');
const http = require('http');
require('dotenv').config();

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

// Store connected clients and their locations
const clients = new Map();

// WebSocket connection handler
wss.on('connection', (ws, req) => {
    console.log('New client connected');
    
    // Generate unique ID for this client
    const clientId = Date.now().toString();
    clients.set(clientId, { ws, location: null });

    // Handle messages from client
    ws.on('message', (message) => {
        try {
            const data = JSON.parse(message);
            if (data.type === 'location') {
                // Update client's location
                const client = clients.get(clientId);
                if (client) {
                    client.location = {
                        latitude: data.latitude,
                        longitude: data.longitude
                    };
                    console.log(`Updated location for client ${clientId}:`, client.location);
                }
            }
        } catch (error) {
            console.error('Error processing message:', error);
        }
    });

    // Handle client disconnection
    ws.on('close', () => {
        console.log('Client disconnected:', clientId);
        clients.delete(clientId);
    });
});

// Helper function to calculate distance between two points
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Earth's radius in km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = 
        Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
        Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
}

// Function to notify nearby clients
function notifyNearbyClients(earthquake, radius = 100) { // radius in km
    const notification = {
        type: 'earthquake',
        data: {
            magnitude: earthquake.magnitude,
            latitude: earthquake.latitude,
            longitude: earthquake.longitude,
            location: earthquake.location,
            timestamp: earthquake.timestamp,
            depth: earthquake.depth,
            confidence: earthquake.confidence
        }
    };

    clients.forEach((client, clientId) => {
        if (client.location) {
            const distance = calculateDistance(
                earthquake.latitude,
                earthquake.longitude,
                client.location.latitude,
                client.location.longitude
            );

            if (distance <= radius) {
                // Add distance to notification
                notification.data.distance = Math.round(distance);
                
                // Send notification to client
                if (client.ws.readyState === WebSocket.OPEN) {
                    client.ws.send(JSON.stringify(notification));
                    console.log(`Notification sent to client ${clientId} (${distance.toFixed(1)}km away)`);
                }
            }
        }
    });
}

// Middleware
app.use(cors());
app.use(bodyParser.json());

// PostgreSQL Connection
const sequelize = new Sequelize(process.env.DATABASE_URL || 'postgres://postgres:postgres@localhost:5432/quakeapp', {
    dialect: 'postgres',
    logging: false // Set to console.log to see SQL queries
});

// Earthquake Model
const Earthquake = sequelize.define('Earthquake', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
    magnitude: {
        type: DataTypes.FLOAT,
        allowNull: false
    },
    latitude: {
        type: DataTypes.FLOAT,
        allowNull: false
    },
    longitude: {
        type: DataTypes.FLOAT,
        allowNull: false
    },
    depth: {
        type: DataTypes.FLOAT,
        allowNull: false,
        defaultValue: 0.0
    },
    location: {
        type: DataTypes.STRING,
        allowNull: false
    },
    timestamp: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW
    },
    confidence: {
        type: DataTypes.FLOAT,
        allowNull: false,
        defaultValue: 0.0
    }
}, {
    timestamps: true
});

// Initialize database
async function initializeDatabase() {
    try {
        await sequelize.authenticate();
        console.log('Connected to PostgreSQL database');
        
        // Sync all models
        await sequelize.sync();
        console.log('Database synchronized');
    } catch (error) {
        console.error('Database connection error:', error);
    }
}

initializeDatabase();

// Routes
// POST /api/earthquakes - Create a new earthquake event
app.post('/api/earthquakes', async (req, res) => {
    try {
        const earthquake = await Earthquake.create(req.body);
        // Notify nearby clients
        notifyNearbyClients(earthquake);
        res.status(201).json(earthquake);
    } catch (error) {
        console.error('Error saving earthquake:', error);
        res.status(500).json({ error: 'Error saving earthquake data' });
    }
});

// GET /api/earthquakes - Get all earthquake events
app.get('/api/earthquakes', async (req, res) => {
    try {
        const earthquakes = await Earthquake.findAll({
            order: [['timestamp', 'DESC']],
            limit: 100
        });
        res.json(earthquakes);
    } catch (error) {
        console.error('Error fetching earthquakes:', error);
        res.status(500).json({ error: 'Error fetching earthquake data' });
    }
});

// GET /api/earthquakes/:id - Get a specific earthquake event
app.get('/api/earthquakes/:id', async (req, res) => {
    try {
        const earthquake = await Earthquake.findByPk(req.params.id);
        if (!earthquake) {
            return res.status(404).json({ error: 'Earthquake not found' });
        }
        res.json(earthquake);
    } catch (error) {
        console.error('Error fetching earthquake:', error);
        res.status(500).json({ error: 'Error fetching earthquake data' });
    }
});

// Start server
const PORT = process.env.PORT || 3001;
server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
}); 