app.controller("CommitController", function ($scope, secureService, $routeParams) {

    var repositoryId = parseInt($routeParams.repositoryId);
    var commitId = parseInt($routeParams.commitId);

    secureService.get("/rest/commit/"+repositoryId+"/"+commitId, true, handleCommitResponse);
    secureService.get("/rest/commit-files/"+repositoryId+"/"+commitId, true, handleCommitFilesResponse);


    $scope.commit = {};

    $scope.filesContent = [];
    $scope.filesDiff = [];

    function handleCommitResponse(response) {
        if(response.commit.length == 1) {
            $scope.commit = response.commit[0];
        } else {
            $scope.commit = {};
        }
    }

    function handleCommitFilesResponse(response) {
        $scope.filesContent = response.files;
        secureService.get("/rest/commit-files-diffs/"+repositoryId+"/"+commitId, true, handleCommitFilesDiffResponse);
    }

    function handleCommitFilesDiffResponse(response) {
        $scope.filesDiff = response.files;
        addDiffsToFileContent();
    }

    function addDiffsToFileContent() {

        for(var i=0; i<$scope.filesContent.length; i++) {

            var fileContent = $scope.filesContent[i];
            var changedLines = $scope.filesDiff[i].changedLines;

            if(fileContent.changeType == "modify") {

                var fromLines = _.map(fileContent.fromContent.split("\n"), function(line) {return {line: line, change: "none"}});

                for(var j=0; j<changedLines.length; j++) {
                    var changedLine = changedLines[j];

                    if(changedLine.deleted) {
                        fromLines[changedLine.number-1] = {line: fromLines[changedLine.number-1].line, change: "delete"};
                    }
                }
                fileContent.fromLines = fromLines;

                var toLines = _.map(fileContent.toContent.split("\n"), function(line) {return {line: line, change: "none"}});

                for(var j=0; j<changedLines.length; j++) {
                    var changedLine = changedLines[j];

                    if(changedLine.added) {
                        toLines[changedLine.number-1] = {line: toLines[changedLine.number-1].line, change: "add"};
                    }
                }

                fileContent.toLines = toLines;

            }





        }

    }
});
