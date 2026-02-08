package app.onestepcloser.blog.utility;

public final class Constants {

    private Constants() {}

    public static final String TOKEN_PREFIX = "Bearer";
    public static final String EMPTY_STRING = "";
    public static final String BLANK_STRING = " ";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SLASH = "/";
    public static final String THREE_DOTS = "...";
    public static final String DASH = "-";
    public static final String UNDERSCORE = "_";

    public enum SORT_DIRECTION {
        ASC,
        DESC
    }

    public enum STATUS {
        INACTIVATED,
        ACTIVATED,
        DELETED
    }

    public static class REGEX_PATTERN {
        public static final String VN_PHONE_NUMBER = "^(84|0)[0-9]{9}$";
        public static final String EMAIL = "^[a-z][a-z0-9_.]{5,32}@[a-z0-9]{2,}(.[a-z0-9]{2,4}){1,2}$";
    }

    public static class ENTITY_FIELD {
        public static final String ID = "id";
        public static final String STATUS = "status";
        public static final String CREATE_TIME = "createdTime";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String ADDRESS = "address";
        public static final String IMAGE = "image";
        public static final String USER_ID = "userId";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String TYPE = "type";
        public static final String THUMBNAIL = "thumbnail";
        public static final String NUMBER_OF_VIEW = "numberOfView";
        public static final String SLUG = "slug";
        public static final String DESCRIPTION = "description";
        public static final String TAG_ID = "tagId";
        public static final String POST_ID = "postId";
        public static final String PARA_ID = "paraId";
    }

    public enum PARA_TYPE {
        ARCHIVES(0),
        RESOURCES(1),
        AREAS(2),
        PROJECTS(3);

        private final int type;

        PARA_TYPE(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }
    }

}
