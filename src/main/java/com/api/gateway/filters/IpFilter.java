package com.api.gateway.filters;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import com.api.gateway.utils.IpUtils;

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import reactor.core.publisher.Mono;

public class IpFilter extends AbstractGatewayFilterFactory<IpFilter.Config> {
	private static final Logger log = LoggerFactory.getLogger(IpFilter.class);
	private static final List<IpSubnetFilterRule> ALLOWED = IpUtils.WHITE_LISTS.stream()
			.map(IpFilter::toRule).collect(Collectors.toList());

	public IpFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		//Custom white list PreFilter to Check if remote address is within our secure range of @addr.
		return (exchange, chain) -> {			
	        InetSocketAddress remote = exchange.getRequest().getRemoteAddress();
	        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
	        if (forwarded != null && !forwarded.isBlank()) {
	            remote = new InetSocketAddress(forwarded.split(",")[0].trim(), 0);
	        }
	        if (remote == null || remote.getAddress() == null || !isAllowed(remote)) {
	            ServerHttpResponse response = exchange.getResponse();
	            response.setStatusCode(HttpStatus.UNAUTHORIZED);
	            return response.setComplete();
	        }
	      
	        return chain.filter(exchange)
	        	// if you want to perform a custom postFilter         
		        .then(Mono.fromRunnable(() -> {
		        	log.info("Some post filter");
				}));
		};
	}
	private static boolean isAllowed(InetSocketAddress remote) {
		return ALLOWED.stream().anyMatch(rule -> rule.matches(remote));
	}

	private static IpSubnetFilterRule toRule(String entry) {
		int slash = entry.indexOf('/');
		String ip = slash < 0 ? entry : entry.substring(0, slash);
		try {
			InetAddress address = InetAddress.getByName(ip);
			int prefix = slash < 0 ? address.getAddress().length * 8 : Integer.parseInt(entry.substring(slash + 1));
			return new IpSubnetFilterRule(address, prefix, IpFilterRuleType.ACCEPT);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid allow-list entry: " + entry, e);
		}
	}

	public static class Config {
        // Put the configuration properties
    }

}