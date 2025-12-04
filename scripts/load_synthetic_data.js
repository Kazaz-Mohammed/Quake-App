/**
 * Load Synthetic Data into Database
 * 
 * This script loads synthetic earthquake data from a JSON file into the PostgreSQL database.
 * It connects to the backend database and inserts earthquake records.
 * 
 * Usage:
 *   node scripts/load_synthetic_data.js [data_file] [database_url]
 * 
 * Examples:
 *   node scripts/load_synthetic_data.js data/synthetic_earthquakes.json
 *   node scripts/load_synthetic_data.js data/synthetic_earthquakes.json postgres://user:pass@localhost:5432/quakeapp
 */

const fs = require('fs');
const { Sequelize, DataTypes } = require('sequelize');
require('dotenv').config();

// Earthquake Model (matching server.js)
const createEarthquakeModel = (sequelize) => {
  return sequelize.define('Earthquake', {
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
};

async function main() {
  const dataFile = process.argv[2] || 'data/synthetic_earthquakes.json';
  const databaseUrl = process.argv[3] || process.env.DATABASE_URL || 'postgres://postgres:postgres@localhost:5432/quakeapp';
  
  // Check if data file exists
  if (!fs.existsSync(dataFile)) {
    console.error(`Error: Data file not found: ${dataFile}`);
    console.error('Please generate synthetic data first using: node scripts/generate_synthetic_data.js');
    process.exit(1);
  }
  
  // Read and parse data file
  console.log(`Reading data from: ${dataFile}`);
  const rawData = fs.readFileSync(dataFile, 'utf8');
  const earthquakes = JSON.parse(rawData);
  
  console.log(`Found ${earthquakes.length} earthquake records`);
  
  // Connect to database
  console.log('Connecting to database...');
  const sequelize = new Sequelize(databaseUrl, {
    dialect: 'postgres',
    logging: false
  });
  
  try {
    await sequelize.authenticate();
    console.log('✓ Connected to database');
    
    const Earthquake = createEarthquakeModel(sequelize);
    await sequelize.sync();
    console.log('✓ Database synchronized');
    
    // Insert earthquakes
    console.log('Inserting earthquake records...');
    let inserted = 0;
    let skipped = 0;
    
    for (const eq of earthquakes) {
      try {
        // Check if earthquake with same timestamp and location already exists
        const existing = await Earthquake.findOne({
          where: {
            timestamp: eq.timestamp,
            latitude: eq.latitude,
            longitude: eq.longitude
          }
        });
        
        if (!existing) {
          await Earthquake.create({
            magnitude: eq.magnitude,
            latitude: eq.latitude,
            longitude: eq.longitude,
            depth: eq.depth,
            location: eq.location,
            timestamp: eq.timestamp,
            confidence: eq.confidence
          });
          inserted++;
        } else {
          skipped++;
        }
      } catch (error) {
        console.error(`Error inserting earthquake ${eq.id}:`, error.message);
      }
    }
    
    console.log(`✓ Inserted ${inserted} new records`);
    if (skipped > 0) {
      console.log(`⚠ Skipped ${skipped} duplicate records`);
    }
    
    // Get total count
    const totalCount = await Earthquake.count();
    console.log(`\nTotal earthquakes in database: ${totalCount}`);
    
  } catch (error) {
    console.error('Database error:', error);
    process.exit(1);
  } finally {
    await sequelize.close();
  }
}

main();

