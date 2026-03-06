package com.example.flagsentinelapi.logging;

public final class LogPropertiesKeys {

    private LogPropertiesKeys() {
    }

    //AUTH
    public static final String AUTH_LOGIN_ATTEMPT = "auth.login.attempt";
    public static final String AUTH_LOGIN_SUCCESS = "auth.login.success";
    public static final String AUTH_TOKEN_GENERATED = "auth.token.generated";
    public static final String AUTH_LOGIN_FAILED = "auth.login.failed";
    public static final String AUTH_LOAD_USER_REQUEST = "auth.loadUser.request";
    public static final String AUTH_LOAD_USER_NOT_FOUND = "auth.loadUser.notFound";
    public static final String AUTH_LOAD_USER_SUCCESS = "auth.loadUser.success";

    // FEATURE FLAGS
    public static final String FLAG_RESPONSE_SENT = "flag.response.sent";
    public static final String FLAG_CREATE_REQUEST = "flag.create.request";
    public static final String FLAG_CREATE_DUPLICATE = "flag.create.duplicate";
    public static final String FLAG_CREATE_SUCCESS = "flag.create.success";

    public static final String FLAG_UPDATE_REQUEST = "flag.update.request";
    public static final String FLAG_UPDATE_SUCCESS = "flag.update.success";

    public static final String FLAG_GET_REQUEST = "flag.get.request";
    public static final String FLAG_GET_ALL_REQUEST = "flag.getAll.request";
    public static final String FLAG_GET_ALL_SUCCESS = "flag.getAll.success";

    public static final String FLAG_BOOTSTRAP_REQUEST = "flag.bootstrap.request";
    public static final String FLAG_BOOTSTRAP_SUCCESS = "flag.bootstrap.success";

    public static final String FLAG_DELETE_REQUEST = "flag.delete.request";
    public static final String FLAG_DELETE_SUCCESS = "flag.delete.success";

    public static final String FLAG_NOT_FOUND = "flag.notFound";
    public static final String FLAG_INVALID_RULE_IDS = "flag.invalidRuleIds";

    public static final String FLAG_PAGE_REQUEST = "flag.page.request";
    public static final String FLAG_PAGE_SUCCESS = "flag.page.success";

    // RULES
    public static final String RULE_CREATE_REQUEST = "rule.create.request";
    public static final String RULE_CREATE_SUCCESS = "rule.create.success";
    public static final String RULE_UPDATE_REQUEST = "rule.update.request";
    public static final String RULE_UPDATE_SUCCESS = "rule.update.success";
    public static final String RULE_GET_REQUEST = "rule.get.request";
    public static final String RULE_GET_ALL_REQUEST = "rule.getAll.request";
    public static final String RULE_GET_ALL_SUCCESS = "rule.getAll.success";
    public static final String RULE_BOOTSTRAP_REQUEST = "rule.bootstrap.request";
    public static final String RULE_BOOTSTRAP_SUCCESS = "rule.bootstrap.success";
    public static final String RULE_DELETE_REQUEST = "rule.delete.request";
    public static final String RULE_DELETE_SUCCESS = "rule.delete.success";
    public static final String RULE_NOT_FOUND = "rule.notFound";
    public static final String RULE_PAGE_REQUEST = "rule.page.request";
    public static final String RULE_PAGE_SUCCESS = "rule.page.success";

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
    public static final String FLAG_EVAL_INVALID_KEY = "flag.eval.invalidKey";
    public static final String FLAG_EVAL_REQUEST = "flag.eval.request";
    public static final String FLAG_EVAL_FLAG_NOT_FOUND = "flag.eval.flagNotFound";
    public static final String FLAG_EVAL_RESULT = "flag.eval.result";

    //USER
    public static final String USER_CREATE_REQUEST = "user.create.request";
    public static final String USER_CREATE_DUPLICATE = "user.create.duplicate";
    public static final String USER_CREATE_SUCCESS = "user.create.success";
    public static final String USER_UPDATE_REQUEST = "user.update.request";
    public static final String USER_UPDATE_DUPLICATE = "user.update.duplicate";
    public static final String USER_UPDATE_SUCCESS = "user.update.success";
    public static final String USER_GET_REQUEST = "user.get.request";
    public static final String USER_GET_ALL_REQUEST = "user.getAll.request";
    public static final String USER_GET_ALL_SUCCESS = "user.getAll.success";
    public static final String USER_DELETE_REQUEST = "user.delete.request";
    public static final String USER_DELETE_SUCCESS = "user.delete.success";
    public static final String USER_SELF_DELETE_ATTEMPT = "user.delete.selfAttempt";
    public static final String USER_AUTHENTICATED_NOT_FOUND = "user.authenticated.notFound";
    public static final String USER_NOT_FOUND = "user.notFound";
    public static final String USER_PAGE_REQUEST = "user.page.request";
    public static final String USER_PAGE_SUCCESS = "user.page.success";
}




