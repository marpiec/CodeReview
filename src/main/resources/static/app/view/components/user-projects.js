app.directive("mpUserProjects", function(secureService, session, secureService) {
    return {
        scope: {},
        restrict: "E",
        templateUrl: "app/view/components/user-projects.html",
        link: function(scope) {

            scope.projectsWithRepositories = {};

            function handleUserProjectsResponse(response) {
                scope.projectsWithRepositories = response.projects;
            }

            function loadUserProjects() {
                if(session.isAuthenticated()) {
                    secureService.get("/rest/user-projects-and-repositories", false, handleUserProjectsResponse);
                } else {
                    scope.projectsWithRepositories = {};
                }
            }

            loadUserProjects();
        }
    }
});
