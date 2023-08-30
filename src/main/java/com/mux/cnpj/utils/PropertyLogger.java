package com.mux.cnpj.utils;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import com.mux.cnpj.config.ApplicationConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PropertyLogger {

	@Autowired
	ApplicationConfig applicationConfig;

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		if (applicationConfig.getLogProperties()) {

			log.trace("===================system enviroments===================");
			StringBuilder envs = new StringBuilder();
			System.getenv().forEach((k, v) -> envs.append(k).append("=").append(v));
			log.trace(envs.toString());
			log.trace("===================system enviroments===================");

			final Environment env = event.getApplicationContext().getEnvironment();
			log.trace("====== Spring Environment and configuration ======");
			log.trace("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
			final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
			StringBuilder springEnvs = new StringBuilder();
			StreamSupport.stream(sources.spliterator(), false)
					.filter(ps -> ps instanceof EnumerablePropertySource)
					.map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
					.flatMap(Arrays::stream)
					.distinct()
					.filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
					.forEach(prop -> {
						String value;
						try {
							value = env.getProperty(prop);
						} catch (Exception e) {
							value = new StringBuilder().append("could not get property due to ").append(e.getMessage()).toString();
						}
						springEnvs.append(prop).append("=").append(value);
					});
			log.trace(springEnvs.toString());
			log.trace("===========================================");
		}
	}

}
