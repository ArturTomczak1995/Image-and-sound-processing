import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.sound.sampled.*;

public class SoundProcessingSecond extends Application {
    private static File path;
    private GridPane grid = new GridPane();
    private static double[] cleanSound;
    private static double[] shortArr;
    private static double[] average = new double[2];
    private static double durationInSeconds;
    private static double[] frequency;
    private static double zeroPlaces = 0.0D;
    private static double f = 0.0D;
    private static double T;
    private static double[][] alternatingFrequency;
    private static int removeNoise;
    private static double[] wnd;
    private static double[] frequencyISTFT;


    public static void main(String[] args) {
        launch(args);
    }

    private static File SoundFile() {
        FileChooser fileChooser = new FileChooser();
        Stage primaryStage = new Stage();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("wav", "*.wav"));
        path = fileChooser.showOpenDialog(primaryStage);
        System.out.println(path);
        return path;
    }

    private static void sound(File path) {
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

    private static double[] soundArray(File path) {
        try {
            FileInputStream in = new FileInputStream(path);
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("gilad-OutPut.bin"));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
            long frames = audioInputStream.getFrameLength();
            byte[] buf = new byte[(int) frames * 2];
            shortArr = new double[buf.length / 2];
            in.read(buf);
            for (int i = 0; i < buf.length / 2; ++i) {
                output.writeShort((short) (buf[i * 2] & 255 | buf[i * 2 + 1] << 8));
                shortArr[i] = (double) ((short) (buf[i * 2] & 255 | buf[i * 2 + 1] << 8));
            }
            in.close();
        } catch (Exception var8) {
            var8.printStackTrace();
        }
        return shortArr;
    }

    private void Average() {
        if (shortArr == null) {
            return;
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
    }

    private static void RemoveNoise() {
        cleanSound = new double[shortArr.length];
        for (int i = 19; i < shortArr.length - 19; ++i) {
            for (int j = -9; j <= 9; ++j) {
                if (shortArr[i + j] > average[0] * 3.0D) {
                    shortArr[i + j] = 0.0D;
                }
                if (cleanSound[i - 1] == 0.0D && cleanSound[i + 1] == 0.0D) {
                    cleanSound[i] = 0.0D;
                }
                cleanSound[i] += shortArr[i + j];
            }
            cleanSound[i] /= 10.0D;
        }
    }

    private static double ZeroPlacesNumber() {
        RemoveNoise();
        zeroPlaces = 0.0D;

        for (int i = 0; i < cleanSound.length - 1; ++i) {
            if (cleanSound[i] < 0.0D && cleanSound[i + 1] < 0.0D && cleanSound[i + 2] > 0.0D && cleanSound[i + 3] > 0.0D) {
                ++zeroPlaces;
            }
        }
        return zeroPlaces;
    }

    private static double soundTime() throws IOException, UnsupportedAudioFileException {
        File srcFile = path;
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(srcFile);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        durationInSeconds = ((double) frames + 0.0D) / (double) format.getFrameRate();
        return durationInSeconds;
    }

    private static double frequency() throws IOException, UnsupportedAudioFileException {
        return round(ZeroPlacesNumber() / soundTime());
    }

    private static double round(double value) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(0);
        return value;
    }

    private static void wholeFrequencies() throws IOException, UnsupportedAudioFileException {
        int firstZeroPlaces = 0;
        double previousFrame = 0.0D;
        double timePeriod = 0.0D;
        alternatingFrequency = new double[3][(int) zeroPlaces + 1];

        for (int i = 0; i < cleanSound.length - 3; ++i) {
            if (cleanSound[i] < 0.0D && cleanSound[i + 1] < 0.0D && cleanSound[i + 2] > 0.0D && cleanSound[i + 3] > 0.0D) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
                AudioFormat format = audioInputStream.getFormat();
                long frames = (long) ((double) i - previousFrame);
                double durationInSeconds = ((double) frames + 0.0D) / (double) format.getFrameRate();
                timePeriod += durationInSeconds;
                alternatingFrequency[0][firstZeroPlaces] = (double) ((int) (1.0D / durationInSeconds));
                alternatingFrequency[1][firstZeroPlaces] = timePeriod;
                alternatingFrequency[2][firstZeroPlaces] = durationInSeconds;
                System.out.println(firstZeroPlaces + 1 + ". Frequency: " + alternatingFrequency[0][firstZeroPlaces] + "Hz, Moment: " + timePeriod + ", Duration: " + durationInSeconds);
                ++firstZeroPlaces;
                previousFrame = (double) i;
            }
        }
        System.out.print(Arrays.toString(alternatingFrequency[0]));
    }


    private static void groupFrequencies() {
        int[] array = new int[alternatingFrequency[0].length];

        for (int i = 0; i < alternatingFrequency[0].length; ++i) {
            array[i] = (int) alternatingFrequency[0][i];
        }
        Map<Integer, Integer> hm = new HashMap<>(); ///??
        int highestVote = array.length;

        for (int var4 = 0; var4 < highestVote; ++var4) {
            int x = array[var4];
            if (!hm.containsKey(x)) {
                hm.put(x, 1);
            } else {
                hm.put(x, hm.get(x) + 1);
            }
        }

        System.out.println("Basic frequencies: ");
        System.out.println(hm);
        Integer highestMap = null;
        highestVote = 0;

        for (Entry<Integer, Integer> integerIntegerEntry : hm.entrySet()) {
            if (integerIntegerEntry.getValue() > highestVote) {
                highestMap = integerIntegerEntry.getKey();
                highestVote = integerIntegerEntry.getValue();
            }
        }

        System.out.println("Most frequent frequency: " + highestMap + "Hz, Times occur: " + highestVote);
        if (highestMap != null) {
            f = (double) highestMap;
        } else throw new java.lang.Error("Frequency equals null");
    }

    private BarChart<String, Number> getChart(Series series, String titleSet, String xLabel, String yLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> fc = new BarChart<>(xAxis, yAxis);
        fc.setTitle(titleSet);
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        fc.getData().clear();
        fc.layout();
        fc.getData().addAll(series);
        return fc;
    }

    private BarChart<String, Number> linearGraphData() {
        if (removeNoise == 0) {
            cleanSound = shortArr;
        }
        Series<String, Number> series = new Series<>();
        for (int i = 0; (double) i < (double) shortArr.length * T; ++i) {
            series.getData().add(new Data<>(Integer.toString(i), cleanSound[i]));
        }
        return getChart(series, "Sound signal in time domain", "Time", "Volume");
    }

    private BarChart<String, Number> fourierChart() throws IOException, UnsupportedAudioFileException {
        double[] realAndImaginaryData = new double[1];
        Series<String, Number> series = new Series<>();
        f = frequency();
        for (int i = 0; (double) i < (double) frequency.length * (f / (double) (2 * frequency.length)) - 1.0D; ++i) {
            series.getData().add(new Data<>(Double.toString((double) ((int) frequency[i + 1])), realAndImaginaryData[i]));
        }
        return getChart(series, "Fourier transform of sound signal in time domain", "Frequency", "Amplitude");
    }

    private static void rectangularFunc(int windowMaskSize) {
        wnd = new double[windowMaskSize];
        for (int i = 0; i < wnd.length; ++i) {
            wnd[i] = 1.0D;
        }
    }

    private static void hannFunc(int windowMaskSize) {
        wnd = new double[windowMaskSize];
        for (int i = 0; i < wnd.length; ++i) {
            wnd[i] = 0.5D * (1.0D - Math.cos(6.283185307179586D * (double) i / ((double) wnd.length - 1.0D))) * 2.0D;
        }
    }

    private static void hammingFunc(int windowMaskSize) {
        wnd = new double[windowMaskSize];
        for (int i = 0; i < wnd.length; ++i) {
            wnd[i] = 0.5D - 0.46D * Math.cos(6.283185307179586D * (double) i / ((double) wnd.length - 1.0D));
        }

    }

    private static void lowPassFilter(double windowMaskSize) {
        double samplingFrequency = 44100.0D;
        double[] lowPassFilterArr = new double[wnd.length];
        int L = wnd.length;
        int k;
        for (k = 0; k < L; ++k) {
            if (k % 2 == 0) {
                lowPassFilterArr[k] = 2.0D * windowMaskSize / samplingFrequency;
            } else {
                lowPassFilterArr[k] = Math.sin(6.283185307179586D * windowMaskSize / samplingFrequency * (double) (k - (L - 1) / 2)) / (3.141592653589793D * (double) (k - (L - 1) / 2));
            }
        }
        for (k = 0; k < L; ++k) {
            wnd[k] *= lowPassFilterArr[k];
        }
    }

    private static void stft(double[] inReal, double[] inImag, double[] outReal, double[] outImag, int windowShift) {
        int N = inReal.length;
        int samplesFrequency = 50;
        System.out.println("CzestotlwisocProbek: " + samplesFrequency);
        frequency = new double[N];
        for (int k = 0; k < samplesFrequency; ++k) {
            double sumreal = 0.0D;
            double sumimag = 0.0D;
            for (int n = 0; n < N - 1; ++n) {
                for (int m = 0; m < wnd.length; ++m) {
                    if (n - m + windowShift < 0) {
                        sumreal = 0.0D;
                        sumimag = 0.0D;
                    } else if (n - m + windowShift >= N) {
                        sumreal = 0.0D;
                        sumimag = 0.0D;
                    } else {
                        double angle = 6.283185307179586D * (double) n * (double) k / (double) N;
                        sumreal += inReal[n - m + windowShift] * wnd[m] * Math.cos(angle) + inImag[n - m + windowShift] * wnd[m] * Math.sin(angle);
                        sumimag += -inReal[n - m + windowShift] * wnd[m] * Math.sin(angle) + inImag[n - m + windowShift] * wnd[m] * Math.cos(angle);
                    }
                }
            }
            outReal[k] = sumreal;
            outImag[k] = sumimag;
            if (k < N / 2) {
                frequency[k] = (double) (samplesFrequency * k);
            }
        }
    }


    private static void dataToSTFT(int windowShift) {
        double[][] realAndImaginaryDataSTFT = new double[3][];
        double[] outReal = new double[cleanSound.length];
        double[] outImag = new double[cleanSound.length];
        double[] inReal = new double[cleanSound.length];

        for (int i = 0; i < cleanSound.length - 1; ++i) {
            inReal[i] = 0.0D;
        }

        stft(cleanSound, inReal, outReal, outImag, windowShift);
        double[] wielkosc = new double[cleanSound.length];

        for (int i = 0; i < cleanSound.length - 1; ++i) {
            wielkosc[i] = Math.sqrt(outReal[i] * outReal[i] + outImag[i] * outImag[i]);
        }

        realAndImaginaryDataSTFT[0] = outImag;
        realAndImaginaryDataSTFT[1] = wielkosc;
        realAndImaginaryDataSTFT[2] = outReal;
        System.out.println("STFT: " + Arrays.toString(realAndImaginaryDataSTFT[2]));
    }


    private static void iSTFT() {
        int N = frequency.length;
        double[] inImag = new double[N];
        double[] outReal = new double[N];
        Arrays.fill(inImag, 1);
        int samplesFrequency = 50;
        System.out.println("CzestotlwisocProbek: " + samplesFrequency);
        frequencyISTFT = new double[N];
        for (int n = 0; n < N; ++n) {
            double sumReal = 0.0D;
            for (double v : wnd) {
                for (int k = 0; k < samplesFrequency; ++k) {
                    double angle = 6.283185307179586D * (double) k * (double) n / (double) N;
                    sumReal += frequency[k] * Math.sin(angle) + inImag[k] * Math.cos(angle);
                }
                outReal[n] = 100.0D * sumReal / ((double) N * v);
            }
            frequencyISTFT[n] = outReal[n];
        }
        System.out.println("iSTFT: " + Arrays.toString(outReal));
    }


    private static void playTon(double durationMs, int numberOfTimesFullSinFuncPerSec) throws LineUnavailableException {
        System.out.println("Make sound");
        byte[] buf = new byte[2];
        int frequency = '걄';
        AudioFormat af = new AudioFormat((float) frequency, 16, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open();
        sdl.start();

        for (int i = 0; (double) i < durationMs; ++i) {
            float numberOfSamplesToRepresentFullSin = (float) frequency / (float) numberOfTimesFullSinFuncPerSec;
            double angle = (double) i / ((double) numberOfSamplesToRepresentFullSin / 2.0D) * 3.141592653589793D;
            short a = (short) ((int) (Math.sin(angle) * 32767.0D));
            buf[0] = (byte) (a & 255);
            buf[1] = (byte) (a >> 8);
            sdl.write(buf, 0, 2);

        }
        sdl.drain();
        sdl.stop();
    }

    private static void tonSecond() throws LineUnavailableException {
        for (int z = 0; z < frequencyISTFT.length / 2; ++z) {
            double durationMs = durationInSeconds * 1000.0D / (double) shortArr.length * 44100.0D;
            int numberOfTimesFullSinFuncPerSec = (int) shortArr[z];
            playTon(durationMs, numberOfTimesFullSinFuncPerSec);
        }
    }

    private static void Ton() throws LineUnavailableException {
        for (int z = 0; z < alternatingFrequency[0].length; ++z) {
            double durationMs = alternatingFrequency[2][z] * 44100.0D;
            int numberOfTimesFullSinFuncPerSec = (int) alternatingFrequency[0][z];
            playTon(durationMs, numberOfTimesFullSinFuncPerSec);
        }
    }


    public void start(Stage primaryStage) {
        primaryStage.setTitle("Wav files processing");

        this.grid.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
        this.grid.setVgap(8.0D);
        this.grid.setHgap(10.0D);
        Label windowLenLabel = new Label("Window length: ");
        GridPane.setConstraints(windowLenLabel, 3, 1);
        TextField windowLenTF = new TextField("33");
        GridPane.setConstraints(windowLenTF, 3, 2);
        Label shiftLabel = new Label("Shift: ");
        GridPane.setConstraints(shiftLabel, 3, 3);
        TextField widowShiftHopSizeInput = new TextField("4");
        GridPane.setConstraints(widowShiftHopSizeInput, 3, 4);

        Button squareFuncBtn = new Button("Rectangle window function");
        GridPane.setConstraints(squareFuncBtn, 3, 5);
        squareFuncBtn.setOnAction((e) -> {
            Average();
            try {
                frequency();
            } catch (IOException | UnsupportedAudioFileException var6) {
                var6.printStackTrace();
            }

            removeNoise = 1;
            Average();

            try {
                frequency();
            } catch (IOException | UnsupportedAudioFileException var4) {
                var4.printStackTrace();
            }

            dataToSTFT(0);
            System.out.println("Before STFT: " + Arrays.toString(cleanSound));
            rectangularFunc(Integer.parseInt(windowLenTF.getText()));
            dataToSTFT(Integer.parseInt(widowShiftHopSizeInput.getText()));
        });
        Button vonHannBtn = new Button("Von Hann window function");
        GridPane.setConstraints(vonHannBtn, 3, 6);
        vonHannBtn.setOnAction((e) -> {
            hannFunc(Integer.parseInt(windowLenTF.getText()));
            dataToSTFT(Integer.parseInt(widowShiftHopSizeInput.getText()));
        });
        Button hammingBtn = new Button("Hamming window function");
        GridPane.setConstraints(hammingBtn, 3, 7);
        hammingBtn.setOnAction((e) -> {
            hammingFunc(Integer.parseInt(windowLenTF.getText()));
            dataToSTFT(Integer.parseInt(widowShiftHopSizeInput.getText()));
        });
        Label cutOffFrequencyLbl = new Label("Cut off frequency: ");
        GridPane.setConstraints(cutOffFrequencyLbl, 4, 1);
        TextField cutOffTF = new TextField("800");

        GridPane.setConstraints(cutOffTF, 4, 2);
        Button lowPassFilterBtn = new Button("Low pass filter");
        GridPane.setConstraints(lowPassFilterBtn, 4, 3);
        lowPassFilterBtn.setOnAction((e) -> {
            lowPassFilter((double) Integer.parseInt(cutOffTF.getText()));
            dataToSTFT(Integer.parseInt(widowShiftHopSizeInput.getText()));
        });

        Button inverseFourierBtn = new Button("Inverse Fourier");
        GridPane.setConstraints(inverseFourierBtn, 4, 4);
        inverseFourierBtn.setOnAction((e) -> iSTFT());
        Button inverseDFTSoundButton = new Button("Play sound");
        GridPane.setConstraints(inverseDFTSoundButton, 4, 5);
        inverseDFTSoundButton.setOnAction((e) -> {
            try {
                tonSecond();
            } catch (LineUnavailableException var2) {
                var2.printStackTrace();
            }

        });
        Button fileBtn = new Button("Choose a file");
        GridPane.setConstraints(fileBtn, 0, 1);
        fileBtn.setOnAction((e) -> {
            path = SoundFile();
            if (path != null) {
                shortArr = soundArray(path);
            }

        });
        TextField periodInput = new TextField("3");
        GridPane.setConstraints(periodInput, 2, 2);

        Button graphButton = new Button("Signal in the time domain");
        GridPane.setConstraints(graphButton, 0, 2);
        graphButton.setOnAction((e) -> {
            Average();

            try {
                f = frequency();
            } catch (UnsupportedAudioFileException | IOException var4) {
                var4.printStackTrace();
            }

            T = Double.parseDouble(periodInput.getText()) / f;
            Stage stage = new Stage();
            Scene scene = new Scene(linearGraphData(), 800.0D, 600.0D);
            stage.setScene(scene);
            stage.show();
        });
        Button harmoniousBan = new Button("Harmonious");
        GridPane.setConstraints(harmoniousBan, 0, 3);
        harmoniousBan.setOnAction((e) -> {
            dataToSTFT(1);
            Stage stage = new Stage();
            Scene scene = null;
            try {
                scene = new Scene(fourierChart(), 800.0D, 600.0D);
            } catch (IOException | UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            }
            stage.setScene(scene);
            stage.show();
        });
        Button removeNoiseBtn = new Button("Remove/Reverse noise");
        GridPane.setConstraints(removeNoiseBtn, 2, 1);
        removeNoiseBtn.setOnAction((e) -> {
            if (removeNoise == 0) {
                removeNoise = 1;
                System.out.println("Noise removed");
            } else if (removeNoise == 1) {
                removeNoise = 0;
                System.out.println("Noise restored");
            }

            RemoveNoise();
        });
        Button wholePathBtn = new Button("Play sound");
        GridPane.setConstraints(wholePathBtn, 0, 5);
        wholePathBtn.setOnAction((e) -> sound(path));
        Button wholeFrequenciesBtn = new Button("Display frequencies");
        GridPane.setConstraints(wholeFrequenciesBtn, 0, 6);
        wholeFrequenciesBtn.setOnAction((e) -> {
            try {
                wholeFrequencies();
                groupFrequencies();
            } catch (UnsupportedAudioFileException | IOException var2) {
                var2.printStackTrace();
            }

        });
        Button Play = new Button("Odtworz główną częstotliwość");
        GridPane.setConstraints(Play, 0, 4);
        Play.setOnAction((e) -> {
            try {
                Ton();
            } catch (LineUnavailableException var2) {
                var2.printStackTrace();
            }

        });
        this.grid.getChildren().addAll(inverseDFTSoundButton, inverseFourierBtn, cutOffFrequencyLbl, cutOffTF, lowPassFilterBtn, windowLenLabel, shiftLabel,
                widowShiftHopSizeInput, vonHannBtn, hammingBtn, squareFuncBtn, windowLenTF, graphButton, periodInput, fileBtn, harmoniousBan, removeNoiseBtn,
                wholePathBtn, Play, wholeFrequenciesBtn);
        Scene scene1 = new Scene(this.grid, 850.0D, 350.0D);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    static {
        alternatingFrequency = new double[3][(int) zeroPlaces];
        removeNoise = 0;
    }
}
