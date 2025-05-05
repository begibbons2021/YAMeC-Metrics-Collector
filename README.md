# YAMeC - Yet Another Metrics Collector

YAMeC is a comprehensive system monitoring application that collects and displays real-time metrics about your computer's performance. It provides detailed information about CPU, memory, disk, and network interface usage through an intuitive web-based dashboard.

## Features

- **Real-time System Monitoring**: Continuously collects and displays system metrics with automatic updates
- **Comprehensive Metrics Collection**:
  - **CPU**: Usage percentage
  - **Memory**: Total, used, and free memory
  - **Disk**: Detailed information for each disk including type, capacity, usage, read/write speeds, and transfer times
  - **Network Interfaces**: Information about each network interface including data sent and received
- **Application Monitoring**: Tracks resource usage of individual applications including CPU and memory consumption
- **Web-based Dashboard**: Easy-to-use interface accessible through any web browser
- **Native Performance**: Uses JNI (Java Native Interface) to collect system metrics with minimal overhead
- **Persistent Storage**: Stores historical metrics data in a SQLite database

## Architecture Overview

YAMeC consists of two main components:

1. **yamec-jni**: A native module written in C++ that interfaces with the Windows Performance Counters API to collect system metrics. This module is compiled into a DLL and loaded by the Java application.

2. **yamec-app**: A Spring Boot web application that:
   - Loads the native library
   - Collects metrics at regular intervals
   - Stores metrics in a SQLite database
   - Provides a web interface to display the metrics
   - Exposes REST APIs for accessing the metrics programmatically

The application uses the following technologies:
- Java 23
- Spring Boot 3.4.3
- Thymeleaf for server-side templating
- SQLite for data storage
- Hibernate ORM for database access
- CMake for building the native component
- IzPack for creating installers

## Installation

### Prerequisites

- Windows operating system
- Java 23 or higher
- Modern web browser (Chrome, Firefox, Edge, etc.)

### Installation Steps

1. Download the latest YAMeC installer from the [releases page]([https://github.com/gibbonsdimarco/yamec/releases](https://github.com/begibbons2021/YAMeC-Metrics-Collector/releases)).
3. Run the installer and follow the on-screen instructions.
4. Launch YAMeC from the Start menu or desktop shortcut.
5. Your default web browser will automatically open to the YAMeC dashboard.

If your browser doesn't open automatically, you can access the dashboard by navigating to `http://localhost:8084/` in your web browser.

## Usage

### Dashboard

The main dashboard displays real-time metrics about your system:

- **CPU Usage**: Current CPU utilization percentage
- **Memory Usage**: Total, used, and free memory
- **Disk Information**: For each disk, shows type, capacity, usage, read/write speeds, and transfer times
- **Network Interface Information**: For each network interface, shows type, data sent, and data received

The dashboard automatically updates every second to provide real-time information.

### Application Metrics

To view metrics for individual applications:

1. Navigate to the "Applications" page by clicking the link in the navigation menu.
2. View CPU and memory usage for each running application.
3. Click on an application to see detailed historical metrics.

## Configuration

YAMeC creates a `.yamec-home` directory in your user home folder to store its configuration and database files.

### Application Properties

You can customize YAMeC by modifying the `application.properties` file located in the installation directory:

```properties
# Server configuration
server.port=8084

# Database configuration
spring.datasource.url=jdbc:sqlite:db.sqlite
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update

# Logging configuration
logging.file.name=logs/yamec.log
logging.level.root=INFO
logging.level.com.gibbonsdimarco.yamec=DEBUG
```

## Development Setup

### Prerequisites

- Windows operating system
- Java Development Kit (JDK) 23 or higher
- Maven 3.8 or higher
- Visual Studio 2022 with C++ development tools
- CMake 3.20 or higher
- Git

### Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/gibbonsdimarco/yamec.git
   cd yamec
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   cd yamec-app
   mvn spring-boot:run
   ```

4. Access the application at `http://localhost:8084`

### Project Structure

- `yamec-jni/`: Native JNI module
  - `src/main/cpp/`: C++ source files and JNI headers
  - `src/test/cpp/`: C++ tests

- `yamec-app/`: Spring Boot application
  - `src/main/java/`: Java source files
  - `src/main/resources/`: Configuration files and web resources
  - `src/test/java/`: Java tests

## Contributing

Contributions to YAMeC are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

Please make sure your code follows the existing style and includes appropriate tests.

## License

YAMeC is open-source software licensed under the MIT license.

## Acknowledgements

- The YAMeC team: Brendan Gibbons and C. Marcus DiMarco
- Spring Boot and the Spring community
- The open-source community for providing the tools and libraries that make this project possible
