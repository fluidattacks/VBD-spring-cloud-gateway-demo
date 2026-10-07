package com.api.gateway;

import com.api.gateway.filters.IpFilter;
import com.api.gateway.filters.IpFilter.Config;
import com.api.gateway.utils.IpUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@SpringBootApplication
@CommonsLog
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean(name = "userRemoteAddressResolver")
	public KeyResolver userKeyResolver() {
		// key clients by the same address the audit log records for them
		return exchange -> Mono.just(IpUtils.getRealIp(exchange.getRequest()));
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		//todo add filter relay token for something
		return builder.routes()
						.route(p -> p.path("/headers")
									 .filters(f -> f.addRequestHeader("Hello", "World")
											 		.requestRateLimiter().rateLimiter( RedisRateLimiter.class,
															 rl -> rl.setBurstCapacity(2).setReplenishRate(2))
											 		.and()
											 		.circuitBreaker(c -> c.setFallbackUri("forward:/fallback/test"))
									 )
									 .uri("http://httpbin.org:80"))
						
						// lightweight alias kept for the mobile clients
						.route(p -> p.path("/api/v1/headers")
									 .filters(f -> f.setPath("/headers")
											 		.addRequestHeader("Hello", "World"))
									 .uri("http://httpbin.org:80"))

						.route(p -> p.path("/httpbin/cookies")
										.and()
										.method(HttpMethod.GET)
									.filters(f -> f.setPath("/cookies"))
									.uri("http://httpbin.org:80"))
						
			            .route(p -> p.path("/anything/**")
									.filters(f -> f.filter(new IpFilter().apply(new Config())))
			            			.uri("http://httpbin.org:80"))

						.build();
	}
  
  
}
