import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class newTone {
    public static void main(String[] args) throws  IOException {

        //Sampled sine wave:
        double sampleRate = 44100.0;
        double frequency = 440.0;
        double amplitude = 0.8;
        double seconds = 1.0;
        double twoPiF = 2 * Math.PI * frequency;

        float[] buffer = new float[(int) (seconds * sampleRate)];

        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = (float) (amplitude * Math.sin(twoPiF * time));

            //System.out.println(buffer[sample]);
        }

        //Converting floats to bytes:
        final byte[] byteBuffer = new byte[buffer.length * 2];

        int bufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int) (buffer[bufferIndex++] * 32767.0);
            byteBuffer[i] = (byte) x;
            i++;
            byteBuffer[i] = (byte) (x >>> 8);
        }

        File out = new File("out10.wav");
        boolean bigEndian = false;
        boolean signed = true;
        int bits = 16;
        int channels = 1;
        AudioFormat format;
        format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream audioInputStream;
        audioInputStream = new AudioInputStream(bais, format,buffer.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
        audioInputStream.close();


        try {
            SourceDataLine line;
            DataLine.Info info;
            info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            line.write(byteBuffer, 0, byteBuffer.length);
            line.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

