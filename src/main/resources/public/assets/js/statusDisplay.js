function updateStatusInternal(f) {
    request({url: "/update_stat"}).then(data => {
        console.log("[Updating server status]: " + data);

        if (data !== null) {
            let information = JSON.parse(data);
            if (information.toString() !== "") {
                f(information);
            } else {
                console.log("[Status Update Error]: Got empty update!")
            }
        }

    }).catch(onerror => {
        console.log("[Status Update Error]: " + onerror);
    })
}

function updateStatusBtn() {
    updateStatusInternal(function (newInfo) {
        // Implement updating tables with new data
        try {
            // const newServerInfo = Object.assign(new StatusUpdateInfo(), newInfo);

            console.log(newInfo.jobQueue);

            let $jobQueueTableBody = $("#job-queue-table tbody");
            $jobQueueTableBody.empty();
            newInfo.jobQueue.forEach(row => {
                $jobQueueTableBody.append(row);
            });

            let $serverStatusTableBody = $("#server-status-table tbody");
            $serverStatusTableBody.empty();
            newInfo.serverStat.forEach(row => {
                $serverStatusTableBody.append(row);
            });
        } catch (e) {
            console.error("[Status Update Parse Error]: " + e)
        }
    });
}