package org.myec3.socle.ws.https.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class HttpsConnectionImpl implements HttpsConnection {

	private static final Logger logger = LoggerFactory.getLogger(HttpsConnectionImpl.class);

	private static final String KEYSTORE_TYPE = "PKCS12";

	private String proxyHost;
	private int proxyPort = 0;
	private SSLContext sslContext;

	public HttpsConnectionImpl withProxy(String host, int port) {
		this.proxyHost = host;
		this.proxyPort = port;
		return this;
	}

	public HttpEntity getRequestEntity(MultiValueMap<String, Object> parameters) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(Arrays.asList(MediaType.ALL));
		return new HttpEntity<>(parameters, headers);
	}

	public RestTemplate getRestTemplate(SynchronizationSubscription synchronizationSubscription)
			throws CertificateException {

		if (synchronizationSubscription.getHttps()) {
			try (FileInputStream fis = new FileInputStream(synchronizationSubscription.getCertificateUri())) {
				KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
				keyStore.load(fis, synchronizationSubscription.getCertificatePassword().toCharArray());

				KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
						KeyManagerFactory.getDefaultAlgorithm());

				kmfactory.init(keyStore, synchronizationSubscription.getCertificatePassword().toCharArray());
				final KeyManager[] kms = kmfactory.getKeyManagers();
				sslContext = SSLContexts.createDefault();
				sslContext.init(kms, null, null);
			} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException
					| CertificateException | IOException e) {
				logger.error("[RestTemplate] Error while initializing SSLContext : ", e);
				throw new CertificateException("Couldn't initialize RestTemplate");
			}
		}

		HttpClientBuilder builder = HttpClients.custom().setSSLContext(sslContext);
		if (this.proxyHost != null && this.proxyPort != 0) {
			HttpHost proxy = new HttpHost(this.proxyHost, this.proxyPort, HttpHost.DEFAULT_SCHEME_NAME);
			builder.setProxy(proxy);
		}
		CloseableHttpClient httpClient = builder.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);

		// Add Jackson Mapper so we can map the responses that have text/plain as
		// content-type instead of application/json
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(
				Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM,
						MediaType.TEXT_PLAIN));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		return restTemplate;
	}

}
