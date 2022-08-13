package com.tanhua.dubbo.api;

public interface UserLikeApi {
    /**
     * 左滑右滑--喜欢处理
     * @param userId
     * @param likeUserId
     * @param isLike
     * @return
     */
    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike);
}
