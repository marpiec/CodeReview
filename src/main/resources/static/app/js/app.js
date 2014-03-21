var app = angular.module("calculatorApplication", ['ngRoute']).config(function ($routeProvider) {

    $routeProvider.when("/home", {
        templateUrl: "app/view/home.html",
        controller: "HomeController"
    });

    $routeProvider.otherwise({
        redirectTo: "/home"
    });

});

app.controller("HomeController", function () {
});
