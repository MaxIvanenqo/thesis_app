package wwsis.ivanenqo.server.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class UserImage {
    public static String draw(String fullName){
        String initials = fullName.split(" ")[0].charAt(0) + fullName.split(" ")[1].substring(0, 1);
        Color[] colors = generateColor();
        return UserImage.make(initials, 160, colors);
    }

    private static String make(String initials, int R, Color[] colors){
        BufferedImage bufferedImage = new BufferedImage(R, R, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(new Color(0,0,0,0 ));
        graphics2D.setColor(colors[1]);
        graphics2D.fillOval(0,0,R,R);
        graphics2D.setColor(colors[0]);
        graphics2D.fillOval(2,2, R-4, R-4);
        graphics2D.setColor(colors[1]);
        Font font = new Font("Arial", Font.PLAIN, 70);
        graphics2D.setFont(font);
        FontMetrics metrics = graphics2D.getFontMetrics(font);
        int x = (R - metrics.stringWidth(initials)) / 2;
        int y = ((R - metrics.getHeight()) / 2) + metrics.getAscent();
        graphics2D.drawString(initials, x, y);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            String g = HexArray.bytesToStringHex(imageInByte);
            baos.close();
            return g;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static float generateUnsigned(){
        float half1 = (float) Math.abs(((Math.random() * 127) % 127));
        float half2 = (float) Math.abs(((Math.random() * 127) % 127));
        return (half1 + half2)/255;
    }

    private static Color[] generateColor(){
        float R1 = generateUnsigned();
        float G1 = generateUnsigned();
        float B1 = generateUnsigned();
        float R2 = 1.0f - R1;
        float G2 = 1.0f - G1;
        float B2 = 1.0f - B1;

        return new Color[]{new Color(R1, G1, B1, (float) 1.0), new Color(R2, G2, B2, (float) 1.0)};
    }

    public static void main(String[] args) {
        draw("Max Ivanenqo");
    }
}
