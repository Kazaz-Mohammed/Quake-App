/**
 * Synthetic Earthquake Data Generator
 * 
 * This script generates synthetic earthquake data for testing and experimentation.
 * It creates realistic earthquake events with random magnitudes, locations, and timestamps.
 * 
 * Usage:
 *   node scripts/generate_synthetic_data.js [count] [output_file]
 * 
 * Examples:
 *   node scripts/generate_synthetic_data.js 100 data/synthetic_earthquakes.json
 *   node scripts/generate_synthetic_data.js 50
 */

const fs = require('fs');
const path = require('path');

// Known earthquake-prone regions with realistic coordinates
const EARTHQUAKE_REGIONS = [
  { name: "Pacific Ring of Fire - Japan", lat: 36.2048, lon: 138.2529, minMag: 4.0, maxMag: 8.0 },
  { name: "San Andreas Fault, California", lat: 37.7749, lon: -122.4194, minMag: 3.0, maxMag: 7.5 },
  { name: "Himalayan Belt - Nepal", lat: 28.3949, lon: 84.1240, minMag: 4.5, maxMag: 8.5 },
  { name: "Andes Mountains - Chile", lat: -35.6751, lon: -71.5430, minMag: 4.0, maxMag: 8.0 },
  { name: "Aegean Sea - Greece", lat: 38.2466, lon: 21.7346, minMag: 3.5, maxMag: 7.0 },
  { name: "New Zealand - Alpine Fault", lat: -41.2865, lon: 174.7762, minMag: 3.5, maxMag: 7.5 },
  { name: "Indonesia - Sumatra", lat: -0.7893, lon: 113.9213, minMag: 4.0, maxMag: 8.5 },
  { name: "Turkey - Anatolian Fault", lat: 39.9334, lon: 32.8597, minMag: 3.5, maxMag: 7.5 },
  { name: "Mexico - Cocos Plate", lat: 19.4326, lon: -99.1332, minMag: 4.0, maxMag: 8.0 },
  { name: "Philippines - Manila Trench", lat: 14.5995, lon: 120.9842, minMag: 4.0, maxMag: 7.5 },
];

// Generate random number in range
function randomFloat(min, max) {
  return Math.random() * (max - min) + min;
}

// Generate random integer in range
function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

// Generate random date within the last year
function randomTimestamp() {
  const now = new Date();
  const oneYearAgo = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
  const randomTime = randomFloat(oneYearAgo.getTime(), now.getTime());
  return new Date(randomTime).toISOString();
}

// Generate confidence score based on magnitude
function generateConfidence(magnitude) {
  // Higher magnitude earthquakes typically have higher confidence
  const baseConfidence = 0.7;
  const magnitudeBonus = (magnitude - 3.0) * 0.05;
  return Math.min(0.99, baseConfidence + magnitudeBonus + randomFloat(-0.1, 0.1));
}

// Generate depth based on region and magnitude
function generateDepth(magnitude) {
  // Larger earthquakes tend to be deeper
  const baseDepth = 10;
  const magnitudeFactor = (magnitude - 4.0) * 5;
  return Math.max(1, Math.min(700, baseDepth + magnitudeFactor + randomFloat(-20, 20)));
}

// Generate a single earthquake event
function generateEarthquake(id) {
  const region = EARTHQUAKE_REGIONS[randomInt(0, EARTHQUAKE_REGIONS.length - 1)];
  
  // Add some randomness to coordinates (±2 degrees)
  const latitude = region.lat + randomFloat(-2, 2);
  const longitude = region.lon + randomFloat(-2, 2);
  
  // Generate magnitude based on region characteristics
  const magnitude = parseFloat(randomFloat(region.minMag, region.maxMag).toFixed(1));
  
  const depth = parseFloat(generateDepth(magnitude).toFixed(1));
  const confidence = parseFloat(generateConfidence(magnitude).toFixed(2));
  const timestamp = randomTimestamp();
  
  return {
    id: id,
    magnitude: magnitude,
    latitude: parseFloat(latitude.toFixed(4)),
    longitude: parseFloat(longitude.toFixed(4)),
    depth: depth,
    location: region.name,
    timestamp: timestamp,
    confidence: confidence
  };
}

// Main function
function main() {
  const count = parseInt(process.argv[2]) || 50;
  const outputFile = process.argv[3] || 'data/synthetic_earthquakes.json';
  
  console.log(`Generating ${count} synthetic earthquake events...`);
  
  const earthquakes = [];
  for (let i = 1; i <= count; i++) {
    earthquakes.push(generateEarthquake(i));
  }
  
  // Create output directory if it doesn't exist
  const outputDir = path.dirname(outputFile);
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true });
  }
  
  // Write to file
  fs.writeFileSync(outputFile, JSON.stringify(earthquakes, null, 2));
  
  console.log(`✓ Generated ${count} earthquake events`);
  console.log(`✓ Saved to: ${outputFile}`);
  console.log(`\nSample data:`);
  console.log(JSON.stringify(earthquakes.slice(0, 3), null, 2));
}

main();

