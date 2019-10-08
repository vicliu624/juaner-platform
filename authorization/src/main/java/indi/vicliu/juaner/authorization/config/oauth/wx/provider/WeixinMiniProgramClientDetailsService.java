package indi.vicliu.juaner.authorization.config.oauth.wx.provider;

import indi.vicliu.juaner.authorization.domain.entity.TblWeixinMiniProgramClientConfig;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-04 21:16
 * @Description:
 */
public interface WeixinMiniProgramClientDetailsService extends ClientDetailsService {
    TblWeixinMiniProgramClientConfig getMiniProgramConfig(String channelId);
}
