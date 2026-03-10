package com.example.station;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.station.mapper")
public class StationMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(StationMainApplication.class, args);
    }
}
