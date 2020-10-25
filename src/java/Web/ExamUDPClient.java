/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import Server.ExamServerUDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author vanab
 */
public class ExamUDPClient implements AutoCloseable {

    private DatagramSocket socket;
    private DatagramPacket packet;
    public static final int BUFFER_SIZE = 2048;

    public ExamUDPClient() throws SocketException {
        socket = new DatagramSocket();
        packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
    }

    @Override
    public void close() throws Exception {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean sendMessage(ExamQuery query) {
        try {
            byte[] buf = ExamSerializer.serialize(query);
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    InetAddress.getLoopbackAddress(), ExamServerUDP.SERVER_PORT);
            socket.send(packet);
            return true;
        } catch (IOException ex) {
            System.out.println("Error " + ex.getMessage());
            return false;
        }
    }

    public ExamAnswer getMessage() throws ClassNotFoundException{
        try {
            socket.receive(packet);
            ExamAnswer answer = (ExamAnswer) ExamSerializer.deserialize(packet.getData());
            return answer;
        } catch (IOException e) {
            System.out.println("Error receiving message " + e.getMessage());
            return null;
        }
    }

    public boolean logout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
