package com.mux.cnpj.batch.zip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZIPBufferedReaderFactory implements BufferedReaderFactory {
    private final ZipInputStreamFactory zipInputStreamFactory;

    public ZIPBufferedReaderFactory(ZipInputStreamFactory zipInputStreamFactory) {
        this.zipInputStreamFactory = zipInputStreamFactory;
    }

    @Override
    public BufferedReader create(Resource resource, String encoding) throws IOException {
        log.info("reading zip: {}", resource.getFile().getAbsolutePath());
        return new BufferedReader(
                new InputStreamReader(zipInputStreamFactory.create(resource.getInputStream()), encoding));
    }

}
