/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

/**
 *
 * @author vanab
 */
public interface IHistory {
    String[] getHistory (String name) throws EmployeeException;
    String[] getHistory (int code) throws EmployeeException;
    boolean login (String user, String password);
    boolean logout ();

}
