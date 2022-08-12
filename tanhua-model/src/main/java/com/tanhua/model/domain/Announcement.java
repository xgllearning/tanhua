package com.tanhua.model.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement  implements Serializable {

    private String id;

    private String title;

    private String description;
    @TableField("created")
    private Date createDate;

}
