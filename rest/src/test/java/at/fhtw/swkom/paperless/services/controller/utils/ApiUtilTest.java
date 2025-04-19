package at.fhtw.swkom.paperless.services.controller.utils;

import at.fhtw.swkom.paperless.controller.ApiUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ApiUtilTest {

    @Test
    void testSetExampleResponse_SuccessfulResponse() throws IOException {
        // Arrange
        NativeWebRequest mockRequest = Mockito.mock(NativeWebRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        PrintWriter mockWriter = Mockito.mock(PrintWriter.class);

        when(mockRequest.getNativeResponse(HttpServletResponse.class)).thenReturn(mockResponse);
        when(mockResponse.getWriter()).thenReturn(mockWriter);

        String contentType = "application/json";
        String example = "{\"message\":\"success\"}";

        // Act
        ApiUtil.setExampleResponse(mockRequest, contentType, example);

        // Assert
        verify(mockResponse).setCharacterEncoding("UTF-8");
        verify(mockResponse).addHeader("Content-Type", contentType);
        verify(mockWriter).print(example);
    }

    @Test
    void testSetExampleResponse_IOExceptionThrown() throws IOException {
        // Arrange
        NativeWebRequest mockRequest = Mockito.mock(NativeWebRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);

        when(mockRequest.getNativeResponse(HttpServletResponse.class)).thenReturn(mockResponse);
        when(mockResponse.getWriter()).thenThrow(new IOException("Test IO Exception"));

        String contentType = "application/json";
        String example = "{\"message\":\"error\"}";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ApiUtil.setExampleResponse(mockRequest, contentType, example));

        verify(mockResponse).setCharacterEncoding("UTF-8");
        verify(mockResponse).addHeader("Content-Type", contentType);
    }

    @Test
    void testSetExampleResponse_NullResponse() {
        // Arrange
        NativeWebRequest mockRequest = Mockito.mock(NativeWebRequest.class);

        when(mockRequest.getNativeResponse(HttpServletResponse.class)).thenReturn(null);

        String contentType = "application/json";
        String example = "{\"message\":\"null response\"}";

        // Act & Assert
        assertThrows(NullPointerException.class, () -> ApiUtil.setExampleResponse(mockRequest, contentType, example));
    }
}

