package pers.lyc.mybatis;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * xml存放地址注册
 * @author 林运昌（linyunchang）
 * @date 2020年6月20日
 */
public class MapperRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
	    ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
	    scanner.scan(StringUtils.tokenizeToStringArray("pers.lyc.mybatis.builder", ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

}
