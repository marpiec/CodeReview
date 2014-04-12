app.controller("CommitController", function ($scope, secureService, $routeParams) {

    var repositoryId = parseInt($routeParams.repositoryId);
    var commitId = parseInt($routeParams.commitId);

    secureService.get("/rest/commit/" + repositoryId + "/" + commitId, true, handleCommitResponse);


    $scope.info = {};
    $scope.filesNames = [];
    $scope.filesContent = [];
    $scope.filesDiff = [];

    $scope.files = [];

    $scope.processedFiles = [];

    function handleCommitResponse(response) {
        if (response.commit.length == 1) {
            $scope.info = response.commit[0].info;
            $scope.filesNames = response.commit[0].files;
        } else {
            $scope.info = {};
            $scope.filesNames = [];
        }
        secureService.get("/rest/commit-files/" + repositoryId + "/" + commitId, true, handleCommitFilesResponse);
    }

    function handleCommitFilesResponse(response) {
        $scope.filesContent = response.files;
        secureService.get("/rest/commit-files-diffs/" + repositoryId + "/" + commitId, true, handleCommitFilesDiffResponse);
    }

    function handleCommitFilesDiffResponse(response) {
        $scope.filesDiff = response.files;

        for (var p = 0; p < $scope.filesDiff.length; p++) {
            $scope.files[p] = {name: $scope.filesNames[p], content: $scope.filesContent[p], diff: $scope.filesDiff[p], processed: {}};
        }

        prepareFilesLines();
    }

    function prepareFilesLines() {

        for (var i = 0; i < $scope.files.length; i++) {
            var content = $scope.files[i].content;
            var diff = $scope.files[i].diff;

            var processed = {from: [], to: []};
            $scope.files[i].processed = processed;

            if (content.changeType == "modify") {

                var fromLines = content.fromContent.split("\n");

                for(var j=0; j< fromLines.length; j++) {
                    var line = fromLines[j];

                    if(lineRemoved(diff, j+1)) {
                        processed.from[j] = {change: "deleted", content: line};
                    } else {
                        processed.from[j] = {change: "none", content: line};
                    }
                }


                var toLines = content.toContent.split("\n");

                for(var j=0; j< toLines.length; j++) {
                    var line = toLines[j];

                    if(lineAdded(diff, j+1)) {
                        processed.to[j] = {change: "added", content: line};
                    } else {
                        processed.to[j] = {change: "none", content: line};
                    }
                }

                for(var j=0;j<Math.max(processed.to.length, processed.from.length);j++) {

                    var from = processed.from[j];
                    var to = processed.to[j];
                    if(from.change == "none" && to.change == "added") {
                        processed.from.splice(j, 0, {change: "placeholder", content: ""})
                    } else if(from.change == "deleted" && to.change == "none") {
                        processed.to.splice(j, 0, {change: "placeholder", content: ""})
                    } else if(from.change == "deleted" && to.change == "added") {
                        processed.from[j] = {change: "modifiedFrom", content: processed.from[j].content}
                        processed.to[j] = {change: "modifiedTo", content: processed.to[j].content}
                    }


                }
            }
        }

    }

    function lineRemoved(diff, lineNumber) {
        for(var i = 0; i< diff.changedLines.length; i++) {
            if(diff.changedLines[i].deleted && diff.changedLines[i].number == lineNumber) {
                return true;
            }
        }
        return false;
    }

    function lineAdded(diff, lineNumber) {
        for(var i = 0; i< diff.changedLines.length; i++) {
            if(diff.changedLines[i].added && diff.changedLines[i].number == lineNumber) {
                return true;
            }
        }
        return false;
    }

});
