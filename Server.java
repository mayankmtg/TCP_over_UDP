package tcp_over_udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mayank
 */
public class Server {
    public static void main(String args[]) throws IOException{
        DatagramSocket ds = new DatagramSocket(1234);
        byte[] receive = new byte[65535];
        DatagramPacket DpReceive = null;
        while(true){
            DpReceive = new DatagramPacket(receive, receive.length);
            ds.receive(DpReceive);
            System.out.println("Client:-" + data(receive));
            if (data(receive).toString().equals("exit")){
                System.out.println("Client sent exit.....EXITING");
                break;
            }
            receive = new byte[65535];
        }
    }
    
    public static StringBuilder data(byte[] a){
        if(a==null){
            return null;
        }
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while(a[i]!=0){
            ret.append((char)a[i]);
            i++;
        }
        return ret;
    }
}
