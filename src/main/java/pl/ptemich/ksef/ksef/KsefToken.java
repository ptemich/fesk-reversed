package pl.ptemich.ksef.ksef;

import java.time.OffsetDateTime;

public record KsefToken(
        String token,
        OffsetDateTime expiresOn,
        String refreshToken,
        OffsetDateTime refreshExpiresOn
) {
}
