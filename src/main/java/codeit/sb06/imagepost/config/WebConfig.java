package codeit.sb06.imagepost.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application-local.properties의 'file.upload-dir' 값을 주입
    // 값이 없을 경우(예: 'dev' 프로필) 빈 문자열을 기본값으로 사용
    @Value("${file.upload-dir:}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // --- 1. 'local' 프로필용 업로드 파일 서빙 (/uploads/**) ---
        // uploadDir 값이 비어있지 않은 경우(즉, 'local' 프로필이 활성화된 경우)
        if (uploadDir != null && !uploadDir.isEmpty()) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + uploadDir);
        }

        // --- 2. React SPA 라우팅 및 정적 리소스 서빙 (/app/**) ---
        registry.addResourceHandler("/app/**")
                .addResourceLocations("classpath:/static/app/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // 1. 요청된 리소스(예: /app/static/js/main.js)를 찾습니다.
                        Resource requestedResource = location.createRelative(resourcePath);

                        // 2. 리소스가 존재하고 접근 가능하면 (파일이면) 그대로 반환합니다.
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }

                        // 3. 리소스가 존재하지 않으면 (예: /app/post/1 같은 React 라우트 경로)
                        //    대신 React 앱의 진입점(index.html)을 반환합니다.
                        return new ClassPathResource("/static/app/index.html");
                    }
                });
    }
}