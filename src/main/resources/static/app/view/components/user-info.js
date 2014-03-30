app.directive("mpUserInfo", function($http, $location, session) {
    return {
        restrict: "E",
        templateUrl: "app/view/components/user-info.html",
        link: function(scope) {
            scope.sessionInfo = session.info;
            scope.authenticated = session.isAuthenticated();

            scope.logout = function() {

                $http.get("/rest/logout", {}).
                    success(function (data, status, headers, config) {
                        session.clearSession();
                        $location.path('/');
                    }).
                    error(function (data, status, headers, config) {
                        session.clearSession();
                        alert("Error communication with server!")
                    });

            };
        }
    }
});
