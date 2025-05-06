package at.fhtw.swkom.paperless.services;

public interface FileStorage {
    void upload(String objectName, byte[] file);
    byte[] download(String objectName) ;
    void delete(String objectName) ;
}
