package com.mux.cnpj.batch.zip;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZIPFactoryConfig {
    @Bean
    public ZipInputStreamFactory zipInputStreamFactory() {
        return new ZipInputStreamFactory();
    }

    @Bean
    public ZIPBufferedReaderFactory zipBufferedReaderFactory(ZipInputStreamFactory zipInputStreamFactory) {
        return new ZIPBufferedReaderFactory(zipInputStreamFactory);
    }
}
