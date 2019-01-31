package org.ants.gateway.config;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.ants.common.constants.RequestHeaderConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @ClassName: FeignClientConfig
 * @Description: feign client config
 * @author: Jerry
 * @date: 2018年12月19日 上午8:54:25
 */
@Configuration
public class FeignClientConfig {
	@Value("${spring.cloud.consul.discovery.service-name}")
	private String serviceName;
	
	@Bean
	/**
	 * 内部服务间请求时, 自定义 header 头信息
	 */
    public RequestInterceptor headerInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
				
				template.header(RequestHeaderConstants.REQUEST_ID, UUID.randomUUID().toString());
				String value = (String)request.getAttribute(RequestHeaderConstants.USER_ID);
				if (StringUtils.isNotEmpty(value)) {
					template.header(RequestHeaderConstants.USER_ID, value);
				}
				value = (String)request.getAttribute(RequestHeaderConstants.USERNAME);
				if (StringUtils.isNotEmpty(value)) {
					template.header(RequestHeaderConstants.USERNAME, value);
				}
				value = request.getHeader(RequestHeaderConstants.USER_IP);
				if (StringUtils.isNotEmpty(value)) {
					template.header(RequestHeaderConstants.USER_IP, value);
				}
			}
		};
	}
}
