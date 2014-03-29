app.factory('session', function($http, $cookies) {
    var session = {
        authenticated: false,
        sessionId: "",
        userRights: {},
        clearSession: function() {
            this.authenticated = false;
            this.userRights = {};
            this.sessionId = 0;
        },
        createNewSession: function(sessionId, userRights) {
            this.authenticated = true;
            this.userRights = userRights;
            this.sessionId = sessionId;
            $cookies.sessionId = session.sessionId;
        },
        loadSessionInfo: function() {
           var sessionInfo = window.currentSessionInfo;
           this.authenticated = sessionInfo.userAuthenticated;
           this.userRights = sessionInfo.userRights;
           this.sessionId = sessionInfo.sessionId;
        }
    };
    session.loadSessionInfo();
    return session;
});
