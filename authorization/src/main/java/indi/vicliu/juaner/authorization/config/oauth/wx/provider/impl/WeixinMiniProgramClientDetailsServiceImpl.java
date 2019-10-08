package indi.vicliu.juaner.authorization.config.oauth.wx.provider.impl;

import indi.vicliu.juaner.authorization.config.oauth.wx.provider.WeixinMiniProgramClientDetailsService;
import indi.vicliu.juaner.authorization.config.oauth.wx.WeixinMiniProgramClientDetails;
import indi.vicliu.juaner.authorization.data.mapper.OauthClientDetailsMapper;
import indi.vicliu.juaner.authorization.data.mapper.TblWeixinMiniProgramClientConfigMapper;
import indi.vicliu.juaner.authorization.domain.entity.OauthClientDetails;
import indi.vicliu.juaner.authorization.domain.entity.TblWeixinMiniProgramClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-04 20:18
 * @Description:
 */
@Service
public class WeixinMiniProgramClientDetailsServiceImpl implements WeixinMiniProgramClientDetailsService {

    @Autowired
    private TblWeixinMiniProgramClientConfigMapper weixinMiniProgramClientConfigMapper;

    @Autowired
    private OauthClientDetailsMapper oauthClientDetailsMapper;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        TblWeixinMiniProgramClientConfig weixinMiniProgramClientConfig = weixinMiniProgramClientConfigMapper.selectByPrimaryKey(clientId);
        if(weixinMiniProgramClientConfig == null){
            throw new ClientRegistrationException(clientId + "未进行配置");
        }
        OauthClientDetails oauthClientDetails = this.oauthClientDetailsMapper.selectByPrimaryKey(clientId);
        if(oauthClientDetails == null){
            throw new ClientRegistrationException(clientId + "未进行配置");
        }

        WeixinMiniProgramClientDetails clientDetails = new WeixinMiniProgramClientDetails(weixinMiniProgramClientConfig,oauthClientDetails);
        return clientDetails;
    }

    @Override
    public TblWeixinMiniProgramClientConfig getMiniProgramConfig(String channelId) {
        return weixinMiniProgramClientConfigMapper.selectByPrimaryKey(channelId);
    }
}
