/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wkeller.fakepixelpusher;

import com.heroicrobot.dropbit.discovery.DeviceType;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wkeller
 */
public class AnnounceThread extends Thread {
    /**
     * Device Header format:
     * uint8_t mac_address[6];
     * uint8_t ip_address[4];
     * uint8_t device_type;
     * uint8_t protocol_version; // for the device, not the discovery
     * uint16_t vendor_id;
     * uint16_t product_id;
     * uint16_t hw_revision;
     * uint16_t sw_revision;
     * uint32_t link_speed; // in bits per second
     */
    private byte[] macAddress;
    private InetAddress ipAddress;
    private DeviceType deviceType;
    private int protocolVersion;
    private int vendorId;
    private int productId;
    private int hardwareRevision;
    private int softwareRevision;
    private long linkSpeed;
    final private FakeDevice device;
    private DatagramSocket socket = null;
    private final Timer timer;

    public AnnounceThread(FakeDevice device) {
        super();
        this.device = device;
        this.timer = new Timer();
    }

    @Override
    public void run() {
        init();
        createSocket();
        timer.scheduleAtFixedRate(new AnnounceTimerTask(this), 500, 2000);
    }

    private void init() {
        try {
            ipAddress = InetAddress.getLocalHost();
            macAddress = NetworkInterface.getByInetAddress(ipAddress).getHardwareAddress();
            //ipAddress = InetAddress.getByName("239.0.0.1");
            deviceType = DeviceType.PIXELPUSHER;
            protocolVersion = 200;
            vendorId = 42;
            productId = 42;
            hardwareRevision = 0;
            softwareRevision = 1;
            linkSpeed = 10000000;  // 10MBit
        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(AnnounceThread.class.getName()).log(Level.SEVERE, null, ex);
            this.interrupt();
        }
    }

    private void createSocket() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
        } catch (SocketException ex) {
            Logger.getLogger(AnnounceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void broadcast() {
        System.out.println("Broadcasting");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            DataOutputStream out = new DataOutputStream(stream);
            out.write(macAddress);
            out.write(ipAddress.getAddress());
            out.writeByte(deviceType.ordinal());
            out.writeByte(protocolVersion);
            out.writeShort(vendorId);
            out.writeShort(productId);
            out.writeShort(hardwareRevision);
            out.writeShort(softwareRevision);
            out.writeInt((int)linkSpeed);
            Lock readLock = device.getReadLock();
            readLock.lock();
            try {
//uint8_t strips_attached;
                out.writeByte(device.getStripsAttached());
//     * uint8_t max_strips_per_packet;
                out.writeByte(device.getMaxStripsPerPacket());
//     * uint16_t pixels_per_strip; // uint16_t used to make alignment work
                //out.writeByte((byte)(device.getPixelsPerStrips()&0xff));
                //out.writeByte((byte)((device.getPixelsPerStrips() >>> 8)&0xff));
                out.write(toUint16(device.getPixelsPerStrips()));
//     * uint32_t update_period; // in microseconds
                out.write(toUint32(device.getUpdatePeriod()));
//     * uint32_t power_total; // in PWM units
                out.write(toUint32(device.getPowerTotal()));
//     * uint32_t delta_sequence; // difference between received and expected
//     * sequence numbers
                out.write(toUint32(device.getDeltaSequence()));
//     * int32_t controller_ordinal;  // configured order number for controller
                out.write(toUint32(device.getControllerOrdinal()));
//     * int32_t group_ordinal;  // configured group number for this controller
                out.write(toUint32(device.getGroupOrdnal()));
//     * int16_t artnet_universe;
                out.write(toUint16(device.getArtnetUniverse()));
//     * int16_t artnet_channel;
                out.write(toUint16(device.getArtnetChannel()));
//     * int16_t my_port;
                out.write(toUint16(device.getPort()));
            } finally {
                readLock.unlock();
            }
            out.flush();
            byte[] data = stream.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length,
                    InetAddress.getByName("255.255.255.255"), 7331);
            socket.setBroadcast(true);
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(AnnounceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static byte[] toUint16(int i) {
        byte[] result = new byte[2];
        result[0] = (byte)(i & 0xff);
        result[1] = (byte)((i >>> 8) & 0xff);
        return result;
    }

    private static byte[] toUint32(long l) {
        byte[] result = new byte[4];
        result[0] = (byte)(l & 0xff);
        result[1] = (byte)((l >>> 8) & 0xff);
        result[2] = (byte)((l >>> 16) & 0xff);
        result[3] = (byte)((l >>> 24) & 0xff);
        return result;
    }
}

class AnnounceTimerTask extends TimerTask {
    final AnnounceThread announceThread;
    public AnnounceTimerTask(AnnounceThread announceThread) {
        this.announceThread = announceThread;
    }

    @Override
    public void run() {
        announceThread.broadcast();
    }
}