/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.control.Alert;
import javafxapplication1.entities.Appointment;
import javafxapplication1.entities.TimeZoneDisplay;

/**
 *
 * @author michael
 */
public class Utility {
    public static Boolean isAuthenticated = false;
    public static String currentUserName = "";
    public static ZoneId currentUserTimeZone = ZoneId.systemDefault();
    
    public static Boolean isUserLoggedIn() {
        return isAuthenticated;
    }
    
    public static String getLoggedInUserName() {
        return currentUserName; 
    }
    
    public static ZoneId getCurrentUserTimeZone() {
        return currentUserTimeZone; 
    }
    
    public static ArrayList<TimeZoneDisplay> getTimezones() {
        String[] availableIDs = TimeZone.getAvailableIDs();
        ArrayList<TimeZoneDisplay> tzdList = new ArrayList<>();
        for (String id : availableIDs){
            Date date = new Date();
            Boolean isDaylightTime = TimeZone.getTimeZone(id).inDaylightTime(date);
            
            TimeZoneDisplay tzd = new TimeZoneDisplay(id, TimeZone.getTimeZone(id).getDisplayName(isDaylightTime, TimeZone.LONG));            
            tzdList.add(tzd);
        }
        return tzdList;
    }
    
    public static void login(String userName, String password, String country) throws SQLException, IOException, InvalidLoginException {
        if(Dal.checkCredentials(userName, password)) {
            currentUserName = userName;
            isAuthenticated = true;
            currentUserTimeZone = ZoneId.systemDefault();
            //Requirement J: append login information to a log file
            Charset charSetOfLog = Charset.forName("US-ASCII");
            ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
            String logEntry = "User: " + userName + " logged in at: " + utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
            Path pathOfLog = Paths.get(Paths.get(".").toAbsolutePath().normalize().toString(), "UserLog");
            BufferedWriter bwOfLog = Files.newBufferedWriter(pathOfLog, charSetOfLog, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            bwOfLog.append(logEntry);
            bwOfLog.newLine();
            bwOfLog.close();
        } else {
            currentUserName = "";
            isAuthenticated = false;
            throw new InvalidLoginException();
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void logout(){
        currentUserName = "";
        isAuthenticated = false;
    }
    
    static void outsideBusinessHours(Date start, Date end) throws OutsideBusinessHoursException {

        Calendar startCalendar = GregorianCalendar.getInstance(); 
        startCalendar.setTime(start);   
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY); 
        Calendar endCalendar = GregorianCalendar.getInstance(); 
        endCalendar.setTime(end);   
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY); 
        
        boolean isStartOutsideBusinessHours = (9 > startHour ) && 17 > startHour;
        boolean isEndOutsideBusinessHours = (9 > endHour ) && 17 > endHour;       
        if (isStartOutsideBusinessHours || isEndOutsideBusinessHours) {throw new OutsideBusinessHoursException();};
    }
    
    void overlappingAppointment(Date start, Date end) throws OverbookedAppointmentException {
        String currentUser = Utility.getLoggedInUserName();
        final AtomicInteger count = new AtomicInteger();
        Dal dal = new Dal();

        ArrayList<Appointment> allAppointmentsForUser = dal.getAllAppointmentsForUser(currentUser);
        allAppointmentsForUser.forEach(appt -> {
            if (isOverlapping(start, end, appt.start, appt.end)) 
            {
                count.set(1);
            };
        });
        if (count.get() > 0) {throw new OverbookedAppointmentException();};  
    }
    
    public static boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && start2.before(end1);
    }
    
    //G.  Use lambda expressions to create standard pop-up and alert messages.
    //H.  Write code to provide reminders and alerts 15 minutes in advance of an appointment, based on the user’s log-in.
    public void appointmentReminder(String username) {
        final AtomicInteger count = new AtomicInteger();
        Dal dal = new Dal();
        ArrayList<Appointment> allAppointmentsForUser = dal.getAllAppointmentsForUser(username);
        Date currentDate = new Date();
        allAppointmentsForUser.forEach(appt -> {
           long diffInTime = appt.start.getTime() - currentDate.getTime();
           long timeTillApptInMinutes = diffInTime / (60 * 1000); 
           if (timeTillApptInMinutes >= 0 && timeTillApptInMinutes <= 15) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointment Reminder");
                    alert.setHeaderText("Appointment Reminder");
                    alert.setContentText("You have an appointment starting within " + timeTillApptInMinutes + "Minutes");

                    alert.showAndWait();
           }
        });
    }
      
    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}
