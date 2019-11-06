package com.lazhu.t.allotplanning.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 推广分配计划表
 * </p>
 *
 * @author hz
 * @since 2018-05-10
 */
@ApiModel(value = "推广分配计划表",description="allotPlanning")
@TableName("t_allot_planning")
public class AllotPlanning extends BaseModel {
    private static final long serialVersionUID = 1L;
    //start_time 开始时间 
    @ApiModelProperty(value = "开始时间", required = false)
    @TableField("start_time")
    private Date startTime;
    //end_time 结束时间 
    @ApiModelProperty(value = "结束时间", required = false)
    @TableField("end_time")
    private Date endTime;

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return this.startTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return this.endTime;
    }

}
