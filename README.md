# Birdy

> A script to save geotagged tweets

## Set Up

To run the script, first `cd` into the project directory and install the required modules with:
    
    npm install

Then, you will need to create a Twitter app to authenticate with Twitter via OAuth to get the tweets. Update `config/default.js` with your information.

Finally, start the application with:

    npm start

To view debugging logs, set the `DEBUG` environmental variable to `birdy`.

## Running the Birdy

You can start Birdy by using `npm run prod`. This will run Birdy with the default configuration. You can also pass in CLI arguments to modify the default configuration or modify `config/default.js` directly.

### Example of using the CLI Arguments

`npm run prod --dir=path --totalsize=1000`
