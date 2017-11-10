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
public class Sender_2015056 {

    /**
     * @param args the command line arguments
     */
    static final int packetSize=1024;
    static final double lossProb=0.1;
    static int bufferSize=75;
    public static void main(String[] args) throws IOException{
        Scanner input = new Scanner(System.in);
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Port Number:");
        int port=input.nextInt();
        System.out.println("File Name:");
        String fileName=input.next();
        System.out.println("Making Connection...");
        SynPack_2015056 sp=new SynPack_2015056(0);
        System.out.println("Sending SYN");
        sp.sendSyn(ds, ip, port);
        SynPack_2015056 synAckPack=new SynPack_2015056();
        ds.setSoTimeout(0);
        DatagramPacket type=synAckPack.receiveSyn(ds);

        System.out.println("SYN-ACK Received\nSending ACK");
        SynPack_2015056 ackSyn=new SynPack_2015056(2);
        ackSyn.sendSyn(ds, ip, port);
        System.out.println("Sending the file");
        
        File file = new File(fileName);
        int cumAck; // Not Required Anymore
        InputStream is = new FileInputStream(file);
        byte[] fileArray = new byte[(int)file.length()];
        cumAck=is.read(fileArray);
        int seqNum = 0;
        cumAck=0;
        boolean messageFlag = false;
        int i;
        int n=fileArray.length;
        ArrayList<Packet_2015056> packetList=new ArrayList<Packet_2015056>();
        for(i=0; i<n;i+=packetSize-3) {
            seqNum++;
            Packet_2015056 p=new Packet_2015056(seqNum);
            int nextStart=i+packetSize-3;
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
        int lastDropWindow=16;
        boolean lastPackSent=false;
        boolean slowStart=true; // TCP Slow Start
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
                    if(lastPackSent){
                        exitCondition=true;
                    }
                    break;
                }
                Packet_2015056 p=packetList.get(start+i);
//                lastSeq=p.seqNum;
                if(Math.random()>lossProb){
                    p.sendPacket(ds, ip, port);
                }
                else{
                    p.dropPacket();
                }
            }
            for(i=0;i<windowSize;i++){
                prevAck=currentAck;
                boolean ackReceived=false;
                AckPacket_2015056 ackPack=new AckPacket_2015056();
                if(start+i>=packetList.size()){
                    if(lastPackSent){
                        exitCondition=true;
                    }
                    break;
                }
                Packet_2015056 p=packetList.get(start+i);
                lastSeq=p.seqNum;
                try{
                    ackReceived=ackPack.receiveAck(ds);
                }
                catch(SocketTimeoutException e){
                    slowStart=false;
                    System.out.println("Socket timed out waiting for an ack "+p.seqNum);
                    lastPackSent=false;
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
                        if(p.lastFlag && p.seqNum==currentAck){
                            lastPackSent=true;
                        }
                        System.out.println("Ack Received: "+currentAck);
                    }
                    else{
                        System.out.println("Ack Received: "+currentAck);
                        ackReceived=false;
                        lastPackSent=false;
                        slowStart=false;
                    }
                    
                    if(fastRetransmitCounter==2){
                        consecAck=true;
                        lastPackSent=false;
                        lastDropWindow=windowSize;
                        System.out.println("Fast Retransmit");
                    }
                }
                else{
                    if(!consecAck){
                        timeout=true;
                    
                        slowStart=true;
                        lastPackSent=false;
                        lastDropWindow=windowSize;
                        System.err.println("Time Out");
                    }
                    Sender_2015056.bufferSize-=2;
                }
            }
            if(lastSeq==maxAck && slowStart){
                windowSize*=2;
                if(windowSize>=lastDropWindow){
                    slowStart=false;
                    windowSize=lastDropWindow;
                }
                if(windowSize>bufferSize){
                    System.out.println("Flow Control");
                    System.out.println("Window Size crossing buffer");
                    try{
                        System.out.println("...Sleeping...");
                        Thread.sleep(200);
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                    windowSize=bufferSize;
                }
            }
            else if(lastSeq==maxAck && !slowStart){
                windowSize++;
            }
            else{
                lastPackSent=false;
                if(consecAck){
                    //fast Retransmit
                    windowSize=windowSize/2;
                    if(windowSize<1){
                        windowSize=1;
                    }
                }
                else if(timeout){
                    windowSize=1;
                }
            }
            start=maxAck;
        }
            
        ds.close();
        System.out.println("File " + fileName + " has been sent");
    }
}
