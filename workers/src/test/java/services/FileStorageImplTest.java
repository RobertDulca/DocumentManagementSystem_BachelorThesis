package services;

import at.fhtw.swkom.paperless.config.MinIOConfig;
import at.fhtw.swkom.paperless.services.FileStorageImpl;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileStorageImplTest {

    @Mock
    private MinIOConfig mockMinIOConfig;

    @Mock
    private MinioClient mockMinioClient;

    private FileStorageImpl fileStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileStorage = new FileStorageImpl(mockMinIOConfig, mockMinioClient);
    }

    @Test
    void testUpload_CreatesBucketAndUploadsFile() throws Exception {
        // Arrange
        String bucketName = "test-bucket";
        String objectName = "test-object";
        byte[] fileContent = "test-content".getBytes();

        when(mockMinIOConfig.getBucketName()).thenReturn(bucketName);
        when(mockMinioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        // Act
        fileStorage.upload(objectName, fileContent);

        // Assert
        verify(mockMinioClient).bucketExists(any(BucketExistsArgs.class));
        verify(mockMinioClient).makeBucket(any(MakeBucketArgs.class));
        verify(mockMinioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUpload_SkipsBucketCreationIfExists() throws Exception {
        // Arrange
        String bucketName = "test-bucket";
        String objectName = "test-object";
        byte[] fileContent = "test-content".getBytes();

        when(mockMinIOConfig.getBucketName()).thenReturn(bucketName);
        when(mockMinioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        // Act
        fileStorage.upload(objectName, fileContent);

        // Assert
        verify(mockMinioClient, never()).makeBucket(any(MakeBucketArgs.class));
        verify(mockMinioClient).putObject(any(PutObjectArgs.class));
    }

//    @Test
//    void testDownload_ReturnsFileContent() throws Exception {
//        // Arrange
//        String bucketName = "test-bucket";
//        String objectName = "test-object";
//        byte[] expectedContent = "test-content".getBytes();
//
//        // Mock der Konfiguration
//        when(mockMinIOConfig.getBucketName()).thenReturn(bucketName);
//
//        // Mock des GetObjectResponse und Streams
//        GetObjectResponse mockGetObjectResponse = mock(GetObjectResponse.class);
//
//        when(mockGetObjectResponse.readAllBytes()).thenReturn(expectedContent);
//        when(mockMinioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockGetObjectResponse);
//
//        // Act
//        byte[] actualContent = fileStorage.download(objectName);
//
//        // Assert
//        assertArrayEquals(expectedContent, actualContent);
//        verify(mockMinioClient).getObject(any(GetObjectArgs.class));
//    }

    @Test
    void testDownload_ThrowsExceptionIfMinioFails() throws Exception {
        // Arrange
        String bucketName = "test-bucket";
        String objectName = "test-object";

        when(mockMinIOConfig.getBucketName()).thenReturn(bucketName);
        when(mockMinioClient.getObject(any(GetObjectArgs.class))).thenThrow(new RuntimeException("MinIO error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> fileStorage.download(objectName));
        assertTrue(exception.getMessage().contains("Failed to download file from MinIO"));
    }
}
