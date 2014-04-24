app.directive("mpChangePassword", function($http, $location, session) {
    return {
        restrict: "E",
        templateUrl: "app/view/account/components/change-password.html",
        link: function(scope) {

            scope.form = {
                currentPassword: "",
                newPassword: "",
                newPasswordRepeat: ""
            };


            scope.changePassword = function() {

                if(scope.form.newPassword == scope.form.newPasswordRepeat) {
                    $http.post("/rest/change-user-password", "", {params:{oldPassword: scope.form.currentPassword, newPassword: scope.form.newPassword}}).
                        success(function (data, status, headers, config) {
                            if(data.passwordChanged) {
                                alert("Password has been changed");
                            } else {
                                alert("Password changing did not succeed");
                            }

                        }).
                        error(function (data, status, headers, config) {
                            session.clearSession();
                            alert("Error communication with server!")
                        });
                    scope.form.currentPassword = "";
                    scope.form.newPassword = "";
                    scope.form.newPasswordRepeat = "";
                } else {
                    alert("Passwords don't match!")
                }
            };
        }
    }
});
