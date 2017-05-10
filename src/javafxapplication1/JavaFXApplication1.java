/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafxapplication1.entities.Address;
import javafxapplication1.entities.Appointment;
import javafxapplication1.entities.City;
import javafxapplication1.entities.Country;
import javafxapplication1.entities.Customer;
import javafxapplication1.entities.TimeZoneDisplay;
import tornadofx.control.DateTimePicker;
//import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.control.agenda.Agenda;

import jfxtras.scene.control.agenda.Agenda.LocalDateTimeRange;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import jfxtras.internal.scene.control.skin.agenda.AgendaDaySkin;
import jfxtras.internal.scene.control.skin.agenda.AgendaDaysFromDisplayedSkin;
import jfxtras.internal.scene.control.skin.agenda.AgendaMonthSkin;
import jfxtras.internal.scene.control.skin.agenda.AgendaWeekSkin;
import jfxtras.scene.layout.GridPane;



/**
 *
 * @author michael
 */
public class JavaFXApplication1 extends Application {
    public static Locale lo;
    public static Scene scene;
    public static Stage stage;
    
    public static TimeZone tz = TimeZone.getDefault();
    
    
    
    @Override
    public void start(Stage primaryStage) {
        Dal.makeConnectionPool();
        System.setProperty("prism.allowhidpi", "true");
        //Get current locale
        lo = Locale.getDefault();
        System.out.println(lo.getLanguage());
        //Create login screen
        stage = primaryStage;
        createLoginScreen(primaryStage);
        //Create
        
    }

    public void createLoginScreen(Stage primaryStage){
        //Login text boxes with fancy placeholders
        String userNameText = "";
        String passwordText = "";
        String loginButtonText = "";
        String tzButtonText = "";
        
        if(lo.getLanguage() == "en") {
            userNameText = "username";
            passwordText = "password";
            loginButtonText = "Login";
            tzButtonText = "TimeZone";
        } else if (lo.getLanguage() == "es") {
            userNameText = "el username";
            passwordText = "el password";
            loginButtonText = "El Login";
            tzButtonText = "El TimeZone";
        }
        
        TextField userName = new TextField("");
        userName.setPromptText(userNameText);
        PasswordField password = new PasswordField();
        password.setPromptText(passwordText);
        
        Label tzLbl = new Label(tzButtonText);
        ComboBox<TimeZoneDisplay> timeZoneMenu = new ComboBox<TimeZoneDisplay>(FXCollections.observableList(Utility.getTimezones()));
        
        Button loginBtn = new Button();
        loginBtn.setText(loginButtonText);
        loginBtn.setPrefSize(100, 20);
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Logging in!");
                Utility utl = new Utility();
                try {
                    utl.login(userName.getText(), password.getText(), lo.getLanguage(), timeZoneMenu.getValue());
                    utl.appointmentReminder(userName.getText());
                    createMainMenuScreen(primaryStage);
                } catch (SQLException ex) {
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidLoginException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Bad Login");
                    alert.setHeaderText("Invalid Login");
                    alert.setContentText("Bad username and password");

                    alert.showAndWait();
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: #336699;");
        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(15, 12, 15, 12));
        hbox1.setSpacing(10);
        hbox1.setStyle("-fx-background-color: #336699;");

        hbox1.getChildren().addAll(userName, password);
        
        HBox hbox2 = new HBox();
        hbox2.setPadding(new Insets(15, 12, 15, 12));
        hbox2.setSpacing(10);
        hbox2.setStyle("-fx-background-color: #336699;");

        hbox2.getChildren().addAll(tzLbl, timeZoneMenu);
        
        HBox hbox3 = new HBox();
        hbox3.setPadding(new Insets(15, 12, 15, 12));
        hbox3.setSpacing(10);
        hbox3.setStyle("-fx-background-color: #336699;");

        hbox3.getChildren().addAll(loginBtn);
        
        //Create login stage
        vbox.getChildren().addAll(hbox1, hbox2, hbox3);
        scene = new Scene(vbox, 800, 350);
        
        hbox1.requestFocus();
        
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void createMainMenuScreen(Stage primaryStage){

        String CustomersText = "";
        String LogOffText = "";
        String usersText = "";
        String appointmentText = "";
        
        if(lo.getLanguage() == "en") {
            CustomersText = "Customers";
            LogOffText = "LogOff";
            usersText = "Users";
            appointmentText = "Appointment";
        } else if (lo.getLanguage() == "es") {
            CustomersText = "El Customers";
            LogOffText = "El LogOff";
            usersText = "El Users";
            appointmentText = "El Appointment";
        }
        
        final Hyperlink customersLink = new Hyperlink("Customers");
        final Hyperlink logOffLink = new Hyperlink("LogOff");
        //final Hyperlink usersLink = new Hyperlink("Users");
        final Hyperlink AppointmentLink = new Hyperlink("Appointment");
        final Hyperlink ReportsLink = new Hyperlink("Reports");
               
        customersLink.setPrefSize(100, 20);
        customersLink.setStyle("-fx-text-fill: white;");
        customersLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Customers!");
                createCustomersScreen(primaryStage);
            }
        });
        
        logOffLink.setPrefSize(100, 20);
        logOffLink.setStyle("-fx-text-fill: white;");
        logOffLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Utility.logout();
                createLoginScreen(primaryStage);
            }
        });
        
        //usersLink.setPrefSize(100, 20);
        //usersLink.setStyle("-fx-text-fill: white;");
        //usersLink.setOnAction(new EventHandler<ActionEvent>() {
            
        //    @Override
        //    public void handle(ActionEvent event) { 
        //    }
        //});
        
        AppointmentLink.setPrefSize(100, 20);
        AppointmentLink.setStyle("-fx-text-fill: white;");
        AppointmentLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createAppointmentWeekScreen(primaryStage);
            }
        });
        
        VBox vbox = new VBox();
        vbox.getChildren().addAll(customersLink, AppointmentLink, logOffLink);
        //vbox.getChildren().add(button);
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #336699;");  
        //Create MainMenu stage
        scene = new Scene(vbox, 600, 350);
        vbox.requestFocus();
        
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void createReportScreen(Stage primaryStage){
      
        final Hyperlink AppointmentTypesByMonthReport = new Hyperlink("Appointment types by month");
        final Hyperlink ConsultantSchedule = new Hyperlink("Consultant Schedule");
        final Hyperlink AppointmentScheduleByCountry = new Hyperlink("Appointments by Country");
        final Hyperlink MainMenuLink = new Hyperlink("MainMenu");
               
        AppointmentTypesByMonthReport.setPrefSize(100, 20);
        AppointmentTypesByMonthReport.setStyle("-fx-text-fill: white;");
        AppointmentTypesByMonthReport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createAppointmentTypesByMonthReport(primaryStage);
            }
        });
        
        ConsultantSchedule.setPrefSize(100, 20);
        ConsultantSchedule.setStyle("-fx-text-fill: white;");
        ConsultantSchedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //createConsultantScheduleReportScreen(primaryStage);
            }
        });
        
        AppointmentScheduleByCountry.setPrefSize(100, 20);
        AppointmentScheduleByCountry.setStyle("-fx-text-fill: white;");
        AppointmentScheduleByCountry.setOnAction(new EventHandler<ActionEvent>() {       
            @Override
            public void handle(ActionEvent event) { 
                //createAppointmentScheduleByCountry(primaryStage);
            }
        });
        
        MainMenuLink.setPrefSize(100, 20);
        MainMenuLink.setStyle("-fx-text-fill: white;");
        MainMenuLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createAppointmentWeekScreen(primaryStage);
            }
        });
        
        VBox vbox = new VBox();
        vbox.getChildren().addAll(AppointmentTypesByMonthReport, ConsultantSchedule, AppointmentScheduleByCountry, MainMenuLink);
        //vbox.getChildren().add(button);
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #336699;");  
        //Create MainMenu stage
        scene = new Scene(vbox, 600, 350);
        vbox.requestFocus();
        
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //createAppointmentTypesByMonthReport
    public void createAppointmentTypesByMonthReport(Stage primaryStage){
        try {
            Button runReportButton = new Button("Run Report");
            HBox hb = new HBox();
            
            ComboBox<Customer> customerMenu = new ComboBox<Customer>(FXCollections.observableList(Dal.retrieveCustomers()));
            
            hb.getChildren().addAll(customerMenu);
            hb.setSpacing(10);
            

            
            //Create Customers stage
            scene = new Scene(hb, 600, 350);
            hb.requestFocus();
            
            primaryStage.setTitle("Create Customer");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (SQLException ex) {
            Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void createCustomersScreen(Stage primaryStage){
        try {
            Button newCustButton = new Button("New Customer");
            Button editCustButton = new Button("Edit Customer");
            HBox hb = new HBox();
            
            ComboBox<Customer> customerMenu = new ComboBox<Customer>(FXCollections.observableList(Dal.retrieveCustomers()));
            
            hb.getChildren().addAll(newCustButton, customerMenu, editCustButton);
            hb.setSpacing(10);
            
            //Create NewCustomer Button Handle
            newCustButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    createCustomersEditScreen(primaryStage);
                }
            });
            
            //Create EditCustomer Button Handle
            editCustButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        createCustomersEditScreen(primaryStage, customerMenu.getSelectionModel().getSelectedItem().CustomerId);
                    } catch (SQLException ex) {
                        Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            //Create Customers stage
            scene = new Scene(hb, 600, 350);
            hb.requestFocus();
            
            primaryStage.setTitle("Create Customer");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (SQLException ex) {
            Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createAppointmentWeekScreen(Stage primaryStage){
        final Agenda agenda = new Agenda();
        //agenda.  
        
        Dal dal = new Dal();
        ArrayList<Appointment> appts =  dal.getAllAppointmentsForUser(Utility.getLoggedInUserName());
        
        VBox vb = new VBox();
        agenda.setDisplayedLocalDateTime(LocalDateTime.now());
        agenda.setPrefSize(1000, 800);
        ArrayList<Agenda.Appointment> apptList = new ArrayList<Agenda.Appointment>();
        appts.forEach(item->{
            LocalDateTime localStart = item.start.toInstant().atZone(Utility.getCurrentUserTimeZone()).toLocalDateTime();
            LocalDateTime localEnd = item.end.toInstant().atZone(Utility.getCurrentUserTimeZone()).toLocalDateTime();
            
            Agenda.Appointment agendaAppointment = new Agenda.AppointmentImplLocal()
               .withStartLocalDateTime(localStart)
               .withEndLocalDateTime(localEnd)
               .withSummary(item.title)
               .withLocation(item.location)
               .withDescription(item.description);
            
            apptList.add(agendaAppointment);
        });
        agenda.setEditAppointmentCallback((Agenda.Appointment param) -> {
            int i = apptList.indexOf(param);
            int apptId = appts.get(i).appointmentId;
            try {
                createAppointmentEditScreen(primaryStage, apptId);
            } catch (SQLException ex) {
                Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
            }
            //edit appt
            //param.hashCode()
            //logger.info(param.getSummary() + " is selected. ");
            return null;
        });
        
        agenda.appointments().addAll(apptList);

	/**
	 * 
	 */
        Button monthViewButton = new Button("Month View");
        monthViewButton.setOnMouseClicked( (actionEvent) -> {
            createAppointmentMonthScreen(primaryStage);
        });
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    createMainMenuScreen(primaryStage);
            }
        });

        // accept new appointments
        Button newAppointmentButton = new Button("New Appointment");
        HBox hb = new HBox();
        hb.getChildren().addAll(newAppointmentButton, monthViewButton, mainMenuButton);
        hb.setSpacing(10);
        //Create NewAppointment Button Handle
        newAppointmentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    createAppointmentEditScreen(primaryStage);
                } catch (SQLException ex) {
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        vb.getChildren().addAll(agenda, hb);
        
        //Create Appointments stage
        scene = new Scene(vb, 600, 350);
        hb.requestFocus();
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void createAppointmentMonthScreen(Stage primaryStage){
        final Agenda agenda = new Agenda();
        
        Dal dal = new Dal();
        ArrayList<Appointment> appts =  dal.getAllAppointmentsForUser(Utility.getLoggedInUserName());
        
        VBox vb = new VBox();
        agenda.setDisplayedLocalDateTime(LocalDateTime.now());
        agenda.setPrefSize(1000, 800);
        ArrayList<Agenda.Appointment> apptList = new ArrayList<Agenda.Appointment>();
        appts.forEach(item->{
            LocalDateTime localStart = item.start.toInstant().atZone(Utility.getCurrentUserTimeZone()).toLocalDateTime();
            LocalDateTime localEnd = item.end.toInstant().atZone(Utility.getCurrentUserTimeZone()).toLocalDateTime();
            
            Agenda.Appointment agendaAppointment = new Agenda.AppointmentImplLocal()
               .withStartLocalDateTime(localStart)
               .withEndLocalDateTime(localEnd)
               .withSummary(item.title)
               .withDescription(item.description);
            apptList.add(agendaAppointment);
        });
        agenda.appointments().addAll(apptList);
        
        agenda.setSkin(new AgendaDaySkin(agenda));

        DatePicker monthDatePicker = new DatePicker();
        monthDatePicker.setValue(LocalDate.now());
        
        final Callback<DatePicker, DateCell> dayCellFactory = 
            new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            appts.forEach(i->{ 
                                LocalDate localStart = i.start.toInstant().atZone(Utility.getCurrentUserTimeZone()).toLocalDate();
                                if (item.isEqual(localStart)) {
                                    setDisable(false);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                } 
                            }); 
                        }
                    };
                }
            };
        monthDatePicker.setDayCellFactory(dayCellFactory);
        monthDatePicker.setValue(LocalDate.now(Utility.getCurrentUserTimeZone()));
        
        monthDatePicker.setOnAction(event -> {
            LocalDate date = monthDatePicker.getValue();
            //Tell the day view to change to this
            agenda.setDisplayedLocalDateTime(date.atStartOfDay());
        });
        
        // accept new appointments
        Button newAppointmentButton = new Button("New Appointment");
        Button weekViewButton = new Button("Week View");
        Button mainMenuButton = new Button("Main Menu");
        HBox hb = new HBox();
        hb.getChildren().addAll(newAppointmentButton, weekViewButton, mainMenuButton);
        hb.setSpacing(10);
        //Create NewAppointment Button Handle
        newAppointmentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    createAppointmentEditScreen(primaryStage);
                } catch (SQLException ex) {
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        weekViewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    createAppointmentWeekScreen(primaryStage);
            }
        });
        
        mainMenuButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    createMainMenuScreen(primaryStage);
            }
        });
        
        vb.getChildren().addAll(monthDatePicker, agenda, hb);
        
        //Create Appointments stage
        scene = new Scene(vb, 600, 350);
        hb.requestFocus();
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
        monthDatePicker.show();
    }   
    
    public void createAppointmentEditScreen(Stage primaryStage) throws SQLException{

        //CustomerDropdown field
        Label customerLbl = new Label("Customer:");
        final ComboBox<Customer> customerMenu = new ComboBox<>(FXCollections.observableList(Dal.retrieveCustomers()));

        HBox hb = new HBox();
        hb.getChildren().addAll(customerLbl, customerMenu);
        hb.setSpacing(10);
        
        //Appointment Type field
        //Maybe use an editable dropdown here? to constrain new types from being created?
        ObservableList<String> options = FXCollections.observableArrayList(
            "New Client",
            "Maintenance Appt",
            "Prospect"
        );
        Label apptTypeLbl = new Label("Appointment Type:");
        final ComboBox apptTypeComboBox = new ComboBox(options);
        HBox hb2 = new HBox();
        hb2.getChildren().addAll(apptTypeLbl, apptTypeComboBox);
        hb2.setSpacing(10);
        
        //Description field
        Label descriptionLbl = new Label("Description:");
        final TextArea descriptionArea = new TextArea ();
        descriptionArea.setPrefRowCount(4);
        HBox hb3 = new HBox();
        hb3.getChildren().addAll(descriptionLbl, descriptionArea);
        hb3.setSpacing(10);
        
        //Location field
        Label locationLbl = new Label("Location:");
        final TextField locationField = new TextField ();
        HBox hb4 = new HBox();
        hb4.getChildren().addAll(locationLbl, locationField);
        hb4.setSpacing(10);
        
        //Contact field
        Label contactLbl = new Label("Contact:");
        final TextField contactField = new TextField ();
        HBox hb5 = new HBox();
        hb5.getChildren().addAll(contactLbl, contactField);
        hb5.setSpacing(10);
        
        //Url field
        Label urlLbl = new Label("Url:");
        TextField urlField = new TextField ();
        HBox hb6 = new HBox();
        hb6.getChildren().addAll(urlLbl, urlField);
        hb6.setSpacing(10);
        
        //DateTime appt start field
        Label dateLbl = new Label("Day of appt:");
        //final DatePicker datePicker = new DatePicker();
        final DatePicker datePicker = new DatePicker();
        
        HBox hb7 = new HBox();
        hb7.getChildren().addAll(dateLbl, datePicker);
        hb7.setSpacing(10);
        

        ObservableList<String> apptDurationOptions = FXCollections.observableArrayList(
            "15",
            "30",
            "45",
            "60"
        );
        Label apptDurationLbl = new Label("Appointment Duration:");
        final ComboBox apptDurationComboBox = new ComboBox(apptDurationOptions);
        HBox hb8 = new HBox();
        hb8.getChildren().addAll(apptDurationLbl, apptDurationComboBox);
        hb8.setSpacing(10);        
        
        ObservableList<String> apptOptions = FXCollections.observableArrayList(
            "0:00","0:15","0:30","0:45",
            "1:00","1:15","1:30","1:45",
            "2:00","2:15","2:30","2:45",
            "3:00","3:15","3:30","3:45",
            "4:00","4:15","4:30","4:45",
            "5:00","5:15","5:30","5:45",
            "6:00","6:15","6:30","6:45",
            "7:00","7:15","7:30","7:45",
            "8:00","8:15","8:30","8:45",
            "9:00","9:15","9:30","9:45",
            "10:00","10:15","10:30","10:45",
            "11:00","11:15","11:30","11:45",
            "12:00","12:15","12:30","12:45",
            "13:00","13:15","13:30","13:45",
            "14:00","14:15","14:30","14:45",
            "15:00","15:15","15:30","15:45",
            "16:00","16:15","16:30","16:45",
            "17:00","17:15","17:30","17:45",
            "18:00","18:15","18:30","18:45",
            "19:00","19:15","19:30","19:45",
            "20:00","20:15","20:30","20:45",
            "21:00","21:15","21:30","21:45",
            "22:00","22:15","22:30","22:45",
            "23:00","23:15","23:30","23:45"
        );
        Label apptTimeLbl = new Label("Appointment Time:");
        final ComboBox apptTimeComboBox = new ComboBox(apptOptions);
        HBox hb9 = new HBox();
        hb9.getChildren().addAll(apptTimeLbl, apptTimeComboBox);
        hb9.setSpacing(10);
        
        Button saveApptButton = new Button("Save Appointment");
        Button cancelApptButton = new Button("Cancel");
        HBox hb10 = new HBox();
        hb10.getChildren().addAll(saveApptButton, cancelApptButton);
        hb10.setSpacing(10);
        
        //Create Save Appt Button Handle
        saveApptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { 
                try{
                    Dal dal = new Dal();
                    dal.createAppointment(customerMenu.getSelectionModel().getSelectedItem().CustomerId, apptTypeComboBox.getSelectionModel().getSelectedItem().toString(),descriptionArea.getText(), locationField.getText(), contactField.getText(), urlField.getText(), datePicker.getValue(), apptDurationComboBox.getValue().toString(), apptTimeComboBox.getValue().toString(), tz, Utility.getLoggedInUserName());
                    createAppointmentWeekScreen(primaryStage);
                } catch (OutsideBusinessHoursException ex){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Outside Business Hours");
                    alert.setHeaderText("Outside Business Hours");
                    alert.setContentText("Appointments not allowed after business hours");

                    alert.showAndWait();
                    System.out.println(ex.toString());
                } catch (Exception ex){
                    System.out.println(ex.toString());
                }
            }
        });
        
        //Create Cancel Button Handle
        cancelApptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //return to appointments 
                createAppointmentWeekScreen(primaryStage);
            }
        });
        
        //Vbox for fields
        VBox vbox = new VBox();
        vbox.getChildren().addAll(hb, hb2, hb3, hb4, hb5, hb6, hb7, hb8, hb9, hb10);
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #336699;");  
        
        //Create Customers stage
        scene = new Scene(vbox, 600, 350);
        vbox.requestFocus();
        
        primaryStage.setTitle("New Appt");
        primaryStage.setScene(scene);
        primaryStage.show();
    }   
    
    public void createAppointmentEditScreen(Stage primaryStage, int apptId) throws SQLException{
        Dal dal = new Dal();
        Optional<Appointment> optionalAppointment = Optional.ofNullable(dal.getAppointment(apptId));
        Optional<Customer> optionalCustomer = Optional.ofNullable(Dal.retrieveCustomer(optionalAppointment.get().customerId));
        
        //Build start date
        LocalDate startLocalDate = optionalAppointment.get().start.toInstant().atZone(Utility.currentUserTimeZone).toLocalDate();
        //Build duration
        Date startDate = optionalAppointment.get().start;
        Date endDate = optionalAppointment.get().end;
        //Build Time Dropdown
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String  selectedTime = timeFormat.format(startDate);
        
        long diff = endDate.getTime() - startDate. getTime();
        long diffMinutes = diff / (60 * 1000) % 60;
        
        //CustomerDropdown field
        Label customerLbl = new Label("Customer:");
        final ComboBox<Customer> customerMenu = new ComboBox<>(FXCollections.observableList(Dal.retrieveCustomers()));
        //default to currently selected customer
        customerMenu.setValue(optionalCustomer.get());
        
        HBox hb = new HBox();
        hb.getChildren().addAll(customerLbl, customerMenu);
        hb.setSpacing(10);
        
        //Appointment Type field
        //Maybe use an editable dropdown here? to constrain new types from being created?
        ObservableList<String> options = FXCollections.observableArrayList(
            "New Client",
            "Maintenance Appt",
            "Prospect"
        );
        Label apptTypeLbl = new Label("Appointment Type:");
        final ComboBox apptTypeComboBox = new ComboBox(options);
        apptTypeComboBox.setValue(optionalAppointment.get().title);
        HBox hb2 = new HBox();
        hb2.getChildren().addAll(apptTypeLbl, apptTypeComboBox);
        hb2.setSpacing(10);
        
        //Description field
        Label descriptionLbl = new Label("Description:");
        final TextArea descriptionArea = new TextArea ();
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setText(optionalAppointment.get().description);
        HBox hb3 = new HBox();
        hb3.getChildren().addAll(descriptionLbl, descriptionArea);
        hb3.setSpacing(10);
        
        //Location field
        Label locationLbl = new Label("Location:");
        final TextField locationField = new TextField ();
        locationField.setText(optionalAppointment.get().location);
        HBox hb4 = new HBox();
        hb4.getChildren().addAll(locationLbl, locationField);
        hb4.setSpacing(10);
        
        //Contact field
        Label contactLbl = new Label("Contact:");
        final TextField contactField = new TextField ();
        contactField.setText(optionalAppointment.get().contact);
        HBox hb5 = new HBox();
        hb5.getChildren().addAll(contactLbl, contactField);
        hb5.setSpacing(10);
        
        //Url field
        Label urlLbl = new Label("Url:");
        TextField urlField = new TextField ();
        urlField.setText(optionalAppointment.get().url);
        HBox hb6 = new HBox();
        hb6.getChildren().addAll(urlLbl, urlField);
        hb6.setSpacing(10);
        
        //DateTime appt start field
        Label dateLbl = new Label("Day of appt:");
        //final DatePicker datePicker = new DatePicker();
        final DatePicker datePicker = new DatePicker();
        datePicker.setValue(startLocalDate);
        HBox hb7 = new HBox();
        hb7.getChildren().addAll(dateLbl, datePicker);
        hb7.setSpacing(10);

        ObservableList<String> apptDurationOptions = FXCollections.observableArrayList(
            "15",
            "30",
            "45",
            "60"
        );
        Label apptDurationLbl = new Label("Appointment Duration:");
        final ComboBox apptDurationComboBox = new ComboBox(apptDurationOptions);
        apptDurationComboBox.setValue(String.valueOf(diffMinutes));
        HBox hb8 = new HBox();
        hb8.getChildren().addAll(apptDurationLbl, apptDurationComboBox);
        hb8.setSpacing(10);        
        
        ObservableList<String> apptOptions = FXCollections.observableArrayList(
            "0:00","0:15","0:30","0:45",
            "1:00","1:15","1:30","1:45",
            "2:00","2:15","2:30","2:45",
            "3:00","3:15","3:30","3:45",
            "4:00","4:15","4:30","4:45",
            "5:00","5:15","5:30","5:45",
            "6:00","6:15","6:30","6:45",
            "7:00","7:15","7:30","7:45",
            "8:00","8:15","8:30","8:45",
            "9:00","9:15","9:30","9:45",
            "10:00","10:15","10:30","10:45",
            "11:00","11:15","11:30","11:45",
            "12:00","12:15","12:30","12:45",
            "13:00","13:15","13:30","13:45",
            "14:00","14:15","14:30","14:45",
            "15:00","15:15","15:30","15:45",
            "16:00","16:15","16:30","16:45",
            "17:00","17:15","17:30","17:45",
            "18:00","18:15","18:30","18:45",
            "19:00","19:15","19:30","19:45",
            "20:00","20:15","20:30","20:45",
            "21:00","21:15","21:30","21:45",
            "22:00","22:15","22:30","22:45",
            "23:00","23:15","23:30","23:45"
        );
        Label apptTimeLbl = new Label("Appointment Time:");
        final ComboBox apptTimeComboBox = new ComboBox(apptOptions);
        apptTimeComboBox.setValue(selectedTime);
        HBox hb9 = new HBox();
        hb9.getChildren().addAll(apptTimeLbl, apptTimeComboBox);
        hb9.setSpacing(10);
        
        Button saveApptButton = new Button("Save Customer");
        Button cancelApptButton = new Button("Cancel");
        HBox hb10 = new HBox();
        hb10.getChildren().addAll(saveApptButton, cancelApptButton);
        hb10.setSpacing(10);
        
        //Create Save Appt Button Handle
        saveApptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { 
                
                try{
                    Dal dal = new Dal();
                    dal.updateAppointment(apptId, customerMenu.getSelectionModel().getSelectedItem().CustomerId, apptTypeComboBox.getSelectionModel().getSelectedItem().toString(),descriptionArea.getText(), locationField.getText(), contactField.getText(), urlField.getText(), datePicker.getValue(), apptDurationComboBox.getValue().toString(), apptTimeComboBox.getValue().toString(), tz, Utility.getLoggedInUserName());
                    createAppointmentWeekScreen(primaryStage);
                } catch (OutsideBusinessHoursException ex){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Outside Business Hours");
                    alert.setHeaderText("Outside Business Hours");
                    alert.setContentText("Appointments not allowed after business hours");

                    alert.showAndWait();
                    System.out.println(ex.toString());
                } catch (Exception ex){
                    System.out.println(ex.toString());
                }
            }
        });
        
        //Create Cancel Button Handle
        cancelApptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //return to appointments 
                createAppointmentWeekScreen(primaryStage);
            }
        });
        
        //Vbox for fields
        VBox vbox = new VBox();
        vbox.getChildren().addAll(hb, hb2, hb3, hb4, hb5, hb6, hb7, hb8, hb9, hb10);
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #336699;");  
        
        //Create Customers stage
        scene = new Scene(vbox, 600, 350);
        vbox.requestFocus();
        
        primaryStage.setTitle("New Appt");
        primaryStage.setScene(scene);
        primaryStage.show();
    }   
    
    public void createCustomersEditScreen(Stage primaryStage){
        //Name field
        Label nameLbl = new Label("Name:");
        TextField nameField = new TextField ();
        HBox hb = new HBox();
        hb.getChildren().addAll(nameLbl, nameField);
        hb.setSpacing(10);
        
        //Address line 1 field
        Label addLn1Lbl = new Label("Address Line 1:");
        TextField addressLn1Field = new TextField ();
        HBox hb2 = new HBox();
        hb2.getChildren().addAll(addLn1Lbl, addressLn1Field);
        hb2.setSpacing(10);
        
        //Address line 2 field
        Label addLn2Lbl = new Label("Address Line 2:");
        TextField addressLn2Field = new TextField ();
        HBox hb3 = new HBox();
        hb3.getChildren().addAll(addLn2Lbl, addressLn2Field);
        hb3.setSpacing(10);
        
        //Address City field
        Label addCityLbl = new Label("City:");
        TextField addressCityField = new TextField ();
        HBox hb4 = new HBox();
        hb4.getChildren().addAll(addCityLbl, addressCityField);
        hb4.setSpacing(10);
        
        //Address PostalCode field
        Label addPoLbl = new Label("Postal Code:");
        TextField addressPostalCodeField = new TextField ();
        HBox hb5 = new HBox();
        hb5.getChildren().addAll(addPoLbl, addressPostalCodeField);
        hb5.setSpacing(10);
        
        //Country field
        Label addCountryLbl = new Label("Country:");
        TextField addressCountryField = new TextField ();
        HBox hb6 = new HBox();
        hb6.getChildren().addAll(addCountryLbl, addressCountryField);
        hb6.setSpacing(10);
        
        //Phone field
        Label addPhoneLbl = new Label("Phone Number:");
        TextField addressPhoneNumberField = new TextField ();
        HBox hb7 = new HBox();
        hb7.getChildren().addAll(addPhoneLbl, addressPhoneNumberField);
        hb7.setSpacing(10);
        
        Button saveCustButton = new Button("Save Customer");
        Button cancelButton = new Button("Cancel");
        HBox hb8 = new HBox();
        hb8.getChildren().addAll(saveCustButton, cancelButton);
        hb8.setSpacing(10);
        
        //Create Save Customer Button Handle
        saveCustButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //save customer
                Customer cust = new Customer();
                cust.CustomerName = nameField.getText();
                Address addr = new Address();
                addr.address1 = addressLn1Field.getText();
                addr.address2 = addressLn2Field.getText();
                addr.postalCode = addressPostalCodeField.getText();
                addr.phone = addressPhoneNumberField.getText();
                
                Dal dal = new Dal(); 
                try {
                    dal.createCustomer(cust, addr, addressCityField.getText(), addressCountryField.getText(), Utility.currentUserName);
                } catch (SQLException ex) {
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidCustomerException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Invalid Info");
                    alert.setHeaderText("Invalid Info");
                    alert.setContentText(ex.getMessage());

                    alert.showAndWait();
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                }
                //return to customers 
                createCustomersScreen(primaryStage);
            }
        });
        
        //Create Cancel Button Handle
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //return to customers 
                createCustomersScreen(primaryStage);
            }
        });
        
        //Vbox for fields
        VBox vbox = new VBox();
        vbox.getChildren().addAll(hb, hb2, hb3, hb4, hb5, hb6, hb7, hb8);
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #336699;");  
        
        //Create Customers stage
        scene = new Scene(vbox, 600, 350);
        vbox.requestFocus();
        
        primaryStage.setTitle("New User");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void createCustomersEditScreen(Stage primaryStage, int customerId) throws SQLException{
        Optional<Customer> optionalCustomer = Optional.ofNullable(Dal.retrieveCustomer(customerId));
        Optional<Address> optionalAddress = Optional.ofNullable(Dal.retrieveAddress(optionalCustomer.get().AddressId));
        Optional<City> optionalCity = Optional.ofNullable(Dal.retrieveCity(optionalAddress.get().cityId));
        Optional<Country> optionalCountry = Optional.ofNullable(Dal.retrieveCountry(optionalCity.get().countryId));
        
        //Update logic to save as update.
            
        //Name field
        Label nameLbl = new Label("Name:");
        TextField nameField = new TextField ();
        //assign name data from db
        if (optionalCustomer.isPresent()) {
        nameField.setText(optionalCustomer.get().CustomerName);
        }
        HBox hb = new HBox();
        hb.getChildren().addAll(nameLbl, nameField);
        hb.setSpacing(10);
        
        //Address line 1 field
        Label addLn1Lbl = new Label("Address Line 1:");
        TextField addressLn1Field = new TextField ();
        if (optionalAddress.isPresent()) {
        addressLn1Field.setText(optionalAddress.get().address1);
        }
        HBox hb2 = new HBox();
        hb2.getChildren().addAll(addLn1Lbl, addressLn1Field);
        hb2.setSpacing(10);
        
        //Address line 2 field
        Label addLn2Lbl = new Label("Address Line 2:");
        TextField addressLn2Field = new TextField ();
        if (optionalAddress.isPresent()) {
        addressLn2Field.setText(optionalAddress.get().address2);
        }
        HBox hb3 = new HBox();
        hb3.getChildren().addAll(addLn2Lbl, addressLn2Field);
        hb3.setSpacing(10);
        
        //Address City field
        Label addCityLbl = new Label("City:");
        TextField addressCityField = new TextField ();
        if (optionalCity.isPresent()) {
        addressCityField.setText(optionalCity.get().city);
        }
        HBox hb4 = new HBox();
        hb4.getChildren().addAll(addCityLbl, addressCityField);
        hb4.setSpacing(10);
        
        //Address PostalCode field
        Label addPoLbl = new Label("Postal Code:");
        TextField addressPostalCodeField = new TextField ();
        if (optionalAddress.isPresent()) {
        addressPostalCodeField.setText(optionalAddress.get().postalCode);
        }
        HBox hb5 = new HBox();
        hb5.getChildren().addAll(addPoLbl, addressPostalCodeField);
        hb5.setSpacing(10);
        
        //Country field
        Label addCountryLbl = new Label("Country:");
        TextField addressCountryField = new TextField ();
        if (optionalCountry.isPresent()) {
        addressCountryField.setText(optionalCountry.get().country);
        }
        HBox hb6 = new HBox();
        hb6.getChildren().addAll(addCountryLbl, addressCountryField);
        hb6.setSpacing(10);
        
        //Phone field
        Label addPhoneLbl = new Label("Phone Number:");
        TextField addressPhoneNumberField = new TextField ();
        if (optionalAddress.isPresent()) {
        addressPhoneNumberField.setText(optionalAddress.get().phone);
        }
        HBox hb7 = new HBox();
        hb7.getChildren().addAll(addPhoneLbl, addressPhoneNumberField);
        hb7.setSpacing(10);
        
        Button saveCustButton = new Button("Save Customer");
        Button cancelButton = new Button("Cancel");
        HBox hb8 = new HBox();
        hb8.getChildren().addAll(saveCustButton, cancelButton);
        hb8.setSpacing(10);
        
        //Create Save Customer Button Handle
        saveCustButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //save customer

                Dal dal = new Dal(); 
                try {
                    //TODO: need to revisit update logic
                    dal.updateCustomer(optionalCustomer.get(), optionalAddress.get(), addressCityField.getText(), addressCountryField.getText(), Utility.currentUserName);
                } catch (SQLException ex) {
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidCustomerException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Invalid Info");
                    alert.setHeaderText("Invalid Info");
                    alert.setContentText(ex.getMessage());

                    alert.showAndWait();
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                }
                //return to customers 
                createCustomersScreen(primaryStage);
            }
        });
        
        //Create Cancel Button Handle
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //return to customers 
                createCustomersScreen(primaryStage);
            }
        });
        
        //Vbox for fields
        VBox vbox = new VBox();
        vbox.getChildren().addAll(hb, hb2, hb3, hb4, hb5, hb6, hb7, hb8);
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #336699;");  
        
        //Create Customers stage
        scene = new Scene(vbox, 600, 350);
        vbox.requestFocus();
        
        primaryStage.setTitle("Edit User");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
