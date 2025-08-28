package org.librarymanagement.constant;

public class ApiEndpoints {
    // User
    public static final String BASE_USER_URI = "/api";

    public static final String USER_AUTH = BASE_USER_URI + "/auth";
    public static final String USER_PROFILE = BASE_USER_URI + "/me";
    public static final String USER_BOOK = BASE_USER_URI + "/books";
    public static final String USER_BORROW_REQUEST = BASE_USER_URI + "/borrow-requests";
    // Admin
    public static final String BASE_ADMIN_URI = "/admin";

    public static final String ADMIN_AUTH = BASE_ADMIN_URI + "/auth";
    public static final String ADMIN_DASHBOARD = BASE_ADMIN_URI + "/dashboard";
    public static final String ADMIN_BOOK = BASE_ADMIN_URI + "/books";
    public static final String ADMIN_BORROW_REQUEST = BASE_ADMIN_URI + "/borrow-requests";
    public static final String ADMIN_USER = BASE_ADMIN_URI + "/users";
    public static final String ADMIN_AUTHOR = BASE_ADMIN_URI + "/authors";
    public static final String ADMIN_PUBLISHER = BASE_ADMIN_URI + "/publishers";
    public static final String ADMIN_GENRES = BASE_ADMIN_URI + "/genres";
    public static final String ADMIN_BORROW_REQUEST_ITEM = BASE_ADMIN_URI + "/borrow-requests-items";
}
