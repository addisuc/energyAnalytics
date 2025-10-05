-- Enable TimescaleDB extension
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Convert existing tables to hypertables for time series optimization
SELECT create_hypertable('historical_weather_data', 'timestamp', if_not_exists => TRUE);
SELECT create_hypertable('energy_data', 'timestamp', if_not_exists => TRUE);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_historical_weather_region_time 
ON historical_weather_data (region, timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_energy_data_region_time 
ON energy_data (region, timestamp DESC);

-- Set up data retention policy (keep 2 years of data)
SELECT add_retention_policy('historical_weather_data', INTERVAL '2 years', if_not_exists => TRUE);
SELECT add_retention_policy('energy_data', INTERVAL '2 years', if_not_exists => TRUE);

-- Enable compression for older data (compress data older than 7 days)
ALTER TABLE historical_weather_data SET (
  timescaledb.compress,
  timescaledb.compress_segmentby = 'region'
);

ALTER TABLE energy_data SET (
  timescaledb.compress,
  timescaledb.compress_segmentby = 'region'
);

SELECT add_compression_policy('historical_weather_data', INTERVAL '7 days', if_not_exists => TRUE);
SELECT add_compression_policy('energy_data', INTERVAL '7 days', if_not_exists => TRUE);