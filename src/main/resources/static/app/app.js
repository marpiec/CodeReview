var app = angular.module("calculatorApplication", ['ngRoute']).config(function ($routeProvider) {

    $routeProvider.when("/home", {
        templateUrl: "app/home.html",
        controller: "HomeController"
    });

    $routeProvider.when("/register", {
        templateUrl: "app/register.html",
        controller: "HomeController"
    });

    $routeProvider.when("/login", {
        templateUrl: "app/login.html",
        controller: "HomeController"
    });

    $routeProvider.otherwise({
        redirectTo: "/home"
    });

});

