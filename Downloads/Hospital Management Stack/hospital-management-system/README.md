# MediCare Hospital Management System

A complete desktop Hospital Management System built with **JavaFX 21**, featuring a modern professional UI, H2 embedded database, and comprehensive hospital management functionality.

## Features

- **Splash Screen** - Animated loading screen with progress indicator
- **Login System** - Secure authentication with default admin credentials
- **Dashboard** - Overview with statistics cards, charts, and upcoming appointments
- **Patient Management** - Full CRUD operations for patient records
- **Doctor Management** - Manage doctors, specializations, and departments
- **Appointments** - Schedule and manage patient appointments with status tracking
- **Billing & Invoicing** - Generate bills with automatic tax calculation and PDF export
- **Reports** - Generate various reports with PDF export capability
- **Settings** - Theme switching (Light/Dark), profile management, password change
- **Data Export** - Export all data to CSV format
- **Keyboard Shortcuts** - Ctrl+N (New), Ctrl+S (Save), Ctrl+F (Search), Ctrl+Q (Quit)

## Technology Stack

- **Java 21** (LTS)
- **JavaFX 21** - Desktop UI framework
- **Maven** - Build tool
- **H2 Database** - Embedded database (file-based)
- **Apache PDFBox** - PDF generation
- **Apache Commons CSV** - CSV export

## Project Structure

```
src/
├── main/
│   ├── java/com/hospital/
│   │   ├── Main.java                    # Application entry point
│   │   ├── controller/                  # FXML Controllers
│   │   │   ├── SplashScreenController.java
│   │   │   ├── LoginScreenController.java
│   │   │   ├── MainDashboardController.java
│   │   │   ├── PatientController.java
│   │   │   ├── DoctorController.java
│   │   │   ├── AppointmentController.java
│   │   │   ├── BillingController.java
│   │   │   ├── ReportsController.java
│   │   │   ├── SettingsController.java
│   │   │   └── SampleDataInitializer.java
│   │   ├── model/                       # POJO Classes
│   │   │   ├── User.java
│   │   │   ├── Patient.java
│   │   │   ├── Doctor.java
│   │   │   ├── Appointment.java
│   │   │   └── Bill.java
│   │   ├── dao/                         # Data Access Objects
│   │   │   ├── UserDAO.java
│   │   │   ├── PatientDAO.java
│   │   │   ├── DoctorDAO.java
│   │   │   ├── AppointmentDAO.java
│   │   │   └── BillDAO.java
│   │   ├── service/                     # Business Logic
│   │   │   ├── AuthService.java
│   │   │   └── SessionManager.java
│   │   └── util/                        # Utilities
│   │       ├── DatabaseManager.java
│   │       ├── PDFGenerator.java
│   │       └── CSVExporter.java
│   └── resources/
│       ├── fxml/                        # FXML Layouts
│       │   ├── SplashScreen.fxml
│       │   ├── LoginScreen.fxml
│       │   ├── MainDashboard.fxml
│       │   ├── PatientScreen.fxml
│       │   ├── DoctorScreen.fxml
│       │   ├── AppointmentScreen.fxml
│       │   ├── BillingScreen.fxml
│       │   ├── ReportsScreen.fxml
│       │   └── SettingsScreen.fxml
│       ├── css/                         # Stylesheets
│       │   ├── style.css
│       │   └── dark-theme.css
│       └── images/                      # Images & Icons
├── pom.xml                              # Maven configuration
├── run.bat                              # Windows launcher
├── run.sh                               # Linux/Mac launcher
└── README.md                            # This file
```

## Default Login Credentials

| Username | Password |
|----------|----------|
| admin    | admin123 |

## Getting Started

### Prerequisites

- **JDK 21** or later (OpenJDK or Oracle JDK)
- **Maven 3.8+** (for building from source)
- **JavaFX 21** (handled by Maven dependencies)

### Installation & Running

#### Option 1: Using Launch Scripts

**Windows:**
```bash
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

#### Option 2: Using Maven

```bash
# Clone or extract the project
cd hospital-management-system

# Build the project
mvn clean package

# Run the application
mvn javafx:run
```

#### Option 3: Run JAR directly

```bash
# After building with Maven
java -jar target/hospital-management-system-1.0.0-all.jar
```

## Building a Fat JAR

```bash
mvn clean package
```

The fat JAR with all dependencies will be created at:
```
target/hospital-management-system-1.0.0-all.jar
```

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl + N` | New (clear form) |
| `Ctrl + S` | Save/Add |
| `Ctrl + F` | Focus search field |
| `Ctrl + Q` | Logout/Quit |

## Database

The application uses **H2 embedded database** which stores data in a local file:
- **Location**: `./data/hospital_db.mv.db` (in the project directory)
- **Console**: H2 console is not enabled by default
- **Backup**: Simply copy the `data/` directory to backup

Sample data is automatically created on first launch (10 patients, 8 doctors, 12 appointments, 8 bills).

## Color Scheme

| Role | Color | Hex |
|------|-------|-----|
| Primary | Blue | #2563EB |
| Secondary | Dark Slate | #1E293B |
| Success | Green | #22C55E |
| Danger | Red | #EF4444 |
| Warning | Amber | #F59E0B |

## License

This project is for educational and demonstration purposes.

## Support

For issues or questions, please contact the development team.
