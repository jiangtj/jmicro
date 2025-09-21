import SDK from 'casdoor-js-sdk'


const config = {
    serverUrl: "http://192.168.31.10:8000",
    clientId: "a1f9883530433d009fb1",
    organizationName: "built-in",
    appName: "application_he3oml",
    redirectPath: "/callback",
};
export const casdoorSdk = new SDK(config)