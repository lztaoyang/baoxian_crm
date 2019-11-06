/**
 *角色信息表管理
 */
var vuePage = new Vue({
    el: '#app',
    data: {
        sourceUrl: "sys/role",
        //是否显示编辑窗口
        editFormVisible: false,
        //是否显示详情窗口
        detailFormVisible: false,
        //是否权限窗口
        permissionFormVisible: false,
        ////查询参数
        //searchParams: {roleName:'',roleType:''},
        //分页数据,其中包含分页信息与数据列表
        pageData: {},
        //当前操作的实体（编辑/新增）
        entity: {roleName: '', deptId: '', roleType: '',isMobileVisible:''},
        allMenu: [],
        currentRoleMeuns: {"roleId": '', "menuIds": []},
        menuTreeData: [],
        //表格中选中的行的集合
        selectDatas: [],
        //表单校验规则 api:https://github.com/yiminghe/async-validator
        rules: {
            roleName: [
                Vali.utf8mb4Len(false, 50)
            ],
            deptId: [
                Vali.long()
            ],
            roleType: [
                {required: true, message: '此项不可为空', trigger: 'blur'},
                Vali.int()
            ],
        }
    },
    created: function () {
        var self = this;
        self.readList();
        self.readAllMenu();
    },
    methods: {
        readAllMenu: function () {
            var self = this;
            this.$http.put('sys/menu/read/list', {})
                .then(function (response) {
                    self.allMenu = response.data.data;
                    var tempList = self.convertMenus2Tree(response.data.data);//this.allMenu
                    self.menuTreeData = tempList;
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
                if (typeof this.searchParams[item] == 'boolean') {
                    params[item] = this.searchParams[item];
                } else if (this.searchParams[item] != "") {
                    params[item] = this.searchParams[item];
                }
            }
            this.$http.put(this.sourceUrl + '/read/list', params)
                .then(function (response) {
                    this.pageData = response.data;
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
            this.$http.put(this.sourceUrl + '/read/detail', {id: entityId})
                .then(function (response) {
                    this.entity = response.data.data;
                    this.editFormVisible = true;
                });
        },
        //弹出菜单窗口
        showMenuDialog: function (entityId) {
            var self = this;
            self.permissionFormVisible = true;
            self.$http.put('sys/role/read/menu', {roleId: entityId})
                .then(function (response) {
                    self.permissionFormVisible = true;
                    self.$set(self.currentRoleMeuns, "roleId", entityId);
                    var temp = [];
                    for (var i in response.data.data) {
                        temp.push(response.data.data[i].menuId);
                    }
                    self.$refs.menuTree.setCheckedKeys(temp);
                });
        },
        saveRoleMenus: function () {
            var self = this;
            var menus = self.$refs.menuTree.getCheckedNodes();
            var params = [];
            for(var i in menus){
                if (menus[i].type == "prem" || menus[i].type == "dir"){
                    var tempId = menus[i].id;
                    if (menus[i].type == "prem"){
                        tempId = menus[i].id.substring(0,menus[i].id.indexOf("-"));
                    }
                    var paramItem = {
                        "menuId":tempId,
                        "roleId":self.currentRoleMeuns.roleId,
                        "permission":menus[i].key
                    }
                    params.push(paramItem);
                }
                //console.log(menus[i].id);
            }
            self.$http.post('sys/role/update/permission', params)
                .then(function (response) {
                    self.permissionFormVisible = false;
                });
        },
        //保存
        save: function () {
            var self = this;
            self.$refs["editForm"].validate(function (valid) {
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
            self.$confirm('您确定要删除' + params.ids.length + '条记录吗?, 删除将无法恢复!', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function () {
                self.$http.delete(self.sourceUrl, {body: params})
                    .then(function (response) {
                        var tempIndexArray = [];
                        //同步更新界面中的表格
                        for (var j in params.ids) {
                            for (var i in self.pageData.data) {
                                if (self.pageData.data[i].id == params.ids[j]) {
                                    self.pageData.data.splice(i, 1);
                                    break;
                                }
                            }
                        }
                        console.log(response);
                        //self.entity = response.data.data;
                    });
            }).catch(function () {
            });
        },
        //批量删险
        delBatch: function () {
            var self = this;
            if (self.selectDatas.length == 0) {
                this.$alert('请选择要删除的记录', '提示', {
                    confirmButtonText: '确定',
                    callback: function (action) {
                        //if ("confirm"==action){
                    }
                });
            } else {
                var ids = new Array();
                for (var i in self.selectDatas) {
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
        convertMenus2Tree: function (menus1) {
            var permissionList = [
				{
				    "type": "prem",
				    "key": "read",
				    "label": "查询"
				},
                /*{
                    "type": "prem",
                    "key": "add",
                    "label": "新增"
                },
                {
                    "type": "prem",
                    "key": "delete", "label": "删除"
                },
                {
                    "type": "prem",
                    "key": "update",
                    "label": "修改"
                },
                {
                    "type": "prem",
                    "key": "open",
                    "label": "打开"
                },
                {
                    "type": "prem",
                    "key": "close",
                    "label": "关闭"
                },
                {
                    "type": "prem",
                    "key": "run",
                    "label": "执行"
                }*/
            ];
            var temp = [], lev = 0;
            var menus = clone(menus1);
            for (var j in menus) {
                /*if (menus[j].parentId != 0) {
                    menus[j].children = [];
                    var tempPerm = clone(permissionList);
                    for(var k in tempPerm){
                        tempPerm[k].id = menus[j].id+"-"+k;
                        menus[j].children.push(tempPerm[k]);
                    }
                }else{
                    menus[j].type="dir";
                    menus[j].key="read";
                }*/
            	menus[j].type="dir";
                menus[j].key="read";
            }
            var forFn_convertMenus2TreeItems = function (arr, menu, lev) {
                for (var i in arr) {
                    var item = arr[i];
                    item.label = item.menuName;
                    if (item.parentId == menu.id) {
                        if (typeof(menu.children) == "undefined") {
                            menu.children = [];
                        }
                        menu.children.push(item);
                        item.lev = lev;
                        forFn_convertMenus2TreeItems(arr, item, lev + 1);
                    }
                }
            };
            for (var j in menus) {
                if (menus[j].parentId == "0") {
                    forFn_convertMenus2TreeItems(menus, menus[j], lev);
                    //return depts[j];
                    temp.push(menus[j])
                }
            }
            console.log(temp)
            return temp;
        },
        tableRowClassName : function (row, index) {
            if ("1" == row.isMobileVisible) {
              return 'info-row';
            }
            return '';
        },
    }
});