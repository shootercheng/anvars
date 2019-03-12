
var resourcesTpl = {
  template: '#resources-template',
  data: function() {
    return {
      searchParam: { path: '', reqMethod:'', summary: '', servicesName: '' }, 
      resourcesList: { total: 0, list:[] }, 
      addX: { type: '', path: '', reqMethod:'', summary: '', servicesName: '', description: '' }, 
      editX: { type: '', path: '', reqMethod:'', description: '', summary: '', servicesName: '' }
    };
  }, 
  methods: {
    refreshPage: function(page) {
      var self = this;
      Tmsp('/rbacs/resources?page='+page+'&pageSize=10&path='+self.searchParam.path+
                            "&reqMethod="+self.searchParam.reqMethod+"&summary="+self.searchParam.summary+
                            "&servicesName="+self.searchParam.servicesName, 'get').then(function(result){
    
        console.log(result);
        self.resourcesList = result.data;
        }, function(error) {
          console.log(error);
        });
    }, 
    openEditDlg: function(isAdd) {
      var dlgTitle  = isAdd ? "添加" : '编辑';
      var self = this;
      var dlgIndex = layer.open({
        type: 1,
        scrollbar: false,
        skin: "layer-ext-moon",
        area: ["500px", "450px"],
        title: dlgTitle,
        shadeClose: false,
        btn: ["确认", "取消"],
        content: layui.jquery("#editResources"),
        yes: function() {
          Tmsp('/rbacs/resources', 'post',addX).then(function(result){
            if (result.code==200) {
              self.refreshPage(resourcesList.pageNum);
              layer.close(dlgIndex);
            } else {
              layer.msg(result.message, {
                icon: 2,
                time: 1000
              });
            }
    
          }, function(reason) {
              layer.msg(reason.message, {
                icon: 2,
                time: 5000
              });
          });
    
        }
      });
    }, 
    clearSearch: function() {
      this.searchParam.type='';
      this.searchParam.path='';
      this.searchParam.reqMethod='';
      this.searchParam.summary='';
      this.searchParam.servicesName='';
      this.searchParam.description='';
    }
  }, 
  mounted: function (){
    this.refreshPage(1)
  }
};

/*rbacViewApp.controller('resourcesCtrl', function ($scope, $compile) {

// 新增接口信息
$scope.openAdd = function() {

  var du = layer.open({
    type: 1,
    scrollbar: false,
    skin: "layer-ext-moon",
    area: ["500px", "450px"],
    title: "插入新的接口",
    shadeClose: false,
    btn: ["确认", "取消"],
    content: layui.jquery("#insertRecord"),
    yes: function() {
      Tmsp('/rbacs/resources', 'post',$scope.addX).then(function(result){

        if (result.code==200) {
          $scope.refreshPage($scope.resourcesList.pageNum);
          layer.close(du);
        } else {
          layer.msg(result.message, {
            icon: 2,
            time: 1000
          });
        }

      }, function(reason) {
          layer.msg(reason.message, {
            icon: 2,
            time: 5000
          });
      });

    }
  });
};
// 修改接口信息
$scope.openEdit = function(curX) {
  $scope.editX = {};
  $scope.editX.id = curX.id;
  $scope.editX.path = curX.path;
  $scope.editX.reqMethod = curX.reqMethod;
  $scope.editX.summary = curX.summary;
  $scope.editX.servicesName = curX.servicesName;
  $scope.editX.description = curX.description;
  var du = layer.open({
    type: 1,
    scrollbar: false,
    skin: "layer-ext-moon",
    area: ["500px", "450px"],
    title: "修改当前记录信息",
    shadeClose: false,
    btn: ["确认", "取消"],
    content: layui.jquery("#updateRecord"),
    yes: function() {
      Tmsp('/rbacs/resources', 'put',$scope.editX).then(function(result){

        if (result.code==200) {
          $scope.refreshPage();
          layer.close(du);
        } else {
          layer.msg(result.message, {
            icon: 2,
            time: 1000
          });
        }

      }, function(reason) {
          console.log(reason);
      });
    }
  });
};
/// 删除接口信息
$scope.deleteRecord = function(x) {
  var dr = layer.confirm(
    "确定要删除？",
    {
      btn: ["确定", "取消"] //按钮
    },
    function() {
      Tmsp('/rbacs/resources?id='+x, 'delete').then(function(result){

        if (result.code==200) {
          layer.msg("删除成功", {
            icon: 1,
            time: 1000
          });
          $scope.refreshPage($scope.resourcesList.pageNum);
          layer.close(dr);
        } else {
          layer.msg(result.message, {
            icon: 2,
            time: 1000
          });
        }

      }, function(error) {
          console.log(error);
      });
    }
  );
};
$scope.clearSearch = function() {
  $scope.searchX.type='';
  $scope.searchX.path='';
  $scope.searchX.reqMethod='';
  $scope.searchX.summary='';
  $scope.searchX.servicesName='';
  $scope.searchX.description='';
}
$scope.searchX = {};
// 刷新当前页
$scope.refreshPage = function(page) {

  Tmsp('/rbacs/resources?page='+page+'&pageSize=10&path='+$scope.searchX.path+
                        "&reqMethod="+$scope.searchX.reqMethod+"&summary="+$scope.searchX.summary+
                        "&servicesName="+$scope.searchX.servicesName, 'get').then(function(result){

    console.log(result);
    $scope.resourcesList = result.data;
    $scope.$apply();
    }, function(error) {
      console.log(error);
    });
}
$scope.clearSearch();
$scope.refreshPage(1);
});
*/