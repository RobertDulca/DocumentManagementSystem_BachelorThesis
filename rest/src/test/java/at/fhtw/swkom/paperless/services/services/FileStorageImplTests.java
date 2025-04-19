package at.fhtw.swkom.paperless.services.services;

import at.fhtw.swkom.paperless.config.MinIOConfig;
import at.fhtw.swkom.paperless.services.FileStorageImpl;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FileStorageImplTests {

    private MinioClient minioClient;
    private FileStorageImpl fileStorage;

    @BeforeEach
    void setUp() {
        MinIOConfig minIOConfig = mock(MinIOConfig.class);
        minioClient = mock(MinioClient.class);
        fileStorage = new FileStorageImpl(minIOConfig, minioClient);

        // Standard-Mocks für den Bucket-Namen
        when(minIOConfig.getBucketName()).thenReturn("test-bucket");
    }

    @Test
    void testUploadFileCreatesBucketIfNotExists() throws Exception {
        // Arrange
        String objectName = "folder/test-file.txt";
        byte[] fileContent = "Test Content".getBytes();

        // Simuliere, dass der Bucket nicht existiert
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        // Act
        fileStorage.upload(objectName, fileContent);

        // Assert
        verify(minioClient, times(1)).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUploadFileDoesNotCreateBucketIfExists() throws Exception {
        // Arrange
        String objectName = "folder/test-file.txt";
        byte[] fileContent = "Test Content".getBytes();

        // Simuliere, dass der Bucket bereits existiert
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        // Act
        fileStorage.upload(objectName, fileContent);

        // Assert
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testDownloadThrowsRuntimeExceptionOnFailure() throws Exception {
        // Arrange
        String objectName = "folder/test-file.txt";

        // Simuliere eine Exception beim Herunterladen
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new IOException("Mock IOException"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> fileStorage.download(objectName));
        assertEquals("java.io.IOException: Mock IOException", exception.getMessage());
    }
}

