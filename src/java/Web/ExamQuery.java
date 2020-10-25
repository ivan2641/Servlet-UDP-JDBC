/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author vanab
 */
public class ExamQuery implements Serializable {

    private String login;
    private String password;
    private String name;
    String sessionId;

    private int code;
    private InetAddress address;
    private transient int port;
    Command comm;

    public ExamQuery(String login, String password, Command comm, String sessionId) {
        this.login = login;
        this.password = password;
        this.comm = comm;
        this.sessionId = sessionId;
    }

    public ExamQuery(int code, Command comm, String sessionId) {
        this.code = code;
        this.comm = comm;
        this.sessionId = sessionId;
    }

    public ExamQuery(String name, Command comm, String sessionId) {
        this.name = name;
        this.comm = comm;
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

//    public ExamQuery getHistory(String name) throws EmployeeException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public ExamQuery getHistory(int code) throws EmployeeException {
//        return new ExamQuery(code, Command.GETBYCODE);
//    }
//
//    public ExamQuery login(String user, String password) {
//        return new ExamQuery(user, password, Command.LOGIN);
//    }
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Command getComm() {
        return comm;
    }

    public ExamQuery logout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
