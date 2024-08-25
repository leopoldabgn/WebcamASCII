package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamASCII {

    public static void main(String[] args) {
        // Initialiser la webcam
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        // Créer un panneau pour afficher le flux vidéo
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        // // Créer une fenêtre pour afficher le panneau
        // JFrame window = new JFrame("Webcam Capture Example");
        // window.add(panel);
        // window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // window.pack();
        // window.setVisible(true);

        // Attendre un moment pour que la webcam démarre correctement
        try {
            Thread.sleep(2000); // 2 secondes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

		int size = 170, space = 0;
		boolean x2 = true;

        while(true) {
            // Prendre un screenshot et récupérer un BufferedImage
            BufferedImage photo = webcam.getImage();
            
            BufferedImage img = resize(convertToStandardType(photo), size, size);

            if(img == null)
                return;
    
            drawTab(getASCIITab(getGrayTab(img), img.getWidth(), img.getHeight()), space, x2);
        }

        // Fermer la webcam
        // webcam.close();

    }

    public static BufferedImage convertToStandardType(BufferedImage img) {
        if (img.getType() == BufferedImage.TYPE_CUSTOM || img.getType() == 0) {
            // Créer un nouveau BufferedImage avec un type standard
            BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            
            // Dessiner l'image originale sur la nouvelle image avec un type standard
            newImg.getGraphics().drawImage(img, 0, 0, null);
            return newImg;
        }
        return img; // Si l'image a déjà un type standard, ne rien changer
    }    

	public static int[] getNewDim(BufferedImage pic, int w_max, int h_max)
	{
		if(pic == null)
			return null;
		int w, h;
		if(pic.getWidth(null) > w_max)
			w = w_max;
		else
			w = pic.getWidth(null);

		h = pic.getHeight(null);
		float coeff = (float)w / (float)pic.getWidth(null);
		h *= coeff;

		if(h > h_max)
		{
			coeff = (float)h_max / (float)h;
			h = h_max;
			w *= coeff;
		}
		return new int[] {w, h};
	}

    public static BufferedImage resize(BufferedImage inputImg, int w, int h)
    {
    	if(inputImg == null)
    		return null;
    	int[] dim = getNewDim(inputImg, w, h);
    	w = dim[0];
    	h = dim[1];
        BufferedImage outputImage = new BufferedImage(w,
                h, inputImg.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImg, 0, 0, w, h, null);
        g2d.dispose();

        return outputImage;
    }

	public static BufferedImage loadImg(String imgPath)
	{
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(imgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}

	public static int[][] getGrayTab(BufferedImage img)
	{
		if(img == null)
			return null;
		int pixel, red, green, blue;
		int[][] grayTab = new int[img.getWidth()][img.getHeight()];

		for(int j=0;j<img.getWidth();j++)
		{
			for(int i=0;i<img.getHeight();i++)
			{
				pixel = img.getRGB(j, i);
				red = (pixel >> 16) & 0xff;
				green = (pixel >> 8) & 0xff;
				blue = pixel & 0xff;
				grayTab[j][i] = (red+green+blue)/3;
			}
		}

		return grayTab;
	}

	public static void drawTab(char[][] tab, int space, boolean x2)
	{
		for(int j=0;j<tab[0].length;j++)	// On affiche le tableau dans le
		{									// bon sens...
			if(space > 0)
				System.out.print(" ".repeat(space));
			for(int i=0;i<tab.length;i++)
			{
				System.out.print(tab[i][j]+"");
				if(x2)
					System.out.print(tab[i][j]+"");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

	public static char getASCIIByColor(int color)
	{
		String ASCII = "@%#*+=-:.";
		//String ASCII = "@$?§%#*+=-:.";
		int index = 0, coeff = 255/ASCII.length(), k=coeff;
		for(int i=0;i<ASCII.length();i++)
		{
			if(color < k)
			{
				index = i;
				break;
			}
			else if(i == ASCII.length()-1)
			{
				return ASCII.charAt(i);
			}
			k+=coeff;
		}

		return ASCII.charAt(index);
	}

	public static char[][] getASCIITab(int[][] matrix, int c, int l)
	{
		char[][] tab = new char[c][l];
		//c = matrix.length/c;
		//l = matrix[0].length/l;

		for(int j=0;j<c;j++)
		{
			for(int i=0;i<l;i++)
			{
				tab[j][i] = getASCIIByColor(matrix[j][i]);
			}
		}

		return tab;
	}

	public static int[][] getMatrixOfImage(BufferedImage img)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int[][] matrix = new int[w][h];
		for(int j=0;j<w;j++)
			for(int i=0;i<h;i++)
				matrix[j][i] = img.getRGB(j, i);

		return matrix;
	}

}
