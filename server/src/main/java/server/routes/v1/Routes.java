package server.routes.v1;

/**
 * Created by giovanni on 22/06/2017.
 */
public class Routes {
    public static final String version = "/";
    public static final String SHUTDOWN = version + "shutdown";
    public static final String GET_ALL_FILES = version + "getAllFiles";
    public static final String GET_FILE = version + "getFile";
    public static final String OPEN_PROJECT = version + "openProject";
    public static final String IS_PROJECT_OPEN = version + "isProjectOpen";
    public static final String GET_FILE_BY_TYPE = version + "getFilesByType";
    public static final String GET_THREADS = version + "getThreads";
    public static final String GET_STATUS = version + "getStatus";
    public static final String GET_MAINS = version + "getMains";
    public static final String CLEAN = version + "clean";
    public static final String CLEAN_ALL = version + "cleanAll";
}
