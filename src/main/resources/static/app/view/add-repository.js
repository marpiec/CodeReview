app.controller("AddRepositoryController", function ($scope, $http, $routeParams, $location) {

    $scope.form = {
        cloneUrl: "",
        repoName: "",
        projectId: parseInt($routeParams.projectId)
    };

    $scope.addRepository = function() {

        $http.post("/rest/add-repository", "", {params: $scope.form}).
            success(function (data, status, headers, config) {
                handleAddingRepositoryResponse(data, $scope.form.repoName);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    };

    function handleAddingRepositoryResponse(response, repoName) {
        if(response.successful) {
            $location.path("/repository/"+response.repositoryId[0]+"/"+repoName);
        } else {
            alert("Problem"); //TODO handle this gracefully
        }

    }


});
