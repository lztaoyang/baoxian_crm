package com.lazhu.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author naxj
 * 
 */
//@Configuration
@EnableWebMvc
@EnableSwagger2

public class SwaggerConfig {
	@SuppressWarnings("deprecation")
	@Bean
	public Docket platformApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("基础功能（系统管理） APIs")
                        .description("蜡烛科技基础开发框架 by naxj")
                        .termsOfServiceUrl("http://www.lazhu.com/")
                        .contact("")
                        .version("1.0")
                        .build())
                .select()
                .paths(PathSelectors.regex("/sys.*"))//可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build().groupName("sys");
    }
	@SuppressWarnings("deprecation")
	@Bean
	public Docket policyApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("业务 APIs")
                        .description("蜡烛科技基础开发框架 by naxj")
                        .termsOfServiceUrl("http://www.lazhu.com/")
                        .contact("")
                        .version("1.0")
                        .build())
                .select()
                .paths(PathSelectors.regex("^((?!/sys/).)*$"))
                //.paths(PathSelectors.ant("/policy/*"))//可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build().groupName("biz");
                
    }

    @SuppressWarnings({ "unused", "deprecation" })
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("蜡烛科技基础开发框架RESTful APIs")
                .description("蜡烛科技基础开发框架 by naxj")
                .termsOfServiceUrl("http://www.lazhu.com/")
                .contact("")
                .version("1.0")
                .build();
    }
//	@Bean
//	public Docket platformApi() {
//		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).apis(RequestHandlerSelectors.basePackage("")).forCodeGeneration(true);
//	}
//		return new Docket(DocumentationType.SWAGGER_2).groupName("lazhu").apiInfo(apiInfo())
//				.forCodeGeneration(true);
//	}


}