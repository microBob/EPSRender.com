class StatusUpdateInfo {
    constructor(jobQueues, serverStat) {
        this.jobQueues = jobQueues;
        this.serverStat = serverStat;
    }

    get getServerStat() {
        return this.serverStat;
    }

    set setServerStat(value) {
        this.serverStat = value;
    }

    get getJobQueues() {
        return this.jobQueues;
    }

    set setJobQueues(value) {
        this.jobQueues = value;
    }
}