const express = require('express');
const { Sequelize, DataTypes } = require('sequelize');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();

const app = express();

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
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
}); 