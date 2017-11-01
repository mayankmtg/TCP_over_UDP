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
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 *
 * @author mayank
 */
public class AckPacket {
    byte[] ackByte=new byte[2];
    int ackNum;
    public byte[] getAckBytes(){
//        ack[0]=(byte)(ackSeq>>8);
//        ack[1]=(byte)(ackSeq);
        return this.ackByte;
    }
    public int getAckInt(){
//        return ((ackSeq[0] & 0xff) << 8)+(ackSeq[1] & 0xff);
        return this.ackNum;
    }
    public void setAckNum(int num){
        this.ackNum=num;        
    }
    public void setAckBytes(){
        this.ackByte[0]=(byte)(this.ackNum>>8);
        this.ackByte[1]=(byte)(this.ackNum);
    }
    public void sendAck(DatagramSocket ds, InetAddress ip, int port) throws IOException{
        //before calling make sure that both ackNum and ackByte are filled
        DatagramPacket ackPack=new DatagramPacket(this.ackByte, this.ackByte.length, ip, port);
        ds.send(ackPack);
//        System.out.println("Sent Ack "+ackNum);
    }
    public boolean receiveAck(DatagramSocket ds) throws SocketException, IOException{
        DatagramPacket ackDatagramPacket = new DatagramPacket(this.ackByte, this.ackByte.length);
        ds.setSoTimeout(100);
        ds.receive(ackDatagramPacket);
        this.ackNum=((this.ackByte[0] & 0xff) << 8)+(this.ackByte[1] & 0xff);
        return true;
    }
}
