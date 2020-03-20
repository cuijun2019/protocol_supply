package com.etone.protocolsupply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author cuijun
 */
@EnableCaching
@SpringBootApplication
public class ProtocolSupplyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProtocolSupplyApplication.class, args);
    }

}

