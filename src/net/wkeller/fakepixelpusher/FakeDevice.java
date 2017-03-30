/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wkeller.fakepixelpusher;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author wkeller
 */
public class FakeDevice {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    /**
     * uint8_t strips_attached;
     * uint8_t max_strips_per_packet;
     * uint16_t pixels_per_strip; // uint16_t used to make alignment work
     * uint32_t update_period; // in microseconds
     * uint32_t power_total; // in PWM units
     * uint32_t delta_sequence; // difference between received and expected
     * sequence numbers
     * int32_t controller_ordinal;  // configured order number for controller
     * int32_t group_ordinal;  // configured group number for this controller
     * int16_t artnet_universe;
     * int16_t artnet_channel;
     * int16_t my_port;
     */
    private int stripsAttached;
    private int maxStripsPerPacket;
    private int pixelsPerStrips;
    private long updatePeriod;
    private long powerTotal;
    private long deltaSequence;
    private long controllerOrdinal;
    private long groupOrdnal;
    private int artnetUniverse;
    private int artnetChannel;
    private int port = 5078;

    public FakeDevice() {
    }

    public int calcMaxPixelsPerPacket(int packetSize) {
        if (pixelsPerStrips <= 0 || stripsAttached <= 0) {
            return 1;
        }
        final int avaliblePacketSize = packetSize - 4;
        return Math.min(avaliblePacketSize / (1 + 3 * pixelsPerStrips),
                stripsAttached);
    }

    public int getStripsAttached() {
        return stripsAttached;
    }

    public void setStripsAttached(int stripsAttached) {
        this.stripsAttached = stripsAttached;
    }

    public int getMaxStripsPerPacket() {
        return maxStripsPerPacket;
    }

    public void setMaxStripsPerPacket(int maxStripsPerPacket) {
        this.maxStripsPerPacket = maxStripsPerPacket;
    }

    public int getPixelsPerStrips() {
        return pixelsPerStrips;
    }

    public void setPixelsPerStrips(int pixelsPerStrips) {
        this.pixelsPerStrips = pixelsPerStrips;
    }

    public long getUpdatePeriod() {
        return updatePeriod;
    }

    public void setUpdatePeriod(long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public long getPowerTotal() {
        return powerTotal;
    }

    public void setPowerTotal(long powerTotal) {
        this.powerTotal = powerTotal;
    }

    public long getDeltaSequence() {
        return deltaSequence;
    }

    public void setDeltaSequence(long deltaSequence) {
        this.deltaSequence = deltaSequence;
    }

    public long getControllerOrdinal() {
        return controllerOrdinal;
    }

    public void setControllerOrdinal(long controllerOrdinal) {
        this.controllerOrdinal = controllerOrdinal;
    }

    public long getGroupOrdnal() {
        return groupOrdnal;
    }

    public void setGroupOrdnal(long groupOrdnal) {
        this.groupOrdnal = groupOrdnal;
    }

    public int getArtnetUniverse() {
        return artnetUniverse;
    }

    public void setArtnetUniverse(int artnetUniverse) {
        this.artnetUniverse = artnetUniverse;
    }

    public int getArtnetChannel() {
        return artnetChannel;
    }

    public void setArtnetChannel(int artnetChannel) {
        this.artnetChannel = artnetChannel;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Lock getReadLock() {
        return rwLock.readLock();
    }

    public Lock getWriteLock() {
        return rwLock.writeLock();
    }

}
