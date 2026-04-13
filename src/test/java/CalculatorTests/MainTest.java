package CalculatorTests;

import javafx.application.Application;
import launcher.MainLauncher;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mockStatic;

class MainTest {
    @Test
    void main_delegatesToApplicationLaunchWithMainClass() throws Exception {
        String[] args = new String[0];
        Class<?> mainClass = Class.forName("Main");
        @SuppressWarnings("unchecked")
        Class<? extends Application> appClass = (Class<? extends Application>) mainClass;
        Method mainMethod = mainClass.getMethod("main", String[].class);

        try (MockedStatic<Application> applicationMock = mockStatic(Application.class)) {
            applicationMock.when(() -> Application.launch(appClass, args)).thenAnswer(invocation -> null);

            mainMethod.invoke(null, (Object) args);

            applicationMock.verify(() -> Application.launch(appClass, args));
        }
    }

    @Test
    void mainLauncher_delegatesToApplicationLaunch() {
        String[] args = new String[0];

        try (MockedStatic<Application> applicationMock = mockStatic(Application.class)) {
            applicationMock.when(() -> Application.launch(args)).thenAnswer(invocation -> null);

            MainLauncher.main(args);

            applicationMock.verify(() -> Application.launch(args));
        }
    }
}
