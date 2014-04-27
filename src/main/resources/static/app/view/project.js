app.controller("ProjectController", function ($scope, secureService, $routeParams) {

    var projectId = parseInt($routeParams.projectId);
    $scope.userRole = undefined;

    $scope.project = {
        id: undefined,
        name: undefined
    };

    $scope.repositories = {};

    $scope.canAddRepository = false;

    secureService.get("/rest/current-user-project-role/"+projectId, false, handleUserRoleResponse);
    secureService.get("/rest/project/"+projectId, false, handleProjectResponse);
    secureService.get("/rest/project/"+projectId+"/repositories", false, handleRepositoriesResponse);


    function handleUserRoleResponse(response) {
        if(response.role.length == 1) {
            $scope.userRole = response.role[0];
            $scope.canAddRepository = $scope.userRole == "Admin" || $scope.userRole == "Owner";
        } else {
            $scope.userRole = undefined
        }

    }

    function handleProjectResponse(response) {
        if(response.project.length == 1) {
            $scope.project = response.project[0];
        } else {
            $scope.project = {
                id: undefined,
                name: undefined
            };
        }
    }

    function handleRepositoriesResponse(response) {
        $scope.repositories = response.repositories;
    }


});
