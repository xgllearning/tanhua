package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.model.domain.UserInfo;

public interface BlackListApi {
    /**
     * 查询黑名单用户详细信息
     * @param userId
     * @param page
     * @param size
     * @return
     */
    Page<UserInfo> findByUserId(Long userId, int page, int size);

    /**
     * 移除黑名单用户--根据用户id和黑名单用户id
     * @param userId
     * @param blackUserId
     */
    void deleteBlackList(Long userId, Long blackUserId);
}
