app.controller("RepositoryController", function ($scope, $http, $routeParams, secureService) {

    var pageSize = 20;
    var repositoryId = parseInt($routeParams.repositoryId);

    $scope.repositoryId = repositoryId;
    $scope.commits = [];
    $scope.loadMoreCommitsButtonVisible = true;

    function init() {
        $scope.loadMoreCommits();
    }

    function handleLoadCommitsResponse(response) {
        $scope.commits = $scope.commits.concat(response.commits);
        if(response.commits.length < pageSize) {
            $scope.loadMoreCommitsButtonVisible = false;
        }
    }

    $scope.loadMoreCommits = function() {
        secureService.get("/rest/commits/"+repositoryId+"/"+$scope.commits.length+"/"+pageSize, false, handleLoadCommitsResponse);
    };


    init();

});
