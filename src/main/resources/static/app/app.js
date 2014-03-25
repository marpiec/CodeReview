var app = angular.module("application", ['ngRoute']).config(function ($routeProvider) {

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

    $routeProvider.otherwise({
        redirectTo: "/home"
    });

});

