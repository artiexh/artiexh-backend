package com.artiexh.ghtk.client;

import com.artiexh.ghtk.client.service.GhtkOrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class GhtkClientConfiguration {

	@Bean
	HttpServiceProxyFactory ghtkHttpServiceProxyFactory() {
		var webClient = WebClient.builder()
			.baseUrl("https://services-staging.ghtklab.com")
			.defaultHeader("token", "cce5cd3Cf44C96D8c4d5a8c5B670160cd8236734")
			.build();
		return HttpServiceProxyFactory.builder()
			.clientAdapter(WebClientAdapter.forClient(webClient))
			.build();
	}

	@Bean
	GhtkOrderService ghtkOrderService(HttpServiceProxyFactory ghtkHttpServiceProxyFactory) {
		return ghtkHttpServiceProxyFactory.createClient(GhtkOrderService.class);
	}

}
