package com.stormfives.admin.common.util;


import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.predefined.*;
import com.github.bingoohuang.patchca.utils.encoder.EncoderHelper;
import com.stormfives.admin.common.exception.InvalidArgumentException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author zxb
 */
@Service
public class PictureCodeService {
    private static final Logger logger = LoggerFactory.getLogger(PictureCodeService.class);

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    /**
     * @param
     */
    public Map getPcrimg() throws InvalidArgumentException {
        try {
            ConfigurableCaptchaService cs = PictureCodeUtil.getCS();
            Random random = PictureCodeUtil.getRandom();
            switch (random.nextInt(5)) {
                case 0:
                    cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
                    break;
                case 1:
                    cs.setFilterFactory(new MarbleRippleFilterFactory());
                    break;
                case 2:
                    cs.setFilterFactory(new DoubleRippleFilterFactory());
                    break;
                case 3:
                    cs.setFilterFactory(new WobbleRippleFilterFactory());
                    break;
                case 4:
                    cs.setFilterFactory(new DiffuseRippleFilterFactory());
                    break;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String code = EncoderHelper.getChallangeAndWriteImage(cs, "png", bos);
            String token = UUID.randomUUID().toString();
            byte[] image = bos.toByteArray();

            String key = "picCode:" + token;
            redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("image", Base64.getEncoder().encode(image));
            responseMap.put("token", token);
            return responseMap;
        } catch (Exception e) {
            throw new InvalidArgumentException(messageSourceUtil.getMessage("error"));
        }
    }

    /**
     * @return
     */
    public Boolean  validVcode(String token, String vcode) {
        vcode = vcode.trim();
        String key = "picCode:" + token;
        String originVcode = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(vcode) && vcode.equalsIgnoreCase(originVcode)) {
            return true;
        }
        return false;
    }
}
