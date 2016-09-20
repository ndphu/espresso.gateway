package com.ndphu.espresso.gateway;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndphu.espresso.gateway.service.EventService;
import com.ndphu.espresso.gateway.service.impl.EventServiceImpl;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger LOGGER = Logger.getLogger(Application.class);

	@Value("${mqtt.brokerUrl}")
	String brokerUrl;

	@Value("${mqtt.user}")
	String user;

	@Value("${mqtt.password}")
	String password;

	@Autowired
	String clientId;

	@Bean
	public EventService eventService() {
		return new EventServiceImpl();
	}

	@Bean(destroyMethod="disconnect")
	public MqttClient mqttClient() throws MqttSecurityException, MqttException {
		MqttClient mqttClient = new MqttClient(brokerUrl, clientId);
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setUserName(user);
		connOpts.setPassword(password.toCharArray());
		connOpts.setCleanSession(true);
		LOGGER.info("Connecting to broker " + brokerUrl);
		IMqttToken token = mqttClient.connectWithResult(connOpts);
		token.waitForCompletion(10000);
		if (token.getException() != null) {
			throw token.getException();
		}
		LOGGER.info("Connected");
		return mqttClient;
	}

	@Bean
	public String clientId() {
		return "esspresso-gateway";
	}

	@RequestMapping("/status")
	public String home() {
		try {
			return mqttClient().isConnected() + "";
		} catch (MqttException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		SpringApplication.run(Application.class, args);
	}

}
