package guru.springframework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class MockTest {
    @Mock
    private Map<String, String> mock1;

    @Test
    void annotationMockTest() {
        mock1.put("name", "Amer");
        System.out.println(mock1);
    }

    @Test
    void inlineMockTest() {
        Map mock2 = mock(Map.class);
        mock2.put("name", "Amer");
        System.out.println(mock2);

    }
}
