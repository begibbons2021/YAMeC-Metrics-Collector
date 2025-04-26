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

-- System CPU Metrics table
CREATE TABLE IF NOT EXISTS cpu_system_metrics
(
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    cpu_id              INTEGER  NOT NULL,
    granularity_config_id      INTEGER  NOT NULL,
    timestamp           DATETIME NOT NULL,
    duration            INTEGER  NOT NULL,
    average_utilization REAL,
    min_utilization     REAL,
    max_utilization     REAL,
    FOREIGN KEY (cpu_id) REFERENCES cpu (id),
    FOREIGN KEY (granularity_config_id) REFERENCES granularity_configs (id)
);

-- Application Metrics

-- CPU Application Metrics
CREATE TABLE IF NOT EXISTS cpu_application_metrics
(
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    cpu_id              INTEGER   NOT NULL,
    application_id      INTEGER   NOT NULL,
    granularity_config_id      INTEGER   NOT NULL,
    timestamp           TIMESTAMP NOT NULL,
    duration            INTEGER   NOT NULL,
    average_utilization REAL,
    min_utilization     REAL,
    max_utilization     REAL,
    FOREIGN KEY (cpu_id) REFERENCES cpu (id),
    FOREIGN KEY (application_id) REFERENCES applications (id),
    FOREIGN KEY (granularity_config_id) REFERENCES granularity_configs (id)
);