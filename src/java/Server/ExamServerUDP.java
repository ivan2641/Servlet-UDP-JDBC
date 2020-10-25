/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import JDBCConnector.ExamJDBC;
import Web.EmployeeException;
import Web.ExamAnswer;
import Web.ExamQuery;
import Web.ExamSerializer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 *
 * @author vanab
 */
public class ExamServerUDP implements AutoCloseable {

    public static final int SERVER_PORT = 9999;
    public static final int BUFFER_SIZE = 2048;
    private DatagramSocket socket;
    private DatagramPacket packet;
    ExamJDBC serverDB;

    public ExamServerUDP() throws SocketException, IOException {
        socket = new DatagramSocket(SERVER_PORT);
        packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        serverDB = ExamJDBC.getInstance();
    }

    public static void main(String[] args) throws SocketException, Exception {
        System.out.println("Server UDP started");
        ExamServerUDP server = new ExamServerUDP();
        server.Analysis();
        System.out.println("Server UDP finished");
    }

    private ExamQuery getMessage() throws Exception {
        try {
            socket.receive(packet);
            ExamQuery query = (ExamQuery) ExamSerializer.deserialize(packet.getData());
            return query;
        } catch (IOException e) {
            System.out.println("Error receiving message " + e.getMessage());
            return null;
        }
    }

    private void Analysis() throws Exception {
        while (true) {
            ExamQuery query = getMessage();
            if (null != query.getComm()) {
                switch (query.getComm()) {
                    case LOGIN:
                        commandLogin(query, packet.getAddress(), packet.getPort());
                        break;
                    case GETBYNAME:
                        getHistoryByName(query, packet.getAddress(), packet.getPort());
                        break;
                    case GETBYCODE:
                        getHistoryByCode(query, packet.getAddress(), packet.getPort());
                        break;
                    default:
                        throw new IllegalArgumentException("invalid command argument");
                }
            } else {
                System.out.println("Query is null");
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private void commandLogin(ExamQuery query, InetAddress ip, int port) {
        ExamAnswer answer = new ExamAnswer();
        if (serverDB.login(query.getLogin(), query.getPassword())) {
            answer.setResult(true);
        } else {
            answer.setResult(false);
        }
        answer.setSessionId(query.getSessionId());
        try {
            byte[] buf = ExamSerializer.serialize(answer);
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    ip, port);
            socket.send(packet);
        } catch (IOException ex) {
            System.out.println("Error commandLogin " + ex.getMessage());
        }

    }

    private void getHistoryByCode(ExamQuery query, InetAddress ip, int port) throws EmployeeException {
        ExamAnswer answer = new ExamAnswer();
        answer.setArrHistory(serverDB.getHistory(query.getCode()));
        if (answer.getArrHistory() != null) {
            answer.setResult(true);
            answer.setSessionId(query.getSessionId());
        }
        System.out.println(Arrays.toString(answer.getArrHistory()));
        try {
            byte[] buf = ExamSerializer.serialize(answer);
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    ip, port);
            socket.send(packet);

        } catch (IOException ex) {
            System.out.println("Error commandLogin" + ex.getMessage());
        }
    }

    private void getHistoryByName(ExamQuery query, InetAddress ip, int port) throws EmployeeException {
        ExamAnswer answer = new ExamAnswer();
        answer.setArrHistory(serverDB.getHistory(query.getName()));
        if (answer.getArrHistory() != null) {
            answer.setResult(true);
        } else {
            answer.setResult(false);
        }
        answer.setSessionId(query.getSessionId());
        System.out.println(Arrays.toString(answer.getArrHistory()));
        try {
            byte[] buf = ExamSerializer.serialize(answer);
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    ip, port);
            socket.send(packet);

        } catch (IOException ex) {
            System.out.println("Error commandLogin" + ex.getMessage());
        }
    }
}
