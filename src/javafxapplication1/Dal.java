/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxapplication1.entities.Address;
import javafxapplication1.entities.Appointment;
import javafxapplication1.entities.City;
import javafxapplication1.entities.Country;
import javafxapplication1.entities.Customer;

/**
 *
 * @author michael
 */
public class Dal {

    static ComboPooledDataSource cpds;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    public static void connect() throws ClassNotFoundException {
        Connection conn = null;
        String driver = "com.mysql.jdbc.Driver";

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(ConnectionInfo.url, ConnectionInfo.user, ConnectionInfo.pass);
            System.out.println("Connected to database : " + ConnectionInfo.db);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }

    public static Connection getConnectionfromPool() throws SQLException {
        return cpds.getConnection();
    }

    public static void makeConnectionPool() {
        try {
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass("com.mysql.jdbc.Driver");
            cpds.setJdbcUrl(ConnectionInfo.url);
            cpds.setUser(ConnectionInfo.user);
            cpds.setPassword(ConnectionInfo.pass);
            cpds.setMaxPoolSize(100);
            cpds.setMinPoolSize(3);
            cpds.setAcquireIncrement(20);
            System.out.println("Connected =D");
        } catch (PropertyVetoException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
            //Logger.getLogger(MCE_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Boolean checkCredentials(String userName, String password) throws SQLException {
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            String sql = "SELECT * from user WHERE userName = ? AND password = ?";
            ps = dbConnect.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Access granted: Loggedin!");
                return true;
            } else {
                System.out.println("Access denied: Get out!");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Exception in sql login: " + ex.getStackTrace().toString());
            return false;
        }
    };
    
    public static Customer retrieveCustomer(int customerId) throws SQLException {
        Connection dbConnect = Dal.getConnectionfromPool();
        String lookupSql = "SELECT * from customer where customerId = ?";
        PreparedStatement getCustStatement = dbConnect.prepareStatement(lookupSql);
        getCustStatement.setInt(1, customerId);
        ResultSet rs = getCustStatement.executeQuery();
        Customer cust = new Customer();
        // Fetch each row from the result set
        if (rs.next()) {
          //Assuming you have a user object
          cust.CustomerId = rs.getInt("customerId");
          cust.CustomerName = rs.getString("customerName");
          cust.AddressId = rs.getInt("addressId");
        } 
        return cust;
    };
    
    public static Address retrieveAddress(int addressId) throws SQLException {
        Connection dbConnect = Dal.getConnectionfromPool();
        String lookupSql = "SELECT * from address where addressId = ?";
        PreparedStatement getAddyStatement = dbConnect.prepareStatement(lookupSql);
        getAddyStatement.setInt(1, addressId);
        ResultSet rs = getAddyStatement.executeQuery();
        Address addy = new Address();
        // Fetch each row from the result set
        if (rs.next()) {
          //Assuming you have a user object
          addy.addressId = rs.getInt("addressId");
          addy.address1 = rs.getString("address");
          addy.address2 = rs.getString("address2");
          addy.cityId = rs.getInt("cityId");
          addy.postalCode = rs.getString("postalCode");
          addy.phone = rs.getString("phone");
        } 
        return addy;
    };
    
    public static City retrieveCity(int cityId) throws SQLException {
        Connection dbConnect = Dal.getConnectionfromPool();
        String lookupSql = "SELECT * from city where cityId = ?";
        PreparedStatement getCityStatement = dbConnect.prepareStatement(lookupSql);
        getCityStatement.setInt(1, cityId);
        ResultSet rs = getCityStatement.executeQuery();
        City city = new City();
        // Fetch each row from the result set
        if (rs.next()) {
          //Assuming you have a user object
          city.cityId = rs.getInt("cityId");
          city.city = rs.getString("city");
          city.countryId = rs.getInt("countryId");
        } 
        return city;
    };
    
    public static Country retrieveCountry(int countryId) throws SQLException {
        Connection dbConnect = Dal.getConnectionfromPool();
        String lookupSql = "SELECT * from country where countryId = ?";
        PreparedStatement getCountryStatement = dbConnect.prepareStatement(lookupSql);
        getCountryStatement.setInt(1, countryId);
        ResultSet rs = getCountryStatement.executeQuery();
        Country country = new Country();
        // Fetch each row from the result set
        if (rs.next()) {
          //Assuming you have a user object
          country.countryId = rs.getInt("countryId");
          country.country = rs.getString("country");
        } 
        return country;
    };
        
    public static ArrayList<Customer> retrieveCustomers() throws SQLException {
        ArrayList lc = new ArrayList<Customer>();
        Connection dbConnect = Dal.getConnectionfromPool();
        Statement getCustomersStatement = dbConnect.createStatement();
        ResultSet rs = getCustomersStatement.executeQuery("SELECT * from customer");

        // Fetch each row from the result set
        while (rs.next()) {
          int id = rs.getInt("customerId");
          String customer = rs.getString("customerName");

          //Assuming you have a user object
          Customer cust = new Customer();
          cust.CustomerId = id;
          cust.CustomerName = customer;

          lc.add(cust);
        }
        return lc;
    }
        
    public Boolean createCustomer(Customer customer, Address address, String city, String country, String currentUser) throws SQLException, InvalidCustomerException {
        try {
            //Invalid Data Guard
            if (customer.CustomerName == null || customer.CustomerName == "") throw new InvalidCustomerException("Customer Name Invalid");
            if (address.address1 == null || address.address1 == "") throw new InvalidCustomerException("Customer Address Line 1 Invalid");
            if (address.phone == null || address.phone == "") throw new InvalidCustomerException("Customer Phone Invalid");
            if (address.postalCode == null || address.postalCode == "") throw new InvalidCustomerException("Customer Address Postal Code Invalid");
            if (city == null || city == "") throw new InvalidCustomerException("Customer City Invalid");
            if (country == null || country == "") throw new InvalidCustomerException("Customer Country Invalid");
            
            Connection dbConnect = Dal.getConnectionfromPool();
            dbConnect.setAutoCommit(true);
            //create Country is if it does not exist.
            int countryId = createCountry(country, currentUser);

            //create City if it doesn't exist otherwise lookup.
            int cityId = createCity(city, countryId, currentUser);
            //create Address unless it already exists.
            int addressId = createAddress(address, cityId, currentUser);

            Statement getIdStatement = dbConnect.createStatement();
            ResultSet idMax = getIdStatement.executeQuery("SELECT MAX(customerId) as maxId FROM customer;");
            int currMaxId = 0;
            if (idMax.next()) {
                currMaxId = idMax.getInt("maxId");
            }

            String insertTableSQL = "INSERT INTO customer"
                    + "(customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "(?,?,?,?,?,?,?,?)";
            PreparedStatement insertCustStatement = dbConnect.prepareStatement(insertTableSQL);
            insertCustStatement.setInt(1, currMaxId + 1);
            insertCustStatement.setString(2, customer.CustomerName);
            insertCustStatement.setInt(3, addressId);
            insertCustStatement.setBoolean(4, true);
            insertCustStatement.setTimestamp(5, getCurrentTimeStamp());
            insertCustStatement.setString(6, currentUser);
            insertCustStatement.setTimestamp(7, getCurrentTimeStamp());
            insertCustStatement.setString(8, currentUser);
            // execute insert SQL stetement
            insertCustStatement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Exception in sql login: " + ex.getStackTrace().toString());
            return false;
        }
        return false;
    };  

    public final Boolean updateCustomer(Customer customer, Address address, String city, String country, String currentUser) throws SQLException, InvalidCustomerException {
        //Invalid Data Guard
        if (customer.CustomerName == null || customer.CustomerName == "") throw new InvalidCustomerException("Customer Name Invalid");
        if (address.address1 == null || address.address1 == "") throw new InvalidCustomerException("Customer Address Line 1 Invalid");
        if (address.phone == null || address.phone == "") throw new InvalidCustomerException("Customer Phone Invalid");
        if (address.postalCode == null || address.postalCode == "") throw new InvalidCustomerException("Customer Address Postal Code Invalid");
        if (city == null || city == "") throw new InvalidCustomerException("Customer City Invalid");
        if (country == null || country == "") throw new InvalidCustomerException("Customer Country Invalid");
        
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            dbConnect.setAutoCommit(true);
            //create Country is if it does not exist.
            int countryId = createCountry(country, currentUser);

            //create City if it doesn't exist otherwise lookup.     
            int cityId = createCity(city, countryId, currentUser);
            //create Address unless it already exists.
            int addressId = createAddress(address, cityId, currentUser);

            String insertTableSQL = "UPDATE customer set "
                    + "(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "(?,?,?,?,?,?,?,?) where customerId = ?";
            PreparedStatement insertCustStatement = dbConnect.prepareStatement(insertTableSQL);
            insertCustStatement.setString(1, customer.CustomerName);
            insertCustStatement.setInt(2, addressId);
            insertCustStatement.setBoolean(3, true);
            insertCustStatement.setTimestamp(4, getCurrentTimeStamp());
            insertCustStatement.setString(5, currentUser);
            insertCustStatement.setTimestamp(6, getCurrentTimeStamp());
            insertCustStatement.setString(7, currentUser);
            insertCustStatement.setInt(8, customer.CustomerId);
            // execute insert SQL stetement
            insertCustStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Exception in sql login: " + ex.getStackTrace().toString());
            return false;
        }
        return false;
    };  

    private int createCity(String city, int countryId, String currentUser) {
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            dbConnect.setAutoCommit(true);
            //Check if city exists
            int ctyId = getCity(city);
            if (ctyId < 0){
            //getMaxId
            Statement getIdStatement = dbConnect.createStatement();
            ResultSet idMax = getIdStatement.executeQuery("SELECT MAX(cityId) as maxId FROM city;");
            int currMaxId = 0;
            if (idMax.next()) {
                currMaxId = idMax.getInt("maxId");
            }    
            String insertTableSQL = "INSERT INTO city"
                    + "(cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "(?,?,?,?,?,?,?)";
            PreparedStatement insertCustStatement = dbConnect.prepareStatement(insertTableSQL);
            insertCustStatement.setInt(1, currMaxId + 1);
            insertCustStatement.setString(2, city);
            insertCustStatement.setInt(3, countryId);
            insertCustStatement.setTimestamp(4, getCurrentTimeStamp());
            insertCustStatement.setString(5, currentUser);
            insertCustStatement.setTimestamp(6, getCurrentTimeStamp());
            insertCustStatement.setString(7, currentUser);
            // execute insert SQL stetement
            insertCustStatement.executeUpdate();
            
            return currMaxId + 1;
            } else {
                return ctyId;
            }
        } catch (SQLException ex) {
            System.out.println("Exception in city insert: " + ex.getStackTrace().toString());
            return -1;
        }
    }
    
    private int getCity(String city) {
        int c = 0;
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            //Check if city exists

            String query = "select * from city where city = ?";
            PreparedStatement getCityStatement = dbConnect.prepareStatement(query);
            getCityStatement.setString(1, city);

            ResultSet rs = getCityStatement.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
                c = rs.getInt(1);
            } else {
                c = -1;
            }
            return c;
        } catch (SQLException ex) {
            System.out.println("Exception in sql get city : " + ex.getStackTrace().toString());
            return -1;
        }
    }

    private int createAddress(Address address, int cityId, String currentUser) {
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            dbConnect.setAutoCommit(true);
            //Check if city exists
            int addId = getAddress(address);
            if (addId < 0){
            //getMaxId
            Statement getIdStatement = dbConnect.createStatement();
            ResultSet idMax = getIdStatement.executeQuery("SELECT MAX(addressId) as maxId FROM address;");
            int currMaxId = 0;
            if (idMax.next()) {
                currMaxId = idMax.getInt("maxId");
            }    
            String insertTableSQL = "INSERT INTO address"
                    + "(addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement insertAddrStatement = dbConnect.prepareStatement(insertTableSQL);
            insertAddrStatement.setInt(1, currMaxId + 1);
            insertAddrStatement.setString(2, address.address1);
            insertAddrStatement.setString(3, address.address2);
            insertAddrStatement.setInt(4, cityId);
            insertAddrStatement.setString(5, address.postalCode);
            insertAddrStatement.setString(6, address.phone);
            insertAddrStatement.setTimestamp(7, getCurrentTimeStamp());
            insertAddrStatement.setString(8, currentUser);
            insertAddrStatement.setTimestamp(9, getCurrentTimeStamp());
            insertAddrStatement.setString(10, currentUser);
            // execute insert SQL stetement
            insertAddrStatement.executeUpdate();
            
            return currMaxId + 1;
            } else {
                return addId;
            }
        } catch (SQLException ex) {
            System.out.println("Exception in city insert: " + ex.getStackTrace().toString());
            return -1;
        }
    }

    private int createCountry(String country, String currentUser) {
        int currMaxId = 0;
                    
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            dbConnect.setAutoCommit(true);
            //Check if country exists
            int cntId = getCountry(country);
            if (cntId < 0){
            //getMaxId
            Statement getIdStatement = dbConnect.createStatement();
            ResultSet idMax = getIdStatement.executeQuery("SELECT MAX(countryId) as maxId FROM country;");
            if (idMax.next()) {
                currMaxId = idMax.getInt("maxId");
            }    
            
            
            String insertTableSQL = "INSERT INTO country"
                    + "(countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "(?,?,?,?,?,?)";
            PreparedStatement insertCustStatement = dbConnect.prepareStatement(insertTableSQL);
            insertCustStatement.setInt(1, currMaxId + 1);
            insertCustStatement.setString(2, country);
            insertCustStatement.setTimestamp(3, getCurrentTimeStamp());
            insertCustStatement.setString(4, currentUser);
            insertCustStatement.setTimestamp(5, getCurrentTimeStamp());
            insertCustStatement.setString(6, currentUser);
            // execute insert SQL stetement
            insertCustStatement.executeUpdate();
            } else {
                return cntId;
            }
        } catch (SQLException ex) {
            System.out.println("Exception in sql login: " + ex.getStackTrace().toString());
            return -1;
        }
        return currMaxId + 1;
    }

    private int getCountry(String country) {
        int c = 0;
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            //Check if country exists

            String query = "select * from country where country = ?";
            PreparedStatement getCountryStatement = dbConnect.prepareStatement(query);
            getCountryStatement.setString(1, country);

            ResultSet rs = getCountryStatement.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
                c = rs.getInt(1);
            } else {
                c = -1;
            }
            return c;
        } catch (SQLException ex) {
            System.out.println("Exception in sql get country : " + ex.getStackTrace().toString());
            return -1;
        }
    }
    
    public int createAppointment(int customerId, String apptType, String description, String location, String contact, String url, LocalDate apptDate, String apptDuration, String apptTime, TimeZone tz, String currentUser) throws OutsideBusinessHoursException {
        int currMaxId = 0;
        Date start = new Date();
        Date end = new Date();
                    
        //format calandar and start date
        try {
        String string = apptDate.getMonth().name() + " " + apptDate.getDayOfMonth() + ", " + apptDate.getYear() + " " + apptTime;
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy hh:mm", Locale.ENGLISH);
        format.setTimeZone(tz);
        start = format.parse(string);
        
        
        //Adding end date.
        final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
        long t = start.getTime();
        end = new Date(t + (Integer.parseInt(apptDuration) * ONE_MINUTE_IN_MILLIS));
        
        //Check for business hours
        Utility.outsideBusinessHours(start, end);
        
        } catch (ParseException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            dbConnect.setAutoCommit(true);
            //Check if appointment exists
            //int apptId = getAppointment(appt, currentUser);
            //if (apptId < 0){
            //getMaxId
            Statement getIdStatement = dbConnect.createStatement();
            ResultSet idMax = getIdStatement.executeQuery("SELECT MAX(appointmentId) as maxId FROM appointment;");
            if (idMax.next()) {
                currMaxId = idMax.getInt("maxId");
            }    
            
            String insertTableSQL = "INSERT INTO appointment"
                    + "(appointmentId, customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES"
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement insertCustStatement = dbConnect.prepareStatement(insertTableSQL);
            insertCustStatement.setInt(1, currMaxId + 1);
            insertCustStatement.setInt(2, customerId);
            insertCustStatement.setString(3, apptType);
            insertCustStatement.setString(4, description);
            insertCustStatement.setString(5, location);
            insertCustStatement.setString(6, contact);
            insertCustStatement.setString(7, url);
            insertCustStatement.setTimestamp(8, new Timestamp(start.getTime()));
            insertCustStatement.setTimestamp(9, new Timestamp(end.getTime()));
            insertCustStatement.setTimestamp(10, getCurrentTimeStamp());
            insertCustStatement.setString(11, currentUser);
            insertCustStatement.setTimestamp(12, getCurrentTimeStamp());
            insertCustStatement.setString(13, currentUser);
            // execute insert SQL stetement
            insertCustStatement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Exception in sql login: " + ex.getStackTrace().toString());
            return -1;
        }
        return currMaxId + 1;
    }
    
    private int getAppointment(Appointment appt, String currentUser) {
        int a = 0;
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            //Check if country exists

            String query = "select * from appointment where appointmentId = ?";
            PreparedStatement getApptStatement = dbConnect.prepareStatement(query);
            getApptStatement.setInt(1, appt.appointmentId);

            ResultSet rs = getApptStatement.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
                a = rs.getInt(1);
            } else {
                a = -1;
            }
            return a;
        } catch (SQLException ex) {
            System.out.println("Exception in sql get country : " + ex.getStackTrace().toString());
            return -1;
        }
    }
    
    public void updateAppointment(int apptId, int customerId, String apptType, String description, String location, String contact, String url, LocalDate apptDate, String apptDuration, String apptTime, TimeZone tz, String currentUser) throws OutsideBusinessHoursException {
        int currMaxId = 0;
        Date start = new Date();
        Date end = new Date();
                    
        //format calandar and start date
        try {
        String string = apptDate.getMonth().name() + " " + apptDate.getDayOfMonth() + ", " + apptDate.getYear() + " " + apptTime;
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy hh:mm", Locale.ENGLISH);
        format.setTimeZone(tz);
        start = format.parse(string);
        
        //Adding end date.
        final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
        long t = start.getTime();
        end = new Date(t + (Integer.parseInt(apptDuration) * ONE_MINUTE_IN_MILLIS));
        
        //Check for business hours
        Utility.outsideBusinessHours(start, end);
        //Check for overlapping appts
        
        
        } catch (ParseException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            dbConnect.setAutoCommit(true);
 
            
            String insertTableSQL = "update appointment set "
                    + "customerId = ?, title = ?, description = ?, location = ?, contact = ?, url = ?, start = ?, end = ?, createDate = ?, createdBy = ?, lastUpdate = ?, lastUpdateBy = ? "
                    + "WHERE appointmentId = ?;";
            PreparedStatement insertCustStatement = dbConnect.prepareStatement(insertTableSQL);
            insertCustStatement.setInt(13, apptId);
            insertCustStatement.setInt(1, customerId);
            insertCustStatement.setString(2, apptType);
            insertCustStatement.setString(3, description);
            insertCustStatement.setString(4, location);
            insertCustStatement.setString(5, contact);
            insertCustStatement.setString(6, url);
            insertCustStatement.setTimestamp(7, new Timestamp(start.getTime()));
            insertCustStatement.setTimestamp(8, new Timestamp(end.getTime()));
            insertCustStatement.setTimestamp(9, getCurrentTimeStamp());
            insertCustStatement.setString(10, currentUser);
            insertCustStatement.setTimestamp(11, getCurrentTimeStamp());
            insertCustStatement.setString(12, currentUser);
            // execute insert SQL stetement
            insertCustStatement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Exception in sql login: " + ex.getStackTrace().toString());
        }
    }
    
    public Appointment getAppointment(int apptId) {
        //int a = 0;
        Appointment appointment = new Appointment();
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            //Check if country exists

            String query = "select * from appointment where appointmentId = ?";
            PreparedStatement getApptStatement = dbConnect.prepareStatement(query);
            getApptStatement.setInt(1, apptId);

            ResultSet rs = getApptStatement.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
                appointment.appointmentId = apptId;
                appointment.customerId = rs.getInt(2);
                appointment.title = rs.getString(3);
                appointment.description = rs.getString(4);
                appointment.location = rs.getString(5);
                appointment.contact = rs.getString(6);
                appointment.url = rs.getString(7);
                appointment.start = rs.getTimestamp(8);
                appointment.end = rs.getTimestamp(9);
                appointment.createDate = rs.getTimestamp(10);
                appointment.createdBy = rs.getString(11);
                appointment.lastUpdate = rs.getTimestamp(12);
                appointment.lastUpdateBy = rs.getString(13);
            } else {

            }
            return appointment;
        } catch (SQLException ex) {
            System.out.println("Exception in sql get country : " + ex.getStackTrace().toString());
            return appointment;
        }
    }

    public ArrayList<Appointment> getAllAppointmentsForUser(String currentUser) {
        ArrayList<Appointment> lc = new ArrayList<>();
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            String query = "select * from appointment where lastUpdateBy = ?";
            PreparedStatement getApptStatement = dbConnect.prepareStatement(query);
            getApptStatement.setString(1, currentUser);
            ResultSet rs = getApptStatement.executeQuery();
            // Fetch each row from the result set
            while (rs.next()) {
                    Appointment appt = new Appointment();
                    appt.appointmentId = rs.getInt("appointmentId");
                    appt.contact = rs.getString("contact");
                    appt.createDate = rs.getTimestamp("createDate");
                    appt.createdBy = rs.getString("createdBy");
                    appt.customerId = rs.getInt("customerId");
                    appt.description = rs.getString("description");
                    appt.start = rs.getTimestamp("start");
                    appt.end = rs.getTimestamp("end");
                    appt.lastUpdate = rs.getTimestamp("lastUpdate");
                    appt.lastUpdateBy = rs.getString("lastUpdateBy");
                    appt.location = rs.getString("location");
                    appt.title = rs.getString("title");
                    appt.url = rs.getString("url");
                    lc.add(appt);
                } 
            } catch (SQLException ex) {
                Logger.getLogger(Dal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lc;
    }

    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> lc = new ArrayList<>();
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            String query = "select * from appointment";
            PreparedStatement getApptStatement = dbConnect.prepareStatement(query);
            ResultSet rs = getApptStatement.executeQuery();
            // Fetch each row from the result set
            while (rs.next()) {
                    Appointment appt = new Appointment();
                    appt.appointmentId = rs.getInt("appointmentId");
                    appt.contact = rs.getString("contact");
                    appt.createDate = rs.getTimestamp("createDate");
                    appt.createdBy = rs.getString("createdBy");
                    appt.customerId = rs.getInt("customerId");
                    appt.description = rs.getString("description");
                    appt.start = rs.getTimestamp("start");
                    appt.end = rs.getTimestamp("end");
                    appt.lastUpdate = rs.getTimestamp("lastUpdate");
                    appt.lastUpdateBy = rs.getString("lastUpdateBy");
                    appt.location = rs.getString("location");
                    appt.title = rs.getString("title");
                    appt.url = rs.getString("url");
                    lc.add(appt);
                } 
            } catch (SQLException ex) {
                Logger.getLogger(Dal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lc;
    }

    private static Timestamp getCurrentTimeStamp() {
        return new java.sql.Timestamp(new java.util.Date().getTime());
    }

    private int getAddress(Address address) {
        int c = 0;
        try {
            Connection dbConnect = Dal.getConnectionfromPool();
            //Check if address exists
            String query = "select * from address where address = ? and address2 = ? and cityId = ? and postalCode = ? and phone = ?";
            PreparedStatement getCountryStatement = dbConnect.prepareStatement(query);
            getCountryStatement.setString(1, address.address1);
            getCountryStatement.setString(2, address.address2);
            getCountryStatement.setInt(3, address.cityId);
            getCountryStatement.setString(4, address.postalCode);
            getCountryStatement.setString(5, address.phone);
            ResultSet rs = getCountryStatement.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
                c = rs.getInt(1);
            } else {
                c = -1;
            }
            return c;
        } catch (SQLException ex) {
            System.out.println("Exception in sql get address : " + ex.getMessage());
            return -1;
        }
    }
}
