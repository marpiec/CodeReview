app.controller("ProjectRolesController", function ($scope, secureService, $routeParams, $http) {

    var projectId = parseInt($routeParams.projectId);

    $scope.availableRoles = ["Owner", "Admin", "Developer"];

    $scope.users = {};

    secureService.get("/rest/project/"+projectId+"/users", false, handleUsersResponse);

    function handleUsersResponse(response) {
        $scope.users = response.users;
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
    }

});
