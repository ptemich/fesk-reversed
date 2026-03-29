package pl.ptemich.ksef.ksef;

public record KsefUploadResultDto(
        String invoiceNumber,

        String ksefNumber,
        String upoDownloadUrl,

        int processingCode,
        String processingCodeDescription
) {

}
