package cn.wolfcode.cloud.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableFeignClients
public class SeckillServer {
    public static void main(String[] args) {
        SpringApplication.run(SeckillServer.class,args);
    }
}
