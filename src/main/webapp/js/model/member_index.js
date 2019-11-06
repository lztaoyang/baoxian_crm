	/**
	*成员管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"demo/member",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {account:'',password:'',memberName:'',memberPinyin:'',sex:'',avatar:'',memberLevel:'',phone:'',email:'',locked:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {account:'',password:'',memberName:'',memberPinyin:'',sex:'',avatar:'',memberLevel:'',phone:'',email:'',locked:'',},
            //表格中选中的行的集合
            selectDatas: [],
            //字典值
            dicValues:{},
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		account: [
	    		Vali.utf8mb4Len(false,20)
	    	],
	    		password: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		memberName: [
	    		Vali.utf8mb4Len(false,30)
	    	],
	    		memberPinyin: [
	    		Vali.utf8mb4Len(false,64)
	    	],
	    		sex: [
	        	Vali.int()
	    	],
	    		avatar: [
	    		Vali.utf8mb4Len(false,500)
	    	],
	    		memberLevel: [
	        	Vali.int()
	    	],
	    		phone: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		email: [
	    		Vali.utf8mb4Len(false,64)
	    	],
	    		locked: [
	    	],
            },
            searchRules: {
		    		account: [
		    		Vali.utf8mb4Len(false,20)
		    	],
		    		password: [
		    		Vali.utf8mb4Len(false,50)
		    	],
		    		memberName: [
		    		Vali.utf8mb4Len(false,30)
		    	],
		    		memberPinyin: [
		    		Vali.utf8mb4Len(false,64)
		    	],
		    		sex: [
		        	Vali.int()
		    	],
		    		avatar: [
		    		Vali.utf8mb4Len(false,500)
		    	],
		    		memberLevel: [
		        	Vali.int()
		    	],
		    		phone: [
		    		Vali.utf8mb4Len(false,50)
		    	],
		    		email: [
		    		Vali.utf8mb4Len(false,64)
		    	],
		    		locked: [
		    	],
            }
        },
        created: function () {
            var self = this;
            self.readList();
        	self.readDicValues("SEX");
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    orderBy: "id",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                };
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/read/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //详情
            readDetail: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.detailFormVisible = true;
                        });
            },
            //弹出新增窗口
            showAddDialog: function () {
                this.editFormVisible = true;
                for (var item in this.entity) {
                    this.entity[item] = "";
                }
            },
            //弹出编辑窗口
            showEditDialog: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.editFormVisible = true;
                        });
            },
            //保存
            save: function () {
                var self = this;
                self.$refs["editForm"].validate(function(valid){
                    if (valid) {//校验通过
                        self.$http.post(self.sourceUrl, self.entity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.editFormVisible = false;
                                    if (self.entity.id) {//修改
                                        for (var i = 0; i < self.pageData.data.length; i++) {
                                            if (self.pageData.data[i].id === result.id) {
                                                Vue.set(self.pageData.data, i, result);
                                                break;
                                            }
                                        }
                                    } else {
                                        self.pageData.data.unshift(result);
                                    }
                                });
                    } else {
                        self.$message({
                            message: '请按要求输入内容',
                            type: 'warning'
                        });
                        return false;
                    }
                });

            },
            //删除
            del: function (entityIds) {
                var params = {};
                if (entityIds) {
                    //删除单个实体
                    if (entityIds instanceof Array) {
                        params.ids = entityIds;
                    } else {//批量删除
                        params.ids = [entityIds];
                    }
                }
                var self = this;
                self.$confirm('您确定要删除'+params.ids.length+'条记录吗?, 删除将无法恢复!', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    self.$http.delete(self.sourceUrl, {body: params})
                            .then(function (response) {
                                var tempIndexArray = [];
                                //同步更新界面中的表格
                                for(var j in params.ids){
                                    for(var i in self.pageData.data){
                                        if (self.pageData.data[i].id == params.ids[j]){
                                            self.pageData.data.splice(i,1);
                                            break;
                                        }
                                    }
                                }
                                console.log(response);
                                //self.entity = response.data.data;
                            });
                }).catch(function(){});
            },
            //批量删险
            delBatch: function () {
                var self = this;
                if (self.selectDatas.length == 0) {
                    this.$alert('请选择要删除的记录', '提示', {
                        confirmButtonText: '确定',
                        callback: function(action) {
                            //if ("confirm"==action){
                        }
                    });
                }else{
                    var ids = new Array();
                    for(var i in self.selectDatas){
                        ids.push(self.selectDatas[i].id);
                    }
                    self.del(ids);
                }

            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
                console.log(selection)
            },
            //字典取值
            readDicValues:function(type){
                var self = this;
                self.$http.put('/freeapi/read/dic', {type:type})
                        .then(function (response) {
                            self.dicValues[type] = response.data.data;
                        });
            },
            //从字典中取得文本
            getTxtByCode:function(type,code){
                for(var i in this.dicValues[type]){
                    if (this.dicValues[type][i].code==code){
                        return this.dicValues[type][i].txt;
                    }
                }
            },
			//转换布尔值为文字
            convertBoolean:function(v){
                if (typeof v == 'boolean'){
                    if ( v == true){
                        return "是"
                    }else if ( v == false){
                        return "否";
                    }
                }else if (typeof v == 'string'){
                    if ( v == "true"){
                        return "是"
                    }else if ( v == "false"){
                        return "否";
                    }
                }else{
                    return "否"
                }
            },
            //转换布尔值为文字
            columnFormatByBoolean:function(row, column){
                return this.convertBoolean(row[column.property]);
            },
            //转换为字典值
            columnFormatByDic:function(row, column){
                return this.getTxtByCode(column.property.toUpperCase(),row[column.property]);
            },
        }
    });