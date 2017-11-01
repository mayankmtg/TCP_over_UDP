package tcp_over_udp;

import java.io.File;
import java.io.FileOutputStream;
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
public class Receiver {
    static int c_port;
    static InetAddress c_ip=null;
    public static void main(String args[]) throws IOException{
        Scanner input = new Scanner(System.in);
        System.out.println("Server Port:");
        int port_addr=input.nextInt();
        DatagramSocket ds = new DatagramSocket(port_addr);
        System.out.println("File Name:");
        String fileName=input.next();
        InetAddress address;
        File file = new File(fileName);
        FileOutputStream os = new FileOutputStream(file);
        
        boolean lastMessage = false;
        
        int seqNum=0;
        int lastSeqNum=0;
        boolean false_flag=true;
        while(!lastMessage){
            byte[] message = new byte[1024];
            byte[] fileArray = new byte[1021];
            
            DatagramPacket receivedPacket = new DatagramPacket(message, message.length);
            ds.setSoTimeout(0);
            ds.receive(receivedPacket);
            c_ip = receivedPacket.getAddress();
            c_port = receivedPacket.getPort();
            message = receivedPacket.getData();
            AckPacket ackPack=new AckPacket();
            Packet p=new Packet(message);
            seqNum=p.seqNum;
            if(seqNum==125 && false_flag){
                seqNum--;
                false_flag=false;
            }
            if(seqNum==lastSeqNum+1){
                lastSeqNum=seqNum;
                for (int i=0; i < 1021 ; i++) {
                    fileArray[i] = p.getPayload(i+3);
                }
                os.write(fileArray);
            }
            ackPack.setAckNum(lastSeqNum);
            ackPack.setAckBytes();
            ackPack.sendAck(ds, c_ip, c_port);
                
            
            if(p.lastFlag==true){
                os.close();
                ds.close();
                lastMessage = false;
                break;
            }
        }
        ds.close();
        System.out.println("File " + fileName + " has been received.");
        
        
//        
//        Runnable read=new Runnable(){
//            @Override
//            public void run() {
//                System.out.println("Server Listening");
//                DatagramPacket dp = null;
//                byte[] receive = new byte[1024];
//                while(true){
//                    dp = new DatagramPacket(receive, receive.length);
//                    try {
//                        ds.receive(dp);
//                    } catch (IOException ex) {
//                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    c_ip=dp.getAddress();
//                    c_port=dp.getPort();
//                    
//                    System.out.println("Client:-" + data(receive));
//                    if(data(receive).toString().equals("exit")){
//                        System.out.println("Client sent exit.....EXITING");
//                        break;
//                    }
//                    receive = new byte[65535];
//                }
//            }
//            
//        };
//        
//        Runnable write=new Runnable(){
//            @Override
//            public void run() {
//                System.out.println("Type Message");
//                DatagramPacket dp = null;
//                while(true){
//                    String inp=input.nextLine();
//                    byte[] send = inp.getBytes();
//                    dp = new DatagramPacket(send, send.length, c_ip,c_port);
//                    try {
//                        ds.send(dp);
//                    } catch (IOException ex) {
//                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//                
//                
//            }
//            
//        };
//        Thread recvMessage=new Thread(read);
//        Thread sendMessage=new Thread(write);
//        recvMessage.start();
//        sendMessage.start();
        
        
    }
    
//    public static StringBuilder data(byte[] a){
//        if(a==null){
//            return null;
//        }
//        StringBuilder ret = new StringBuilder();
//        int i = 0;
//        while(a[i]!=0){
//            ret.append((char)a[i]);
//            i++;
//        }
//        return ret;
//    }
}
