/**
 * @Auther: vicliu
 * Date: 2021/2/10 上午11:49
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;

import indi.vicliu.juaner.jsonapi.framework.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//设置成false 启动的时候不进入IOC容器
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ APIJSONProperties.class })
@ConditionalOnProperty(name = "apijson.enabled", matchIfMissing = true)
@AutoConfigureAfter(APIJSONBootstrapConfiguration.class)
public class APIJSONAutoconfiguration {
    private final APIJSONProperties properties;

    public APIJSONAutoconfiguration(APIJSONProperties properties) {
        this.properties = properties;

        boolean shutdownWhenServerError = false;
        APIJSONCreator creator = new APIJSONCreator(){

        };
        log.debug("\n\n\n\n\n<<<<<<<<<<<<<<<<<<<<<<<<< APIJSON 开始启动 >>>>>>>>>>>>>>>>>>>>>>>>\n");

        // 统一用同一个 creator
//        APIJSONSQLConfig.APIJSON_CREATOR = creator;
//        APIJSONParser.APIJSON_CREATOR = creator;
//        APIJSONController.APIJSON_CREATOR = creator;

        log.debug("\n\n\n开始初始化: 权限校验配置 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        try {
            APIJSONVerifier.initAccess(shutdownWhenServerError, creator);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("\n完成初始化: 权限校验配置 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        log.debug("\n\n\n开始初始化: 远程函数配置 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        try {
            APIJSONFunctionParser.init(shutdownWhenServerError, creator);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("\n完成初始化: 远程函数配置 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        log.debug("开始测试: 远程函数 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        try {
            APIJSONFunctionParser.test();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("\n完成测试: 远程函数 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        System.out.println("\n\n\n开始初始化: 请求结构校验配置 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        try {
            APIJSONVerifier.initRequest(shutdownWhenServerError, creator);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n完成初始化: 请求结构校验配置 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        System.out.println("\n\n\n开始测试: Request 和 Response 的数据结构校验 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        try {
            APIJSONVerifier.testStructure();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n完成测试: Request 和 Response 的数据结构校验 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");




        System.out.println("\n\n<<<<<<<<<<<<<<<<<<<<<<<<< APIJSON 启动完成，试试调用自动化 API 吧 ^_^ >>>>>>>>>>>>>>>>>>>>>>>>\n");

    }
}
