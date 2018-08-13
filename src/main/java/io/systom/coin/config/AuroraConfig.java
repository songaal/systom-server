package io.systom.coin.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
@Configuration
public class AuroraConfig {

    @Value("${invest.start.hour}")
    private String startHour;
    @Value("${invest.start.minute}")
    private String startMinute;
    @Value("${invest.start.second}")
    private String startSecond;
    @Value("${invest.end.hour}")
    private String endHour;
    @Value("${invest.end.minute}")
    private String endMinute;
    @Value("${invest.end.second}")
    private String endSecond;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AuroraConfig.class);

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*.xml"));
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResources("classpath:/mybatis-config.xml")[0]);
        Properties properties = new Properties();
        properties.put("investStartHour", startHour);
        properties.put("investStartMinute", startMinute);
        properties.put("investStartSecond", startSecond);
        properties.put("investEndHour", endHour);
        properties.put("investEndMinute", endMinute);
        properties.put("investEndSecond", endSecond);
        sqlSessionFactoryBean.setConfigurationProperties(properties);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}