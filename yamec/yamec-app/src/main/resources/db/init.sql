-- Enable foreign key support
PRAGMA foreign_keys = ON;

-- Hardware Information Tables

-- Disk Hardware Information table
CREATE TABLE IF NOT EXISTS disk_hardware_information (
                                                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                         friendly_name TEXT NOT NULL,
                                                         unique_id TEXT NOT NULL,
                                                         disk_number INTEGER NOT NULL,
                                                         media_type INTEGER NOT NULL,
                                                         capacity INTEGER NOT NULL,
                                                         capacity_is_unsigned INTEGER DEFAULT 0
);

-- Disk partitions table (for @ElementCollection)
CREATE TABLE IF NOT EXISTS disk_partitions (
                                               disk_id INTEGER NOT NULL,
                                               partition TEXT,
                                               FOREIGN KEY (disk_id) REFERENCES disk_hardware_information(id)
    );

-- Memory Hardware Information table
CREATE TABLE IF NOT EXISTS memory_hardware_information (
                                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                           capacity INTEGER NOT NULL,
                                                           speed INTEGER NOT NULL,
                                                           slots_used INTEGER NOT NULL,
                                                           slots_total INTEGER NOT NULL,
                                                           capacity_is_unsigned INTEGER DEFAULT 0,
                                                           speed_is_unsigned INTEGER DEFAULT 0
);

-- NIC Hardware Information table
CREATE TABLE IF NOT EXISTS nic_hardware_information (
                                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                        friendly_name TEXT NOT NULL,
                                                        label TEXT NOT NULL,
                                                        unique_id TEXT NOT NULL,
                                                        nic_type INTEGER NOT NULL
);

-- Metric Tables

-- Base metrics indices
CREATE TABLE IF NOT EXISTS metric_indices (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              last_collection_timestamp TIMESTAMP,
                                              collection_interval INTEGER NOT NULL
);

-- System CPU Metrics table
CREATE TABLE IF NOT EXISTS cpu_metrics (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           device_name TEXT NOT NULL,
                                           timestamp TIMESTAMP NOT NULL,
                                           duration INTEGER NOT NULL,
                                           usage REAL NOT NULL,
                                           temperature REAL
);

-- System GPU Metrics table
CREATE TABLE IF NOT EXISTS gpu_metrics (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           device_name TEXT NOT NULL,
                                           timestamp TIMESTAMP NOT NULL,
                                           duration INTEGER NOT NULL,
                                           usage REAL NOT NULL,
                                           temperature REAL,
                                           dedicated_memory_use INTEGER,
                                           shared_memory_use INTEGER
);

-- System Memory Metrics table
CREATE TABLE IF NOT EXISTS memory_metrics (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              physical_memory_available INTEGER NOT NULL,
                                              virtual_memory_committed INTEGER NOT NULL,
                                              committed_virtual_memory_usage REAL NOT NULL,
                                              physical_memory_available_is_unsigned INTEGER DEFAULT 0,
                                              virtual_memory_committed_is_unsigned INTEGER DEFAULT 0,
                                              timestamp TIMESTAMP NOT NULL,
                                              duration INTEGER NOT NULL
);

-- System Disk Metrics table
CREATE TABLE IF NOT EXISTS disk_metrics (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            device_name TEXT NOT NULL,
                                            timestamp TIMESTAMP NOT NULL,
                                            duration INTEGER NOT NULL,
                                            usage REAL NOT NULL,
                                            read_bandwidth INTEGER NOT NULL,
                                            write_bandwidth INTEGER NOT NULL,
                                            average_time_to_transfer REAL NOT NULL,
                                            read_bandwidth_is_unsigned INTEGER DEFAULT 0,
                                            write_bandwidth_is_unsigned INTEGER DEFAULT 0
);

-- System NIC Metrics table
CREATE TABLE IF NOT EXISTS nic_metrics (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           device_name TEXT NOT NULL,
                                           timestamp TIMESTAMP NOT NULL,
                                           duration INTEGER NOT NULL,
                                           nic_bandwidth INTEGER NOT NULL,
                                           bytes_sent INTEGER NOT NULL,
                                           bytes_received INTEGER NOT NULL,
                                           nic_bandwidth_is_unsigned INTEGER DEFAULT 0,
                                           bytes_sent_is_unsigned INTEGER DEFAULT 0,
                                           bytes_received_is_unsigned INTEGER DEFAULT 0
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_cpu_metrics_timestamp ON cpu_metrics(timestamp);
CREATE INDEX IF NOT EXISTS idx_gpu_metrics_timestamp ON gpu_metrics(timestamp);
CREATE INDEX IF NOT EXISTS idx_memory_metrics_timestamp ON memory_metrics(timestamp);
CREATE INDEX IF NOT EXISTS idx_disk_metrics_timestamp ON disk_metrics(timestamp);
CREATE INDEX IF NOT EXISTS idx_nic_metrics_timestamp ON nic_metrics(timestamp);

-- Create indexes for hardware information lookups
CREATE INDEX IF NOT EXISTS idx_disk_hardware_unique_id ON disk_hardware_information(unique_id);
CREATE INDEX IF NOT EXISTS idx_nic_hardware_unique_id ON nic_hardware_information(unique_id);

-- Create index for metric indices
CREATE INDEX IF NOT EXISTS idx_metric_indices_timestamp ON metric_indices(last_collection_timestamp);