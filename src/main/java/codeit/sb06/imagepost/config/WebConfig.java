package codeit.sb06.imagepost.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // --- 1. 'local' 프로필용 업로드 파일 서빙 (/uploads/**) ---
        if (uploadDir != null && !uploadDir.isEmpty()) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + uploadDir);
        }

        // --- 2. React 정적 리소스 서빙 (/app/**) ---
        // 여기서는 실제 존재하는 파일만 처리하도록 합니다.
        registry.addResourceHandler("/app/**")
                .addResourceLocations("classpath:/static/app/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        // 파일이 존재하고 읽기 가능하면 반환, 아니면 null 반환 (SPA 경로는 SpaController가 처리)
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : null;
                    }
                });
    }
}