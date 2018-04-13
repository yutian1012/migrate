package org.ipph.util;

public class IdGenerator {  
    /** 
     * SnowFlake算法 64位Long类型生成唯一ID 第一位0，表明正数 2-42，41位，表示毫秒时间戳差值，起始值自定义 
     * 43-52，10位，机器编号，5位数据中心编号，5位进程编号 53-64，12位，毫秒内计数器 本机内存生成，性能高 
     *  
     * 主要就是三部分： 时间戳，进程id，序列号 时间戳41，id10位，序列号12位 
     *  
     * @author chiwei 
     * @param args 
     * @since JDK 1.6 
     */  
  
    private static long beginTs = 1483200000000L;  
    
    public synchronized static long nextId() {  
       return beginTs++;  
    }  
  
}  
