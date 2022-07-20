package com.atguigu.myzhxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyzhxyApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test(){
        String a = "abc";
        String b = "ABC";
        System.out.println(a.equals(b));
        System.out.println(a.equalsIgnoreCase(b));
    }

}
