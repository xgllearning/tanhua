package com.tanhua.dubbo.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.model.domain.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 查询黑名单用户详细信息--根据用户的id
     * @param page
     * @param userId
     * @return
     */
    @Select("SELECT * FROM tb_user_info WHERE id in (SELECT black_user_id FROM tb_black_list WHERE user_id=#{userId})")
    Page<UserInfo> findBlackList(@Param("page") Page<UserInfo> page, @Param("userId") Long userId);
}
