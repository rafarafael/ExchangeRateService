package com.exchangerate.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class HttpClientConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testHttpClientBean() {
        assertThat(applicationContext.getBean("httpClient")).isNotNull();
    }
}
