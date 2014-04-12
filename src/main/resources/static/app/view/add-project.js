app.controller("AddProjectController", function ($scope, $http, $location) {

    $scope.form = {
        projectName: ""
    };


    $scope.addProject = function() {

        $http.post("/rest/add-project", "", {params: $scope.form}).
            success(function (data, status, headers, config) {
                handleAddingProjectResponse(data, $scope.form.projectName);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    };

    function handleAddingProjectResponse(response, projectName) {
        if(response.successful) {

            $location.path("/project/"+response.projectId[0]+"/"+projectName);

        } else {
            alert("Problem"); //TODO handle gracefully
        }

    }


});
