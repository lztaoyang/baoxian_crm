/**
 * Created by naxj on 17/5/17.
 */
/**
 * 部门选择组件
 */
//所有部门
var storeDics = new Vuex.Store({
    state: {
        data: {},
    },
    mutations: {
        SET_DATA: function (state, allDic) {
            Vue.set(state,"data",allDic);
        }
    },
    actions: {
        LOAD_DATA: function (context) {
        	var dic = JSON.parse(sessionStorage.getItem("dic"));
        	if (dic && dic != undefined && dic != "") {
        		context.commit("SET_DATA", dic);
			} else {
				Vue.http.get('sys/dic/read/all')
				.then(function (response) {
					if (null != response.data.data) {
						context.commit("SET_DATA", response.data.data);
						//全局变量【web存储】
                        sessionStorage.setItem("dic", JSON.stringify(response.data.data));
					}
				});
			}
        }
    }
});
storeDics.dispatch("LOAD_DATA");
//字典选择组件
var dicSelectComp = Vue.extend({
    template: `
    <el-select clearable filterable placeholder="选择" v-model="currentValue">
        <el-option
                v-for="(val, key) in dicValues"
                :key="key+''"
                :label="val+''"
                :value="key+''"
                >
        </el-option>
    </el-select>
    `,
    data: function () {
        return {

        };
    },
    computed: {
        dicValues: function () {
            if (typeof(this.$store.state.data[this.dicType]) == "undefined") {
                return [{"txt":"","code":""}];
            }else{
                return this.$store.state.data[this.dicType];
            }
        },
        currentValue: {
            // 动态计算currentValue的值
            get:function() {
                return this.value;
            },
            set:function(val) {
                this.$emit('input', val);
            }
        }
    },
    store: storeDics,
    props: {
        dicType: {
            type: String,
            default: ""
        },
    value:{type: String},
    }

});
//字典显示组件
var dicTxtComp = Vue.extend({
    template: `
    <span>{{dicTxt}}</span>
    `,
    data: function () {
        return {};
    },
    computed: {
        dicTxt: function () {
            if (typeof(this.$store.state.data[this.dicType]) != "undefined"){
                for(var code in this.$store.state.data[this.dicType]){
                    if (code == this.dicCode){
                        return this.$store.state.data[this.dicType][code];
                    }
                }
            }else{
                return "";
            }
        }
 
    },
    store: storeDics,
    props: {
        dicCode: {
            default: ""
        },
        dicType: {
            type: String,
            default: ""
        }
    }
});
//字典选择组件（产品名称专用【加宽型】）
var dicSelectCompBroaden = Vue.extend({
    template: `
    <el-select style="width:360px" clearable filterable placeholder="请选择产品" v-model="currentValue">
        <el-option
                v-for="(val, key) in dicValues"
                :key="key+''"
                :label="val+''"
                :value="key+''"
                >
        </el-option>
    </el-select>
    `,
    data: function () {
        return {

        };
    },
    computed: {
        dicValues: function () {
            if (typeof(this.$store.state.data[this.dicType]) == "undefined") {
                return [{"txt":"","code":""}];
            }else{
                return this.$store.state.data[this.dicType];
            }
        },
        currentValue: {
            // 动态计算currentValue的值
            get:function() {
                return this.value;
            },
            set:function(val) {
                this.$emit('input', val);
            }
        }
    },
    store: storeDics,
    props: {
        dicType: {
            type: String,
            default: ""
        },
    value:{type: String},
    }

});
//字典选择组件（支持多选）
var dicSelectCompMultiple = Vue.extend({
	template: `
	<el-select clearable multiple filterable placeholder="请选择产品" v-model="currentValue">
<el-option
v-for="(val, key) in dicValues"
	:key="key+''"
		:label="val+''"
			:value="key+''"
				>
</el-option>
</el-select>
`,
data: function () {
	return {
		
	};
},
computed: {
	dicValues: function () {
		if (typeof(this.$store.state.data[this.dicType]) == "undefined") {
			return [{"txt":"","code":""}];
		}else{
			return this.$store.state.data[this.dicType];
		}
	},
	currentValue: {
		// 动态计算currentValue的值
		get:function() {
			return this.value;
		},
		set:function(val) {
			this.$emit('input', val);
		}
	}
},
store: storeDics,
props: {
	dicType: {
		type: String,
	default: ""
	},
	value:{type: String},
}

});
Vue.component('lz-dic-select', dicSelectComp);
Vue.component('lz-dic-txt', dicTxtComp);
Vue.component('lz-dic-select-broaden', dicSelectCompBroaden);
Vue.component('lz-dic-select-multiple', dicSelectCompMultiple);