import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;


public class javaSockets {

    AudioInputStream audioInputStream;
    static AudioInputStream ais;
    static AudioFormat format;
    static boolean status = true;
    static int port = 50005;
    static int sampleRate = 44100;

    public static void main(String[] args) throws Exception {


        DatagramSocket serverSocket = new DatagramSocket(50005);


        byte[] receiveData = new byte[1280];
        // ( 1280 for 16 000Hz and 3584 for 44 100Hz (use AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) to get the correct size)

        format = new AudioFormat(sampleRate, 16, 1, true, false);

        while (status == true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            serverSocket.receive(receivePacket);

            ByteArrayInputStream baiss = new ByteArrayInputStream(
                    receivePacket.getData(), 0, receivePacket.getLength());

            ais = new AudioInputStream(baiss, format, receivePacket.getLength());

            // A thread solve the problem of chunky audio
            new Thread(() -> toSpeaker(receivePacket.getData())).start();

            serverSocket.close();
        }
    }

    public static void toSpeaker(byte[] soundbytes) {
        try {

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            sourceDataLine.open(format);

            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(100.0f);

            sourceDataLine.start();
            sourceDataLine.open(format);
            System.out.println("XD");

            sourceDataLine.start();

            System.out.println("format? :" + sourceDataLine.getFormat());

            sourceDataLine.write(soundbytes, 0, soundbytes.length);
            System.out.println(soundbytes.toString());
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }



}