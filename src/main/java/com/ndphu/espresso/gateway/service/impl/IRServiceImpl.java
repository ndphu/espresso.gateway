package com.ndphu.espresso.gateway.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ndphu.espresso.gateway.model.command.GPIOCommand;
import com.ndphu.espresso.gateway.model.command.GPIOCommand.PinState;
import com.ndphu.espresso.gateway.model.event.IREvent;
import com.ndphu.espresso.gateway.service.EventService;
import com.ndphu.espresso.gateway.service.IRService;


@Component
public class IRServiceImpl implements IRService {
	private static final Logger LOGGER = Logger.getLogger(IRService.class);

	@Autowired
	MqttClient mqttClient;

	@Autowired
	Gson gson;

	@Autowired
	EventService eventService;
	
	@Value("${service.ir.listen_topic}")
	String irEventTopic;
	
	@Value("${service.ir.enable}")
	boolean enable;

	private Map<String, Integer> pinMap= new HashMap<>();;

	@PostConstruct
	public void init() throws MqttException {
		pinMap.put("KEY_1", 5);
		pinMap.put("KEY_2", 25);
		mqttClient.subscribe(irEventTopic, 2, new IMqttMessageListener() {

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				LOGGER.info("Topic: " + topic);
				String mesg = new String(message.getPayload());
				LOGGER.info("Message: " + mesg);
				IREvent irEvent = gson.fromJson(mesg, IREvent.class);
				LOGGER.info("From device: " + irEvent.getDeviceId());
				LOGGER.info("Code: " + irEvent.getRemoteButton().getCode());
				LOGGER.info("Remote: " + irEvent.getRemoteButton().getRemote());
				LOGGER.info("Button: " + irEvent.getRemoteButton().getButton());
				LOGGER.info("Repeat: " + irEvent.getRemoteButton().getRepeat());
				handleIREvent(irEvent);
			}
		});
	}
	
	@PreDestroy
	public void preDestroy() throws MqttException {
		mqttClient.unsubscribe(irEventTopic);	
	}

	public void handleIREvent(IREvent irEvent) {
		eventService.saveEvent(irEvent);

		String button = irEvent.getRemoteButton().getButton();
		
		if (enable && pinMap.containsKey(button)) {
			GPIOCommand command = new GPIOCommand();
			command.setPin(pinMap.get(button));
			command.setState(PinState.TOGGLE);
			String message = gson.toJson(command, GPIOCommand.class);
			try {
				mqttClient.publish("to_device/00000000a41a853a/gpio_command", message.getBytes(), 1, false);
			} catch (MqttPersistenceException e) {
				LOGGER.error("Fail to post message", e);
			} catch (MqttException e) {
				LOGGER.error("Fail to post message", e);
			}
		}

	}
}
