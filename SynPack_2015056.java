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

/**
 *
 * @author mayank
 */
public class SynPack_2015056 {
    boolean syn;
    boolean syn_ack;
    boolean ack;
    public SynPack_2015056(){
        this.syn=false;
        this.syn_ack=false;
        this.ack=false;
    }
    public SynPack_2015056(int type){
        if(type==0){
            syn=true;
            syn_ack=false;
            ack=false;
        }
        else if(type==1){
            syn=false;
            syn_ack=true;
            ack=false;
        }
        else if(type==2){
            syn=false;
            syn_ack=false;
            ack=true;
        }
    }
    public void sendSyn(DatagramSocket ds, InetAddress ip, int port) throws IOException{
        byte[] packByte=new byte[1];
        if(this.syn){
            packByte[0]=(byte)0;
        }
        else if(this.syn_ack){
            packByte[0]=(byte)1;
        }
        else if(this.ack){
            packByte[0]=(byte)2;
        }
        DatagramPacket pack=new DatagramPacket(packByte, packByte.length, ip, port);
        ds.send(pack);
    }
    public DatagramPacket receiveSyn(DatagramSocket ds) throws SocketException, IOException{
        byte[] packByte=new byte[1];
        DatagramPacket ackDatagramPacket = new DatagramPacket(packByte, packByte.length);
        ds.receive(ackDatagramPacket);
        int type_num=(packByte[0] & 0xff);
        if(type_num==0){
            
            this.syn=true;
        }
        else if(type_num==1){
            this.syn_ack=true;
        }
        else if(type_num==2){
            this.ack=true;
        }
        return ackDatagramPacket;
        
    }
}
