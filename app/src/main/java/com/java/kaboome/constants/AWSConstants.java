package com.java.kaboome.constants;

public enum AWSConstants {

    COGNITO_USER_POOL_ID("us-west-2_nSwN0D7pC"),
    COGNITO_CLIENT_ID("3lqpbi6q2horn84d8umr0t5tab"),
    COGNITO_IDENTITY_POOL_ID("us-west-2:bbe9e101-2e25-49e0-91a2-9b653ad2ed6a"),
    IOT_ENDPOINT("apmu29qna9qgy.iot.us-west-2.amazonaws.com"),
    COGNITO_LOGIN_TOKEN_PROVIDER("cognito-idp.us-west-2.amazonaws.com/us-west-2_nSwN0D7pC"),
    //    API_GATEWAY_DEVELOPMENT_URL("https://2y970t5a81.execute-api.us-west-2.amazonaws.com/development/"),
    API_GATEWAY_DEVELOPMENT_URL("https://pxzotgz8hg.execute-api.us-west-2.amazonaws.com/development/"),
    S3_CLIENT_ENDPOINT("https://s3-us-west-2.amazonaws.com"),
    S3_BUCKET_NAME("headsup-west-01"),
    S3_BASE_URL("https://headsup-west-01.s3-us-west-2.amazonaws.com/");


    private final String label;

    AWSConstants(String value) {
        this.label = value;
    }


    @Override
    public String toString() {
        return this.label;
    }
}
