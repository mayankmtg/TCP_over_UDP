/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp_over_udp;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.InputStream;

import java.net.DatagramSocket;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mayank
 */
public class Sender {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        Scanner input = new Scanner(System.in);
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Port Number:");
        int port=input.nextInt();
        System.out.println("File Name:");
        String fileName=input.next();
        System.out.println("Sending the file");
        
        File file = new File(fileName); 
        
        InputStream is = new FileInputStream(file);
        byte[] fileArray = new byte[(int)file.length()];
        is.read(fileArray);
        int seqNum = 0;
        boolean messageFlag = false;
        int i;
        int n=fileArray.length;
        for(i=0; i<n;i=i+1021) {
            seqNum++;
            Packet p=new Packet(seqNum);
            int nextStart=i+1021;
            int j;
            if(nextStart >= n){
                messageFlag = true;
                p.setFlag(messageFlag);
                for(j=0;j<(n - i);j++){
                    p.setPayload(j+3, fileArray[i+j]);
                }
            }
            else{
                messageFlag = false;
                p.setFlag(messageFlag);
                for(j=0;j<=1020;j++) {
                    p.setPayload(j+3, fileArray[i+j]);
                }
            }
            p.sendPacket(ds, ip, port);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ds.close();
        System.out.println("File " + fileName + " has been sent");
//        
//        Runnable read = new Runnable(){
//            @Override
//            public void run() {
//                System.out.println("Client Listening");
//                byte byte_buffer_receive[] = null;
//                while(true){
//                    byte_buffer_receive=new byte[1024];
//                    DatagramPacket dp1=new DatagramPacket(byte_buffer_receive, byte_buffer_receive.length);
//                    try {
//                        ds.receive(dp1);
//                    } catch (IOException ex) {
//                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    String received_data=new String(dp1.getData());
//                    System.out.println(received_data);
//                }
//                
//            }
//            
//        };
//        Runnable write=new Runnable(){
//            @Override
//            public void run() {
//                System.out.println("Type Message:");
//                byte byte_buffer_send[] = null;
//                while(true){
//                    String inp=input.nextLine();
//                    byte_buffer_send = inp.getBytes();
//                    DatagramPacket dp=new DatagramPacket(byte_buffer_send, byte_buffer_send.length, ip, port);
//                    try {
//                        ds.send(dp);
//                    } catch (IOException ex) {
//                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    if (inp.equals("exit")){
//                        break;
//                    }
//                    
//                }       
//            }
//            
//        };
//        Thread recvMessage=new Thread(read);
//        Thread sendMessage=new Thread(write);
//        recvMessage.start();
//        sendMessage.start();
    }
    
}
