app.controller("RepositoryController", function ($scope, $http, $routeParams, secureService) {

    var repositoryId = parseInt($routeParams.repositoryId);

    $scope.repositoryId = repositoryId;

    $scope.commits = [];

    function init() {
        secureService.get("/rest/commits/"+repositoryId+"/0/20", true, handleLoadCommitsResponse);
    }

    function handleLoadCommitsResponse(response) {
        $scope.commits = response.commits;
    }


    init();


});
