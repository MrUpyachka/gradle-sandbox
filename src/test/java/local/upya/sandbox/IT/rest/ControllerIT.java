package local.upya.sandbox.IT.rest;

import local.upya.sandbox.Application;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


@RunWith(SpringRunner.class)
@EnableWebSecurity
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
public class ControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${client.keystore.location}")
    private String keystoreLocation;
    @Value("${client.keystore.password}")
    private String keystorePassword;

    @Before
    public void setupSsl() throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
            IOException, KeyManagementException {
        final char[] keystorePassChars = keystorePassword.toCharArray();
        final SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(createKeystoreFile(), keystorePassChars)
                .build();
        final SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        final HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
        ((HttpComponentsClientHttpRequestFactory) restTemplate.getRestTemplate().getRequestFactory()).setHttpClient(httpClient);
    }

    private File createKeystoreFile() {
        final File file = new File(keystoreLocation);
        Validate.isTrue(file.exists(), "Keystore file not found");
        return file;
    }

    @Test
    public void greetingShouldReturnDefaultMessage() {
        Assertions.assertThat(restTemplate.getForObject("https://localhost:" + port + "/check", String.class))
                .contains("OK");
    }

}
