package com.lazhu.crm.developdaycall.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 经纪人日通话统计表
 * </p>
 *
 * @author hz
 * @since 2018-01-09
 */
@ApiModel(value = "经纪人日通话统计表",description="developDayCall")
@TableName("crm_develop_day_call")
public class DevelopDayCall extends BaseModel {
    private static final long serialVersionUID = 1L;
    //day_date  
    @ApiModelProperty(value = "日期", required = false)
    @TableField("day_date")
    private Date dayDate;
    //saler_id 经纪人ID 
    @ApiModelProperty(value = "经纪人ID", required = false)
    @TableField("saler_id")
    private Long salerId;
    //manager_id 经理ID 
    @ApiModelProperty(value = "经理ID", required = false)
    @TableField("manager_id")
    private Long managerId;
    //director_id 总监ID 
    @ApiModelProperty(value = "总监ID", required = false)
    @TableField("director_id")
    private Long directorId;
    //minister_id 总经理ID 
    @ApiModelProperty(value = "总经理ID", required = false)
    @TableField("minister_id")
    private Long ministerId;
    //call_num 拨打次数 
    @ApiModelProperty(value = "拨打次数", required = false)
    @TableField("call_num")
    private Integer callNum;
    //through_num 接通次数 
    @ApiModelProperty(value = "接通次数", required = false)
    @TableField("through_num")
    private Integer throughNum;
    //call_valid_num 有效接通次数 
    @ApiModelProperty(value = "有效接通次数", required = false)
    @TableField("call_valid_num")
    private Integer callValidNum;
    //total_length 总通话时长 
    @ApiModelProperty(value = "总通话时长", required = false)
    @TableField("total_length")
    private Long totalLength;
    //valid_length 有效通话时长 
    @ApiModelProperty(value = "有效通话时长", required = false)
    @TableField("valid_length")
    private Long validLength;
    //tochat_num 可聊资源数 
    @ApiModelProperty(value = "可聊资源数", required = false)
    @TableField("tochat_num")
    private Integer tochatNum;
    //disposed_num 意向资源数 
    @ApiModelProperty(value = "意向资源数", required = false)
    @TableField("disposed_num")
    private Integer disposedNum;
    //promote_resource_num 推广资源数 
    @ApiModelProperty(value = "推广资源数", required = false)
    @TableField("promote_resource_num")
    private Integer promoteResourceNum;
    //extract_num 抽取资源数 
    @ApiModelProperty(value = "抽取资源数", required = false)
    @TableField("extract_num")
    private Integer extractNum;
    //lose_resource_num 丢弃资源数 
    @ApiModelProperty(value = "丢弃资源数", required = false)
    @TableField("lose_resource_num")
    private Integer loseResourceNum;
    //sign_num 签单数 
    @ApiModelProperty(value = "签单数", required = false)
    @TableField("sign_num")
    private Integer signNum;
    
    
	public Date getDayDate() {
		return dayDate;
	}
	public void setDayDate(Date dayDate) {
		this.dayDate = dayDate;
	}
	public Long getSalerId() {
		return salerId;
	}
	public void setSalerId(Long salerId) {
		this.salerId = salerId;
	}
	public Long getManagerId() {
		return managerId;
	}
	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}
	public Long getDirectorId() {
		return directorId;
	}
	public void setDirectorId(Long directorId) {
		this.directorId = directorId;
	}
	public Long getMinisterId() {
		return ministerId;
	}
	public void setMinisterId(Long ministerId) {
		this.ministerId = ministerId;
	}
	public Integer getCallNum() {
		return callNum;
	}
	public void setCallNum(Integer callNum) {
		this.callNum = callNum;
	}
	public Integer getThroughNum() {
		return throughNum;
	}
	public void setThroughNum(Integer throughNum) {
		this.throughNum = throughNum;
	}
	public Integer getCallValidNum() {
		return callValidNum;
	}
	public void setCallValidNum(Integer callValidNum) {
		this.callValidNum = callValidNum;
	}
	public Long getTotalLength() {
		return totalLength;
	}
	public void setTotalLength(Long totalLength) {
		this.totalLength = totalLength;
	}
	public Long getValidLength() {
		return validLength;
	}
	public void setValidLength(Long validLength) {
		this.validLength = validLength;
	}
	public Integer getTochatNum() {
		return tochatNum;
	}
	public void setTochatNum(Integer tochatNum) {
		this.tochatNum = tochatNum;
	}
	public Integer getDisposedNum() {
		return disposedNum;
	}
	public void setDisposedNum(Integer disposedNum) {
		this.disposedNum = disposedNum;
	}
	public Integer getPromoteResourceNum() {
		return promoteResourceNum;
	}
	public void setPromoteResourceNum(Integer promoteResourceNum) {
		this.promoteResourceNum = promoteResourceNum;
	}
	public Integer getExtractNum() {
		return extractNum;
	}
	public void setExtractNum(Integer extractNum) {
		this.extractNum = extractNum;
	}
	public Integer getLoseResourceNum() {
		return loseResourceNum;
	}
	public void setLoseResourceNum(Integer loseResourceNum) {
		this.loseResourceNum = loseResourceNum;
	}
	public Integer getSignNum() {
		return signNum;
	}
	public void setSignNum(Integer signNum) {
		this.signNum = signNum;
	}

}
