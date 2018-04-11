module.exports = {
    twitter: {
        consumer_key: '',
        consumer_secret: '',
        access_token: '',
        access_token_secret: ''
    },
    dataDir: 'data',
    requestLimit: 10, // Max number of concurrent requests

    // Max number of characters to store in a single file. 10000000 characters ~= 10 MB
    fileSizeLimit: 10000000,
    // Max number of characters to store overall. 10000000 * 750 character ~= 7.5 GB
    totalSizeLimit: 10000000 * 750,
    numWorkers: 3,
    linkRegex: /https?:\/\/[-a-zA-Z.?0-9@:%._+~#=/&]+/ // A regex used to find links
}
