app.directive("mpUserProjects", function($http, $location, session) {
    return {
        scope: {},
        restrict: "E",
        templateUrl: "app/view/components/user-projects.html",
        link: function(scope) {

            scope.projects = {};

            function handleUserProjectsResponse(response) {
                scope.projects = response.projects;
            }

            function loadUserProjects() {
                if(session.isAuthenticated()) {
                    $http.get("/rest/user-projects", {cache: true}).
                        success(function (data, status, headers, config) {
                            handleUserProjectsResponse(data);
                        }).
                        error(function (data, status, headers, config) {
                            alert("Error communication with server!")
                        });
                } else {
                    scope.projects = {};
                }
            }

            loadUserProjects();
        }
    }
});
