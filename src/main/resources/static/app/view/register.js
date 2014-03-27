app.controller("RegisterController", function ($scope, $http) {

    $scope.form = {
        userName: "",
        email: "",
        password: "",
        passwordRepeat: ""
    };


    $scope.registerFormVisible = true;
    $scope.registrationSuccessfulVisible = false;
    $scope.userAlreadyRegistered = false;

    $scope.registerUser = function() {

        if($scope.form.password == $scope.form.passwordRepeat) {
            $http.post("/rest/register-user", "", {params: {name: $scope.form.userName, email: $scope.form.email, password: $scope.form.password}}).
                success(function (data, status, headers, config) {
                    handleRegistrationResponse(data);
                }).
                error(function (data, status, headers, config) {
                    alert("Error communication with server!")
                });

        } else {
            alert("Passwords don't match!")
        }

    };

    function handleRegistrationResponse(response) {
        if(response.userRegistered) {
            $scope.registerFormVisible = false;
            $scope.registrationSuccessfulVisible = true;
            $scope.userAlreadyRegistered = false;
        } else {
            $scope.registerFormVisible = false;
            $scope.registrationSuccessfulVisible = false;
            $scope.userAlreadyRegistered = true;
        }
    }

});
