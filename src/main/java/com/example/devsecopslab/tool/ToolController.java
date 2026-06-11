package com.example.devsecopslab.tool;

import com.example.devsecopslab.config.LabProperties;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolController {

    private static final Pattern HOST_PATTERN =
            Pattern.compile("^[a-zA-Z0-9.-]{1,253}$");

    private final LabProperties labProperties;

    @GetMapping("/ping")
    public ResponseEntity<String> ping(@RequestParam String host) throws IOException, InterruptedException {
        if (labProperties.isVulnerableMode()) {
            Process process = Runtime.getRuntime().exec("ping " + host);
            String output = new String(process.getInputStream().readAllBytes());
            return ResponseEntity.ok(output);
        }

        validateHost(host);

        Process process = new ProcessBuilder("ping", "-c", "3", host)
                .redirectErrorStream(true)
                .start();

        boolean finished = process.waitFor(5, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Command timeout");
        }

        String output = new String(process.getInputStream().readAllBytes());
        return ResponseEntity.ok(output);
    }

    @PostMapping("/fetch-url")
    public ResponseEntity<String> fetchUrl(@Valid @RequestBody FetchUrlRequest request) {
        if (!labProperties.isVulnerableMode()) {
            validateFetchUrl(request.url());
        }

        RestClient restClient = RestClient.builder()
                .build();

        String result = restClient.get()
                .uri(request.url())
                .retrieve()
                .body(String.class);

        if (result != null && result.length() > 2000) {
            result = result.substring(0, 2000);
        }

        return ResponseEntity.ok(result);
    }

    private void validateHost(String host) {
        if (!HOST_PATTERN.matcher(host).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid host");
        }
    }

    private void validateFetchUrl(String rawUrl) {
        URI uri = URI.create(rawUrl);

        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only HTTPS is allowed");
        }

        String host = uri.getHost();
        if (host == null || !labProperties.getAllowedFetchHosts().contains(host)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Host not allowed");
        }

        blockPrivateOrMetadataAddress(host);
    }

    private void blockPrivateOrMetadataAddress(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);

            if (address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private/internal address is not allowed");
            }

            String ip = address.getHostAddress();
            Set<String> blocked = Set.of("169.254.169.254");

            if (blocked.contains(ip)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metadata endpoint is not allowed");
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot resolve host");
        }
    }
}
