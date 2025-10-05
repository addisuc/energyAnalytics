#!/bin/bash

# Energy Analytics Platform - Database Backup Script

set -e

# Configuration
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-energydb}
DB_USER=${DB_USER:-energy_user}
BACKUP_DIR=${BACKUP_DIR:-./backup}
RETENTION_DAYS=${RETENTION_DAYS:-30}

# Create backup directory
mkdir -p $BACKUP_DIR

# Generate backup filename with timestamp
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/energydb_backup_$TIMESTAMP.sql"

echo "Starting database backup..."
echo "Host: $DB_HOST:$DB_PORT"
echo "Database: $DB_NAME"
echo "Backup file: $BACKUP_FILE"

# Create database backup
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
  --verbose --clean --no-owner --no-privileges \
  --format=custom --compress=9 \
  --file="$BACKUP_FILE.custom"

# Also create SQL dump for easier inspection
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
  --verbose --clean --no-owner --no-privileges \
  --format=plain \
  --file="$BACKUP_FILE"

# Compress SQL backup
gzip "$BACKUP_FILE"

echo "Backup completed: $BACKUP_FILE.gz"
echo "Custom format backup: $BACKUP_FILE.custom"

# Clean up old backups
echo "Cleaning up backups older than $RETENTION_DAYS days..."
find $BACKUP_DIR -name "energydb_backup_*.sql.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "energydb_backup_*.custom" -mtime +$RETENTION_DAYS -delete

echo "Backup process completed successfully!"