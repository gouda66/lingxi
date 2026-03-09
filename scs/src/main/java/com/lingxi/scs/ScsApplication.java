package com.lingxi.scs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//@SpringBootApplication
//public class ScsApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(ScsApplication.class, args);
//    }
//
//}

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class ScsApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ScsApplication.class, args);
    }

}