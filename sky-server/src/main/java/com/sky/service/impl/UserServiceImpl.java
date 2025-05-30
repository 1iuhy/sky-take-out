package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String openid = getOpenId(userLoginDTO.getCode());

        //判断openid是否为空
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前用户是否为新用户
        //如果是新用户啊，自动完成注册
        User user = userMapper.getUserByOpenId(openid);
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回这个用户对象

        return user;
    }

    /**
     * 调用微信的接口服务，获取openid
     * @param code
     * @return
     */
    private String getOpenId(String code){
        //调用微信接口服务，获得当前微信用户的微信openid
        Map<String,String> map = new HashMap<String,String>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN,map);

        //微信接口的返回值是一个json对象，我们可以使用fastjson中的parseObject创建成为JSONObject对象
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
