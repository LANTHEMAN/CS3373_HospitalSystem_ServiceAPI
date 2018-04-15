package edu.wpi.cs3733d18.teamF.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class qrConverter {
    private String src;
    private ImageView qrView;

    public qrConverter(String src) {
        this.src = src;
        toQrView();
    }

    private void toQrView(){

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 256;
        int height = 256;
        String fileType = "png";

        BufferedImage bufferedImage = null;
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(src, BarcodeFormat.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();

            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
        } catch (WriterException ex) {
            Logger.getLogger(qrConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        ImageView qrView = new ImageView();
        qrView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

        this.qrView =  qrView;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
        toQrView();
    }

    public ImageView getQrView() {
        return qrView;
    }
}
