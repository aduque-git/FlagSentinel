package com.example.flagsentinelapi.logging;

public final class LogPropertiesKeys {

    private LogPropertiesKeys() {
    }

    //AUTH
    public static final String AUTH_LOGIN_ATTEMPT = "auth.login.attempt";
    public static final String AUTH_LOGIN_SUCCESS = "auth.login.success";
    public static final String AUTH_TOKEN_GENERATED = "auth.token.generated";
    public static final String AUTH_LOGIN_FAILED = "auth.login.failed";

    // FEATURE FLAGS
    public static final String FLAG_REQUEST_RECEIVED = "flag.request.received";
    public static final String FLAG_RESPONSE_SENT = "flag.response.sent";

    // RULES / ASSOCIATIONS
    public static final String FLAG_RULES_ASSOCIATED = "flag.rules.associated";

    //EXCEPTIONS
    public static final String BUSINESS_EXCEPTION = "exception.business";
    public static final String DATABASE_EXCEPTION = "exception.database";
    public static final String AUTH_EXCEPTION = "exception.auth";
    public static final String ACCESS_DENIED_EXCEPTION = "exception.access_denied";
    public static final String UNEXPECTED_EXCEPTION = "exception.unexpected";


    //HTTPLOGGING
    public static final String HTTP_REQUEST_START = "http.request.start";
    public static final String HTTP_REQUEST_COMPLETED = "http.request.completed";
    public static final String HTTP_REQUEST_WARNING = "http.request.warning";
    public static final String HTTP_REQUEST_ERROR = "http.request.error";

    // WEBSOCKET / EVENTS
    public static final String WS_AUTH_HEADER_MISSING = "ws.auth.header.missing";
    public static final String WS_INVALID_TOKEN = "ws.auth.token.invalid";
    public static final String WS_AUTH_SUCCESS = "ws.auth.success";

    // JWT
    public static final String JWT_AUTH_SUCCESS = "jwt.auth.success";
    public static final String JWT_INVALID = "jwt.invalid";
    public static final String JWT_AUTH_FAILED = "jwt.auth.failed";
    public static final String JWT_UNEXPECTED_ERROR = "jwt.unexpected.error";

    //RULE EVALUATION
    public static final String RULE_NULL = "rule.null";
    public static final String CONTEXT_NULL = "rule.context.null";
    public static final String RULE_ATTRIBUTE_MISSING = "rule.attribute.missing";
    public static final String RULE_OPERATOR_UNSUPPORTED = "rule.operator.unsupported";
    public static final String RULE_EVALUATED = "rule.evaluated";

    //ADMIN INITIALIZER
    public static final String ADMIN_INIT_SKIPPED = "admin.init.skipped";
    public static final String ADMIN_INIT_START = "admin.init.start";
    public static final String ADMIN_INIT_CREATED = "admin.init.created";

    //DATA INITIALIZER
    public static final String DATA_INIT_SKIPPED = "data.init.skipped";
    public static final String DATA_INIT_START = "data.init.start";
    public static final String DATA_INIT_COMPLETED = "data.init.completed";

    // FLAG EVALUATION
    public static final String FLAG_EVAL_NULL_FLAG = "flag.eval.null.flag";
    public static final String FLAG_EVAL_START = "flag.eval.start";
    public static final String FLAG_EVAL_DISABLED = "flag.eval.disabled";
    public static final String FLAG_EVAL_NO_RULES = "flag.eval.no.rules";
    public static final String FLAG_EVAL_RULE_RESULT = "flag.eval.rule.result";
    public static final String FLAG_EVAL_RULE_FAILED = "flag.eval.rule.failed";
    public static final String FLAG_EVAL_ALL_RULES_OK = "flag.eval.all.ok";
}




