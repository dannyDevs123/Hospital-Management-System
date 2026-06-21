package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.model.Appointment;
import com.hospital.service.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Main Dashboard with sidebar navigation.
 */
public class MainDashboardController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(MainDashboardController.class.getName());
    
    // Sidebar buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnPatients;
    @FXML private Button btnDoctors;
    @FXML private Button btnAppointments;
    @FXML private Button btnBilling;
    @FXML private Button btnReports;
    @FXML private Button btnSettings;
    @FXML private Button btnLogout;
    
    // Content area
    @FXML private StackPane contentArea;
    @FXML private ScrollPane dashboardView;
    
    // Dashboard cards
    @FXML private Label totalPatientsLabel;
    @FXML private Label totalDoctorsLabel;
    @FXML private Label todayAppointmentsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label dateLabel;
    @FXML private Label dbStatusLabel;
    
    // Chart containers (charts are built programmatically to avoid FXML axis path issues)
    @FXML private StackPane barChartContainer;
    @FXML private StackPane lineChartContainer;
    
    private BarChart<String, Number> patientsBarChart;
    private LineChart<String, Number> revenueLineChart;
    
    // Tables
    @FXML private TableView<Appointment> upcomingAppointmentsTable;
    @FXML private TableColumn<Appointment, String> colUpPatient;
    @FXML private TableColumn<Appointment, String> colUpDoctor;
    @FXML private TableColumn<Appointment, String> colUpDate;
    @FXML private TableColumn<Appointment, String> colUpTime;
    @FXML private TableColumn<Appointment, String> colUpStatus;
    
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCharts();
        setupDateDisplay();
        loadDashboardData();
        setupUpcomingAppointmentsTable();
        setupKeyboardShortcuts();
    }
    
    /**
     * Build dashboard charts and attach them to their FXML containers.
     */
    private void setupCharts() {
        CategoryAxis patientsXAxis = new CategoryAxis();
        patientsXAxis.setLabel("Month");
        NumberAxis patientsYAxis = new NumberAxis();
        patientsYAxis.setLabel("Patients");
        
        patientsBarChart = new BarChart<>(patientsXAxis, patientsYAxis);
        patientsBarChart.setLegendVisible(false);
        patientsBarChart.setAnimated(false);
        patientsBarChart.prefWidthProperty().bind(barChartContainer.widthProperty());
        patientsBarChart.prefHeightProperty().bind(barChartContainer.heightProperty());
        barChartContainer.getChildren().add(patientsBarChart);
        
        CategoryAxis revenueXAxis = new CategoryAxis();
        revenueXAxis.setLabel("Month");
        NumberAxis revenueYAxis = new NumberAxis();
        revenueYAxis.setLabel("Revenue ($)");
        
        revenueLineChart = new LineChart<>(revenueXAxis, revenueYAxis);
        revenueLineChart.setLegendVisible(false);
        revenueLineChart.setAnimated(false);
        revenueLineChart.setCreateSymbols(true);
        revenueLineChart.prefWidthProperty().bind(lineChartContainer.widthProperty());
        revenueLineChart.prefHeightProperty().bind(lineChartContainer.heightProperty());
        lineChartContainer.getChildren().add(revenueLineChart);
    }
    
    /**
     * Setup date display
     */
    private void setupDateDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        dateLabel.setText(LocalDate.now().format(formatter));
    }
    
    /**
     * Load all dashboard data
     */
    private void loadDashboardData() {
        // Load counts
        totalPatientsLabel.setText(String.valueOf(patientDAO.getCount()));
        totalDoctorsLabel.setText(String.valueOf(doctorDAO.getCount()));
        todayAppointmentsLabel.setText(String.valueOf(appointmentDAO.getTodayCount()));
        
        // Format revenue
        BigDecimal revenue = billDAO.getTotalRevenue();
        totalRevenueLabel.setText(String.format("$%,.2f", revenue));
        
        // Load charts
        loadPatientsChart();
        loadRevenueChart();
        
        // Load upcoming appointments
        loadUpcomingAppointments();
        
        // Check database status
        if (com.hospital.util.DatabaseManager.getInstance().isDatabaseAccessible()) {
            dbStatusLabel.setText("Connected (H2 Embedded)");
        } else {
            dbStatusLabel.setText("Disconnected");
        }
    }
    
    /**
     * Setup upcoming appointments table
     */
    private void setupUpcomingAppointmentsTable() {
        colUpPatient.setCellValueFactory(data -> data.getValue().patientNameProperty());
        colUpDoctor.setCellValueFactory(data -> data.getValue().doctorNameProperty());
        colUpDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getAppointmentDate().toString()));
        colUpTime.setCellValueFactory(data -> data.getValue().appointmentTimeProperty());
        colUpStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        
        // Style status column
        colUpStatus.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().clear();
                    switch (item) {
                        case "Scheduled" -> getStyleClass().add("status-scheduled");
                        case "Completed" -> getStyleClass().add("status-completed");
                        case "Cancelled" -> getStyleClass().add("status-cancelled");
                        case "No-Show" -> getStyleClass().add("status-no-show");
                    }
                }
            }
        });
    }
    
    /**
     * Load patients bar chart
     */
    private void loadPatientsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Patients");
        
        List<AppointmentDAO.MonthlyCount> counts = appointmentDAO.getMonthlyCounts(6);
        for (AppointmentDAO.MonthlyCount count : counts) {
            series.getData().add(new XYChart.Data<>(count.getMonth(), count.getCount()));
        }
        
        patientsBarChart.getData().clear();
        patientsBarChart.getData().add(series);
    }
    
    /**
     * Load revenue line chart
     */
    private void loadRevenueChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        
        List<BillDAO.MonthlyRevenue> revenues = billDAO.getMonthlyRevenue(6);
        for (BillDAO.MonthlyRevenue rev : revenues) {
            series.getData().add(new XYChart.Data<>(rev.getMonth(), rev.getRevenue().doubleValue()));
        }
        
        revenueLineChart.getData().clear();
        revenueLineChart.getData().add(series);
    }
    
    /**
     * Load upcoming appointments
     */
    private void loadUpcomingAppointments() {
        List<Appointment> appointments = appointmentDAO.getUpcomingAppointments(5);
        upcomingAppointmentsTable.getItems().setAll(appointments);
    }
    
    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> {
            contentArea.getScene().getAccelerators().put(
                new javafx.scene.input.KeyCodeCombination(
                    javafx.scene.input.KeyCode.Q, 
                    javafx.scene.input.KeyCombination.CONTROL_DOWN
                ),
                this::handleLogout
            );
        });
    }
    
    /**
     * Set active sidebar button
     */
    private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("active");
        btnPatients.getStyleClass().remove("active");
        btnDoctors.getStyleClass().remove("active");
        btnAppointments.getStyleClass().remove("active");
        btnBilling.getStyleClass().remove("active");
        btnReports.getStyleClass().remove("active");
        btnSettings.getStyleClass().remove("active");
        
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
    
    /**
     * Load content into main area
     */
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            
            // Clear existing content except dashboard
            contentArea.getChildren().removeIf(node -> node != dashboardView);
            contentArea.getChildren().add(content);
            
            // Hide dashboard, show new content
            dashboardView.setVisible(false);
            dashboardView.setManaged(false);
            content.setVisible(true);
            content.setManaged(true);
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading content: " + fxmlPath, e);
        }
    }
    
    /**
     * Show dashboard view
     */
    @FXML
    private void showDashboard() {
        setActiveButton(btnDashboard);
        
        // Hide all other content
        contentArea.getChildren().forEach(node -> {
            if (node != dashboardView) {
                node.setVisible(false);
                node.setManaged(false);
            }
        });
        
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        
        // Refresh data
        loadDashboardData();
    }
    
    @FXML
    private void showPatients() {
        setActiveButton(btnPatients);
        loadContent("/fxml/PatientScreen.fxml");
    }
    
    @FXML
    private void showDoctors() {
        setActiveButton(btnDoctors);
        loadContent("/fxml/DoctorScreen.fxml");
    }
    
    @FXML
    private void showAppointments() {
        setActiveButton(btnAppointments);
        loadContent("/fxml/AppointmentScreen.fxml");
    }
    
    @FXML
    private void showBilling() {
        setActiveButton(btnBilling);
        loadContent("/fxml/BillingScreen.fxml");
    }
    
    @FXML
    private void showReports() {
        setActiveButton(btnReports);
        loadContent("/fxml/ReportsScreen.fxml");
    }
    
    @FXML
    private void showSettings() {
        setActiveButton(btnSettings);
        loadContent("/fxml/SettingsScreen.fxml");
    }
    
    /**
     * Handle logout with confirmation
     */
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be returned to the login screen.");
        
        ButtonType logoutButton = new ButtonType("Logout", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(logoutButton, cancelButton);
        
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == logoutButton) {
                com.hospital.service.AuthService.getInstance().logout();
                SessionManager.getInstance().navigateToLogin();
            }
        });
    }
}
