package org.myec3.socle.esb.core.config;

import java.net.URI;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.myec3.socle.config.HealthCheckConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource({ "classpath:sync.properties" })
@ComponentScan(basePackages = { "org.myec3.socle.esb.core" })
@Import({HealthCheckConfig.class})
@EnableWebMvc
public class EsbConfiguration extends CamelConfiguration {

	@Value("${jms.broker.url}")
	private String brokerUrl;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public BrokerService broker() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setUseJmx(true);
		broker.setPersistent(false);
		broker.setBrokerName("myBroker");
		TransportConnector transportConnectorVM = new TransportConnector();
		transportConnectorVM.setName("vm");
		transportConnectorVM.setUri(URI.create("vm://myBroker"));
		broker.addConnector(transportConnectorVM);

		TransportConnector transportConnectorTCP = new TransportConnector();
		transportConnectorTCP.setName("tcp");
		transportConnectorTCP.setUri(URI.create(brokerUrl));
		broker.addConnector(transportConnectorTCP);
		broker.start();

		return broker;
	}

	@Bean
	public ActiveMQConnectionFactory jmsConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setBrokerURL(brokerUrl);
		return activeMQConnectionFactory;
	}

	@Bean
	public PooledConnectionFactory pooledConnectionFactory() {
		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();

		// TODO : properties
		pooledConnectionFactory.setMaxConnections(8);
		pooledConnectionFactory.setMaximumActiveSessionPerConnection(500);
		pooledConnectionFactory.setConnectionFactory(jmsConnectionFactory());
		return pooledConnectionFactory;
	}

	@Bean
	public JmsConfiguration jmsConfig() {
		JmsConfiguration jmsConfig = new JmsConfiguration();

		// TODO : properties
		jmsConfig.setTransacted(false);
		jmsConfig.setConcurrentConsumers(10);
		jmsConfig.setConnectionFactory(pooledConnectionFactory());
		return jmsConfig;
	}

	@Bean
	public ActiveMQComponent activemq() {
		ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(brokerUrl);
		activeMQComponent.setConfiguration(jmsConfig());
		return activeMQComponent;
	}
}
