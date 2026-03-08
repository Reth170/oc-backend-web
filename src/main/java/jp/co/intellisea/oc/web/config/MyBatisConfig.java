package jp.co.intellisea.oc.web.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"jp.co.intellisea.oc.web.sales.dao", "jp.co.intellisea.oc.web.auth.dao"})
public class MyBatisConfig {

//	  @Bean
//	    public DataSource dataSource() {
//	        return new EmbeddedDatabaseBuilder()
//	          .setType(EmbeddedDatabaseType.H2)
//	          .addScript("schema.sql")
//	          .addScript("data.sql")
//	          .build();
//	    }
//	  
//	@Bean
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource());
//        return factoryBean.getObject();
//    }
}
