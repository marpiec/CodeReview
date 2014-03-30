app.factory('session', function($http, $cookieStore) {
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
            $cookieStore.remove("sessionInfo");
        },
        createNewSession: function(sessionInfo) {
            this.info = sessionInfo;
            $cookieStore.put("sessionInfo", this.info);
        },
        loadSessionInfo: function() {
            var info =  $cookieStore.get("sessionInfo");
            if(info != undefined) {
                this.info = info;
            }
        }
    };
    session.loadSessionInfo();
    return session;
});
