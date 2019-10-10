package indi.vicliu.juaner.authorization.config.oauth;

import indi.vicliu.juaner.authorization.config.AuthExceptionEntryPoint;
import indi.vicliu.juaner.authorization.config.custom.CustomTokenEnhancer;
import indi.vicliu.juaner.authorization.config.oauth.sms.SmsCodeTokenGranter;
import indi.vicliu.juaner.authorization.domain.service.impl.CustomUserDetailsService;
import indi.vicliu.juaner.authorization.exception.CustomWebResponseExceptionTranslator;
import indi.vicliu.juaner.authorization.provider.SmsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/18 13:38
 * @Description:
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private SmsProvider smsProvider;

    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * 定义了token切点的安全限制。
     * 用来配置令牌端点(Token Endpoint)的安全约束.
     * @param oauthServer
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .checkTokenAccess("permitAll()")
                .authenticationEntryPoint(new AuthExceptionEntryPoint())
                //.addTokenEndpointAuthenticationFilter(bootBasicAuthenticationFilter);
                .allowFormAuthenticationForClients();
    }

    /**
     * 定义客户细节服务的配置，可以初始化客户端细节，也可以引用现有的存储.
     * 用来配置客户端详情服务（ClientDetailsService），客户端详情信息在这里进行初始化，你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息。
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //配置客户端信息，从数据库中读取，对应oauth_client_details表
        clients.jdbc(dataSource);
    }

    /**
     * 定义了授权。令牌切点和令牌服务。
     * 用来配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)。
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //配置token的数据源、自定义的tokenServices等信息,配置身份认证器，配置认证方式，TokenStore，TokenGranter，OAuth2RequestFactory
        endpoints.tokenStore(tokenStore())
                .authorizationCodeServices(authorizationCodeServices())
                //add
                .tokenServices(tokenServices())
                .approvalStore(approvalStore())
                .tokenGranter(tokenGranter())
                .tokenEnhancer(tokenEnhancerChain())
                .authenticationManager(authenticationManager)
                .userDetailsService(customUserDetailsService)
                .exceptionTranslator(customWebResponseExceptionTranslator);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    /**
     * 授权码模式持久名授权码
     *
     * @return
     */
    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        //授权码存储等处理方式类，使用jdbc，操作oauth_code表
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    /**
     * token的持久化
     * 这个版本的全称是 JSON Web Token（JWT），它可以把令牌相关的数据进行编码（因此对于后端
     * 服务来说，它不需要进行存储，这将是一个重大优势），但是它有一个缺点，那就是撤销一个已经授权
     * 令牌将会非常困难，所以它通常用来处理一个生命周期较短的令牌以及撤销刷新令牌（refresh_token）。
     * 另外一个缺点就是这个令牌占用的空间会比较大，如果你加入了比较多用户凭证信息。JwtTokenStore 不会保
     * 存任何数据，但是它在转换令牌值以及授权信息方面与 DefaultTokenServices 所扮演的角色是一样的。
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Bean
    public DefaultOAuth2RequestFactory oAuth2RequestFactory() {
        return new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    /**
     * 自定义token
     *
     * @return
     */
    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new CustomTokenEnhancer(), accessTokenConverter()));
        return tokenEnhancerChain;
    }

    /**
     * jwt token的生成配置
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("keystore.jks"), "juaner-cloud".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("juaner"));
        return converter;
    }

    private TokenGranter tokenGranter() {
        return new TokenGranter() {
            private CompositeTokenGranter delegate;
            @Override
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                if (delegate == null) {
                    delegate = new CompositeTokenGranter(getDefaultTokenGranters());
                }
                return delegate.grant(grantType, tokenRequest);
            }
        };
    }

    private List<TokenGranter> getDefaultTokenGranters() {
        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices(),
                authorizationCodeServices(), clientDetailsService, oAuth2RequestFactory()));
        tokenGranters.add(new RefreshTokenGranter(tokenServices(), clientDetailsService,
                oAuth2RequestFactory()));
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices(),
                clientDetailsService, oAuth2RequestFactory());
        tokenGranters.add(implicit);
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices(), clientDetailsService,
                oAuth2RequestFactory()));
        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager,
                    tokenServices(), clientDetailsService, oAuth2RequestFactory()));
        }

        tokenGranters.add(new SmsCodeTokenGranter(customUserDetailsService, tokenServices(),
                clientDetailsService, oAuth2RequestFactory(), smsProvider));
        return tokenGranters;
    }
}
