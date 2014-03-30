app.controller("AddProjectController", function ($scope, $http) {

    $scope.form = {
        projectName: ""
    };

    $scope.formVisible = true;
    $scope.successMessageVisible = false;
    $scope.projectId = 0;

    $scope.addProject = function() {

        $http.post("/rest/add-project", "", {params: $scope.form}).
            success(function (data, status, headers, config) {
                handleAddingProjectResponse(data);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    };

    function handleAddingProjectResponse(response) {
        if(response.successful) {
            $scope.formVisible = false;
            $scope.successMessageVisible = true;
            $scope.projectId = response.projectId[0];
        } else {
            $scope.formVisible = true;
            $scope.successMessageVisible = false;
        }

    }


});
