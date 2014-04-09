var app = angular.module("application", ["ngRoute", "ngCookies"]).config(function ($routeProvider) {

    $routeProvider.when("/home", {
        templateUrl: "app/view/home.html",
        controller: "HomeController"
    });

    $routeProvider.when("/register", {
        templateUrl: "app/view/register.html",
        controller: "RegisterController"
    });

    $routeProvider.when("/login", {
        templateUrl: "app/view/login.html",
        controller: "LoginController"
    });

    $routeProvider.when("/project/:projectId/:projectName/add-repository", {
        templateUrl: "app/view/add-repository.html",
        controller: "AddRepositoryController"
    });

    $routeProvider.when("/repository/:repositoryId/:repositoryName", {
        templateUrl: "app/view/repository.html",
        controller: "RepositoryController"
    });

    $routeProvider.when("/add-project", {
        templateUrl: "app/view/add-project.html",
        controller: "AddProjectController"
    });

    $routeProvider.when("/project/:projectId/:projectName", {
        templateUrl: "app/view/project.html",
        controller: "ProjectController"
    });

    $routeProvider.when("/commit/:repositoryId/:commitId", {
        templateUrl: "app/view/commit.html",
        controller: "CommitController"
    });

    $routeProvider.otherwise({
        redirectTo: "/home"
    });



});

app.run(function($rootScope, $location, session) {

    $rootScope.$on('$routeChangeStart', function (event, next, current) {
        var url = $location.url();

        var whitelist = ["/home", "/login", "/register"];

        if (!session.isAuthenticated() && !_.contains(whitelist, url)) {
            $location.path('/login');
        }
    });
});


app.directive('autoFocus', function($timeout) {
    return {
        restrict: 'AC',
        link: function(_scope, _element) {
            $timeout(function(){
                _element[0].focus();
            }, 0);
        }
    };
});
