package service;

public class ServiceException extends RuntimeException {
    public ServiceException(String s) {
        super(s);
    }

    public ServiceException(Exception e) {
        super(e);
    }
}
