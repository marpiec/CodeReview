app.controller("AddRepositoryController", function ($scope, $http) {

    $scope.form = {
        cloneUrl: "",
        repoName: "",
        projectId: 1
    };

    $scope.formVisible = true;
    $scope.successMessageVisible = false;
    $scope.repositoryId = 0;

    $scope.addRepository = function() {

        $http.post("/rest/add-repository", "", {params: $scope.form}).
            success(function (data, status, headers, config) {
                handleAddingRepositoryResponse(data);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    };

    function handleAddingRepositoryResponse(response) {
        if(response.successful) {
            $scope.formVisible = false;
            $scope.successMessageVisible = true;
            $scope.repositoryId = response.repositoryId[0];
        } else {
            $scope.formVisible = true;
            $scope.successMessageVisible = false;
        }

    }


});
