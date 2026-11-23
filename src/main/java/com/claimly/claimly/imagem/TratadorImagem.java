package com.claimly.claimly.imagem;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class TratadorImagem {

    private static final int LARGURA_MINIMA = 1500;

    static {
        OpenCV.loadLocally();
    }

    public BufferedImage tratar(BufferedImage original) {
        Mat colorida = paraMat(original);

        Mat cinza = new Mat();
        Imgproc.cvtColor(colorida, cinza, Imgproc.COLOR_BGR2GRAY);

        Mat ampliada = new Mat();
        if (cinza.cols() < LARGURA_MINIMA) {
            double escala = (double) LARGURA_MINIMA / cinza.cols();
            Imgproc.resize(cinza, ampliada, new Size(), escala, escala, Imgproc.INTER_CUBIC);
        } else {
            ampliada = cinza;
        }

        Mat semRuido = new Mat();
        Imgproc.medianBlur(ampliada, semRuido, 3);

        Mat binaria = new Mat();
        Imgproc.adaptiveThreshold(semRuido, binaria, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 31, 15);

        return paraBufferedImage(binaria);
    }

    private Mat paraMat(BufferedImage imagem) {
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            ImageIO.write(imagem, "png", saida);
            return Imgcodecs.imdecode(new MatOfByte(saida.toByteArray()), Imgcodecs.IMREAD_COLOR);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao preparar a imagem para tratamento.", e);
        }
    }

    private BufferedImage paraBufferedImage(Mat mat) {
        try {
            MatOfByte bytes = new MatOfByte();
            Imgcodecs.imencode(".png", mat, bytes);
            return ImageIO.read(new ByteArrayInputStream(bytes.toArray()));
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao converter a imagem tratada.", e);
        }
    }
}
