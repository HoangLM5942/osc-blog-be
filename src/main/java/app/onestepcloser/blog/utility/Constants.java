package app.onestepcloser.blog.utility;

public final class Constants {

    private Constants() {}

    public static final String TOKEN_PREFIX = "bearer";
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

    public static class ROLE_TIER {
        public static final byte UNDEFINED = -1;
        public static final byte ADMINISTRATOR = 0;
        public static final byte MODIFIER = 1;
    }

    public static class STATUS {
        public static final byte INACTIVATED = 0;
        public static final byte ACTIVATED = 1;
        public static final byte DELETED = 2;
    }

    public static class REGEX_PATTERN {
        public static final String VN_PHONE_NUMBER = "^(84|0)[0-9]{9}$";
        public static final String EMAIL = "^[a-z][a-z0-9_.]{5,32}@[a-z0-9]{2,}(.[a-z0-9]{2,4}){1,2}$";
    }

    public static class MODEL_FIELD {
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

    public static class CONTENT_TYPE {
        public static final byte POST = 0;
        public static final byte QUOTE = 1;
        public static final byte DRAFT = 2;
        public static final byte NOTE = 3;
    }

    public static class PARA {
        public static final byte ARCHIVES = 0;
        public static final byte RESOURCES = 1;
        public static final byte AREAS = 2;
        public static final byte PROJECTS = 3;
    }

    public enum PARA_TYPE {
        ARCHIVES(PARA.ARCHIVES),
        RESOURCES(PARA.RESOURCES),
        AREAS(PARA.AREAS),
        PROJECTS(PARA.PROJECTS);

        private final byte type;

        PARA_TYPE(byte type) {
            this.type = type;
        }
    }

}
