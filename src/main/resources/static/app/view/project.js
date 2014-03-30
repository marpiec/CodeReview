app.controller("ProjectController", function ($scope, $http, $routeParams) {

    var projectId = parseInt($routeParams.projectId);

    $scope.projectId = projectId;
    $scope.projectName = "aaaa";


});
