app.controller("HomeController", function ($scope, $http) {

    $scope.userProjects = {};

    function init() {
        $http.get("/rest/user-projects", {cache: true}).
            success(function (data, status, headers, config) {
                handleUserProjectsResponse(data);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });
    }

    function handleUserProjectsResponse(response) {
        $scope.projects = response.projects;
    }

    init();
});
