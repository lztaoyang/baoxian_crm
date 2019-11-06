package com.lazhu.resource.test.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author hz
 * @since 2018-10-11
 */
@ApiModel(value = "",description="test")
@TableName("resource_test")
public class TestResource extends BaseModel {
    private static final long serialVersionUID = 1L;
    //mobile  
    @ApiModelProperty(value = "", required = false)
    @TableField("mobile")
    private String mobile;
    //director_id  
    @ApiModelProperty(value = "", required = false)
    @TableField("director_id")
    private Long directorId;

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getMobile()
    {
        return this.mobile;
    }

    public void setDirectorId(Long directorId)
    {
        this.directorId = directorId;
    }

    public Long getDirectorId()
    {
        return this.directorId;
    }
}
