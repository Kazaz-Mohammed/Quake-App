/**
 * Experiment Runner Script
 * 
 * This script runs various experiments to test the Quake-App system:
 * 1. Data generation and loading
 * 2. API endpoint testing
 * 3. WebSocket connection testing
 * 4. Performance benchmarking
 * 
 * Usage:
 *   node scripts/run_experiments.js [experiment_name]
 * 
 * Examples:
 *   node scripts/run_experiments.js all
 *   node scripts/run_experiments.js api
 *   node scripts/run_experiments.js websocket
 */

const http = require('http');
const WebSocket = require('ws');
require('dotenv').config();

const API_BASE_URL = process.env.API_URL || 'http://localhost:3001';
const WS_URL = process.env.WS_URL || 'ws://localhost:3001';

// Helper function to make HTTP requests
function httpRequest(method, path, data = null) {
  return new Promise((resolve, reject) => {
    const url = new URL(path, API_BASE_URL);
    const options = {
      method: method,
      headers: {
        'Content-Type': 'application/json'
      }
    };
    
    const req = http.request(url, options, (res) => {
      let body = '';
      res.on('data', (chunk) => body += chunk);
      res.on('end', () => {
        try {
          const json = body ? JSON.parse(body) : {};
          resolve({ status: res.statusCode, data: json });
        } catch (e) {
          resolve({ status: res.statusCode, data: body });
        }
      });
    });
    
    req.on('error', reject);
    
    if (data) {
      req.write(JSON.stringify(data));
    }
    
    req.end();
  });
}

// Experiment 1: Test API Endpoints
async function testAPIEndpoints() {
  console.log('\n=== Experiment 1: API Endpoint Testing ===\n');
  
  try {
    // Test GET /api/earthquakes
    console.log('1. Testing GET /api/earthquakes...');
    const getResponse = await httpRequest('GET', '/api/earthquakes');
    console.log(`   Status: ${getResponse.status}`);
    console.log(`   Records returned: ${Array.isArray(getResponse.data) ? getResponse.data.length : 0}`);
    
    if (getResponse.status === 200 && Array.isArray(getResponse.data) && getResponse.data.length > 0) {
      console.log(`   Sample record:`, JSON.stringify(getResponse.data[0], null, 2));
    }
    
    // Test POST /api/earthquakes
    console.log('\n2. Testing POST /api/earthquakes...');
    const testEarthquake = {
      magnitude: 5.5,
      latitude: 37.7749,
      longitude: -122.4194,
      depth: 10.0,
      location: "Test Location - San Francisco",
      timestamp: new Date().toISOString(),
      confidence: 0.85
    };
    
    const postResponse = await httpRequest('POST', '/api/earthquakes', testEarthquake);
    console.log(`   Status: ${postResponse.status}`);
    if (postResponse.status === 201) {
      console.log(`   Created earthquake ID: ${postResponse.data.id}`);
    } else {
      console.log(`   Error: ${JSON.stringify(postResponse.data)}`);
    }
    
    // Test GET /api/earthquakes/:id
    if (postResponse.status === 201 && postResponse.data.id) {
      console.log('\n3. Testing GET /api/earthquakes/:id...');
      const getByIdResponse = await httpRequest('GET', `/api/earthquakes/${postResponse.data.id}`);
      console.log(`   Status: ${getByIdResponse.status}`);
      if (getByIdResponse.status === 200) {
        console.log(`   Retrieved earthquake:`, JSON.stringify(getByIdResponse.data, null, 2));
      }
    }
    
    console.log('\n✓ API endpoint testing completed');
    
  } catch (error) {
    console.error('✗ API endpoint testing failed:', error.message);
  }
}

// Experiment 2: Test WebSocket Connection
async function testWebSocket() {
  console.log('\n=== Experiment 2: WebSocket Connection Testing ===\n');
  
  return new Promise((resolve) => {
    console.log('1. Connecting to WebSocket server...');
    const ws = new WebSocket(WS_URL);
    
    let connected = false;
    let messageReceived = false;
    
    ws.on('open', () => {
      connected = true;
      console.log('   ✓ WebSocket connection established');
      
      // Send location update
      console.log('\n2. Sending location update...');
      const locationMessage = {
        type: 'location',
        latitude: 37.7749,
        longitude: -122.4194
      };
      ws.send(JSON.stringify(locationMessage));
      console.log('   ✓ Location message sent');
      
      // Wait a bit for potential responses
      setTimeout(() => {
        if (messageReceived) {
          console.log('   ✓ Received message from server');
        }
        ws.close();
        console.log('\n✓ WebSocket testing completed');
        resolve();
      }, 2000);
    });
    
    ws.on('message', (data) => {
      messageReceived = true;
      try {
        const message = JSON.parse(data.toString());
        console.log('\n3. Received message:', JSON.stringify(message, null, 2));
      } catch (e) {
        console.log('\n3. Received message (raw):', data.toString());
      }
    });
    
    ws.on('error', (error) => {
      console.error('✗ WebSocket error:', error.message);
      resolve();
    });
    
    ws.on('close', () => {
      if (!connected) {
        console.error('✗ WebSocket connection failed');
      }
      resolve();
    });
    
    // Timeout after 5 seconds
    setTimeout(() => {
      if (ws.readyState === WebSocket.OPEN) {
        ws.close();
      }
      if (!connected) {
        console.error('✗ WebSocket connection timeout');
      }
      resolve();
    }, 5000);
  });
}

// Experiment 3: Performance Benchmark
async function performanceBenchmark() {
  console.log('\n=== Experiment 3: Performance Benchmarking ===\n');
  
  const iterations = 10;
  const times = [];
  
  console.log(`Running ${iterations} API requests to measure performance...`);
  
  for (let i = 0; i < iterations; i++) {
    const start = Date.now();
    try {
      await httpRequest('GET', '/api/earthquakes');
      const duration = Date.now() - start;
      times.push(duration);
      process.stdout.write(`   Request ${i + 1}: ${duration}ms\r`);
    } catch (error) {
      console.error(`\n   Request ${i + 1} failed:`, error.message);
    }
  }
  
  if (times.length > 0) {
    const avg = times.reduce((a, b) => a + b, 0) / times.length;
    const min = Math.min(...times);
    const max = Math.max(...times);
    
    console.log(`\n   Average response time: ${avg.toFixed(2)}ms`);
    console.log(`   Minimum response time: ${min}ms`);
    console.log(`   Maximum response time: ${max}ms`);
    console.log('\n✓ Performance benchmarking completed');
  } else {
    console.log('\n✗ Performance benchmarking failed - no successful requests');
  }
}

// Main function
async function main() {
  const experiment = process.argv[2] || 'all';
  
  console.log('Quake-App Experiment Runner');
  console.log('============================');
  console.log(`API Base URL: ${API_BASE_URL}`);
  console.log(`WebSocket URL: ${WS_URL}`);
  
  if (experiment === 'all' || experiment === 'api') {
    await testAPIEndpoints();
  }
  
  if (experiment === 'all' || experiment === 'websocket') {
    await testWebSocket();
  }
  
  if (experiment === 'all' || experiment === 'performance') {
    await performanceBenchmark();
  }
  
  if (!['all', 'api', 'websocket', 'performance'].includes(experiment)) {
    console.log(`\nUnknown experiment: ${experiment}`);
    console.log('Available experiments: all, api, websocket, performance');
  }
  
  console.log('\n============================');
  console.log('All experiments completed');
}

main().catch(console.error);

