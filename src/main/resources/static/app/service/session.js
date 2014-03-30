app.factory('session', function($http, $cookies) {
    var session = {
        info: {
            sessionId: undefined,
            userName: undefined
        },
        isAuthenticated: function() {
            return this.info.sessionId != undefined;
        },
        clearSession: function() {
            this.info = {
                sessionId: undefined,
                userName: undefined
            };
            delete $cookies.sessionId;
            delete $cookies.userName;
        },
        createNewSession: function(sessionInfo) {
            this.info = sessionInfo;
            $cookies.sessionId = this.info.sessionId;
            $cookies.userName = this.info.userName;
        },
        loadSessionInfo: function() {
            this.info = {
                sessionId: $cookies.sessionId,
                userName: $cookies.userName
            };
        }
    };
    session.loadSessionInfo();
    return session;
});
