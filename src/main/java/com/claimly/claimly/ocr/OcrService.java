package com.claimly.claimly.ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class OcrService {

    private final String pastaDados;
    private final String idioma;

    public OcrService(@Value("${tesseract.datapath:}") String pastaDados,
                      @Value("${tesseract.idioma:por}") String idioma) {
        this.pastaDados = pastaDados;
        this.idioma = idioma;
    }

    public String extrairTexto(BufferedImage imagem) {
        Tesseract tesseract = new Tesseract();
        if (!pastaDados.isBlank()) {
            tesseract.setDatapath(pastaDados);
        }
        tesseract.setLanguage(idioma);

        try {
            return tesseract.doOCR(imagem);
        } catch (TesseractException e) {
            throw new IllegalStateException("Falha ao executar o OCR.", e);
        }
    }
}
