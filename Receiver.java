package tcp_over_udp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
        System.out.println("Waiting for Connection");
        SynPack syn=new SynPack();
        ds.setSoTimeout(0);
        DatagramPacket type=syn.receiveSyn(ds);
        c_ip=type.getAddress();
        c_port=type.getPort();
        System.out.println("SYN Recieved");
        SynPack synAck=new SynPack(1);
        System.out.println("Sending SYN-ACK");
        synAck.sendSyn(ds, c_ip, c_port);
        SynPack ackSyn=new SynPack();
        ds.setSoTimeout(0);
        DatagramPacket ack_type=syn.receiveSyn(ds);
        System.out.println("ACK Recieved");
        
        File file = new File(fileName);
        FileOutputStream os = new FileOutputStream(file);
        
        boolean lastMessage = false;
        
        int seqNum=0;
        int lastSeqNum=0;
        boolean file_flag=false;
        boolean false_flag=true;
        while(!lastMessage){
            byte[] message = new byte[1024];
            byte[] fileArray = new byte[1021];
            
            DatagramPacket receivedPacket = new DatagramPacket(message, message.length);
            ds.setSoTimeout(0);
            ds.receive(receivedPacket);
//            c_ip = receivedPacket.getAddress();
//            c_port = receivedPacket.getPort();
            message = receivedPacket.getData();
            AckPacket ackPack=new AckPacket();
            Packet p=new Packet(message);
            seqNum=p.seqNum;
            if(seqNum==123 && false_flag){
                seqNum=2;
                false_flag=false;
//                continue;
            }
            if(seqNum==lastSeqNum+1){
                lastSeqNum=seqNum;
                for (int i=0; i < 1021 ; i++) {
                    fileArray[i] = p.getPayload(i+3);
                }
                os.write(fileArray);
                file_flag=true;
            }
            else{
                file_flag=false;
            }
            ackPack.setAckNum(lastSeqNum);
            ackPack.setAckBytes();
            ackPack.sendAck(ds, c_ip, c_port);
            
            if(p.lastFlag==true && file_flag){
                os.close();
                ds.close();
                lastMessage = false;
                break;
            }
        }
        ds.close();
        System.out.println("File " + fileName + " has been received.");
    }
}
