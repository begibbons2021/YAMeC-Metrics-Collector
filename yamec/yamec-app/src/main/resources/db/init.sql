-- Disable foreign key support while cleaning DB
PRAGMA foreign_keys = OFF;

-- Wipe all before starting
DROP TABLE IF EXISTS granularity_configs;
DROP TABLE IF EXISTS granularities;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS cpu;
DROP TABLE IF EXISTS cpu_system_metrics;
DROP TABLE IF EXISTS cpu_application_metrics;

-- Reset auto-increment counters
DELETE
FROM sqlite_sequence;

-- Re-enable foreign keys
PRAGMA foreign_keys = ON;

-- Meta Tables

-- Granularity Configs
CREATE TABLE IF NOT EXISTS granularity_configs
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    record_timespan INTEGER NOT NULL,
    time_to_age     INTEGER NOT NULL
);

-- Record Granularity
CREATE TABLE IF NOT EXISTS granularities
(
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    label             TEXT    NOT NULL,
    current_config_id INTEGER NOT NULL,
    FOREIGN KEY (current_config_id) REFERENCES granularity_configs (id)
);

-- Hardware Information Tables

-- CPU
CREATE TABLE IF NOT EXISTS cpu
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    name               TEXT NOT NULL,
    base_speed         INTEGER,
    cores              INTEGER,
    logical_processors INTEGER,
    virtualization     BOOLEAN,
    l1_cache_size      INTEGER,
    l2_cache_size      INTEGER,
    l3_cache_size      INTEGER
);

-- -- Disk Hardware Information table
-- CREATE TABLE IF NOT EXISTS disk_hardware_information (
--                                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                                          friendly_name TEXT NOT NULL,
--                                                          unique_id TEXT NOT NULL,
--                                                          disk_number INTEGER NOT NULL,
--                                                          media_type INTEGER NOT NULL,
--                                                          capacity INTEGER NOT NULL,
--                                                          capacity_is_unsigned INTEGER DEFAULT 0
-- );
--
-- -- Disk partitions table (for @ElementCollection)
-- CREATE TABLE IF NOT EXISTS disk_partitions (
--                                                disk_id INTEGER NOT NULL,
--                                                partition TEXT,
--                                                FOREIGN KEY (disk_id) REFERENCES disk_hardware_information(id)
--     );
--
-- -- Memory Hardware Information table
-- CREATE TABLE IF NOT EXISTS memory_hardware_information (
--                                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                                            capacity INTEGER NOT NULL,
--                                                            speed INTEGER NOT NULL,
--                                                            slots_used INTEGER NOT NULL,
--                                                            slots_total INTEGER NOT NULL,
--                                                            capacity_is_unsigned INTEGER DEFAULT 0,
--                                                            speed_is_unsigned INTEGER DEFAULT 0
-- );
--
-- -- NIC Hardware Information table
-- CREATE TABLE IF NOT EXISTS nic_hardware_information (
--                                                         id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                                         friendly_name TEXT NOT NULL,
--                                                         label TEXT NOT NULL,
--                                                         unique_id TEXT NOT NULL,
--                                                         nic_type INTEGER NOT NULL
-- );

-- Software Tables

-- Applications Table
CREATE TABLE IF NOT EXISTS applications
(
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    path TEXT NOT NULL
);

-- Metric Tables
--
-- -- Base metrics indices
-- CREATE TABLE IF NOT EXISTS metric_indices (
--                                               id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                               last_collection_timestamp TIMESTAMP,
--                                               collection_interval INTEGER NOT NULL
-- );
--
-- System CPU Metrics table
CREATE TABLE IF NOT EXISTS cpu_system_metrics
(
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    cpu_id              INTEGER  NOT NULL,
    granularity_id      INTEGER  NOT NULL,
    timestamp           DATETIME NOT NULL,
    duration            INTEGER  NOT NULL,
    average_utilization REAL,
    min_utilization     REAL,
    max_utilization     REAL,
    FOREIGN KEY (cpu_id) REFERENCES cpu (id),
    FOREIGN KEY (granularity_id) REFERENCES granularities (id)
);
--
-- -- System GPU Metrics table
-- CREATE TABLE IF NOT EXISTS gpu_metrics (
--                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                            device_name TEXT NOT NULL,
--                                            timestamp TIMESTAMP NOT NULL,
--                                            duration INTEGER NOT NULL,
--                                            usage REAL NOT NULL,
--                                            temperature REAL,
--                                            dedicated_memory_use INTEGER,
--                                            shared_memory_use INTEGER
-- );
--
-- -- System Memory Metrics table
-- CREATE TABLE IF NOT EXISTS memory_metrics (
--                                               id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                               physical_memory_available INTEGER NOT NULL,
--                                               virtual_memory_committed INTEGER NOT NULL,
--                                               committed_virtual_memory_usage REAL NOT NULL,
--                                               physical_memory_available_is_unsigned INTEGER DEFAULT 0,
--                                               virtual_memory_committed_is_unsigned INTEGER DEFAULT 0,
--                                               timestamp TIMESTAMP NOT NULL,
--                                               duration INTEGER NOT NULL
-- );
--
-- -- System Disk Metrics table
-- CREATE TABLE IF NOT EXISTS disk_metrics (
--                                             id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                             device_name TEXT NOT NULL,
--                                             timestamp TIMESTAMP NOT NULL,
--                                             duration INTEGER NOT NULL,
--                                             usage REAL NOT NULL,
--                                             read_bandwidth INTEGER NOT NULL,
--                                             write_bandwidth INTEGER NOT NULL,
--                                             average_time_to_transfer REAL NOT NULL,
--                                             read_bandwidth_is_unsigned INTEGER DEFAULT 0,
--                                             write_bandwidth_is_unsigned INTEGER DEFAULT 0
-- );
--
-- -- System NIC Metrics table
-- CREATE TABLE IF NOT EXISTS nic_metrics (
--                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
--                                            device_name TEXT NOT NULL,
--                                            timestamp TIMESTAMP NOT NULL,
--                                            duration INTEGER NOT NULL,
--                                            nic_bandwidth INTEGER NOT NULL,
--                                            bytes_sent INTEGER NOT NULL,
--                                            bytes_received INTEGER NOT NULL,
--                                            nic_bandwidth_is_unsigned INTEGER DEFAULT 0,
--                                            bytes_sent_is_unsigned INTEGER DEFAULT 0,
--                                            bytes_received_is_unsigned INTEGER DEFAULT 0
-- );

-- Application Metrics

-- CPU Application Metrics
CREATE TABLE IF NOT EXISTS cpu_application_metrics
(
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    cpu_id              INTEGER   NOT NULL,
    application_id      INTEGER   NOT NULL,
    granularity_id      INTEGER   NOT NULL,
    timestamp           TIMESTAMP NOT NULL,
    duration            INTEGER   NOT NULL,
    average_utilization REAL,
    min_utilization     REAL,
    max_utilization     REAL,
    FOREIGN KEY (cpu_id) REFERENCES cpu (id),
    FOREIGN KEY (application_id) REFERENCES applications (id),
    FOREIGN KEY (granularity_id) REFERENCES granularities (id)
);

-- -- Create indexes for performance
-- CREATE INDEX IF NOT EXISTS idx_cpu_metrics_timestamp ON cpu_metrics(timestamp);
-- CREATE INDEX IF NOT EXISTS idx_gpu_metrics_timestamp ON gpu_metrics(timestamp);
-- CREATE INDEX IF NOT EXISTS idx_memory_metrics_timestamp ON memory_metrics(timestamp);
-- CREATE INDEX IF NOT EXISTS idx_disk_metrics_timestamp ON disk_metrics(timestamp);
-- CREATE INDEX IF NOT EXISTS idx_nic_metrics_timestamp ON nic_metrics(timestamp);
--
-- -- Create indexes for hardware information lookups
-- CREATE INDEX IF NOT EXISTS idx_disk_hardware_unique_id ON disk_hardware_information(unique_id);
-- CREATE INDEX IF NOT EXISTS idx_nic_hardware_unique_id ON nic_hardware_information(unique_id);
--
-- -- Create index for metric indices
-- CREATE INDEX IF NOT EXISTS idx_metric_indices_timestamp ON metric_indices(last_collection_timestamp);