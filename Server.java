package tcp_over_udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    static int c_port;
    static InetAddress c_ip=null;
    public static void main(String args[]) throws IOException{
        DatagramSocket ds = new DatagramSocket(1234);
        Scanner input = new Scanner(System.in);
        
        
        Runnable read=new Runnable(){
            @Override
            public void run() {
                System.out.println("Server Listening");
                DatagramPacket dp = null;
                byte[] receive = new byte[65535];
                while(true){
                    dp = new DatagramPacket(receive, receive.length);
                    try {
                        ds.receive(dp);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    c_ip=dp.getAddress();
                    c_port=dp.getPort();
                    
                    System.out.println("Client:-" + data(receive));
                    if(data(receive).toString().equals("exit")){
                        System.out.println("Client sent exit.....EXITING");
                        break;
                    }
                    receive = new byte[65535];
                }
            }
            
        };
        
        Runnable write=new Runnable(){
            @Override
            public void run() {
                System.out.println("Type");
                DatagramPacket dp = null;
                while(true){
                    String inp=input.nextLine();
                    byte[] send = inp.getBytes();
                    dp = new DatagramPacket(send, send.length, c_ip,c_port);
                    try {
                        ds.send(dp);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                
            }
            
        };
        Thread recvMessage=new Thread(read);
        Thread sendMessage=new Thread(write);
        recvMessage.start();
        sendMessage.start();
        
        
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
