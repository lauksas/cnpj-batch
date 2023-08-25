package com.mux.cnpj.batch.client;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mux.cnpj.config.ApplicationConfig;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class CnpjClient {

	@Autowired
	ApplicationConfig applicationConfig;

	private static final String BASE_URL = "https://dadosabertos.rfb.gov.br/CNPJ/";

	private static final String[] SINGLE_FILES = {
			"Cnaes.zip",
			"Motivos.zip",
			"Municipios.zip",
			"Naturezas.zip",
			"Paises.zip",
			"Qualificacoes.zip",
			"Simples.zip",
	};

	private static final String[] MULTIPLE_FILES = {
			"Empresas%s.zip",
			"Estabelecimentos%s.zip",
			"Socios%s.zip",
	};

	public void updateFilesOnDisk() throws IOException, NoFileUpdatedException {
		boolean anyFileUpdated = false;
		for (String csvFile : CnpjClient.SINGLE_FILES) {
			if (updateFileIfNewer(csvFile))
				anyFileUpdated = true;
		}

		for (String csvFile : CnpjClient.MULTIPLE_FILES) {
			Exception fileNotFound = null;

			int count = 0;

			while (fileNotFound == null) {
				String csvSequence = String.format(csvFile, count);
				try {
					if (updateFileIfNewer(csvSequence))
						anyFileUpdated = true;
					count++;
				} catch (Exception e) {
					fileNotFound = e;
					log.info("last file reached");
				}
			}
		}
		if (!anyFileUpdated) {
			throw new NoFileUpdatedException();
		}
	}

	private Instant getRemoteFileLastModified(String singleFile) {
		LocalDateTime lastModified = null;
		WebClient client = createWebClient();
		URI fileUri = URI.create(BASE_URL + singleFile);

		lastModified = client.head()
				.uri(fileUri)
				.retrieve()
				.toBodilessEntity()
				.map(res -> getLastModifiedHeader(res.getHeaders()))
				.block();

		return lastModified.toInstant(ZoneOffset.UTC);
	}

	private WebClient createWebClient() {
		return WebClient.create(BASE_URL);
	}

	private LocalDateTime getLastModifiedHeader(HttpHeaders httpHeaders) {
		return LocalDateTime.parse(httpHeaders.get("Last-Modified").get(0),
				DateTimeFormatter.RFC_1123_DATE_TIME);
	}

	private void download(String csvFileName, Instant modifiedTime) throws IOException {

		String destination = applicationConfig.getCsvLocation() + "/" + csvFileName;
		URI uri = URI.create(BASE_URL + csvFileName);

		Flux<DataBuffer> flux = createWebClient()
				.get()
				.uri(uri)
				.retrieve()
				.bodyToFlux(DataBuffer.class);
		Path path = Paths.get(destination);
		Files.createDirectories(path.getParent());

		DataBufferUtils.write(flux, path)
				.block();
		setFileLastModified(path, modifiedTime);
		log.info("download finished");
	}

	private void setFileLastModified(Path path, Instant fileDate) {
		try {
			Files.setLastModifiedTime(path, FileTime.from(fileDate));
		} catch (IOException e) {
			log.error("error setting last modified", e);
		}
	}

	private boolean updateFileIfNewer(String csvFilePath) throws IOException {
		Boolean isRemoteFileNewer = true;
		log.info("checking file: {}", csvFilePath);

		Instant remoteFileInstant = getRemoteFileLastModified(csvFilePath);

		boolean fileExists = downloadedFileExists(csvFilePath);

		if (fileExists) {
			Instant savedFileInstant = getLocalFileLastModified(csvFilePath);
			isRemoteFileNewer = savedFileInstant.compareTo(remoteFileInstant) != 0;
		}

		if (isRemoteFileNewer) {
			log.info("remote is newer, downloading...");
			download(csvFilePath, remoteFileInstant);
			log.info("download finished");
		}
		return isRemoteFileNewer;
	}

	private Instant getLocalFileLastModified(String csvFileName) {
		Path path = Path.of(getCsvPath(csvFileName));
		try {
			return Files.getLastModifiedTime(path).toInstant();
		} catch (IOException e) {
			return Instant.now();
		}
	}

	private boolean downloadedFileExists(String csvFile) {
		return Files.exists(Path.of(getCsvPath(csvFile)));
	}

	private String getCsvPath(String fileName) {
		return applicationConfig.getCsvLocation() + "/" + fileName;
	}

	public String getUpdatedFilesTimeStampCsv() throws IOException {
		StringBuilder result = new StringBuilder();
		for (String csvFile : CnpjClient.SINGLE_FILES) {
			Instant remoteFileLastModified = getRemoteFileLastModified(csvFile);
			result.append(csvFile)
					.append(":")
					.append(remoteFileLastModified)
					.append(",");
		}

		for (String csvFile : CnpjClient.MULTIPLE_FILES) {
			Exception fileNotFound = null;

			int count = 0;

			while (fileNotFound == null) {
				String csvSequence = String.format(csvFile, count);
				try {
					Instant remoteFileLastModified = getRemoteFileLastModified(csvSequence);
					result.append(csvSequence)
							.append(":")
							.append(remoteFileLastModified)
							.append(",");
					count++;
				} catch (Exception e) {
					fileNotFound = e;
					log.info("last file reached");
				}
			}
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}
}
