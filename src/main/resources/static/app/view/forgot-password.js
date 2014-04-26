app.controller("ForgotPasswordController", function ($rootScope, $scope, $http, session, $location, $cookies) {

    $scope.form = {
        user: ""
    };

    $scope.loginUser = function() {

        $http.post("/rest/forgot-password", "", {params: {user: $scope.form.user}}).
            success(function (data, status, headers, config) {
                handleForgotPasswordResponse(data);
            }).
            error(function (data, status, headers, config) {
                alert("Error communication with server!")
            });

    };

    function handleForgotPasswordResponse(response) {
        alert("Password has been reset");
        $location.path("/");
    }

});
