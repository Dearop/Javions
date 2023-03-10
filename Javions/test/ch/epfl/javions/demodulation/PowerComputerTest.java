package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PowerComputerTest {
    @Test
    public void PowerComputerTestFirstValuesAreCorrect() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        int pow16 = 32;

        PowerComputer computer = new PowerComputer(stream, (int)Math.pow(2,16));
        int[] batch = new int[(int) Math.pow(2,16)];

        computer.readBatch(batch);
        System.out.println(Arrays.toString(batch));
        assertEquals(73, batch[0]);
        assertEquals(292, batch[1]);
        assertEquals(65, batch[2]);
        assertEquals(745, batch[3]);
        assertEquals(98, batch[4]);
        assertEquals(4226,batch[5]);
        assertEquals(12244, batch[6]);
        assertEquals(25722,batch[7]);
        assertEquals(36818, batch[8]);
        assertEquals(23825, batch[9]);
    }

    @Test
    public void PowerGraphTest() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerComputer computer = new PowerComputer(stream, 2400);
        int[] batch = new int[2400];

        String manyValues = "73 292 65 745 98 4226 12244 25722 36818 23825 10730 1657 1285 1280 394 521 1370 200 292 290 106 116 194 64 37 50 149 466 482 180 148 5576 13725 26210 28305 14653 4861 1489 85 845 3016 9657 19233 29041 25433 13842 3112 392 346 677 160 208 505 697 450 244 49 117 61 205 232 65 37 149 81 2 74 17 208 265 676 466 145 185 100 1586 9529 17901 28618 27296 16409 5189 2384 377 13 265 178 25 89 148 650 8528 19457 29105 31252 15172 4181 1745 85 293 680 7306 14401 24505 33010 16250 5713 1313 397 65 953 5193 20498 31880 41225 38537 30025 35272 32113 33329 20921 8005 1818 100 52 2626 8450 18794 28642 24392 13525 3922 1669 340 401 257 4 229 1021 585 2804 9325 19661 32378 30420 30298 32218 33800 33610 23666 10244 3589 740 26 137 130 521 890 765 841 3812 13124 29273 43445 48724 47525 44116 42304 37369 24961 8957 2404 578 1224 481 586 733 269 545 146 533 3125 12469 20402 19645 13058 5653 3716 1037 68 793 3985 10889 20281 24226 15641 6893 2306 136 538 4093 16265 29338 39440 38393 38900 39978 38578 40144 24946 12682 3961 904 200 130 10 90 617 544 1226 3488 8545 16417 31025 32677 18394 6292 1013 40 1 169 5765 15514 31265 36469 32114 33210 28837 34112 32805 19490 6565 2225 148 290 4525 10018 19924 25064 19345 6953 709 144 617 169 80 90 37 82 73 3573 11465 19321 29978 25721 13525 6340 2468 2309 821 4050 14722 21605 34514 37225 34450 38432 36121 35873 26969 13537 4321 436 226 128 1450 10265 20434 28097 30266 14161 4877 1642 281 565 2290 10240 18976 32045 26613 14900 5200 1189 185 145 29 29 317 65 306 1018 6976 22625 41725 46925 26725 9433 2692 225 485 586 7624 15688 23585 33562 29970 34666 33140 29250 25133 12769 5402 1189 265 650 1961 8546 21008 36305 28793 11716 4525 808 914 586 360 360 450 104 101 1602 12053 26585 41764 41869 38857 39850 40885 34493 23642 12752 4505 2421 538 185 5 100 82 265 113 1285 6305 16420 31841 32162 18980 6705 2477 761 4 1492 6964 17377 31765 28762 16810 7085 1229 80 425 745 6245 15236 29653 25933 12893 2873 337 449 400 1808 8978 21841 35378 35729 34325 39013 41012 37162 28565 12965 5626 1898 65 1044 265 580 26 509 32 746 7093 19645 27898 36605 35261 30500 32058 27220 22049 13600 3281 2657 1145 68 2196 10730 17797 24281 18173 9594 5057 2113 290 160 181 29 821 218 34 2018 13162 25810 38196 30181 13448 3250 445 701 941 5954 12833 21025 34369 21320 11720 3728 818 137 289 4265 9109 19325 29770 38116 38186 42865 44785 44329 40093 21537 10121 3754 346 586 820 72 68 45 250 1396 11177 20068 38194 36020 30650 29530 27040 32941 24425 14436 4352 1361 890 116 205 85 522 82 442 1765 11080 23050 36010 26045 12266 4145 857 306 397 3769 13445 22882 33188 26090 8186 2690 3944 4946 788 3620 15529 28013 36929 39418 29650 35837 35573 37757 32489 14600 3961 424 464 250 1537 6196 18245 29725 28349 20637 6676 1525 400 170 962 11338 23882 39700 32917 13901 7312 2000 802 800 677 533 925 404 157 1453 9850 22688 36788 29285 10946 5050 1220 369 74 1781 7690 20665 35378 31841 19610 5557 1629 1229 360 884 7272 18889 27380 25765 16250 6376 1321 10 692 1040 6205 16769 35593 39922 44314 44500 44881 39266 23090 14920 4240 1745 449 125 725 6197 18080 34469 35685 19762 4041 116 724 725 13 82 514 169 306 2516 12833 24770 34954 29429 11882 4010 1513 941 296 2650 11140 19520 30290 27562 11204 3425 953 554 221 821 6322 15304 21860 27586 12308 4426 1921 522 65 1300 6050 15892 25229 29097 12850 4610 1282 244 221 3332 11565 27338 36973 48400 44258 40400 44404 34946 28169 14274 5905 1424 50 512 584 169 65 52 85 1341 8452 17498 33205 34697 32045 32845 30370 35218 20725 9125 2957 482 1060 325 1565 9221 17440 29665 34018 20200 9122 2306 52 392 121 466 340 490 305 52 5513 15842 30125 35881 20138 6052 613 149 200 661 6001 12605 24208 23209 13298 5585 1312 485 281 2113 9197 19825 28601 23809 15529 3778 2637 1828 293 3330 11065 17896 32113 41197 35657 43784 41680 39665 26585 10210 3226 785 554 360 26 148 153 4 49 1037 9377 21445 31509 36737 16930 4453 2353 936 89 388 3985 10953 24858 32418 31586 31784 30280 28610 23204 16960 6497 2866 325 178 225 73 170 68 90 1189 7034 17221 33169 43444 38245 31365 31469 24916 22052 14373 4250 2069 1732 680 801 557 26 365 313 1189 5800 17450 33466 38897 22321 8138 2098 260 325 778 7229 16904 27274 28368 13253 4580 2929 657 148 269 5525 21037 39625 47720 47268 44761 38288 36650 24525 13753 3573 962 882 725 4736 13793 22265 33832 31813 14548 3785 394 340 90 18 202 157 1429 1450 3961 11525 17393 28225 28048 14810 4420 1640 146 317 2704 8125 19897 31338 36104 40501 36181 38146 35977 28925 18500 6416 1970 153 193 701 601 221 13 185 3028 9028 18769 31181 36097 35405 33169 36441 37498 33172 20008 7012 1908 512 73 872 6121 15845 28946 28772 14482 3716 554 725 490 197 410 328 373 449 2581 10865 19573 34625 32689 26794 29156 27658 33538 27121 12149 5265 2276 890 125 1105 8905 17680 33265 24050 13060 4225 1476 1682 1381 1189 725 32 265 178 2900 10705 17725 28180 20753 10984 4265 2410 1450 298 2813 14641 23732 35802 35594 31954 33921 31720 33749 25810 12170 5108 881 1369 233 2308 10280 22664 33761 30809 19053 4018 1073 244 1802 3978 20450 33050 44993 30689 7888 1609 160 562 16 1970 10205 20213 37034 24973 9832 3425 2465 1985 370 410 13 52 29 68 1089 8104 19769 34597 44244 38900 36569 34525 33185 28705 17218 7610 4597 857 8 226 202 73 370 1370 1625 10765 19944 32229 32825 14170 5777 2125 485 13 625 5380 12340 24228 34984 39650 41257 40354 39385 25749 13837 3249 773 306 157 1165 8450 20282 37544 40772 18857 6632 2228 5 164 197 157 565 585 128 1189 7569 14989 32420 39645 32552 32801 29489 32210 24993 12325 6074 1864 218 149 250 5581 10532 24930 31538 15381 6698 1274 405 205 221 74 265 298 10 1989 10645 22117 33337 36721 33282 30802 30509 29786 28705 13000 3796 477 953 450 634 117 136 416 16 170 5930 16084 27509 29250 14500 4201 890 265 17 232 5153 13625 27261 36805 33129 31601 29341 31201 23418 13940 5017 1405 697 260 548 49 205 100 580 116 2873 9445 24125 31410 33857 31333 36181 40853 36424 22570 7013 1865 458 170 1514 11492 23185 37780 33445 14297 4040 746 52 178 37 580 298 32 612 1466 9224 22061 32080 36324 19784 3370 1130 360 740 585 ";
        // first is 8pow2 + 3pow2
        computer.readBatch(batch);
        int[] firstValues = new int[160];
        for (int i = 0; i < firstValues.length; i++) {
            firstValues[i] = batch[i];
        }
        assertEquals(73, batch[0]);
    }

    @Test
    void PowerComputerConstructorThrowsExceptionWithBatchSizeNotAMultipleOf8() throws FileNotFoundException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        assertThrows(IllegalArgumentException.class,() -> new PowerComputer(stream, 2));
    }

    @Test
    void PowerComputerReadBatchMethodThrowsIllegalArgumentExceptionIfBatchTableSizeNotEqualToBatchSize() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        PowerComputer computer = new PowerComputer(stream, 8);
        assertThrows(IllegalArgumentException.class,() -> computer.readBatch(new int[9]));
    }
}
