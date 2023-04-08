package ch.epfl.cs108;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.net.HttpURLConnection.*;

public class Submit {
    ;
    // CONFIGURATIONpackage ch.epfl.cs108;
    // -------------
    // Jeton du premier membre du groupe
    private static final String TOKEN_1 = "Eitaef9e";
    // Jeton du second membre (identique au premier pour les personnes travaillant seules)
    private static final String TOKEN_2 = "goo2Dohz";
    // -------------

    private static final String ZIP_ENTRY_NAME_PREFIX = "Javions/";
    private static final int TOKEN_LENGTH = 8;
    private static final int TIMEOUT_SECONDS = 5;

    private static final LocalDate SEMESTER_START_DATE = LocalDate.of(2023, Month.FEBRUARY, 20);
    private static final URI baseUri = URI.create("https://cs108.epfl.ch/");

    private static final String BASE32_ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
    private static final Pattern SUBMISSION_ID_RX =
            Pattern.compile(
                    Stream.generate(() -> "[%s]{4}".formatted(Submit.BASE32_ALPHABET))
                            .limit(4)
                            .collect(Collectors.joining("-")));

    public static void main(final String[] args) {
        final var token1 = 1 <= args.length ? args[0] : Submit.TOKEN_1;
        final var token2 = 2 <= args.length ? args[1] : Submit.TOKEN_2;

        if (TOKEN_LENGTH != token1.length()) {
            System.err.println("Erreur : vous n'avez correctement défini TOKEN_1 dans Submit.java !");
            System.exit(1);
        }
        if (TOKEN_LENGTH != token2.length()) {
            System.err.println("Erreur : vous n'avez correctement défini TOKEN_2 dans Submit.java !");
            System.exit(1);
        }

        try {
            final var projectRoot = Path.of(System.getProperty("user.dir"));
            final var submissionTimeStamp = LocalDateTime.now();

            final var submissionsDir = projectRoot.resolve("submissions");
            if (!Files.isDirectory(submissionsDir)) {
                try {
                    Files.createDirectory(submissionsDir);
                } catch (final FileAlreadyExistsException e) {
                    System.err.printf("Erreur : impossible de créer le dossier %s !\n", submissionsDir);
                }
            }

            final var fileList = Submit.getFileList(Submit.semesterWeek(LocalDate.now()));
            final var paths = Submit.filesToSubmit(projectRoot, p -> fileList.stream().anyMatch(p::endsWith));

            final var zipArchive = Submit.createZipArchive(paths);
            final var backupName = "%tF_%tH%tM%tS".formatted(
                    submissionTimeStamp, submissionTimeStamp, submissionTimeStamp, submissionTimeStamp);
            var backupPath = submissionsDir.resolve(backupName + ".zip");
            Submit.writeZip(backupPath, zipArchive);

            final var response = Submit.submitZip(token1 + token2, zipArchive);
            final var wasCreated = HTTP_CREATED == response.statusCode();
            final var printStream = wasCreated ? System.out : System.err;
            switch (response.statusCode()) {
                case HTTP_CREATED -> {
                    final var subIdMatcher = Submit.SUBMISSION_ID_RX.matcher(response.body());
                    final var subId = subIdMatcher.find() ? subIdMatcher.group() : "ERREUR";
                    final var oldBackupPath = backupPath;
                    backupPath = submissionsDir.resolve(backupName + "_" + subId + ".zip");
                    Files.move(oldBackupPath, backupPath);
                    printStream.printf("""
                                    Votre rendu a bien été reçu par le serveur et stocké sous le nom :
                                      %s
                                    Il est composé des fichiers suivants :
                                      %s
                                    Votre rendu sera prochainement validé et le résultat de cette
                                    validation vous sera communiqué par e-mail, à votre adresse de l'EPFL.
                                    """,
                            subId,
                            paths.stream().map(Object::toString).collect(Collectors.joining("\n  ")));
                }
                case HTTP_ENTITY_TOO_LARGE -> printStream.println("Erreur : l'archive est trop volumineuse !");
                case HTTP_UNAUTHORIZED -> printStream.println("Erreur : au moins un des jetons est invalide !");
                case HTTP_BAD_GATEWAY -> printStream.println("Erreur : le serveur de rendu n'est pas actif !");
                default -> printStream.printf("Erreur : réponse inattendue (%s)", response);
            }
            printStream.printf("\nUne copie de sauvegarde de l'archive a été stockée dans :\n  %s\n",
                    projectRoot.relativize(backupPath));
            System.exit(wasCreated ? 0 : 1);
        } catch (final IOException | InterruptedException e) {
            System.err.println("Erreur inattendue !");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static int semesterWeek(final LocalDate date) {
        assert Submit.SEMESTER_START_DATE.isBefore(date);
        return 1 + (int) ChronoUnit.WEEKS.between(Submit.SEMESTER_START_DATE, date);
    }

    private static List<Path> getFileList(final int stage) throws IOException, InterruptedException {
        final var fileListUri = Submit.baseUri.resolve("p/f/files-to-submit-%02d.txt".formatted(stage));
        final var httpRequest = HttpRequest.newBuilder(fileListUri)
                .GET()
                .build();
        return HttpClient.newHttpClient()
                .send(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .body()
                .map(Path::of)
                .toList();
    }

    private static List<Path> filesToSubmit(final Path projectRoot, final Predicate<Path> keepFile) throws IOException {
        try (final var paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(projectRoot::relativize)
                    .filter(keepFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static byte[] createZipArchive(final List<Path> paths) throws IOException {
        final var byteArrayOutputStream = new ByteArrayOutputStream();
        try (final var zipStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (final var path : paths) {
                final var entryPath = IntStream.range(0, path.getNameCount())
                        .mapToObj(path::getName)
                        .map(Path::toString)
                        .collect(Collectors.joining("/", Submit.ZIP_ENTRY_NAME_PREFIX, ""));
                zipStream.putNextEntry(new ZipEntry(entryPath));
                try (final var fileStream = new FileInputStream(path.toFile())) {
                    fileStream.transferTo(zipStream);
                }
                zipStream.closeEntry();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static HttpResponse<String> submitZip(final String submissionToken, final byte[] zipArchive)
            throws IOException, InterruptedException {
        final var httpRequest = HttpRequest.newBuilder(Submit.baseUri.resolve("api/submissions"))
                .POST(HttpRequest.BodyPublishers.ofByteArray(zipArchive))
                .header("Authorization", "token %s".formatted(submissionToken))
                .header("Content-Type", "application/zip")
                .header("Accept", "text/plain")
                .header("Accept-Language", "fr")
                .timeout(Duration.ofSeconds(Submit.TIMEOUT_SECONDS))
                .build();

        return HttpClient.newHttpClient()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    private static void writeZip(final Path filePath, final byte[] zipArchive) throws IOException {
        try (final var c = new FileOutputStream(filePath.toFile())) {
            c.write(zipArchive);
        }
    }
}
