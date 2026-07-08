package com.example.demo.service;

import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader.FileMode;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

@Service
public class GeoIpCountryService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GeoIpCountryService.class);

    private final boolean enabled;
    private final Path databasePath;

    private DatabaseReader databaseReader;
    private long loadedLastModified = -1L;
    private boolean missingDatabaseLogged;

    public GeoIpCountryService(
            @Value("${app.localization.geoip.enabled:true}")
            boolean enabled,
            @Value("${app.localization.geoip.database-path:geoip/GeoLite2-Country.mmdb}")
            String databasePath
    ) {
        this.enabled = enabled;
        this.databasePath = Path.of(databasePath)
                .toAbsolutePath()
                .normalize();
    }

    public synchronized Optional<String> findCountryCode(InetAddress ipAddress) {
        if (!enabled || ipAddress == null || !isPublicAddress(ipAddress)) {
            return Optional.empty();
        }

        DatabaseReader reader = getOrReloadReader();
        if (reader == null) {
            return Optional.empty();
        }

        try {
            return reader.tryCountry(ipAddress)
                    .map(response -> response.country().isoCode())
                    .filter(code -> code != null && code.matches("[A-Za-z]{2}"))
                    .map(code -> code.toUpperCase(Locale.ROOT));
        } catch (IOException | GeoIp2Exception exception) {
            LOGGER.warn(
                    "GeoIP lookup failed for the resolved client address: {}",
                    exception.getMessage()
            );
            return Optional.empty();
        }
    }

    public synchronized boolean isReady() {
        return getOrReloadReader() != null;
    }

    private synchronized DatabaseReader getOrReloadReader() {
        if (!enabled) {
            return null;
        }

        if (!Files.isRegularFile(databasePath)) {
            closeReader();
            if (!missingDatabaseLogged) {
                LOGGER.info(
                        "GeoIP database is not available at {}. "
                                + "Localization will use trusted country headers "
                                + "or Accept-Language until the database is mounted.",
                        databasePath
                );
                missingDatabaseLogged = true;
            }
            return null;
        }

        try {
            long currentLastModified =
                    Files.getLastModifiedTime(databasePath).toMillis();

            if (databaseReader != null
                    && currentLastModified == loadedLastModified) {
                return databaseReader;
            }

            DatabaseReader replacement =
                    new DatabaseReader.Builder(databasePath.toFile())
                            .fileMode(FileMode.MEMORY)
                            .withCache(new CHMCache())
                            .build();

            closeReader();
            databaseReader = replacement;
            loadedLastModified = currentLastModified;
            missingDatabaseLogged = false;

            LOGGER.info("GeoIP country database loaded from {}", databasePath);
            return databaseReader;
        } catch (IOException exception) {
            LOGGER.warn(
                    "GeoIP database could not be loaded from {}: {}",
                    databasePath,
                    exception.getMessage()
            );
            return null;
        }
    }

    private boolean isPublicAddress(InetAddress address) {
        return !address.isAnyLocalAddress()
                && !address.isLoopbackAddress()
                && !address.isLinkLocalAddress()
                && !address.isSiteLocalAddress()
                && !address.isMulticastAddress();
    }

    @PreDestroy
    public synchronized void close() {
        closeReader();
    }

    private void closeReader() {
        if (databaseReader == null) {
            loadedLastModified = -1L;
            return;
        }

        try {
            databaseReader.close();
        } catch (IOException exception) {
            LOGGER.debug(
                    "GeoIP database reader could not be closed cleanly: {}",
                    exception.getMessage()
            );
        } finally {
            databaseReader = null;
            loadedLastModified = -1L;
        }
    }
}
