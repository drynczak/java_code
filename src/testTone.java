import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;


public class testTone {

    //Sampled sine wave parameters:
    private double sampleRate = 44100.0;
    private double frequency = 500.0;
    private double amplitude = 0.8;
    private double seconds = 2.0;

    //Audio parameters:
    private int bits = 16;
    private int channels = 1;
    private boolean signed = true;
    private boolean bigEndian = false;
    private String path_name = "pure_sine.wav";

    //------------------------------------------------------------------------------------------------------------------
    //Sampled sine wave:

    private float[] newSine(double _sampleRate, double _frequency, double _amplitude, double _seconds){

        double twoPiF = 2 * Math.PI * _frequency;
        float[] buffer = new float[(int) (_seconds * _sampleRate)];

        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / _sampleRate;
            buffer[sample] = (float) (_amplitude * Math.sin(twoPiF * time));

            //System.out.println(buffer[sample]);
        }

        return buffer;
    }

    //------------------------------------------------------------------------------------------------------------------
    //Converting floats to bytes:

    private byte[] bytesGenerator(float[] _buffer){

        final byte[] byteBuffer = new byte[_buffer.length * 2];

        int bufferIndex = 0;

        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int) (_buffer[bufferIndex++] * 32767.0);
            byteBuffer[i] = (byte) x;
            i++;
            byteBuffer[i] = (byte) (x >>> 8);
        }

        return byteBuffer;
    }

    //------------------------------------------------------------------------------------------------------------------
    //Writing a WAV-file and playing buffer:

    private void createTone(double _sampleRate, int _bits, int _channels, boolean _signed, boolean _bigEndian,
                                 byte[] _byteBuffer, float[] _buffer) throws  IOException {

        File out = new File(path_name);

        AudioFormat format;
        format = new AudioFormat((float) _sampleRate, _bits, _channels, _signed, _bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(_byteBuffer);
        AudioInputStream audioInputStream;
        audioInputStream = new AudioInputStream(bais, format,_buffer.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
        audioInputStream.close();


        try {
            SourceDataLine line;
            DataLine.Info info;
            info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            line.write(_byteBuffer, 0, _byteBuffer.length);
            line.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //main:

    public static void main(String[] args) throws IOException {

        System.out.println("PURE SINE GENERATOR");
        System.out.println("Creating new instance...");
        testTone newTone = new testTone();
        System.out.println("Creating new sampled sine wave...");
        float[] sampledSine = newTone.newSine(newTone.sampleRate, newTone.frequency, newTone.amplitude, newTone.seconds);
        System.out.println("Converting floats to bytes...");
        byte[] byteSine = newTone.bytesGenerator(sampledSine);
        System.out.println("Play sine wave...");
        newTone.createTone(newTone.sampleRate, newTone.bits, newTone.channels, newTone.signed, newTone.bigEndian, byteSine, sampledSine);
        System.out.println("Sound save as pure_sine.wav" );
    }
}

