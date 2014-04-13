app.controller("CommitController", function ($scope, secureService, $routeParams) {

    var repositoryId = parseInt($routeParams.repositoryId);
    var commitId = parseInt($routeParams.commitId);

    secureService.get("/rest/commit/" + repositoryId + "/" + commitId, true, handleCommitResponse);
    secureService.get("/rest/commit-files/" + repositoryId + "/" + commitId, true, handleCommitFilesResponse);

    $scope.info = {};
    $scope.files = [];

    $scope.processedFiles = [];

    function handleCommitResponse(response) {
        if (response.commit.length == 1) {
            $scope.info = response.commit[0].info;
        } else {
            $scope.info = {};
        }
    }

    function handleCommitFilesResponse(response) {
        $scope.files = response.files;

        prepareFilesLines(response.files);
    }

    function prepareFilesLines(files) {

        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var lineChanges = files[i].lineChanges;

            var processed = {from: [], to: []};
            $scope.files[i].processed = processed;

            if (file.changeType == "Modify") {

                var fromLines = file.fromContent[0].split("\n");

                for(var j=0; j< fromLines.length; j++) {
                    var line = fromLines[j];

                    if(lineRemoved(lineChanges, j+1)) {
                        processed.from[j] = {change: "deleted", content: line};
                    } else {
                        processed.from[j] = {change: "none", content: line};
                    }
                }


                var toLines = file.toContent[0].split("\n");

                for(var j=0; j< toLines.length; j++) {
                    var line = toLines[j];

                    if(lineAdded(lineChanges, j+1)) {
                        processed.to[j] = {change: "added", content: line};
                    } else {
                        processed.to[j] = {change: "none", content: line};
                    }
                }

                for(var j=0;j<processed.from.length; j++) {
                    processed.from[j].number = addPrecedingSpaces(processed.from.length, (j + 1));
                }

                for(var j=0;j<processed.to.length; j++) {
                    processed.to[j].number = addPrecedingSpaces(processed.to.length, (j + 1));
                }

                for(var j=0;j<Math.max(processed.to.length, processed.from.length);j++) {


                    var from = processed.from[j];
                    var to = processed.to[j];

                    if (from.change == "none" && to.change == "added") {
                        processed.from.splice(j, 0, {change: "placeholder", content: "", number: ""})
                    } else if (from.change == "deleted" && to.change == "none") {
                        processed.to.splice(j, 0, {change: "placeholder", content: "", number: ""})
                    } else if (from.change == "deleted" && to.change == "added") {
                        processed.from[j].change="modifiedFrom";
                        processed.to[j].change="modifiedTo";
                    }
                }
            }
        }
    }

    function addPrecedingSpaces(maximumNumber, currentNumber) {
        var maxDigits = (maximumNumber+"").length;
        var currentDigits = (currentNumber+"").length;
        var result = currentNumber+"";
        for(var p = currentDigits; p< maxDigits;p++) {
            result = " "+result;
        }
        return result;
    }



    function lineRemoved(lineChanges, lineNumber) {
        for(var i = 0; i< lineChanges.length; i++) {
            if(lineChanges[i].changeType == "Delete" && lineChanges[i].number == lineNumber) {
                return true;
            }
        }
        return false;
    }

    function lineAdded(lineChanges, lineNumber) {
        for(var i = 0; i< lineChanges.length; i++) {
            if(lineChanges[i].changeType == "Add" && lineChanges[i].number == lineNumber) {
                return true;
            }
        }
        return false;
    }

});
