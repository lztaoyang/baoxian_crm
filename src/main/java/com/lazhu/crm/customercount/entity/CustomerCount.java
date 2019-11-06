package com.lazhu.crm.customercount.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 会员统计表
 *
  * @author ty
 * @date 2018年11月21日
 */
@ApiModel(value = "会员统计表",description="customerCount")
@TableName("crm_customer_count")
public class CustomerCount extends BaseModel {
    private static final long serialVersionUID = 1L;
    //customer_id  会员id
    @ApiModelProperty(value = "会员id", required = false)
    @TableField("customer_id")
    private Long customerId;
    //allot_id  推广分配id
    @ApiModelProperty(value = "推广分配id", required = false)
    @TableField("allot_id")
    private Long allotId;
    //apply_num  签单总次数
    @ApiModelProperty(value = "签单总次数", required = false)
    @TableField("apply_num")
    private Integer applyNum = 0;
    //upgrade_num  升级总次数
    @ApiModelProperty(value = "升级总次数", required = false)
    @TableField("upgrade_num")
    private Integer upgradeNum = 0;
    //refund_num  犹豫期内退保总次数
    @ApiModelProperty(value = "犹豫期内退保总次数", required = false)
    @TableField("refund_inner_num")
    private Integer refundInnerNum = 0;
    //refund_num  犹豫期外退保总次数
    @ApiModelProperty(value = "犹豫期外退保总次数", required = false)
    @TableField("refund_outer_num")
    private Integer refundOuterNum = 0;
    //apply_money  签单总金额
    @ApiModelProperty(value = "签单总金额", required = false)
    @TableField("apply_money")
    private Double applyMoney = 0.00;
    //upgrade_money  升级总金额
    @ApiModelProperty(value = "升级总金额", required = false)
    @TableField("upgrade_money")
    private Double upgradeMoney = 0.00;
    //refund_money  犹豫期内退保总金额
    @ApiModelProperty(value = "犹豫期内退保总金额", required = false)
    @TableField("refund_inner_money")
    private Double refundInnerMoney = 0.00;
    //refund_money  犹豫期外退保总金额
    @ApiModelProperty(value = "犹豫期外退保总金额", required = false)
    @TableField("refund_outer_money")
    private Double refundOuterMoney = 0.00;
    //insure_money  总保费
    @ApiModelProperty(value = "总保费", required = false)
    @TableField("insure_money")
    private Double insureMoney = 0.00;
    //valid_money  有效保费
    @ApiModelProperty(value = "有效保费", required = false)
    @TableField("valid_money")
    private Double validMoney = 0.00;
    //channel  渠道
    @ApiModelProperty(value = "渠道", required = false)
    @TableField("channel")
    private String channel;
    //executer  执行人
    @ApiModelProperty(value = "执行人", required = false)
    @TableField("executer")
    private String executer;
    //plan_code  广告
    @ApiModelProperty(value = "广告", required = false)
    @TableField("plan_code")
    private String planCode;
    //enter_time  客户提交时间
    @ApiModelProperty(value = "客户提交时间", required = false)
    @TableField("enter_time")
    private Date enterTime;
    //enter_time_solt  时段
    @ApiModelProperty(value = "时段", required = false)
    @TableField("enter_time_solt")
    private String enterTimeSolt;
    //age  年龄
    @ApiModelProperty(value = "年龄", required = false)
    @TableField("age")
    private Integer age;
    //province  投保人省
    @ApiModelProperty(value = "投保人省", required = false)
    @TableField("province")
    private String province;
    //city  投保人市
    @ApiModelProperty(value = "投保人市", required = false)
    @TableField("city")
    private String city;
    //sex  投保人性别
    @ApiModelProperty(value = "投保人性别", required = false)
    @TableField("sex")
    private Integer sex;
    //name  投保人姓名
    @ApiModelProperty(value = "投保人姓名", required = false)
    @TableField("name")
    private String name;
    
    //saler_id 经纪人ID 
    @ApiModelProperty(value = "经纪人ID", required = false)
    @TableField("saler_id")
    private Long salerId;

    public Long getSalerId() {
		return salerId;
	}

	public void setSalerId(Long salerId) {
		this.salerId = salerId;
	}

	public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return this.customerId;
    }

    public void setAllotId(Long allotId)
    {
        this.allotId = allotId;
    }

    public Long getAllotId()
    {
        return this.allotId;
    }

    public void setApplyNum(Integer applyNum)
    {
        this.applyNum = applyNum;
    }

    public Integer getApplyNum()
    {
        return this.applyNum;
    }

    public void setUpgradeNum(Integer upgradeNum)
    {
        this.upgradeNum = upgradeNum;
    }

    public Integer getUpgradeNum()
    {
        return this.upgradeNum;
    }

    public Integer getRefundInnerNum() {
		return refundInnerNum;
	}

	public void setRefundInnerNum(Integer refundInnerNum) {
		this.refundInnerNum = refundInnerNum;
	}

	public Integer getRefundOuterNum() {
		return refundOuterNum;
	}

	public void setRefundOuterNum(Integer refundOuterNum) {
		this.refundOuterNum = refundOuterNum;
	}

	public void setApplyMoney(Double applyMoney)
    {
        this.applyMoney = applyMoney;
    }

    public Double getApplyMoney()
    {
        return this.applyMoney;
    }

    public void setUpgradeMoney(Double upgradeMoney)
    {
        this.upgradeMoney = upgradeMoney;
    }

    public Double getUpgradeMoney()
    {
        return this.upgradeMoney;
    }

    public Double getRefundInnerMoney() {
		return refundInnerMoney;
	}

	public void setRefundInnerMoney(Double refundInnerMoney) {
		this.refundInnerMoney = refundInnerMoney;
	}

	public Double getRefundOuterMoney() {
		return refundOuterMoney;
	}

	public void setRefundOuterMoney(Double refundOuterMoney) {
		this.refundOuterMoney = refundOuterMoney;
	}

	public void setInsureMoney(Double insureMoney)
    {
        this.insureMoney = insureMoney;
    }

    public Double getInsureMoney()
    {
        return this.insureMoney;
    }

    public void setValidMoney(Double validMoney)
    {
        this.validMoney = validMoney;
    }

    public Double getValidMoney()
    {
        return this.validMoney;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return this.channel;
    }

    public void setExecuter(String executer)
    {
        this.executer = executer;
    }

    public String getExecuter()
    {
        return this.executer;
    }

    public void setPlanCode(String planCode)
    {
        this.planCode = planCode;
    }

    public String getPlanCode()
    {
        return this.planCode;
    }

    public void setEnterTime(Date enterTime)
    {
        this.enterTime = enterTime;
    }

    public Date getEnterTime()
    {
        return this.enterTime;
    }

    public void setEnterTimeSolt(String enterTimeSolt)
    {
        this.enterTimeSolt = enterTimeSolt;
    }

    public String getEnterTimeSolt()
    {
        return this.enterTimeSolt;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public Integer getAge()
    {
        return this.age;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getProvince()
    {
        return this.province;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return this.city;
    }

    public void setSex(Integer sex)
    {
        this.sex = sex;
    }

    public Integer getSex()
    {
        return this.sex;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
    	sb.append(", customerId=").append(getCustomerId());
    	sb.append(", allotId=").append(getAllotId());
    	sb.append(", applyNum=").append(getApplyNum());
    	sb.append(", upgradeNum=").append(getUpgradeNum());
    	sb.append(", refundInnerNum=").append(getRefundInnerNum());
    	sb.append(", refundOuterNum=").append(getRefundOuterNum());
    	sb.append(", applyMoney=").append(getApplyMoney());
    	sb.append(", upgradeMoney=").append(getUpgradeMoney());
    	sb.append(", refundinnerMoney=").append(getRefundInnerMoney());
    	sb.append(", refundOuterMoney=").append(getRefundOuterMoney());
    	sb.append(", insureMoney=").append(getInsureMoney());
    	sb.append(", validMoney=").append(getValidMoney());
    	sb.append(", channel=").append(getChannel());
    	sb.append(", executer=").append(getExecuter());
    	sb.append(", planCode=").append(getPlanCode());
    	sb.append(", enterTime=").append(getEnterTime());
    	sb.append(", enterTimeSolt=").append(getEnterTimeSolt());
    	sb.append(", age=").append(getAge());
    	sb.append(", province=").append(getProvince());
    	sb.append(", city=").append(getCity());
    	sb.append(", sex=").append(getSex());
    	sb.append(", name=").append(getName());
		sb.append("]");
		return sb.toString();
	}
}
