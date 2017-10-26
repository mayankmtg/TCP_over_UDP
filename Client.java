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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mayank
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        Scanner input = new Scanner(System.in);
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Port Number:");
        int port=input.nextInt();
        
        Runnable read = new Runnable(){
            @Override
            public void run() {
                System.out.println("Type Message:");
                byte byte_buffer_receive[] = null;
                while(true){
                    byte_buffer_receive=new byte[1024];
                    DatagramPacket dp1=new DatagramPacket(byte_buffer_receive, byte_buffer_receive.length);
                    try {
                        ds.receive(dp1);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String received_data=new String(dp1.getData());
                    System.out.println(received_data);
                }
                
            }
            
        };
        Runnable write=new Runnable(){
            @Override
            public void run() {
                System.out.println("Type Message:");
                byte byte_buffer_send[] = null;
                while(true){
                    String inp=input.nextLine();
                    byte_buffer_send = inp.getBytes();
                    DatagramPacket dp=new DatagramPacket(byte_buffer_send, byte_buffer_send.length, ip, port);
                    try {
                        ds.send(dp);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (inp.equals("exit")){
                        break;
                    }
                    
                }
                
                
                        
            }
            
        };
    }
}
