app.controller("LoginController", function ($rootScope, $scope, $http) {

    $scope.user = "";
    $scope.password = "";

    $scope.loginIncorrectVisible = false;

    $scope.registerUser = function() {

        $http.post("/rest/authenticate-user", "", {params: {user: $scope.userName, password: $scope.password}}).
            success(function (data, status, headers, config) {
                if(data.indexOf("UserAuthenticated") == 0) {
                    userAuthenticated();
                } else if (data == "IncorrectUserOrPassword") {
                    incorrectUserOrPassword();
                } else {
                    alert("Unknown response [" + data + "]");
                }
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });

    };

    function incorrectUserOrPassword() {
        $scope.loginIncorrectVisible = true;
        $rootScope.session.authenticated = true;
    }

    function userAuthenticated() {
        $scope.loginIncorrectVisible = false;
    }



});
