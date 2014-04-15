app.controller("AddRepositoryController", function ($scope, $http, $routeParams, $location, $timeout) {

    $scope.form = {
        cloneUrl: "",
        repoName: "",
        projectId: parseInt($routeParams.projectId)
    };

    $scope.taskProgress = {
        visible: false,
        totalWork: 1,
        percentDone: 0,
        completed: 0,
        taskName: 0
    };

    $scope.taskMonitorId = undefined;

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
            $scope.repositoryId = response.repositoryId[0];
            $scope.repoName = repoName;
            $scope.taskMonitorId = response.taskMonitorId;
            $timeout($scope.monitorProgress, 200);
            //
        } else {
            alert("Problem"); //TODO handle this gracefully
        }

    }

    $scope.monitorProgress = function() {


        $http.get("/rest/task-progress/"+$scope.taskMonitorId).success(function (data, status, headers, config) {
            $scope.taskProgress.visible = true;
            $scope.taskProgress.totalWork = data.totalWork;
            $scope.taskProgress.percentDone = data.percentDone;
            $scope.taskProgress.completed = data.completed;
            $scope.taskProgress.taskName = data.taskName;

            $scope.progressBar = {width: data.percentDone+"%"};

            if(data.taskName != "Updating references" ) {
                $timeout($scope.monitorProgress, 500);
            } else {
                $location.path("/repository/"+$scope.repositoryId+"/"+$scope.repoName);
            }



        }).error(function (data, status, headers, config) {
            alert("Error communication with server!")
        });
    }

});
