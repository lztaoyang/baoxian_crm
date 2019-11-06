/**
 * Created by naxj on 17/5/17.
 */
/**
 * 部门选择组件
 */
//将部门转换成树形结构
var convertDepts2Tree =  function (depts1,withOutLeaf) {
    var temp = [], lev = 0;
    var depts = clone(depts1);
    var forFn_convertDepts2SelectItems = function (arr, dept, lev) {
        for (var i in arr) {
            var item = arr[i];
            if (typeof(withOutLeaf)!= "undefined" && withOutLeaf == true && item.leaf == 1) {
                continue;
            }
            item.label = item.deptName;
            item.value = item.id;
            if (item.parentId == dept.id) {
                if (typeof(dept.children) == "undefined") {
                    dept.children = [];
                }
                dept.children.push(item);
                item.lev = lev;
                forFn_convertDepts2SelectItems(arr, item, lev + 1);
            }
        }
    };
    for (var j in depts) {
        if (depts[j].parentId == "0") {
            forFn_convertDepts2SelectItems(depts, depts[j], lev);
            //return depts[j];
            temp.push(depts[j])
        }
    }
    return temp;
};
//所有部门
var storeDepts = new Vuex.Store({
    state: {
        data: [],
        selectItems:[],
        selectItemsWithOutLeaf:[]
    },
    mutations:{
        SET_DATA:function(state,depts){
            state.data = depts;
        },
        SET_SELECT_ITEMS:function(state,selectItems){
            state.selectItems = selectItems;
        },
        SET_SELECT_ITEMS_WITH_OUT_LEAF:function(state,selectItems){
            state.selectItemsWithOutLeaf = selectItems;
        }
    },
    actions:{
        LOAD_DATA:function(context){
            Vue.http.put('sys/dept/read/list', {
                    asc: false,
                    orderBy: "id",
                    pageNum: (1),
                    pageSize: (1000)
                })
                .then(function (response) {
                    context.commit("SET_DATA", response.data.data);
                    var tempItems = convertDepts2Tree(response.data.data);
                    var tempItemsWithOutLeaf = convertDepts2Tree(response.data.data,true);
                    context.commit("SET_SELECT_ITEMS", tempItems);
                    context.commit("SET_SELECT_ITEMS_WITH_OUT_LEAF", tempItemsWithOutLeaf);
                    //vueSetAttribute();
                });
        }
    }
});
//加载全部部门
storeDepts.dispatch("LOAD_DATA");
//部门选择组件
var deptSelectComp = Vue.extend({
    template: `
    <el-cascader
            placeholder="选择部门"
            :options="selectItems"
            change-on-select
            @change="onChange"
            :show-all-levels="showAllLevels"
            :value='curValue'
    ></el-cascader>
    `,
    data: function () {
        return {};
    },
    computed:{
        curValue:function () {
            return this.buildValue();
        },
        selectItems:function () {
            if (this.withOutLeaf){
                return this.$store.state.selectItemsWithOutLeaf
            }else{
                return this.$store.state.selectItems
            }
        }
    },
    store:storeDepts,
    props: {
        ////是否去除最终的叶子节点
        withOutLeaf: {
            type: Boolean,
            default: false
        },
        //返回时是否去除父节点,只返回最终选择的节点,否则返回最终选择的节点及它的父节点
        resultWithOutParent: {
            type: Boolean,
            default: false
        },
        showAllLevels: {
            type: Boolean,
            default: false
        },
        inputValue: {
            type: String,
            default: ""
        },
    },
    methods: {
        buildValue: function () {
            var self = this;
            if (typeof(self.inputValue) == "undefined" && self.inputValue == "undefined") {
                return [];
            } else {
                var tempCurValue = [];
                //self.curValue.splice(0, self.curValue.length);
                var tempArray = self.familyTree(self.$store.state.data, self.inputValue);
                for (var i in tempArray) {
                    tempCurValue.unshift(tempArray[i]);
                }
                return tempCurValue;
            }
        },
        //从根节点向上找到它全部父节点
        familyTree: function (depts, id) {
            var temp = [];
            var forFn = function (arr, pid) {
                if (typeof(pid) == "undefined" || pid == "") {
                    return;
                }
                for (var i = 0; i < arr.length; i++) {
                    var item = arr[i];
                    if (item.id == pid) {
                        temp.push(item.id);
                        if (item.parentId) {
                            forFn(arr, item.parentId);
                        }
                    }
                }
            };
            for (var j in depts) {
                if (depts[j].id == id) {
                    temp.push(depts[j].id);
                    forFn(depts, depts[j].parentId);
                    return temp;
                }
            }
        },

        onChange: function (v) {
            var self = this;
            if (v && v instanceof Array && v.length > 0) {
                var rv = v;
                if (self.resultWithOutParent) {
                    rv = v[v.length - 1]
                }
                try {
                    self.$emit('input', rv);
                } catch (e) {
                }
                ;
            }
        }
    }
});
//根据部门ID显示部门名称组件
var deptNameComp = Vue.extend({
    template: `
    <span>{{deptName}}</span>
    `,
    data: function () {
        return {};
    },
    computed:{
        deptName:function () {
            var self = this;
            for(var i in self.$store.state.data){
                if (self.$store.state.data[i].id==this.deptId){
                    return self.$store.state.data[i].deptName;
                }
            }
        }
    },
    store:storeDepts,
    props: {
        deptId: {
            type: String,
            default: ""
        },
    },
    methods: {

    }
});
Vue.component('lz-dept-select', deptSelectComp);
Vue.component('lz-dept-name', deptNameComp);