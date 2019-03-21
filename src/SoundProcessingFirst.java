import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SoundProcessingFirst extends Application {
    public static void main(String[] args) {
        launch(args);

    }

    private static File path;
    private GridPane grid = new GridPane();
    private static double[] cleanSound;
    private static double[] shortArr;
    private static double[] average = new double[2];
    private static double[][] realAndImaginaryData = new double[2][];
    private static double[] frequency;
    private static double zeroPlaces = 0;
    private static double f = 0;
    private static double T;
    private static int[] alternatingFrequency = new int[(int) zeroPlaces];
    private static int removeNoise = 0;


    private File SoundFile() {
        FileChooser fileChooser = new FileChooser();
        Stage primaryStage = new Stage();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("wav", "*.wav")
        );
        return fileChooser.showOpenDialog(primaryStage);
    }

    private static void PlaySound() {
        try {
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;

            stream = AudioSystem.getAudioInputStream(path);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void soundArray() {
        try {
            File srcFile = path;
            FileInputStream in = new FileInputStream(srcFile);
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("gilad-OutPut.bin"));
            byte[] buf = new byte[80000];
            shortArr = new double[buf.length / 2];
            in.read(buf);
            for (int i = 0; i < buf.length / 2; i++) {
                output.writeShort((short) ((buf[i * 2] & 0xff) | (buf[i * 2 + 1] << 8)));
                shortArr[i] = ((short) ((buf[i * 2] & 0xff) | (buf[i * 2 + 1] << 8)));
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean Average() {
        if (shortArr == null){
            return false;
        }
        int Plus = 0;
        int Minus = 0;
        double positiveValues = 0;
        double negativeValues = 0;

        for (int i = 0; i < shortArr.length - 1; i++) {
            if (shortArr[i] > 0) {
                positiveValues = shortArr[i] + positiveValues;
                Plus++;
            }
            if (shortArr[i] < 0) {
                negativeValues = shortArr[i] + negativeValues;
                Minus++;
            }
        }
        average[0] = positiveValues / Plus;
        average[1] = negativeValues / Minus;
        System.out.println("Average wartosci dodatnich : " + average[0] + " Average wartosci ujemnych: " + average[1]);
        return true;
    }

    private static void RemoveNoise() {
        cleanSound = new double[shortArr.length];
        for (int i = 19; i < shortArr.length - 19; i++) {
            for (int j = -19; j <= 19; j++) {
                if (shortArr[i + j] > average[0] * 3) {
                    shortArr[i + j] = 0;
                }
                cleanSound[i] = shortArr[i + j] + cleanSound[i];
            }
            cleanSound[i] = cleanSound[i] / 20;

        }
    }

    private static double ZeroPlacesCount() throws IOException, UnsupportedAudioFileException {
        RemoveNoise();
        zeroPlaces = 0;
        for (int i = 0; i < cleanSound.length - 1; i++) {
            if (cleanSound[i] < 0 && cleanSound[i + 1] < 0 && cleanSound[i + 2] > 0 && cleanSound[i + 3] > 0) {
                zeroPlaces++;
            }
        }
        System.out.println("Liczba miejsc zerowych: " + zeroPlaces + " Czas sciezki: " + SoundTime() + "s");

        return zeroPlaces;
    }

    private static double SoundTime() throws IOException, UnsupportedAudioFileException {
        File srcFile = path;
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(srcFile);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();

        return (frames + 0.0) / format.getFrameRate();
    }

    private static void Frequency() throws IOException, UnsupportedAudioFileException {
        f = 0;
        f = Round(ZeroPlacesCount() / SoundTime());
    }

    private static double Round(double value) {
        java.text.DecimalFormat df = new java.text.DecimalFormat();
        df.setMaximumFractionDigits(0);
        return value;
    }

    private static void AllFrequencies() throws IOException, UnsupportedAudioFileException {
        int firstZeroPlaces = 1;
        double frontFrame = 0;
        alternatingFrequency = new int[(int) zeroPlaces];
        for (int i = 0; i < cleanSound.length - 3; i++) {
            if (cleanSound[i] < 0 && cleanSound[i + 1] < 0 && cleanSound[i + 2] > 0 && cleanSound[i + 3] > 0) {

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
                AudioFormat format = audioInputStream.getFormat();
                long frames = (long) (i - frontFrame);
                double durationInSeconds = (frames + 0.0) / format.getFrameRate();

                alternatingFrequency[firstZeroPlaces - 1] = (int) (1 / durationInSeconds);
                System.out.println(firstZeroPlaces + ". " + "Frequency: " + alternatingFrequency[firstZeroPlaces - 1] + "Hz" + ", Chwila czasowa: " + firstZeroPlaces * durationInSeconds
                        + ", Czas trwania dźwięku: " + durationInSeconds);
                firstZeroPlaces++;
                frontFrame = i;
            }

        }
    }

    private static void GroupFrequencies() {
        int[] array = alternatingFrequency;
        Map<Integer, Integer> hm = new HashMap();

        for (int x : array) {
            if (!hm.containsKey(x)) {
                hm.put(x, 1);
            } else {
                hm.put(x, hm.get(x) + 1);
            }
        }
        System.out.println("Czestotliwosci podstawowe: ");
        System.out.println(hm);

        Integer highestMap = null;
        int highestVote = 0;
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            if (entry.getValue() > highestVote) {
                highestMap = entry.getKey();
                highestVote = entry.getValue();
            }
        }
        System.out.println("Najczesciej wystepujaca frequency: " + highestMap + "Hz" + ", Liczba wysatpien: " + highestVote);
        if (highestMap != null) f = highestMap;
        else System.out.println("No frequencies to present");

    }

    private XYChart getSeries() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setTitle("Sygnał dźwiękoway w dziedzinie czasu");
        xAxis.setLabel("Czas");
        yAxis.setLabel("Amplituda");
        bc.getData().clear();
        bc.layout();
        return bc;
    }

    private XYChart LinearGraph() {
        if (removeNoise == 0) {
            cleanSound = shortArr;
        }
        XYChart bc = getSeries();

        XYChart.Series series = new XYChart.Series();
        series.setName("Dzwiek");
        for (int i = 0; i < shortArr.length * T; i++) {
            series.getData().add(new XYChart.Data(Integer.toString(i), cleanSound[i]));
        }
        bc.getData().addAll(series);
        return bc;
    }

////////////////////////////////////////////////////////////////
////////////////// FOURIER TRANSFORMATION /////////////////////////
////////////////////////////////////////////////////////////////


    private static void dft(double[] inreal, double[] inimag,
                            double[] outreal, double[] outimag) {
        int n = inreal.length * 5 / 100;
        int CzestotlwisocProbek = cleanSound.length / n;
        frequency = new double[n];
        for (int k = 0; k < n; k++) {  // For each output element
            double sumreal = 0;
            double sumimag = 0;

            for (int t = 0; t < n; t++) {  // For each input element
                double angle = 2 * Math.PI * t * k / n;
                sumreal += inreal[t] * Math.cos(angle) + inimag[t] * Math.sin(angle);
                sumimag += -inreal[t] * Math.sin(angle) + inimag[t] * Math.cos(angle);
            }
            outreal[k] = (sumreal * 2) / n;
            outimag[k] = (sumimag * 2) / n;

            if (k < n / 2) {
                frequency[k] = CzestotlwisocProbek * (k);
            }
        }

    }


    private static void FourierAfterTransform() {
        realAndImaginaryData = new double[2][];
        double[] outReal = new double[cleanSound.length];
        double[] outImag = new double[cleanSound.length];
        double[] inReal = new double[cleanSound.length];

        for (int i = 0; i < cleanSound.length - 1; i++) {
            inReal[i] = 0;
        }
        dft(cleanSound, inReal, outReal, outImag);
        double[] soundLen = new double[cleanSound.length];
        for (int i = 0; i < cleanSound.length - 1; i++) {
            soundLen[i] = Math.sqrt(outReal[i] * outReal[i] + outImag[i] * outImag[i]);
        }
        realAndImaginaryData[0] = outReal;
        realAndImaginaryData[1] = soundLen;

    }

    private XYChart FourierChart() {
        XYChart fc = getSeries();
        XYChart.Series series = new XYChart.Series();
        series.setName("Dzwiek");
        for (int i = 0; i < frequency.length * (f / (2 * frequency.length)) - 1; i++) {
            series.getData().add(new XYChart.Data(Double.toString((int) frequency[i + 1]), (int) realAndImaginaryData[1][i]));
        }
        fc.getData().addAll(series);
        return fc;
    }


    private static void Ton() throws LineUnavailableException {
        byte[] buf = new byte[1];

        AudioFormat af = new AudioFormat((float) f, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();
        for (int i = 0; i < 1000 * (float) f / 1000; i++) {
            double angle = i / ((float) f / 440) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * 100);
            sdl.write(buf, 0, 1);
        }
        sdl.drain();
        sdl.stop();
    }

    private void showScene(XYChart graph) {
        Stage stage = new Stage();
        Scene scene = new Scene(graph, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void start(Stage primaryStage) {

        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(12);

        Button fileBtn = new Button("Wybierz plik");
        GridPane.setConstraints(fileBtn, 0, 1);
        fileBtn.setOnAction(e -> {
            path = SoundFile();
            if (path != null) soundArray();
        });

        TextField cycleBtn = new TextField("3");
        GridPane.setConstraints(cycleBtn, 2, 2);

        Button graphBtn;
        primaryStage.setTitle("Title of the Window");
        graphBtn = new Button("Sygnal w dziedzinie czasu");
        GridPane.setConstraints(graphBtn, 0, 2);
        graphBtn.setOnAction(e -> {
            if (Average()) {
                try {
                    Frequency();
                } catch (IOException | UnsupportedAudioFileException e1) {
                    e1.printStackTrace();
                }
                T = Double.parseDouble(cycleBtn.getText()) / f;
                showScene(LinearGraph());
            }
        });

        Button harmoniousBtn = new Button("Harmoniczne");
        GridPane.setConstraints(harmoniousBtn, 0, 3);
        harmoniousBtn.setOnAction(e -> {
            FourierAfterTransform();
            showScene(FourierChart());
        });


        Button removeNoiseBtn = new Button("Odszum/Zaszum wykres");
        GridPane.setConstraints(removeNoiseBtn, 2, 1);
        removeNoiseBtn.setOnAction(e -> {
            if (removeNoise == 0) {
                removeNoise = 1;
                System.out.println("odszumiono");
            } else if (removeNoise == 1) {
                removeNoise = 0;
                System.out.println("zaszumiono");
            }
            RemoveNoise();
        });

        Button wholeSoundBtn = new Button("Odtworz Dzwiek");
        GridPane.setConstraints(wholeSoundBtn, 0, 5);
        wholeSoundBtn.setOnAction(e -> PlaySound());

        Button wholeFrequenciesBtn = new Button("Wyswietl czestotlwisci");
        GridPane.setConstraints(wholeFrequenciesBtn, 0, 6);
        wholeFrequenciesBtn.setOnAction(e -> {
            try {
                AllFrequencies();
                GroupFrequencies();
            } catch (IOException | UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            }
        });

        Button playBtn = new Button("Odtworz główną częstotliwość");
        GridPane.setConstraints(playBtn, 0, 4);
        playBtn.setOnAction(e -> {
            try {
                Ton();
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            }
        });

        grid.getChildren().addAll(graphBtn, cycleBtn, fileBtn, harmoniousBtn, removeNoiseBtn, wholeSoundBtn, playBtn, wholeFrequenciesBtn);
        Scene scene1 = new Scene(grid, 400, 250);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }
}
