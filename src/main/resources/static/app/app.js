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

    $routeProvider.when("/add-repository", {
        templateUrl: "app/view/add-repository.html",
        controller: "AddRepositoryController"
    });

    $routeProvider.when("/repository/:repositoryId/:repositoryName", {
        templateUrl: "app/view/repository.html",
        controller: "RepositoryController"
    });

    $routeProvider.otherwise({
        redirectTo: "/home"
    });



});

app.run(function($rootScope, $location, session) {

    $rootScope.$on('$routeChangeStart', function (event, next, current) {
        var url = $location.url();

        var whitelist = ["/login", "/register"];

        if (!session.isAuthenticated() && !_.contains(whitelist, url)) {
            $location.path('/login');
        }
    });
});

