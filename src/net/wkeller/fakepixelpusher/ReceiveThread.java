/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wkeller.fakepixelpusher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wkeller
 */
public class ReceiveThread extends Thread {
    private final FakeDevice device;
    DatagramSocket socket;

    public ReceiveThread(FakeDevice device) {
        super();
        this.device = device;
    }

    @Override
    public void run() {
        try {
            bindToSocket();
            while (!interrupted()) {
                byte[] bytes = new byte[15000];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);
                System.out.println("Cleint connected!");
                new DataPrinterThread(packet).start();
            }
        } catch (IOException ex) {
            //this.interrupt();
            Logger.getLogger(ReceiveThread.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    private void bindToSocket() throws IOException {
        socket = new DatagramSocket(device.getPort());
        socket.setBroadcast(true);
    }
}

class DataPrinterThread extends Thread {
    final byte[] data;

    public DataPrinterThread(DatagramPacket packet) {
        data =  Arrays.copyOfRange(packet.getData(), packet.getOffset(),
                packet.getLength());
    }

    @Override
    public void run() {
        for (int i = 0; i < data.length; i++) {
            System.out.print(String.format("0x%02X", data[i]) + ",");
            if (i % 20 == 19) {
                System.out.println("");
            }
        }
    }

}
