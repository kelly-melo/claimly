package com.claimly.claimly.imagem;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class ConversorDocumento {

    public BufferedImage paraImagem(MultipartFile arquivo) {
        try {
            byte[] bytes = arquivo.getBytes();

            if ("application/pdf".equals(arquivo.getContentType())) {
                try (PDDocument pdf = Loader.loadPDF(bytes)) {
                    return new PDFRenderer(pdf).renderImageWithDPI(0, 300);
                }
            }

            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(bytes));
            if (imagem == null) {
                throw new IllegalArgumentException("Não foi possível ler a imagem enviada.");
            }
            return imagem;
        } catch (IOException e) {
            throw new IllegalArgumentException("Arquivo corrompido ou ilegível.", e);
        }
    }
}
