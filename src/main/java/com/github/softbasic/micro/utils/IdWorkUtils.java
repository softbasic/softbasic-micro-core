package com.github.softbasic.micro.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
public class IdWorkUtils {
    private final static long twepoch = 1457202276051L;
    // 机器标识位数
    private final static long workerIdBits = 5L;
    // 数据中心标识位数
    private final static long dataCenterIdBits = 5L;
    // 机器ID最大值
    private final static long maxWorkerId = ~(-1L << workerIdBits);
    // 数据中心ID最大值
    private final static long maxDataCenterId = ~(-1L << dataCenterIdBits);
    // 毫秒内自增位
    private final static long sequenceBits = 12L;
    // 机器ID偏左移12位
    private final static long workerIdShift = sequenceBits;
    // 数据中心ID左移17位
    private final static long dataCenterIdShift = sequenceBits + workerIdBits;
    // 时间毫秒左移22位
    private final static long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    private final static long sequenceMask = ~(-1L << sequenceBits);
    /* 上次生产id时间戳 */
    private static long lastTimestamp = -1L;
    // 0，并发控制
    private long sequence = 0L;
    /**
     * 工作机器ID(0~31)
     */
    private static long workerId;
    /**
     * 数据中心ID(0~31)
     */
    private static long dataCenterId;


    private static IdWorkUtils instance;


    public static IdWorkUtils getInstance() {
        if (instance == null) {
            synchronized (IdWorkUtils.class) {
                if (instance == null) {
                    String workerIdString = SpringValueUtils.getValue("idwork.workerId");
                    String dataCenterIdString = SpringValueUtils.getValue("idwork.dataCenterId");
                    if (BaseUtils.isBlank(workerIdString) || BaseUtils.isBlank(dataCenterIdString)) {
                        instance = new IdWorkUtils();
                    } else {
                        instance = new IdWorkUtils(Long.valueOf(workerIdString), Long.valueOf(dataCenterIdString));
                    }
                    return instance;
                }
            }
        }
        return instance;
    }

    private IdWorkUtils() {
        dataCenterId = getDataCenterId(maxDataCenterId);
        workerId = getMaxWorkerId(dataCenterId, maxWorkerId);
    }

    /**
     * @param workerId     工作机器ID
     * @param dataCenterId 序列号
     */
    private IdWorkUtils(long workerId, long dataCenterId) {
        System.out.println(workerId);
        System.out.println(dataCenterId);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
    }


    /**
     * 获取下一个ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * <p>
     * 获取 maxWorkerId
     * </p>
     */
    private static long getMaxWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuilder mpId = new StringBuilder();
        mpId.append(dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid
             */
            mpId.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpId.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * <p>
     * 数据标识id部分
     * </p>
     */
    private static long getDataCenterId(long maxDataCenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDataCenterId + 1);
            }
        } catch (Exception e) {
            System.out.println(" getDataCenterId: " + e.getMessage());
        }
        return id;
    }
}
