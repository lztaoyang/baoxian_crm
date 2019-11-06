	/**
	*菜单管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
            sourceUrl: "sys/menu",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {
                menuName: '',
                menuType: '',
                parentId: '',
                iconcls: '',
                request: '',
                expand: '',
                sortNo: '',
                isShow: '',
                permission: '',
            },
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            allMenu:[],
            //当前操作的实体（编辑/新增）
            entity: {
                menuName: '',
                menuType: '',
                parentId: '',
                request: '',
                expand: '',
                sortNo: '',
                isShow: '',
                permission: '',
            },
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
                menuName: [
                    {required: true, message: '此项不可为空', trigger: 'blur'},
                    Vali.utf8mb4Len(false, 50)
                ],
                menuType: [{required: true, message: '此项不可为空', trigger: 'blur'}],
                parentId: [
                    {required: true, message: '此项不可为空', trigger: 'blur'},
                    Vali.long()
                ],
                request: [
                    {required: true, message: '此项不可为空', trigger: 'blur'},
                    Vali.utf8mb4Len(false, 100)
                ],
                expand: [
                    {required: true, message: '此项不可为空', trigger: 'blur'},
                ],
                sortNo: [
                    {required: true, message: '此项不可为空', trigger: 'blur'},
                    Vali.int()
                ],
                isShow: [
                    {required: true, message: '此项不可为空', trigger: 'blur'},
                ]
            },
        },

        created: function () {
            var self = this;
            self.readAll();
            self.readList();
        },
        methods: {
            readAll: function () {
                this.$http.put(this.sourceUrl+'/read/list', {})
                    .then(function (response) {
                        this.allMenu = response.data.data;
                        this.allMenu.unshift({"id":"0","menuName":"根节点","parentId":0})
                    });
            },
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
                this.$http.put(this.sourceUrl+'/read/page', params)
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
            converId2Name:function(menuId){
                for(var i in this.allMenu){
                    if (this.allMenu[i].id == menuId){
                        return this.allMenu[i].menuName;
                    }
                }
            }
        }
    });