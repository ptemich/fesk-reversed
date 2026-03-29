package pl.ptemich.ksef.ksef;

public record KsefUploadResultDto(
        String ksewfNuumber,
        int processingCode,
        String processingCodeDescription
) {

}
