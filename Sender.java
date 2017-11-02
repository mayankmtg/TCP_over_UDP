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
import java.net.SocketTimeoutException;
import java.util.ArrayList;

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
        ArrayList<Packet> packetList=new ArrayList<Packet>();
        for(i=0; i<n;i+=1021) {
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
            packetList.add(p);
//            p.sendPacket(ds, ip, port);
//            boolean ackReceived=false;
//            while(!ackReceived){
//                AckPacket ackPack=new AckPacket();
//                try{
//                    ackReceived=ackPack.receiveAck(ds);
//                }
//                catch(SocketTimeoutException e){
//                    System.out.println("Socket timed out waiting for an ack");
//                    ackReceived = false;
//                }
//                
//                if(ackReceived && ackPack.getAckInt()==seqNum){
//                    System.out.println("Ack Received: "+ackPack.getAckInt());
//                    break;
//                }
//                else{
//                    if(ackReceived){
//                        System.out.println("Ack Received: "+ackPack.getAckInt());
//                    }
//                    else{
//                        System.err.println("Time Out");
//                    }
//                    p.sendPacket(ds, ip, port);
//                    ackReceived=false;
//                }
//            }
        }
        int windowSize=5;
        ArrayList<Integer> ackRegister=new ArrayList<Integer>();
        int start=0;
        boolean exitCondition=false;
        boolean cumAck=true;
        while(!exitCondition){
            cumAck=true;
            for(i=0;i<windowSize;i++){
                if(start+i>=packetList.size()){
                    exitCondition=true;
                    break;
                }
                Packet p=packetList.get(start+i);
                p.sendPacket(ds, ip, port);
                
                boolean ackReceived=false;
                AckPacket ackPack=new AckPacket();
                try{
                    ackReceived=ackPack.receiveAck(ds);
                }
                catch(SocketTimeoutException e){
                    System.out.println("Socket timed out waiting for an ack");
                    ackReceived = false;
                }

                if(ackReceived && ackPack.getAckInt()>=p.seqNum){
                    System.out.println("Ack Received: "+ackPack.getAckInt());
                }
                else{
                    if(ackReceived){
                        System.out.println("Ack Received: "+ackPack.getAckInt());
                    }
                    else{
                        System.err.println("Time Out");
                    }
                    //retransmission
//                    p.sendPacket(ds, ip, port);
                    ackReceived=false;
                }
                
                if(!ackReceived){
                    cumAck=false;
                }
            }
            if(cumAck){
                start=start+windowSize;
            }
        }
            
        ds.close();
        System.out.println("File " + fileName + " has been sent");
    }
    
}
