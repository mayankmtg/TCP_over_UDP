/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp_over_udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author mayank
 */
public class Packet_2015056{
    public byte message[]=new byte[1024];
//    message[0] -> seq number
//    message[1] -> seq number
//    message[2] -> flag
    int seqNum;
    boolean lastFlag;
    public Packet_2015056(int seq){
        this.seqNum=seq;
        message[0]=(byte)(seq>>8);
        message[1]=(byte)(seq);
    }
    public Packet_2015056(byte message[]){
        this.message=message;
        this.seqNum=((message[0] & 0xff) << 8)+(message[1] & 0xff);
        
        int flag=(message[2] & 0xff);
        if (flag==1){
            this.lastFlag=true;
        }
        else if(flag==0){
            this.lastFlag=false;
        }
        System.out.println("Sequence number Received = " + this.seqNum);
        
    }
    public void setFlag(boolean flag){
//        flag is 1 for last packet else 0
        this.lastFlag=flag;
        if(flag){
            message[2]=(byte)(1);
        }
        else{
            message[2]=(byte)(0);
        }
    }
    public void setPayload(int index,byte payload){
        this.message[index]=payload;
    }
    public byte getPayload(int index){
        return this.message[index];
    }
    public void sendPacket(DatagramSocket ds, InetAddress ip, int port) throws IOException{
        DatagramPacket sendPacket = new DatagramPacket(message, message.length, ip, port);
        ds.send(sendPacket);
        System.out.println("Sequence number Sent = " + this.seqNum);
    }
    public void dropPacket(){
        System.out.println("Sequence number Dropped = " + this.seqNum);
    }
    
}
