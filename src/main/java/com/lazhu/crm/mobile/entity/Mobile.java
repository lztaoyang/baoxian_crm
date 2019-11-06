package com.lazhu.crm.mobile.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 客户联系方式表
 * </p>
 *
 * @author hz
 * @since 2018-02-01
 */
@ApiModel(value = "客户联系方式表",description="mobile")
@TableName("crm_mobile")
public class Mobile extends BaseModel {
    private static final long serialVersionUID = 1L;
    //customer_id 资源ID 
    @ApiModelProperty(value = "资源ID", required = false)
    @TableField("customer_id")
    private Long customerId;
    //
    @ApiModelProperty(value = "手机号码", required = false)
    @TableField(exist = false)
    private String mobile;
    //
    @ApiModelProperty(value = "手机掩码", required = false)
    @TableField("fuzzy_mobile")
    private String fuzzyMobile;
    //
    @ApiModelProperty(value = "MD5加密", required = false)
    @TableField("md5_mobile")
    private String md5Mobile;
    //
    @ApiModelProperty(value = "私钥加密", required = false)
    @TableField("rsa_mobile")
    private String rsaMobile;

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return this.customerId;
    }

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFuzzyMobile() {
		return fuzzyMobile;
	}

	public void setFuzzyMobile(String fuzzyMobile) {
		this.fuzzyMobile = fuzzyMobile;
	}

	public String getMd5Mobile() {
		return md5Mobile;
	}

	public void setMd5Mobile(String md5Mobile) {
		this.md5Mobile = md5Mobile;
	}

	public String getRsaMobile() {
		return rsaMobile;
	}

	public void setRsaMobile(String rsaMobile) {
		this.rsaMobile = rsaMobile;
	}

}
