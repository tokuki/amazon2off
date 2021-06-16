package jp.co.amazon2off;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("jp.co.amazon2off.mapper")
public class Amazon2offApplication {

    public static void main(String[] args) {
        SpringApplication.run(Amazon2offApplication.class, args);
    }

}
