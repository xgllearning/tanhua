package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {

    @DubboReference
    private UserLocationApi userLocationApi;

    /**
     * 更新地理位置
     * @param longitude
     * @param latitude
     * @param address
     */
    public void updateLocation(Double longitude, Double latitude, String address) {
        //1.调用api根据当前用户id在user_localtion表执行保存更新操作
        Boolean flag = userLocationApi.updateLocation(UserHolder.getUserId(),longitude,latitude,address);
        if(!flag) {
            throw  new BusinessException(ErrorResult.error());
        }
    }
}
