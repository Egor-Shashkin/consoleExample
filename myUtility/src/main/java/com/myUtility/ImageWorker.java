package com.myUtility;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferByte;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.numbers.complex.Complex;

public class ImageWorker {
  private static float[] defaultBlur = {
    1/9f, 1/9f, 1/9f,
    1/9f, 1/9f, 1/9f,
    1/9f, 1/9f, 1/9f
  };
  private BufferedImage image;

  public void setImage(File file) throws IOException{
    image = ImageIO.read(file);
  }

  public BufferedImage applyBlur(){
    
  Kernel kernel = new Kernel(3, 3, defaultBlur);
  ConvolveOp op = new ConvolveOp(kernel);
  image = op.filter(image, null);
  return image;
  }

  public BufferedImage applyOffset(int offsetX, int offsetY){
    BufferedImage shiftedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    Graphics2D g2d = shiftedImage.createGraphics();
    g2d.drawImage(image, offsetX, offsetY, null); // Смещаем изображение на offsetX и offsetY
    g2d.dispose();
    image = shiftedImage;
    return shiftedImage;
  }

  public BufferedImage inverseFilter(){
    byte[] imgBytes = getAsByteArray();
    Complex[] imgComplex = new Complex[imgBytes.length];
    for (int i = 0; i < imgBytes.length; i++){
      imgComplex[i] = Complex.ZERO.add(imgBytes[i]);
    }
    Complex[] fftimg = FourierTransformer.fft(imgComplex);

  }

  public byte[] getAsByteArray(){
    WritableRaster raster = image.getRaster();
    DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
    return data.getData();
  }

  public BufferedImage getAsBufferedImage(){
    return image;
  }
}
