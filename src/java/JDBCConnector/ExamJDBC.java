/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JDBCConnector;

import Web.EmployeeException;
import Web.IHistory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author vanab
 */
public class ExamJDBC implements IHistory {

    private static Properties property;
    private static String URL;
    private static Connection conn;
    private static Statement st;
    private ArrayList<String> listHistory;
    private static ExamJDBC instance;
    private static Object syncRoot = new Object();

    private static final String INIFILE = "C:\\source\\Java\\ExamTest\\Exam\\sql.ini";
    private static PreparedStatement FindUser;
    private static PreparedStatement FindHistoryByCode;
    private static PreparedStatement FindHistoryByName;

    public static ExamJDBC getInstance() throws IOException {
        synchronized (syncRoot) {
            if (instance == null) {
                instance = new ExamJDBC();
            }
        }
        return instance;
    }

    private ExamJDBC() throws IOException {
        property = new Properties();
        try {
            property.load(new FileInputStream(INIFILE));
            URL = property.getProperty("URL");
            System.out.println(URL);
            conn = DriverManager.getConnection(URL);
            st = conn.createStatement();
            ExamJDBC.FindUser = conn.prepareStatement("SELECT * FROM employees WHERE login = ? AND psw = ?");
            ExamJDBC.FindHistoryByCode = conn.prepareStatement("SELECT * FROM employeehistory WHERE code = ?");
            ExamJDBC.FindHistoryByName = conn.prepareStatement("SELECT * FROM employeehistory WHERE code IN"
                    + "(SELECT code FROM employees WHERE last_name = ?)");
        } catch (FileNotFoundException | SQLException ex) {
            System.err.println(">>>>> Error " + ex.getMessage());
        }
    }

    @Override
    public synchronized String[] getHistory(String name) throws EmployeeException {
        try {
            listHistory = new ArrayList<>();
            FindHistoryByName.setString(1, name);
            ResultSet resHistory = FindHistoryByName.executeQuery();
            while (resHistory.next()) {
                listHistory.add(resHistory.getInt("id") + " "
                        + resHistory.getString("position") + " "
                        + resHistory.getInt("manager") + " "
                        + resHistory.getDate("hire") + " "
                        + resHistory.getDate("dismiss") + " "
                        + resHistory.getInt("code"));
            }
        } catch (SQLException ex) {
            System.err.println("Error in printing protocol " + ex.getMessage());
        }
        String[] arrHistory = new String[listHistory.size()];
        return listHistory.toArray(arrHistory);
    }

    @Override
    public synchronized String[] getHistory(int code) throws EmployeeException {
        try {
            listHistory = new ArrayList<>();
            FindHistoryByCode.setInt(1, code);
            ResultSet resHistory = FindHistoryByCode.executeQuery();
            while (resHistory.next()) {
                listHistory.add(resHistory.getInt("id") + " "
                        + resHistory.getString("position") + " "
                        + resHistory.getInt("manager") + " "
                        + resHistory.getDate("hire") + " "
                        + resHistory.getDate("dismiss") + " "
                        + resHistory.getInt("code"));
            }
        } catch (SQLException ex) {
            System.err.println("Error in printing protocol " + ex.getMessage());
        }
        String[] arrHistory = new String[listHistory.size()];
        return listHistory.toArray(arrHistory);
    }

    @Override
    public synchronized boolean login(String user, String password) {
        try {
            FindUser.setString(1, user);
            FindUser.setString(2, password);
            ResultSet resLogin = FindUser.executeQuery();
            if (resLogin.next()) {
                System.out.println(resLogin.getString("login") + " " + resLogin.getString("psw"));
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("Error in printing protocol " + ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean logout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
