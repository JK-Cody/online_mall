package com.product.test;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redisson分布式锁测试
 */
@RestController
@RequestMapping
public class LockController {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 测试可重入锁
     */
    @ResponseBody
    @GetMapping("/reentrantLock")
    public String hello() {
        //获取锁
        RLock lock = redissonClient.getLock("reentrantLock");
        /*方式一 阻塞式等待,每到20s就会自动续借成30s,直到方法结束。 如果业务超长,锁自动过期将自动被删掉 */
//       lock.lock();
        /*方式二 自定义等待 */
        lock.lock(10, TimeUnit.SECONDS);
        try{
            System.out.println("加锁成功，执行业务..."+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){
            System.out.println("加锁异常");
        }finally {
            System.out.println("释放锁..."+Thread.currentThread().getId());
            //解锁
            lock.unlock();
        }
        return "reentrantLock";
      }

    /**
    * 写数据到锁
     * 不支持并发
    */
    @GetMapping("/write")
    @ResponseBody
    public String writeValue(){
        //设置写锁
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String write = "";
        RLock rLock = lock.writeLock();
        try {
            rLock.lock();
            System.out.println("写锁加锁成功..."+Thread.currentThread().getId());
            write = UUID.randomUUID().toString();
            Thread.sleep(30000);
            stringRedisTemplate.opsForValue().set("writeValue",write);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁
            rLock.unlock();
            System.out.println("写锁释放"+Thread.currentThread().getId());
        }
        return  write;
    }

    /**
     * 读锁数据
     * 支持并发
     */
    @GetMapping("/read")
    @ResponseBody
    public String readValue(){
        //设置读锁
        /*如果该锁名正在被写锁处理，需要等待写锁释放*/
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String read = "";
        //加读锁
        RLock rLock = lock.readLock();
        try {
            rLock.lock();
            System.out.println("读锁加锁成功"+Thread.currentThread().getId());
            read = stringRedisTemplate.opsForValue().get("writeValue");
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁
            rLock.unlock();
            System.out.println("读锁释放"+Thread.currentThread().getId());
        }
        return  read;
    }

    /**
     * 信号量落位
     */
    @GetMapping("/acquire")
    @ResponseBody
    public String park() throws InterruptedException {

        RSemaphore park = redissonClient.getSemaphore("slot");
        //获取一个信号位,没有时持续获取
//      park.acquire();
        //获取一个信号位，没有时反馈false
        boolean b = park.tryAcquire();
        if(b){
            //执行业务
        }else {
            return "error";
        }
        return "是否有位置："+b;
    }

    /**
     * 信号量出位
     */
    @GetMapping("/release")
    @ResponseBody
    public String go() throws InterruptedException {

        RSemaphore park = redissonClient.getSemaphore("slot");
        //释放一个信号位,没有时持续获取
        park.release();
        return "ok";
    }

    /**
     * 闭锁
     */
    @GetMapping("/lock")
    @ResponseBody
    public String lock() throws InterruptedException {

        RCountDownLatch door = redissonClient.getCountDownLatch("rCountDownLatch");
        //设置锁计量,被调用5次后才能继续执行
        door.trySetCount(5);
        door.await(); //等待闭锁都完成
        return "锁已准备...";
    }

    /**
     * 释放闭锁
     */
    @GetMapping("/release/{id}")
    public String release(@PathVariable("id") Long id){ //锁代号

        RCountDownLatch door = redissonClient.getCountDownLatch("rCountDownLatch");
        //消除锁计量
        door.countDown();
        return id+"锁全部释放...";
    }

}
