#!/bin/bash

echo "Starting Angular frontend for local development..."

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

# Start Angular development server
echo "Starting Angular dev server..."
ng serve --configuration=development --host=0.0.0.0 --port=4200

echo "Frontend available at http://localhost:4200"