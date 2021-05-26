package youssef.kecheima.topchat_v12.Model;

public class Request {
    private String request_id,equest_type;

    public Request() {
    }

    public Request(String request_id, String equest_type) {
        this.request_id = request_id;
        this.equest_type = equest_type;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getEquest_type() {
        return equest_type;
    }

    public void setEquest_type(String equest_type) {
        this.equest_type = equest_type;
    }
}
