app.factory("secureService", function($http, session, $route, $location) {
   return {
       get: function(url, cache, handler) {
           $http.get(url, {cache: cache}).
               success(function (data, status, headers, config) {
                   handler(data);
               }).
               error(function (data, status, headers, config) {
                   if(status==401) {
                       session.clearSession();
                       $route.reload();
                   } if(status==403) {
                       alert("Forbidden");
                       $location.path("/");
                   } else {
                       alert("Error communication with server! ("+status+")")
                   }

               });
       }

   }
});
