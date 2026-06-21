#!/bin/bash
# MediCare Hospital Management System - Linux/Mac Launcher

echo "============================================"
echo "   MediCare Hospital Management System"
echo "   Starting Application..."
echo "============================================"
echo ""

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "Found Maven. Building and running..."
    mvn clean javafx:run
else
    echo "Maven not found. Trying to run pre-built JAR..."
    
    # Check if JAR exists
    if [ -f "target/hospital-management-system-1.0.0-all.jar" ]; then
        java -jar target/hospital-management-system-1.0.0-all.jar
    else
        echo ""
        echo "ERROR: No JAR file found and Maven is not installed."
        echo ""
        echo "Please install Maven 3.8+ or build the project manually:"
        echo "  sudo apt-get install maven    (Debian/Ubuntu)"
        echo "  sudo yum install maven        (RHEL/CentOS)"
        echo "  brew install maven            (Mac)"
        echo ""
        exit 1
    fi
fi
