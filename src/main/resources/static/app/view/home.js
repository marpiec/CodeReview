app.controller("HomeController", function ($scope, $http, session) {

    $scope.authenticated = session.isAuthenticated()

});
