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
        System.out.println("Making Connection...");
        SynPack sp=new SynPack(0);
        System.out.println("Sending SYN");
        sp.sendSyn(ds, ip, port);
        SynPack synAckPack=new SynPack();
        ds.setSoTimeout(0);
        DatagramPacket type=synAckPack.receiveSyn(ds);

        System.out.println("SYN-ACK Received\nSending ACK");
        SynPack ackSyn=new SynPack(2);
        ackSyn.sendSyn(ds, ip, port);
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
        }
        int windowSize=1;
        ArrayList<Integer> ackRegister=new ArrayList<Integer>();
        int start=0;
        boolean exitCondition=false;
        while(!exitCondition){
            int maxAck=0;
            int lastSeq=0;
            boolean timeout=false;
            boolean consecAck=false;
            int fastRetransmitCounter=0;
            int currentAck=0;
            int prevAck=0;
            System.out.println("Window Size: "+windowSize);
            for(i=0;i<windowSize;i++){
                if(start+i>=packetList.size()){
                    exitCondition=true;
                    break;
                }
                Packet p=packetList.get(start+i);
//                lastSeq=p.seqNum;
                p.sendPacket(ds, ip, port);
            }
            for(i=0;i<windowSize;i++){
                prevAck=currentAck;
                boolean ackReceived=false;
                AckPacket ackPack=new AckPacket();
                if(start+i>=packetList.size()){
                    exitCondition=true;
                    break;
                }
                Packet p=packetList.get(start+i);
                lastSeq=p.seqNum;
                try{
                    ackReceived=ackPack.receiveAck(ds);
                }
                catch(SocketTimeoutException e){
                    System.out.println("Socket timed out waiting for an ack "+p.seqNum);
                    ackReceived = false;
                }
                if(ackReceived){
                    currentAck=ackPack.getAckInt();
                    if(currentAck>maxAck){
                        maxAck=ackPack.getAckInt();
                    }
                    
                    if(currentAck==prevAck){
                        fastRetransmitCounter++;
                    }
                    
                    if(currentAck>=p.seqNum){
                        System.out.println("Ack Received: "+currentAck);
                    }
                    else{
                        System.out.println("Ack Received: "+currentAck);
                        ackReceived=false;
                    }
                    
                    if(fastRetransmitCounter==2){
                        consecAck=true;
                        System.out.println("Fast Retransmit");
                    }
                }
                else{
                    timeout=true;
                    System.err.println("Time Out");
                }
            }
            if(lastSeq==maxAck){
                windowSize++;
            }
            else{
                if(timeout){
                    windowSize=1;
                }
                else if(consecAck){
                    //fast Retransmit
                    windowSize=windowSize/2;
                    if(windowSize<1){
                        windowSize=1;
                    }
                }
            }
            start=maxAck;
        }
            
        ds.close();
        System.out.println("File " + fileName + " has been sent");
    }
}
