app.controller("ProjectController", function ($scope, secureService, $routeParams) {

    var projectId = parseInt($routeParams.projectId);

    $scope.project = {
        id: undefined,
        name: undefined
    };

    $scope.repositories = {};

    secureService.get("/rest/project/"+projectId, true, handleProjectResponse);
    secureService.get("/rest/project/"+projectId+"/repositories", true, handleRepositoriesResponse);

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
