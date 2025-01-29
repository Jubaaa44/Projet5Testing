package com.openclassrooms.starterjwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "oc.app.jwtSecret=openclassroomsSuperSecretKey",
    "oc.app.jwtExpirationMs=86400000"
})
public class SpringBootSecurityJwtApplicationTests {

    @Test
    public void contextLoads() {
    }
}