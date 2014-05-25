app.directive("mpTimeLabel", function() {
   return {
       scope: {
           timestamp : "@"
       },
       restrict: "E",
       template:"{{timeString}}",
       link: function(scope) {
           var time = parseTimestamp(scope.timestamp);
           scope.timeString = timeDifferenceLabel(new Date(), time);
       }
   }
});
