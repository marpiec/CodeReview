app.controller("LoginController", function ($rootScope, $scope, $http, session, $location, $cookies) {

    $scope.user = "";
    $scope.password = "";

    $scope.loginIncorrectVisible = false;

    $scope.loginUser = function() {

        $http.post("/rest/authenticate-user", "", {params: {user: $scope.userName, password: $scope.password}}).
            success(function (data, status, headers, config) {
                handleAuthenticationResponse(data);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });

    };

    function handleAuthenticationResponse(response) {
        if(response.userAuthenticated) {
            session.createNewSession(response.sessionInfo[0]);
            $location.path("/");
        } else {
            session.clearSession();
            $scope.loginIncorrectVisible = true;
        }
    }

});
