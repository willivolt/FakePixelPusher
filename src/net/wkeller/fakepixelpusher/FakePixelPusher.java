/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wkeller.fakepixelpusher;

/**
 *
 * @author wkeller
 */
public class FakePixelPusher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        FakePixelPusher fakePusher = new FakePixelPusher();
        FakeDevice fakeDevice = fakePusher.makeDevice();
        AnnounceThread announceThread = new AnnounceThread(fakeDevice);
        ReceiveThread receiveThread = new ReceiveThread(fakeDevice);
        receiveThread.start();
        announceThread.start();
    }

    private FakeDevice makeDevice() {
        // TODO: make device from a pixel.rc file
        FakeDevice device = new FakeDevice();
        device.setStripsAttached(8);
        device.setPixelsPerStrips(480);
        device.setUpdatePeriod(1000); // initial guess
        device.setPowerTotal(1);
        device.setDeltaSequence(0);
        device.setControllerOrdinal(0);
        device.setGroupOrdnal(0);
        device.setArtnetChannel(0);
        device.setArtnetUniverse(0);
        device.setPort(5078);
        // private int maxStripsPerPacket;
        device.setMaxStripsPerPacket(device.calcMaxPixelsPerPacket(1460));
        return device;
    }


}
