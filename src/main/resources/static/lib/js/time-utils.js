function parseTimestamp(timestamp) {
    //example 2014-04-24 23:14:56:000
    return new Date(parseInt(timestamp.substr(0, 4)), parseInt(timestamp.substr(5, 7))-1, parseInt(timestamp.substr(8, 10)),
        parseInt(timestamp.substr(11, 13)), parseInt(timestamp.substr(14, 16)), parseInt(timestamp.substr(17, 19)), parseInt(timestamp.substr(20, 22)));
}

function timeDifferenceLabel(current, previous) {

    function timeAgoLabel(timeUnitDuration, mills, label) {
        var timeUnits = Math.round(mills / timeUnitDuration);
        if(timeUnits==1) {
            return timeUnits + " " + label + " ago";
        } else {
            return timeUnits + "  " + label + "s ago";
        }
    }

    var msPerSecond = 1000;
    var msPerMinute = msPerSecond * 60;
    var msPerHour = msPerMinute * 60;
    var msPerDay = msPerHour * 24;
    var msPerMonth = msPerDay * 30;
    var msPerYear = msPerDay * 365;

    var elapsed = current.getTime() - previous.getTime();

    if (elapsed < msPerMinute) {
        return timeAgoLabel(msPerSecond, elapsed, "second");
    } if (elapsed < msPerHour * 3) {
        return timeAgoLabel(msPerMinute, elapsed, "minute");
    } if (elapsed < msPerDay * 2) {
        return timeAgoLabel(msPerHour, elapsed, "hour");
    } else if (elapsed < msPerMonth * 3) {
        return timeAgoLabel(msPerDay, elapsed, "day");
    } else if (elapsed < msPerYear * 2) {
        return timeAgoLabel(msPerMonth, elapsed, "month");
    } else {
        return timeAgoLabel(msPerYear, elapsed, "year");
    }
}
