package com.mux.cnpj.batch.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.apache.http.client.entity.InputStreamFactory;

public class ZipInputStreamFactory implements InputStreamFactory {

	private static final ZipInputStreamFactory INSTANCE = new ZipInputStreamFactory();

	public static ZipInputStreamFactory getInstance() {
		return INSTANCE;
	}

	@Override
	public InputStream create(final InputStream inputStream) throws IOException {
		// return new ZipInputStream(inputStream);
		InputStream result = null;
        // ByteArrayInputStream inputStream = new ByteArrayInputStream(reqifzFileBytes);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // ZipEntry zipEntry = 
				zipInputStream.getNextEntry();
				result = zipInputStream;

        // while (zipEntry != null) {
        //     String fileName = zipEntry.getName();
        //     if (fileName.endsWith("")) {
        //         result = zipInputStream;
        //         break;
        //     }
        //     zipEntry = zipInputStream.getNextEntry();
        // }
        return result;
	}

}