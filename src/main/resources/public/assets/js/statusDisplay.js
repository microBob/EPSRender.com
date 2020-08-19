function updateStatusInternal(f) {
    request({url: "/update_stat"}).then(data => {
        console.log("Updating server status");

        if (data !== null) {
            let information = JSON.parse(data);
            if (information.toString() !== "") {
                f(information);
            } else {
                console.log("Server Update Error: Got empty update!")
            }
        }

    }).catch(onerror => {
        console.log("Server Update Error: " + onerror);
    })
}

function updateStatusBtn() {
    updateStatusInternal(function (newInfo) {
        // Implement updating tables with new data

    });
}