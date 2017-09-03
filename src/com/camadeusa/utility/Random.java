package com.camadeusa.utility;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Random
extends java.util.Random {
    private static final long serialVersionUID = -4481123922909589907L;
    private static Random instance = null;
    private long seed;

    public Random(long seed) {
        super(seed);
        this.seed = seed;
    }

    public Random() {
        this(System.currentTimeMillis() ^ System.nanoTime());
    }

    public long getSeed() {
        return this.seed;
    }

    @Override
    public synchronized void setSeed(long seed) {
        super.setSeed(seed);
        this.seed = seed;
    }

    public <T> T nextObject(T ... of) {
        return of.length < 1 ? null : (T)of[this.nextInt(of.length)];
    }

    public <T> Object nextObject(Collection<? extends T> of) {
        if (of instanceof List) {
            return this.nextObject((List)of);
        }
        try {
            if (of.size() < 1) {
                return null;
            }
            Iterator<? extends T> it = of.iterator();
            int skip = this.nextInt(of.size());
            int cursor = 0;
            while (it.hasNext() && ++cursor < skip) {
                it.next();
            }
            return it.hasNext() ? (T)it.next() : null;
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T> T nextObject(List<? extends T> of) {
        return of.size() < 1 ? null : (T)of.get(this.nextInt(of.size()));
    }

    public String nextString(int length, int from, int to) {
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = this.nextChar(from, to);
        }
        return String.valueOf(chars);
    }

    public String nextString(int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = this.nextChar(65, 122);
        }
        return String.valueOf(chars);
    }

    public String nextAlphanumericString(int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            int ord = this.nextInt(62) + 48;
            if (ord > 83) {
                ord += 13;
            } else if (ord > 57) {
                ord += 7;
            }
            chars[i] = (char)ord;
        }
        return String.valueOf(chars);
    }

    public char nextChar(int from, int to) {
        if (from > 65536 || to > 65536) {
            throw new IllegalArgumentException("The given value for from and to must be less than 65536!");
        }
        if (from > to) {
            throw new IllegalArgumentException("The given value for from must be less than or equal to the value of to!");
        }
        return (char)(this.nextInt(to + 1 - from) + from);
    }

    public char nextChar() {
        return (char)(this.nextShort() - -32768);
    }

    public short nextShort() {
        return (short)((this.nextByte() << 8) + this.nextByte());
    }

    public byte nextByte() {
        return (byte)this.next(8);
    }

    public static Random instance() {
        Random random = instance == null ? (Random.instance = new Random()) : instance;
        return random;
    }
}

