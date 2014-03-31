app.factory("secureService", function($http, session, $route) {
   return {
       get: function(url, cache, handler) {
           $http.get(url, {cache: cache}).
               success(function (data, status, headers, config) {
                   handler(data);
               }).
               error(function (data, status, headers, config) {
                   alert(status);
                   if(status==401) {
                       session.clearSession();
                       alert("You have been logged off");
                       $route.reload();
                   } else {
                       alert("Error communication with server!")
                   }

               });
       }

   }
});
