package com.lazhu.sys.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 配置表
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
@ApiModel(value = "配置表",description="properties")
@TableName("sys_properties")
public class Properties extends BaseModel {
    private static final long serialVersionUID = 1L;
    //
    @ApiModelProperty(value = "名称", required = false)
    @TableField("name")
    private String name;
    //key_string 键 
    @ApiModelProperty(value = "键", required = false)
    @TableField("key_string")
    private String keyString;
    //value_string 值 
    @ApiModelProperty(value = "值", required = false)
    @TableField("value_string")
    private String valueString;

    
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKeyString(String keyString)
    {
        this.keyString = keyString;
    }

    public String getKeyString()
    {
        return this.keyString;
    }

    public void setValueString(String valueString)
    {
        this.valueString = valueString;
    }

    public String getValueString()
    {
        return this.valueString;
    }
    
}
