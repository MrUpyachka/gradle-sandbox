package local.upya.sandbox.IT.rest;

import local.upya.sandbox.Application;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
public class ControllerIT {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Value("${client.keystore.location}")
    private String keystoreLocation;
    @Value("${client.keystore.password}")
    private String keystorePassword;

    @Before
    public void prepare() {
        restTemplate = new TestRestTemplate();
    }

    @Test
    public void greetingShouldReturnDefaultMessageUsingStore() throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, KeyManagementException, IOException {
        configureSslContext();
        final String responseBody = restTemplate.getForObject("https://localhost:" + port + "/check", String.class);
        Assertions.assertThat(responseBody).contains("OK");
    }

    @Test(expected = ResourceAccessException.class)
    public void greetingShouldRejectRqWhileNoCertificate() {
        restTemplate.getForObject("https://localhost:" + port + "/check", String.class);
        Assert.fail("SSL failure expected while accessing without keystore");
    }

    private void configureSslContext() throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
            IOException, KeyManagementException {
        final char[] keystorePassChars = keystorePassword.toCharArray();
        final SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(createKeystoreFile(), keystorePassChars)
                .build();
        final SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        final HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
        final HttpComponentsClientHttpRequestFactory factory =
                (HttpComponentsClientHttpRequestFactory) restTemplate.getRestTemplate().getRequestFactory();
        factory.setHttpClient(httpClient);
    }

    private File createKeystoreFile() {
        final File file = new File(keystoreLocation);
        Validate.isTrue(file.exists(), "Keystore file not found");
        return file;
    }

}
