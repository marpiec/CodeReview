app.directive("mpUserProjects", function(secureService, session, secureService) {
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
                    secureService.get("/rest/user-projects", true, handleUserProjectsResponse);
                } else {
                    scope.projects = {};
                }
            }

            loadUserProjects();
        }
    }
});
