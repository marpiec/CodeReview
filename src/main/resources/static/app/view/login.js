app.controller("LoginController", function ($rootScope, $scope, $http, session, $location) {

    $scope.user = "";
    $scope.password = "";

    $scope.loginIncorrectVisible = false;

    $scope.registerUser = function() {

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
            session.authenticated = true;
            session.userName = response.userName[0];
            session.userId = response.userId[0];

            $location.path("#/home");
        } else {
            session.authenticated = false;
            session.userName = "";
            session.userId = 0;
            $scope.loginIncorrectVisible = true;
        }
    }



});
