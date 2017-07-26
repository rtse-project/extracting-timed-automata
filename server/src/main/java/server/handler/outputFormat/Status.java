package server.handler.outputFormat;

/**
 * Created by giovanni on 07/03/2017.
 */
public class Status {
    String status;
    String description;

    public Status(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

