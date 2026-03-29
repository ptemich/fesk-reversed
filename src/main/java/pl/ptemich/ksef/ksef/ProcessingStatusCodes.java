package pl.ptemich.ksef.ksef;

public class ProcessingStatusCodes {

    public static final int PROCESSING = 150;

    public static final int DUPLICATED = 440;


//            100	Faktura przyjęta do dalszego przetwarzania	-	-
//            150	Trwa przetwarzanie	-	-
//            200	Sukces	-	-
//            405	Przetwarzanie anulowane z powodu błędu sesji	-	-
//            410	Nieprawidłowy zakres uprawnień	-	-
//            415	Brak możliwości wysyłania faktury z załącznikiem	-	-
//            430	Błąd weryfikacji pliku faktury	-	-
//            435	Błąd odszyfrowania pliku	-	-
//            440	Duplikat faktury	-	'originalSessionReferenceNumber', 'originalKsefNumber'
//            450	Błąd weryfikacji semantyki dokumentu faktury	-	-
//            500	Nieznany błąd ({statusCode})	-	-
//            550	Operacja została anulowana przez system	Przetwarzanie zostało przerwane z przyczyn wewnętrznych systemu. Spróbuj ponownie	-

}
