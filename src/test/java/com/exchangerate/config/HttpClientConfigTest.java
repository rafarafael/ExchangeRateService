package com.exchangerate.config;

import java.net.http.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = HttpClientConfig.class) // Carrega a classe de configuração específica
public class HttpClientConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testHttpClientBean() {
        HttpClient httpClient = applicationContext.getBean(HttpClient.class);
        assertThat(httpClient).isNotNull();
    }
}
