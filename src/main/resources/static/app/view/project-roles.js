app.controller("ProjectRolesController", function ($scope, secureService, $routeParams, $http) {

    var projectId = parseInt($routeParams.projectId);

    $scope.availableRoles = ["Owner", "Admin", "Developer"];

    $scope.users = [];
    $scope.usersIds = {};


    $scope.addUserFormVisible = false;
    $scope.foundUsers = [];
    $scope.usersQuery = "";
    $scope.newUserRole = "";

    $scope.previousValueOfCurrentActiveControl = undefined;

    secureService.get("/rest/project/"+projectId+"/users", false, handleUsersResponse);

    function handleUsersResponse(response) {
        $scope.users = response.users;
        $scope.usersIds = {};
        for(var i=0; i< $scope.users.length;i++) {
            $scope.usersIds[$scope.users[i].userId] = true;
        }

    }

    $scope.roleChanged = function(user, previousValue) {

        if(confirm("Are you sure?")) {
            user.disabled = true;
            $http.post("/rest/change-user-role", "", {params: {userId: user.userId, projectId: projectId, newRole: user.role}}).
                success(function (data, status, headers, config) {
                    user.disabled = false;
                }).
                error(function (data, status, headers, config) {
                    alert("Error communication with server!")
                });
        } else {
            user.role = previousValue;
        }
    };

    $scope.showAddUserForm = function() {
        $scope.addUserFormVisible = true;

        secureService.get("/rest/find-users/0/100/", false, handleFoundUsersResponse)
    };

    $scope.findUsers = function() {
        secureService.get("/rest/find-users/0/100/"+encodeURIComponent($scope.usersQuery), false, handleFoundUsersResponse)
    };

    function handleFoundUsersResponse(response) {
        $scope.foundUsers = _.filter(response.users, function(user) {
            return !(user.userId in $scope.usersIds);
        });
    }

    $scope.addUser = function(user, role) {
        if(confirm("Are you sure?")) {
            user.disabled = true;
            $http.post("/rest/add-user-to-project", "", {params: {userId: user.userId, projectId: projectId, role: role}}).
                success(function (data, status, headers, config) {
                    $scope.users.push({userId: user.userId, userName:user.name, role:role});
                }).
                error(function (data, status, headers, config) {
                    alert("Error communication with server!")
                });
        } else {
            user.newUserRole = "";
        }
    };

    $scope.removeUser = function(user) {
        if(confirm("Are you sure?")) {
            $http.post("/rest/remove-user-from-project", "", {params: {userId: user.userId, projectId: projectId}}).
                success(function (data, status, headers, config) {
                    user.cancelRemoveVisible = true;
                }).
                error(function (data, status, headers, config) {
                    alert("Error communication with server!")
                });
        }
    };

    $scope.cancelRemoveUser = function(user) {
        $http.post("/rest/add-user-to-project", "", {params: {userId: user.userId, projectId: projectId, role: user.role}}).
            success(function (data, status, headers, config) {
                user.cancelRemoveVisible = false;
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    }
});
