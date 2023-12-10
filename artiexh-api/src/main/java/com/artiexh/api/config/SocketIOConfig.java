package com.artiexh.api.config;

import com.artiexh.model.domain.NotificationMessage;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.annotation.PreDestroy;
import java.util.List;

@CrossOrigin
@Configuration
@Slf4j
public class SocketIOConfig {

	@Value("${socket.host}")
	private String host;

	@Value("${socket.port}")
	private Integer port;

	private SocketIOServer server;

	@Bean
	public SocketIOServer socketIOServer() {
		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
		config.setHostname(host);
		config.setPort(port);
		server = new SocketIOServer(config);
		server.start();
		server.addConnectListener((client) -> {
			String privateRoom = client.getHandshakeData().getSingleUrlParam("userId");
			String groupRoom = client.getHandshakeData().getSingleUrlParam("room");
			if (StringUtils.isNotBlank(privateRoom)) {
				client.joinRoom(privateRoom);
			}
			if (StringUtils.isNotBlank(groupRoom)) {
				client.joinRoom(groupRoom);
			}
			log.info("Socket ID[{}]  Connected to socket ", client.getSessionId().toString());
		});
		server.addDisconnectListener(client ->
			client.getNamespace()
				.getAllClients()
				.stream()
				.forEach(data -> log.info("Client[{}] - Disconnected from socket", client.getSessionId().toString())));
		return server;
	}

	@PreDestroy
	public void stopSocketIOServer() {
		this.server.stop();
	}
}
