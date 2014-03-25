app.controller("RegisterController", function ($scope, $http) {

    $scope.userName = "";
    $scope.email = "";
    $scope.password = "";
    $scope.passwordRepeat = "";

    $scope.registerUser = function() {

        if($scope.password == $scope.passwordRepeat) {
            $http.post("/rest/register-user", "", {params: {name: $scope.userName, email: $scope.email, password: $scope.password}}).
                success(function (data, status, headers, config) {
                    alert("OK "+data)
                }).
                error(function (data, status, headers, config) {
                    alert("Error communication with server!")
                });

        } else {
            alert("Passwords don't match!")
        }

    }



});
