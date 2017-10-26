/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp_over_udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

import java.net.DatagramSocket;

import java.util.Scanner;

/**
 *
 * @author mayank
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        Scanner sc = new Scanner(System.in);
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        byte buf[] = null;
        
        while(true){
            String inp = sc.nextLine();
            buf = inp.getBytes();
            DatagramPacket DpSend=new DatagramPacket(buf, buf.length, ip, 1234);
            ds.send(DpSend);
            if (inp.equals("bye")){
                break;
            }
        }
    }
}
