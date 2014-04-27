app.controller("ProjectRolesController", function ($scope, secureService, $routeParams, $http) {

    var projectId = parseInt($routeParams.projectId);

    $scope.availableRoles = ["Owner", "Admin", "Developer"];

    $scope.users = [];
    $scope.usersIds = {};


    $scope.addUserFormVisible = false;
    $scope.foundUsers = [];
    $scope.usersQuery = "";

    secureService.get("/rest/project/"+projectId+"/users", false, handleUsersResponse);

    function handleUsersResponse(response) {
        $scope.users = response.users;
        $scope.usersIds = {};
        for(var i=0; i< $scope.users.length;i++) {
            $scope.usersIds[$scope.users[i].userId] = true;
        }

    }

    $scope.roleChanged = function(user) {

        user.disabled = true;
        $http.post("/rest/change-user-role", "", {params: {userId: user.userId, projectId: projectId, newRole: user.role}}).
            success(function (data, status, headers, config) {
                user.disabled = false;
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    };

    $scope.showAddUserForm = function() {
        $scope.addUserFormVisible = true;

        secureService.get("/rest/find-users/0/20/", false, handleFoundUsersResponse)
    };

    $scope.findUsers = function() {
        secureService.get("/rest/find-users/0/20/"+encodeURIComponent($scope.usersQuery), false, handleFoundUsersResponse)
    };

    function handleFoundUsersResponse(response) {
        $scope.foundUsers = _.filter(response.users, function(user) {
            return !(user.userId in $scope.usersIds);
        });
    }
});
