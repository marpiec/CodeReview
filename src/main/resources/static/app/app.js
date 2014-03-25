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

app.run(function($rootScope, $location) {

    $rootScope.session = {authenticated: false};

    $rootScope.$on('$routeChangeStart', function (event, next, current) {
        if (!$rootScope.session.authenticated) {
            $location.path('/login');
        }
    });
});

