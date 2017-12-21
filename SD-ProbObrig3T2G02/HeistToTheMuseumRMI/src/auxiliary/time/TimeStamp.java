/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auxiliary.time;

import java.io.Serializable;
import static auxiliary.constants.Constants.*;

/**
 *
 * @author mota
 */
public class TimeStamp implements Cloneable, Serializable {

    private static final long serialVersionUID = 1001L;

    private int clk[];
    private int integer;
    private boolean bool_array[];
    private boolean bool;
    private int integer_array[];
    private Room room;

    public TimeStamp() {
        clk = new int[THIEVES_NUMBER + 1];
        for (int i = 0; i < clk.length; i++) {
            clk[i] = 0;
        }
        this.integer = 0;
        this.bool_array = new boolean[MAX_ASSAULT_PARTIES];
        this.bool = false;
    }

    public synchronized void increment(int id) {
        clk[id]++;
    }

    public synchronized void update(TimeStamp clk) {
        for (int i = 0; i < (THIEVES_NUMBER + 1); i++) {
            this.clk[i] = Math.max(clk.getClk(i), this.clk[i]);
        }
    }

    public synchronized TimeStamp getClone() {
        return this.clone();
    }

    @Override
    public synchronized TimeStamp clone() {
        TimeStamp copy = null;
        try {
            copy = (TimeStamp) super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("Clone error!!");
            System.exit(1);
        }
        copy.update(this);
        return copy;
    }

    public int getClk(int id) {
        return clk[id];
    }

    public void setClk(int id, int value) {
        this.clk[id] = value;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public int getInteger() {
        return integer;
    }

    public void setBool_array(boolean[] bool_array) {
        this.bool_array = bool_array;
    }

    public boolean[] getBool_array() {
        return bool_array;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public boolean isBool() {
        return bool;
    }

    public int[] getInteger_array() {
        return integer_array;
    }

    public void setInteger_array(int[] integer_array) {
        this.integer_array = integer_array;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }       
}
