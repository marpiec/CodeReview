app.controller("RepositoryController", function ($scope, $http, $routeParams) {

    var repositoryId = parseInt($routeParams.repositoryId);

    $scope.repositoryId = repositoryId;

    $scope.commits = [];

    function init() {
        $http.get("/rest/commits/"+repositoryId+"/0/20", {cache: true}).
            success(function (data, status, headers, config) {
                handleLoadCommitsResponse(data);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    }

    function handleLoadCommitsResponse(response) {
        $scope.commits = response.commits;
    }


    init();


});
