package com.lazhu.product.insuranceproduct.entity;

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
 * @since 2017-06-20
 */
/**
 * @author hz
 * @date 2017年7月28日  
 * @version 1.0.0 
 */
/**
 * @author hz
 * @date 2017年7月28日  
 * @version 1.0.0 
 */
@ApiModel(value = "",description="insuranceProduct")
@TableName("bd_insurance_product")
public class InsuranceProduct extends BaseModel {
    private static final long serialVersionUID = 1L;
    //name 名称 
    @ApiModelProperty(value = "名称", required = false)
    @TableField("name")
    private String name;
    //code 名称 
    @ApiModelProperty(value = "产品编码", required = false)
    @TableField("code")
    private String code;
    //insurance_type 种类 
    @ApiModelProperty(value = "保险种类", required = false)
    @TableField("type")
    private Integer type;
    
    @ApiModelProperty(value = "是否上架", required = false)
    @TableField("is_putaway")
    private Integer isPutaway;

    @ApiModelProperty(value = "新客数", required = false)
    @TableField("effective_num")
    private Long effectiveNum;
    @ApiModelProperty(value = "总签单数", required = false)
    @TableField("sign_num")
    private Long signNum;
    @ApiModelProperty(value = "总签单金额", required = false)
    @TableField("sum_amount")
    private Double sumAmount;

    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getIsPutaway() {
		return isPutaway;
	}
	public void setIsPutaway(Integer isPutaway) {
		this.isPutaway = isPutaway;
	}
	public Long getEffectiveNum() {
		return effectiveNum;
	}
	public void setEffectiveNum(Long effectiveNum) {
		this.effectiveNum = effectiveNum;
	}
	public Long getSignNum() {
		return signNum;
	}
	public void setSignNum(Long signNum) {
		this.signNum = signNum;
	}
	public Double getSumAmount() {
		return sumAmount;
	}
	public void setSumAmount(Double sumAmount) {
		this.sumAmount = sumAmount;
	}
  
}
