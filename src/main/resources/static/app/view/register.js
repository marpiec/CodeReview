app.controller("RegisterController", function ($scope, $http) {

    $scope.userName = "";
    $scope.email = "";
    $scope.password = "";
    $scope.passwordRepeat = "";

    $scope.registerFormVisible = true;
    $scope.registrationSuccessfulVisible = false;
    $scope.userAlreadyRegistered = false;

    $scope.registerUser = function() {

        if($scope.password == $scope.passwordRepeat) {
            $http.post("/rest/register-user", "", {params: {name: $scope.userName, email: $scope.email, password: $scope.password}}).
                success(function (data, status, headers, config) {
                    if(data == "UserRegistered") {
                        userRegistered();
                    } else if (data == "UserAlreadyExists") {
                        userAlreadyExists();
                    }
                }).
                error(function (data, status, headers, config) {
                    alert("Error communication with server!")
                });

        } else {
            alert("Passwords don't match!")
        }

    };

    function userRegistered() {
        $scope.registerFormVisible = false;
        $scope.registrationSuccessfulVisible = true;
        $scope.userAlreadyRegistered = false;
    }

    function userAlreadyExists() {
        $scope.registerFormVisible = false;
        $scope.registrationSuccessfulVisible = false;
        $scope.userAlreadyRegistered = true;
    }



});
